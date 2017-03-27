package com.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.zombies.abstract_classes.Overlappable;
import com.badlogic.gdx.math.Vector2;
import com.zombies.data.D;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.interfaces.ThreadedModelBuilderCallback;
import com.zombies.interfaces.Updateable;
import com.zombies.map.Grass;
import com.zombies.map.neighborhood.Street;
import com.zombies.map.neighborhood.StreetSegment;
import com.zombies.map.room.*;
import com.zombies.map.room.Building;
import com.zombies.util.Assets.MATERIAL;
import com.zombies.util.Bounds2;
import com.zombies.util.ThreadedModelBuilder;
import com.zombies.util.ThreadedModelBuilder.MODELING_STATE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class Zone {
    private ThreadedModelBuilder modelBuilder = new ThreadedModelBuilder(new ThreadedModelBuilderCallback() {
        @Override
        public void response(Model m) {
            if (model != null)
                model.dispose();
            model = m;
            modelInstance = new ModelInstance(model);
            modelInstance.transform.setTranslation(center.x, center.y, 0);
        }
    });
    private Thread modelingThread;
    private static class ZoneModelingRunnable implements Runnable {
        private ThreadedModelBuilder modelBuilder;
        private Zone zone;
        public ZoneModelingRunnable(Zone zone, ThreadedModelBuilder modelBuilder) {
            this.modelBuilder = modelBuilder;
            this.zone = zone;
        }
        @Override
        public void run() {
            D.addRunningThread(Thread.currentThread());
            while (modelBuilder.modelingState != MODELING_STATE.DORMANT)
                try { Thread.sleep(500l); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            zone.rebuildModel();
            if (zone.needsRemodel) { // reuse the thread
                run();
                return;
            }
            D.removeRunningThread(Thread.currentThread());
        }
    };



    private Vector2 position, center;
    public Bounds2 bounds;
    public int loadIndex; // Tracks on what frame the zone was loaded (garbage collection)
    private static Random r = new Random();

    private Model model;
    private ModelInstance modelInstance;

    public enum GENERATOR_STATE { UNINITIATED, GENERATING, GENERATED }
    public GENERATOR_STATE genState = GENERATOR_STATE.UNINITIATED;

    // Static Variables
    public static HashMap<String, Zone> zones;
    public static HashSet<Zone> loadedZones;
    public static Zone currentZone;
    public static int globalLoadIndex = 0;

    private LinkedHashMap<MATERIAL, LinkedHashSet<ModelMeCallback>> modelables = new LinkedHashMap<>();
    public boolean needsRemodel = false; // flag true, and the model will rebuild at next possible moment

    // Collections

    private LinkedHashSet<Box> boxes = new LinkedHashSet<>();
    private LinkedHashSet<Room> rooms = new LinkedHashSet<>();
    private LinkedHashSet<Building> buildings = new LinkedHashSet<>();
    private LinkedHashSet<Street> streets = new LinkedHashSet<>();
    private LinkedHashSet<StreetSegment> streetSegments = new LinkedHashSet<>();

    private LinkedHashMap<Integer, LinkedHashSet<Zone>> adjZones = new LinkedHashMap<>();
    private LinkedHashSet<Overlappable> overlappables = new LinkedHashSet<>();
    private LinkedHashSet<Updateable> updateables = new LinkedHashSet<>();
    private LinkedHashSet<Wall> walls = new LinkedHashSet<>();
    private LinkedHashSet<Loadable> loadables = new LinkedHashSet<>();
    private LinkedHashSet<Drawable> drawables = new LinkedHashSet<>();
    private LinkedHashSet<Drawable> debugLines = new LinkedHashSet<>();

    private Set pendingObjects = Collections.synchronizedSet(new LinkedHashSet());

    public int numRooms = 6; // number of rooms that are supposed to exist in the zone

    public Zone(float x, float y) {
        position = new Vector2(x, y);
        center = position.cpy().add(C.ZONE_HALF_SIZE, C.ZONE_HALF_SIZE);
        bounds = new Bounds2(x, y, C.ZONE_SIZE, C.ZONE_SIZE);
        numRooms = r.nextInt(numRooms);
        addObject(new Grass(this, C.ZONE_SIZE, C.ZONE_SIZE));

        if (C.ENABLE_DEBUG_LINES) {
            debugLines.add(new DebugLine(new Vector2(position.x, position.y), new Vector2(position.x, position.y + C.ZONE_SIZE)));
            debugLines.add(new DebugLine(new Vector2(position.x, position.y), new Vector2(position.x + C.ZONE_SIZE, position.y)));
        }
    }

    public void addModelingCallback(MATERIAL m, ModelMeCallback mmc) {
        LinkedHashSet<ModelMeCallback> modelableSet = modelables.get(m);
        if (modelableSet == null) {
            modelableSet = new LinkedHashSet<>();
            modelables.put(m, modelableSet);
        }
        modelableSet.add(mmc);
    }
    public void removeModelingCallback(MATERIAL m, ModelMeCallback mmc) {
        LinkedHashSet<ModelMeCallback> modelableSet = modelables.get(m);
        if (modelableSet != null)
            modelableSet.remove(mmc);
    }

    public void draw(int limit) {
        HashSet<Zone> zones = getAdjZones(limit);
        for (Zone z : zones)
            z.draw();
    }
    public void draw() {
        drawThineself();
        for (Drawable d : drawables)
            if (d.getZone() == this)
                d.draw(GameView.gv.spriteBatch, GameView.gv.shapeRenderer, GameView.gv.modelBatch);
        for (Drawable d : debugLines)
            d.draw(GameView.gv.spriteBatch, GameView.gv.shapeRenderer, GameView.gv.modelBatch);
    }
    private void drawThineself() {
        if (modelInstance == null)
            return;
        GameView.gv.modelBatch.begin(GameView.gv.getCamera());
        GameView.gv.modelBatch.render(modelInstance, GameView.outsideEnvironment);
        GameView.gv.modelBatch.end();
    }

    public void update(int limit) {
        HashSet<Zone> zones = getAdjZones(limit);
        for (Zone z : zones)
            z.update();
    }
    public void update() {
        synchronized (pendingObjects) {
            Iterator i = pendingObjects.iterator();
            while (i.hasNext()) {
                addObject((HasZone) i.next());
                i.remove();
            }
        }

        if (needsRemodel && (modelingThread == null || !modelingThread.isAlive())) {
            needsRemodel = false;
            modelingThread = new Thread(new ZoneModelingRunnable(this, modelBuilder));
            modelingThread.start();
        }

        for (Updateable u : updateables)
            if (u.getZone() == this)
                u.update();
    }

    public void load(int limit) {
        HashSet<Zone> zones = getAdjZones(limit);
        for (Zone z: zones)
            z.load();
    }
    public void load() {
        if (loadIndex == Zone.globalLoadIndex)
            return; // already loaded
        loadIndex = Zone.globalLoadIndex;
        Zone.loadedZones.add(this);

        for (Loadable l: loadables)
            l.load();
    }

    public void unload() {
        for (Loadable l: loadables)
            l.unload();
    }

    public static Zone getZone(float x, float y) {
        int indX = (int)Math.floor(x / C.ZONE_SIZE);
        int indY = (int)Math.floor(y / C.ZONE_SIZE);

        Zone z = zones.get(indY + "," + indX);
        if (z == null) {
            z = new Zone(indX * C.ZONE_SIZE, indY * C.ZONE_SIZE);
            zones.put(indY + "," + indX, z);
        }
        return z;
    }
    public static Zone getZone(Vector2 v) {
        return getZone(v.x, v.y);
    }
    public static boolean setCurrentZone(Zone z) {
        if (Zone.currentZone == z)
            return false;

        Zone.globalLoadIndex++;
        Zone.currentZone = z;
        z.load(C.DRAW_DISTANCE);

        // unload dormant zones
        for (Iterator<Zone> it = loadedZones.iterator(); it.hasNext();) {
            Zone zz = it.next();
            if (zz.loadIndex != Zone.globalLoadIndex) {
                zz.unload();
                it.remove();
            }
        }
        return true;
    }

    public static HashSet<Zone> zonesOnLine(Vector2 start, Vector2 end) {
        // slope intercept form (y = mx + b)
        float m = (end.y - start.y) / (end.x - start.x);
        float b = start.y - (m * start.x);

        HashSet<Zone> zones = new HashSet<>(); // HashSet gives uniqueness constraint for free
        float xStart = (float)(C.ZONE_SIZE * Math.floor(Math.min(start.x, end.x) / C.ZONE_SIZE));
        float xEnd   = (float)(C.ZONE_SIZE * Math.ceil(Math.max(start.x, end.x) / C.ZONE_SIZE));
        float yStart = (float)(C.ZONE_SIZE * Math.floor(Math.min(start.y, end.y) / C.ZONE_SIZE));
        float yEnd   = (float)(C.ZONE_SIZE * Math.ceil(Math.max(start.y, end.y) / C.ZONE_SIZE));

        float halfZoneSize = C.ZONE_SIZE / 2;

        // find all vertical intercepts of line on zone grid
        for (float x = xStart; x < xEnd; x = x + C.ZONE_SIZE) {
            // add zones from both sides of line
            zones.add(getZone(x - halfZoneSize, m * x + b));
            zones.add(getZone(x + halfZoneSize, m * x + b));
        }
        if (m != 0) {
            // find all horizontal intercepts of line on zone grid
            for (float y = yStart; y < yEnd; y = y + C.ZONE_SIZE) {
                zones.add(getZone((y - b) / m, y - halfZoneSize));
                zones.add(getZone((y - b) / m, y + halfZoneSize));
            }
        }
        return zones;
    }

    public static HashSet<Wall> getWallsOverlappingCircle(Vector2 c, float r) {
        Vector2 p1, p2;
        HashSet<Wall> walls = new HashSet<>();
        for (Zone z : Zone.getZone(c).getAdjZones(C.DRAW_DISTANCE)) {
            for (Wall w : z.getWalls()) {
                p1 = w.getStart();
                p2 = w.getEnd();
                // http://math.stackexchange.com/questions/275529/check-if-line-intersects-with-circles-perimeter
                boolean intersects =
                        (Math.abs((p2.x - p1.x) * c.x + (p1.y - p2.y) * c.y + (p1.x - p2.x) * p1.y + (p2.y - p1.y) * p1.x)) /
                                Math.sqrt(Math.pow((double)(p2.x - p1.x), 2) + Math.pow((double)(p1.y - p2.y), 2)) <= r;
                if (intersects)
                    walls.add(w);
            }
        }
        return walls;
    }

    public static void createHole(Vector2 center, Float radius) {
        HashSet<Zone> zones = Zone.getZone(center).getAdjZones(C.DRAW_DISTANCE);
        for (Zone z : zones) {
            for (Wall w : z.getWalls()) {
                Float m = w.getEnd().cpy().sub(w.getStart()).y / w.getEnd().cpy().sub(w.getStart()).x;
                Float d, a, b, c, square, xi1, yi1, xi2, yi2;

                // a variation of the formula has to be used for vertical lines.
                if (m == Float.POSITIVE_INFINITY || m == Float.NEGATIVE_INFINITY) {
                    d = w.getStart().x;
                    a = 1.0f;
                    b = -2 * center.y;
                    c = (float) Math.pow(center.x, 2) + (float) Math.pow(center.y, 2) - (float) Math.pow(radius, 2) - 2 * center.x * d + (float) Math.pow(d, 2);

                    square = (float) Math.pow(b, 2) - 4 * a * c;

                    // this line misses or is tangent to the circle.
                    if (square <= 0.0f)
                        continue;

                    xi1 = d;
                    yi1 = (-b + (float) Math.pow(square, 0.5)) / (2 * a);
                    xi2 = d;
                    yi2 = (-b - (float) Math.pow(square, 0.5)) / (2 * a);
                } else {
                    d = w.getEnd().y - m * w.getEnd().x;
                    a = (float) Math.pow(m, 2) + 1;
                    b = 2 * (m * d - m * center.y - center.x);
                    c = (float) Math.pow(center.y, 2) - (float) Math.pow(radius, 2) + (float) Math.pow(center.x, 2) - 2 * center.y * d + (float) Math.pow(d, 2);

                    square = (float) Math.pow(b, 2) - 4 * a * c;

                    if (square <= 0.0f)
                        continue;

                    xi1 = (-b + (float) Math.pow(square, 0.5)) / (2 * a);
                    yi1 = m * xi1 + d;
                    xi2 = (-b - (float) Math.pow(square, 0.5)) / (2 * a);
                    yi2 = m * xi2 + d;
                }

                Vector2 i1 = new Vector2(xi1, yi1);
                Vector2 i2 = new Vector2(xi2, yi2);

                // if either lineIntersectionPoint is beyond the wall, pull it back to the wall's endpoint so
                // the hole will draw correctly.
                if (i1.cpy().dst(w.getStart()) < i1.cpy().dst(w.getEnd())) {
                    if (w.getEnd().dst(i1) > w.getEnd().dst(w.getStart()))
                        i1 = w.getStart();
                } else {
                    if (w.getStart().dst(i1) > w.getStart().dst(w.getEnd()))
                        i1 = w.getEnd();
                }

                if (i2.cpy().dst(w.getStart()) < i2.cpy().dst(w.getEnd())) {
                    if (w.getEnd().dst(i2) > w.getEnd().dst(w.getStart()))
                        i2 = w.getStart();
                } else {
                    if (w.getStart().dst(i2) > w.getStart().dst(w.getEnd()))
                        i2 = w.getEnd();
                }

                // if both intersections are beyond one of the wall's endpoints, they both be set to the
                // same endpoint by the code above. the segment is not actually being intersected. only
                // create the hole if that is not the case.
                if (!i1.equals(i2))
                    w.createHole(i1.cpy().add(i2).scl(0.5f), i1.cpy().dst(i2));
            }
        }
    }

    public Box getBox(float x, float y) {
        for (Zone z : getAdjZones(C.DRAW_DISTANCE))
            for (Box b : z.getBoxes())
                if (b.contains(x, y))
                    return b;
        return null;
    }
    public Box getBox(Vector2 v) {
        return getBox(v.x, v.y);
    }

    public Zone addPendingObject(Object o) {
        synchronized (pendingObjects) {
            pendingObjects.add(o);
        }
        return this;
    }
    public Zone addObject(Object o) {
        if (o instanceof HasZone)
            ((HasZone)o).setZone(this);

        // KEEP RECORDS
        if (o instanceof Wall)
            addWall((Wall) o);
        if (o instanceof Building)
            addBuilding((Building) o);
        if (o instanceof Room)
            addRoom((Room) o);
        if (o instanceof Box)
            addBox((Box) o);
        if (o instanceof Overlappable)
            addOverlappable((Overlappable) o);
        if (o instanceof Loadable)
            addLoadable((Loadable) o);
        if (o instanceof Updateable)
            addUpdateable((Updateable) o);
        if (o instanceof Drawable)
            addDrawable((Drawable) o);

        return this;
    }
    public Zone removeObject(HasZone o) {
        o.setZone(null);

        if (o instanceof Wall)
            removeWall((Wall) o);
        if (o instanceof Building)
            removeBuilding((Building) o);
        if (o instanceof Room)
            removeRoom((Room) o);
        if (o instanceof Box)
            removeBox((Box) o);
        if (o instanceof Overlappable)
            removeOverlappable((Overlappable) o);
        if (o instanceof Loadable)
            removeLoadable((Loadable) o);
        if (o instanceof Updateable)
            removeUpdateable((Updateable) o);
        if (o instanceof Drawable)
            removeDrawable((Drawable) o);

        return this;
    }

    public String getKey() { return (int)Math.floor(position.x / C.ZONE_SIZE) + "," + (int)Math.floor(position.y / C.ZONE_SIZE); }
    public Vector2 getPosition() { return position; }
    public Vector2 getCenter() { return center; }
    public LinkedHashSet<Overlappable> getOverlappables() { return overlappables; }
    public Set getPendingObjects() { return pendingObjects; }
    public LinkedHashSet<Box> getBoxes() { return boxes; }
    public LinkedHashSet<Room> getRooms() { return rooms; }
    public LinkedHashSet<Building> getBuildings() { return buildings; }
    public LinkedHashSet<Street> getStreets() { return streets; }
    public LinkedHashSet<StreetSegment> getStreetSegments() { return streetSegments; }

    private void addBuilding(Building b) { buildings.add(b); }
    private void addRoom(Room r) {
        rooms.add(r);
    }
    private void addBox(Box b) {
        boxes.add(b);
    }

    private void removeWall(Wall w) {
        walls.remove(w);
    }
    private void removeBuilding(Building b) {
        buildings.remove(b);
        for (Room r : b.getRooms())
            removeObject(r);
    }
    private void removeRoom(Room r) {
        rooms.remove(r);
        for (Box b : r.getBoxes())
            removeObject(b);
    }
    private void removeBox(Box b) {
        boxes.remove(b);
    }

    private void addOverlappable(Overlappable o) {
        overlappables.add(o);
    }
    private void removeOverlappable(Overlappable o) {
        overlappables.remove(o);
    }
    private void addDrawable(Drawable d) { drawables.add(d); }
    private void removeDrawable(Drawable d) {
        drawables.remove(d);
    }
    private void addLoadable(Loadable l) {
        loadables.add(l);
    }
    private void removeLoadable(Loadable l) {
        loadables.remove(l);
    }
    private void addUpdateable(Updateable u) {
        updateables.add(u);
    }
    private void removeUpdateable(Updateable u) {
        updateables.remove(u);
    }

    public HashSet<com.zombies.map.room.Wall> getWalls() { return walls; }
    private void addWall(com.zombies.map.room.Wall w) { walls.add(w); }

    public Overlappable checkOverlap(Overlappable overlappable, int limit, Collection ignore) {
        HashSet<Zone> zones = getAdjZones(limit);
        for (Zone z : zones) {
            for (Overlappable o : z.getOverlappables()) {
                if (ignore != null && ignore.contains(o))
                    continue;
                if (overlappable.overlaps(o))
                    return o;
            }
            synchronized (z.getPendingObjects()) {
                for (Object o : z.getPendingObjects()) {
                    if (ignore != null && ignore.contains(o))
                        continue;
                    if (o instanceof Overlappable && overlappable.overlaps((Overlappable) o))
                        return (Overlappable) o;
                }
            }
        }
        return null;
    }

    public HashSet<Zone> getAdjZones(int limit) {
        LinkedHashSet<Zone> zones = adjZones.get(limit);
        if (zones != null)
            return zones;

        zones = new LinkedHashSet<>();
        float variance = C.ZONE_SIZE * limit;

        for (float x = center.x - variance; x <= center.x + variance; x += C.ZONE_SIZE)
            for (float y = center.y - variance; y <= center.y + variance; y += C.ZONE_SIZE)
                zones.add(Zone.getZone(x, y));

        adjZones.put(limit, zones);
        return zones;
    }

    public HashSet<Overlappable> checkOverlap(float x, float y, int limit) {
        HashSet<Overlappable> overlapped = new HashSet<>();
        HashSet<Zone> zones = getAdjZones(limit);
        Iterator<Zone> iterator = zones.iterator();
        while (iterator.hasNext())
            iterator.next().checkOverlap(x, y, overlapped);
        return overlapped;
    }
    public HashSet<Overlappable> checkOverlap(Vector2 point, int limit) { return checkOverlap(point.x, point.y, limit);}
    private HashSet<Overlappable> checkOverlap(float x, float y, HashSet<Overlappable> overlapped) {
        for (Overlappable o : overlappables)
            if (o.contains(x, y))
                overlapped.add(o);
        return overlapped;
    }

    public void rebuildModel() {
        // only run in modeling thread
        if (modelingThread == null || Thread.currentThread().getId() != modelingThread.getId()) {
            needsRemodel = true;
            return;
        }

        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("Walls",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        for (Wall w : walls)
            w.buildWallMesh(builder, center);
        for (MATERIAL m : modelables.keySet()) {
            builder = modelBuilder.part(m.partName,
                    GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                    new Material(m.texture.textureAttribute));
            for (ModelMeCallback mc : modelables.get(m))
                mc.buildModel(builder, center);
        }
        modelBuilder.finish();
    }
}
