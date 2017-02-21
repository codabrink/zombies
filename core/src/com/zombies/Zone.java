package com.zombies;

import com.zombies.abstract_classes.Overlappable;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Updateable;
import com.zombies.map.Grass;
import com.zombies.map.room.*;
import com.zombies.map.room.Building;
import com.zombies.util.U;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Zone {
    private Vector2 position;
    public int loadIndex; // Tracks on what frame the zone was loaded (garbage collection)
    private static Random r;

    // Static Variables
    public static HashMap<String, Zone> zones;
    public static ArrayList<Zone> loadedZones;
    public static HashSet<Room> readyToModel;
    public static Zone currentZone;
    public static int globalLoadIndex = 0;

    // Collections
    private HashMap<Integer, HashSet<Zone>> adjZones = new HashMap<>();
    private HashSet<Overlappable> overlappables = new HashSet<>();
    private HashSet<Updateable> updateables = new HashSet<>();
    private HashSet<Box> boxes = new HashSet<>();
    private HashSet<Room> rooms = new HashSet<>();
    private HashSet<Building> buildings = new HashSet<>();
    private HashSet<Wall> walls = new HashSet<>();
    private HashSet<Loadable> loadables = new HashSet<>();
    private HashSet<Drawable> drawables = new HashSet<>();
    private HashSet<Drawable> debugLines = new HashSet<>();

    public int numRooms = 6; // number of rooms that are supposed to exist in the zone

    public Zone(float x, float y) {
        r = GameView.gv.random;
        position = new Vector2(x, y);
        numRooms = r.nextInt(numRooms);

        if (C.ENABLE_DEBUG_LINES) {
            debugLines.add(new DebugLine(new Vector2(position.x, position.y), new Vector2(position.x, position.y + C.ZONE_SIZE)));
            debugLines.add(new DebugLine(new Vector2(position.x, position.y), new Vector2(position.x + C.ZONE_SIZE, position.y)));
        }

        addObject(new Grass(position, C.ZONE_SIZE, C.ZONE_SIZE));
    }


    public void draw(int limit) {
        HashSet<Zone> zones = getAdjZones(limit);
        for (Zone z : zones)
            z.draw();
    }
    public void draw() {
        for (Drawable d : drawables)
            if (d.getZone() == this)
                d.draw(GameView.gv.spriteBatch, GameView.gv.shapeRenderer, GameView.gv.modelBatch);
        for (Drawable d : debugLines)
            d.draw(GameView.gv.spriteBatch, GameView.gv.shapeRenderer, GameView.gv.modelBatch);
    }

    public void update(int limit) {
        HashSet<Zone> zones = getAdjZones(limit);
        for (Zone z : zones)
            z.update();
    }
    public void update() {
        for (Updateable u : updateables)
            if (u.getZone() == this)
                u.update();

        if (readyToModel.size() == 0)
            return;
        Iterator iter = readyToModel.iterator();
        while (iter.hasNext()) {
            ((Room)iter.next()).buildModel();
            iter.remove();
        }
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

        Zone z = zones.get("row"+indY+"column"+indX);
        if (z == null) {
            z = new Zone(indX * C.ZONE_SIZE, indY * C.ZONE_SIZE);
            zones.put("row"+indY+"column"+indX, z);
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
        float m = (end.y - start.y) / (end.x - end.y);
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
        // find all horizontal intercepts of line on zone grid
        for (float y = yStart; y < yEnd; y = y + C.ZONE_SIZE) {
            zones.add(getZone((y - b) / m, y - halfZoneSize));
            zones.add(getZone((y - b) / m, y + halfZoneSize));
        }
        return zones;
    }

    public static HashSet<Wall> getWallsOverlappingCircle(Vector2 c, float r) {
        Vector2 p1, p2;
        HashSet<Wall> walls = new HashSet<>();
        for (Zone z : Zone.getZone(c).getAdjZones(1)) {
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
        HashSet<Zone> zones = Zone.getZone(center).getAdjZones(1);
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

                // if either intersection is beyond the wall, pull it back to the wall's endpoint so
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
        for (Zone z : getAdjZones(1))
            for (Box b : z.getBoxes())
                if (b.contains(x, y))
                    return b;
        return null;
    }
    public Box getBox(Vector2 v) {
        return getBox(v.x, v.y);
    }

    public Zone addObject(HasZone o) {
        o.setZone(this);

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

        if (o.getZone() != this)
            return this;

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
    public HashSet<Overlappable> getOverlappables() { return overlappables; }
    public HashSet<Box> getBoxes() { return boxes; }
    public HashSet<Room> getRooms() { return rooms; }
    public HashSet<Building> getBuildings() { return buildings; }

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

    public Overlappable checkOverlap(float x, float y, float w, float h, int limit, ArrayList<Overlappable> ignore) {
        HashSet<Zone> zones = getAdjZones(1);
        for (Zone z : zones) {
            for (Overlappable o : z.getOverlappables()) {
                if (o.overlaps(x, y, w, h)) {
                    if (ignore != null && ignore.indexOf(o) != -1)
                        continue;
                    return o;
                }
            }
        }
        return null;
    }
    public Overlappable checkOverlap(Vector2 v, float w, float h, int limit) {
        return checkOverlap(v.x, v.y, w, h, limit, null);
    }

    private Vector2 center() {
        return new Vector2(position.x + C.ZONE_SIZE / 2, position.y + C.ZONE_SIZE / 2);
    }

    public HashSet<Zone> getAdjZones(int limit) {
        HashSet<Zone> zones = adjZones.get(limit);
        if (zones != null)
            return zones;

        zones = new HashSet<>();
        Vector2 center = center();
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

    public Vector2 randomPosition() {
        float randomX = r.nextFloat() * C.ZONE_SIZE;
        float randomY = r.nextFloat() * C.ZONE_SIZE;
        return position.cpy().add(randomX, randomY);
    }
}
