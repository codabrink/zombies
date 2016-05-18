package com.zombies;

import com.HUD.DebugText;
import com.badlogic.gdx.math.Vector2;
import com.interfaces.Drawable;
import com.interfaces.HasZone;
import com.interfaces.Loadable;
import com.interfaces.Overlappable;
import com.interfaces.Updateable;
import com.map.MapGen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Zone {
    private Vector2 position;
    private int updateFrame, drawFrame;
    public int loadIndex; // Tracks on what frame the zone was loaded
    private static Random r;

    // Static Variables
    public static HashMap<String, Zone> zones;
    public static ArrayList<Zone> loadedZones;
    public static Zone currentZone;
    public static int globalLoadIndex = 0;
    // Collections
    private ArrayList<Zone> adjZones = new ArrayList<Zone>();
    private ArrayList<Overlappable> overlappables = new ArrayList<Overlappable>();
    private ArrayList<Updateable> updateables = new ArrayList<Updateable>();
    private ArrayList<Box> boxes = new ArrayList<Box>();
    private ArrayList<Room> rooms = new ArrayList<Room>();
    private ArrayList<Loadable> loadables = new ArrayList<Loadable>();
    private ArrayList<Drawable> drawables = new ArrayList<Drawable>();
    private ArrayList<Drawable> debugLines = new ArrayList<Drawable>();

    private ArrayList<ArrayList<Drawable>> drawablesList = new ArrayList<ArrayList<Drawable>>();

    public int numRooms = 2; // number of rooms that are supposed to exist in the zone
    public int roomGenFailureCount = 0; // number of rooms that failed to generate due to overlap

    public Zone(float x, float y) {
        r = GameView.gv.random;
        position = new Vector2(x, y);
        for (int i=0;i<=C.DRAW_LAYERS;i++) {
            drawablesList.add(new ArrayList<Drawable>());
        }
        numRooms = r.nextInt(numRooms);

        if (C.ENABLE_DEBUG_LINES) {
            debugLines.add(new DebugLine(new Vector2(position.x, position.y), new Vector2(position.x, position.y + C.ZONE_SIZE)));
            debugLines.add(new DebugLine(new Vector2(position.x, position.y), new Vector2(position.x + C.ZONE_SIZE, position.y)));
        }
    }

    public void generate() {
        MapGen.fillZone(this);
    }

    public void draw(int frame, int limit) {
        if (drawFrame == frame)
            return;
        drawFrame = frame;

        for (Drawable d: drawables)
            d.draw(GameView.gv.spriteBatch, GameView.gv.shapeRenderer, GameView.gv.modelBatch);
        for (Drawable d: debugLines)
            d.draw(GameView.gv.spriteBatch, GameView.gv.shapeRenderer, GameView.gv.modelBatch);

        if (limit > 0)
            for (Zone z: adjZones) {
                z.draw(frame, limit - 1);
            }
    }

    public void update(int frame, int limit) {
        if (updateFrame == frame)
            return;
        updateFrame = frame;

        MapGen.update(this); // generate the map

        for (Updateable u: updateables)
            u.update();

        DebugText.addMessage("rooms", "Rooms in zone: " + rooms.size());

        if (limit > 0)
            for (Zone z: adjZones) {
                z.update(frame, limit - 1);
            }
    }

    public void findAdjZones() {
        float[] zonePositions = {position.x - C.ZONE_SIZE, position.y - C.ZONE_SIZE,
        position.x - C.ZONE_SIZE, position.y,
        position.x - C.ZONE_SIZE, position.y + C.ZONE_SIZE,
        position.x, position.y + C.ZONE_SIZE,
        position.x + C.ZONE_SIZE, position.y + C.ZONE_SIZE,
        position.x + C.ZONE_SIZE, position.y,
        position.x + C.ZONE_SIZE, position.y - C.ZONE_SIZE,
        position.x, position.y - C.ZONE_SIZE};

        for (int i=0;i<zonePositions.length;i+=2) {
            Zone z = Zone.getZone(zonePositions[i], zonePositions[i+1]);
            if (z != this && adjZones.indexOf(z) == -1)
                adjZones.add(z);
        }
    }

    public void load(int limit) {
        if (loadIndex == Zone.globalLoadIndex)
            return; // already loaded
        loadIndex = Zone.globalLoadIndex;
        Zone.loadedZones.add(this);

        for (Loadable l: loadables)
            l.load();

        if (limit > 0)
            for (Zone z: adjZones)
                z.load(limit - 1);
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

    public Box getBox(float x, float y) {
        for (Box b: boxes) {
            if (b.insideBox(x, y))
                return b;
        }
        return null;
    }
    public Box getBox(Vector2 v) {
        return getBox(v.x, v.y);
    }

    public void addObject(HasZone o) {
        if (o.getZone() != null)
            o.getZone().removeObject(o);
        o.setZone(this);

        // KEEP RECORDS
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
    }
    public void removeObject(HasZone o) {
        o.setZone(null);

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
    }

    public Vector2 getPosition() {
        return position;
    }
    public ArrayList<Box> getBoxes() { return boxes; }
    public ArrayList<Room> getRooms() { return rooms; }
    public ArrayList<Zone> getAdjZones() { return adjZones; }
    public ArrayList<Zone> getAdjZonesPlusSelf() {
        ArrayList<Zone> allZones = (ArrayList<Zone>)adjZones.clone();
        allZones.add(this);
        return allZones;
    }

    private void addRoom(Room r) {
        if (rooms.indexOf(r) == -1)
            rooms.add(r);
        for (Box b : r.getBoxes()) {
            Zone.getZone(b.getCenter()).addObject(b);
        }
    }
    private void addBox(Box b) {
        if (boxes.indexOf(b) == -1)
            boxes.add(b);
    }
    private void removeRoom(Room r) {
        rooms.remove(r);
        for (Box b : r.getBoxes()) {
            removeObject(b);
        }
    }
    private void removeBox(Box b) {
        boxes.remove(b);
    }
    private void addDrawable(Drawable d) {
        if (drawables.indexOf(d) == -1)
            drawables.add(d);
    }
    private void addDrawableNoCheck(Drawable d) {
        drawables.add(d);
    }
    private void removeDrawable(Drawable d) {
        drawables.remove(d);
    }
    private void addOverlappable(Overlappable o) {
        if (overlappables.indexOf(o) == -1)
            overlappables.add(o);
    }
    private void removeOverlappable(Overlappable o) {
        overlappables.remove(o);
    }
    private void addLoadable(Loadable l) {
        if (loadables.indexOf(l) == -1)
            loadables.add(l);
    }
    private void removeLoadable(Loadable l) {
        loadables.remove(l);
    }
    private void addUpdateable(Updateable u) {
        if (updateables.indexOf(u) == -1)
            updateables.add(u);
    }
    private void removeUpdateable(Updateable u) {
        updateables.remove(u);
    }

    public Overlappable checkOverlap(float x, float y, float w, float h, int limit, ArrayList<Overlappable> ignore) {
        for (Overlappable o: overlappables) {
            if (o.overlaps(x, y, w, h)) {
                if (ignore != null) {
                    if (ignore.indexOf(o) != -1)
                        continue;
                    return o;
                } else {
                    return o;
                }
            }
        }
        if (limit > 0) {
            for (Zone z : adjZones) {
                Overlappable o = checkOverlap(x, y, w, h, limit - 1, ignore);
                if (o != null)
                    return o;
            }
        }
        return null;
    }
    public Overlappable checkOverlap(float x, float y, float w, float h, int limit) {
        return checkOverlap(x, y, w, h, limit, null);
    }
    public Overlappable checkOverlap(Vector2 v, float w, float h, int limit) {
        return checkOverlap(v.x, v.y, w, h, limit, null);
    }
    public Overlappable checkOverlap(Vector2 v, float w, float h, int limit, ArrayList<Overlappable> ignore) {
        return checkOverlap(v.x, v.y, w, h, limit, ignore);
    }

    public Vector2 randomPosition() {
        float randomX = r.nextFloat() * C.ZONE_SIZE;
        float randomY = r.nextFloat() * C.ZONE_SIZE;
        return position.cpy().add(randomX, randomY);
    }

    public Vector2 randomDiscreetPosition(int numIncrements) {
        float randomX = r.nextInt(numIncrements) * (C.ZONE_SIZE / numIncrements);
        float randomY = r.nextInt(numIncrements) * (C.ZONE_SIZE / numIncrements);
        return position.cpy().add(randomX, randomY);
    }

    public Box randomBox() {
        if (boxes.size() > 0)
            return boxes.get(r.nextInt(boxes.size()));
        else
            return null;
    }
}
