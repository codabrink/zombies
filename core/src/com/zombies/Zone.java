package com.zombies;

import com.zombies.abstract_classes.Overlappable;
import com.zombies.HUD.DebugText;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Updateable;
import com.zombies.map.*;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Zone {
    private Vector2 position;
    private int updateFrame, drawFrame;
    public int loadIndex; // Tracks on what frame the zone was loaded (garbage collection)
    private static Random r;

    // Static Variables
    public static HashMap<String, Zone> zones;
    public static ArrayList<Zone> loadedZones;
    public static Zone currentZone;
    public static int globalLoadIndex = 0;
    private static Box zone;

    public enum Directions { N, NE, E, SE, S, SW, W, NW };

    // Collections

    private HashSet<Zone> adjZones = new HashSet<Zone>();
    private HashSet<Overlappable> overlappables = new HashSet<>();
    private HashSet<Updateable> updateables = new HashSet<>();
    private HashSet<Box> boxes = new HashSet<>();
    private HashSet<com.zombies.map.room.Room> rooms = new HashSet<>();
    //private ArrayList<Hallway> hallways = new ArrayList<>();
    private HashSet<Wall> walls = new HashSet<>();
    private HashSet<Loadable> loadables = new HashSet<>();
    private HashSet<Drawable> drawables = new HashSet<>();
    private HashSet<Drawable> debugLines = new HashSet<>();

    private ArrayList<ArrayList<Drawable>> drawablesList = new ArrayList<>();

    public int numRooms = 6; // number of rooms that are supposed to exist in the zone
    public int roomGenFailureCount = 0; // number of rooms that failed to generate due to overlap

    public Zone(float x, float y) {
        r = GameView.gv.random;
        position = new Vector2(x, y);
        for (int i = 0; i <= C.DRAW_LAYERS; i++) {
            drawablesList.add(new ArrayList<Drawable>());
        }
        numRooms = r.nextInt(numRooms);

        if (C.ENABLE_DEBUG_LINES) {
            debugLines.add(new DebugLine(new Vector2(position.x, position.y), new Vector2(position.x, position.y + C.ZONE_SIZE)));
            debugLines.add(new DebugLine(new Vector2(position.x, position.y), new Vector2(position.x + C.ZONE_SIZE, position.y)));
        }

        addObject(new Grass(position, C.ZONE_SIZE, C.ZONE_SIZE));
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
        float[] zonePositions = {
                position.x - C.ZONE_SIZE, position.y - C.ZONE_SIZE,
                position.x - C.ZONE_SIZE, position.y,
                position.x - C.ZONE_SIZE, position.y + C.ZONE_SIZE,
                position.x, position.y + C.ZONE_SIZE,
                position.x + C.ZONE_SIZE, position.y + C.ZONE_SIZE,
                position.x + C.ZONE_SIZE, position.y,
                position.x + C.ZONE_SIZE, position.y - C.ZONE_SIZE,
                position.x, position.y - C.ZONE_SIZE
        };

        for (int i = 0; i < zonePositions.length; i += 2) {
            Zone z = Zone.getZone(zonePositions[i], zonePositions[i+1]);
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

    public Zone adjZoneByDirection(Directions direction) {
        switch (direction) {
            case N:
                return Zone.getZone(position.x, position.y + C.ZONE_SIZE);
            case NE:
                return Zone.getZone(position.x + C.ZONE_SIZE, position.y + C.ZONE_SIZE);
            case E:
                return Zone.getZone(position.x + C.ZONE_SIZE, position.y);
            case SE:
                return Zone.getZone(position.x + C.ZONE_SIZE, position.y - C.ZONE_SIZE);
            case S:
                return Zone.getZone(position.x, position.y - C.ZONE_SIZE);
            case SW:
                return Zone.getZone(position.x - C.ZONE_SIZE, position.y - C.ZONE_SIZE);
            case W:
                return Zone.getZone(position.x - C.ZONE_SIZE, position.y);
            case NW:
                return Zone.getZone(position.x - C.ZONE_SIZE, position.y + C.ZONE_SIZE);
        }
        return null;
    }

    public Float pointToZoneDistance(Vector2 point) {
        if (point.x > this.position.x && point.x < this.position.x + C.ZONE_SIZE && point.y > this.position.y && point.y < this.position.y + C.ZONE_SIZE) {
            return 0.0f;
        } else if (point.x > this.position.x && point.x < this.position.x + C.ZONE_SIZE) {
            return Math.min(point.dst(point.x, this.position.y), point.dst(point.x, this.position.y + C.ZONE_SIZE));
        } else if (point.y > this.position.y && point.y < this.position.y + C.ZONE_SIZE) {
            return Math.min(point.dst(this.position.x, point.y), point.dst(this.position.x + C.ZONE_SIZE, point.y));
        } else {
            return Math.min(
                Math.min(point.dst(this.position.x, this.position.y), point.dst(this.position.x + C.ZONE_SIZE, this.position.y)),
                    Math.min(point.dst(this.position.x, this.position.y + C.ZONE_SIZE), point.dst(this.position.x + C.ZONE_SIZE, this.position.y + C.ZONE_SIZE))
            );
        }
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
        HashSet<Zone> zones = getAdjZones(1);
        Iterator<Zone> iterator = zones.iterator();
        while (iterator.hasNext())
            for (Box b: iterator.next().getBoxes())
                if (b.contains(x, y))
                    return b;
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

    public String getKey() { return (int)Math.floor(position.x / C.ZONE_SIZE) + "," + (int)Math.floor(position.y / C.ZONE_SIZE); }
    public Vector2 getPosition() { return position; }
    public HashSet<Box> getBoxes() { return boxes; }
    public HashSet<Overlappable> getOverlappables() { return overlappables; }
    public HashSet<Room> getRooms() { return rooms; }
    public HashSet<Zone> getAdjZones() { return adjZones; }

    private void addRoom(Room r) {
        rooms.add(r);
        for (Box b : r.getBoxes())
            addObject(b);
    }
    private void addBox(Box b) {
        boxes.add(b);
    }
    private void removeRoom(Room r) {
        rooms.remove(r);
        for (Box b : r.getBoxes()) {
            removeObject(b);
        }
    }
    private void removeBox(Box b) { boxes.remove(b); }
    private void addDrawable(Drawable d) {
        drawables.add(d);
    }
    private void addDrawableNoCheck(Drawable d) {
        drawables.add(d);
    }
    private void removeDrawable(Drawable d) {
        drawables.remove(d);
    }
    private void addOverlappable(Overlappable o) {
        overlappables.add(o);
    }
    private void removeOverlappable(Overlappable o) {
        overlappables.remove(o);
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

    public HashSet<Wall> getWalls() { return walls; }
    public void addWall(Wall w) { walls.add(w); }

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
    public Overlappable checkOverlap(float x, float y, float w, float h, int limit) {
        return checkOverlap(x, y, w, h, limit, null);
    }
    public Overlappable checkOverlap(Vector2 v, float w, float h, int limit) {
        return checkOverlap(v.x, v.y, w, h, limit, null);
    }
    public Overlappable checkOverlap(Vector2 v, float w, float h, int limit, ArrayList<Overlappable> ignore) {
        return checkOverlap(v.x, v.y, w, h, limit, ignore);
    }

    private Vector2 center() {
        return new Vector2(position.x + C.ZONE_SIZE / 2, position.y + C.ZONE_SIZE / 2);
    }

    public HashSet<Zone> getAdjZones(int limit) {
        HashSet<Zone> zones = new HashSet<>();
        Vector2 center = center();
        float variance = C.ZONE_SIZE * limit;

        for (float x = center.x - variance; x <= center.x + variance; x += C.ZONE_SIZE)
            for (float y = center.y - variance; y <= center.y + variance; y += C.ZONE_SIZE)
                zones.add(Zone.getZone(x, y));
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

    public Vector2 randomDiscretePosition(float interval) {
        interval = interval * C.SCALE;
        float randomX = r.nextInt((int)Math.floor(C.ZONE_SIZE / interval)) * (C.ZONE_SIZE / interval);
        float randomY = r.nextInt((int)Math.floor(C.ZONE_SIZE / interval)) * (C.ZONE_SIZE / interval);
        return position.cpy().add(randomX, randomY);
    }

    public Box randomBox() {
        if (boxes.size() == 0) return null;

        int ri = GameView.r.nextInt(boxes.size()), i = 0;
        for (Box b: boxes) {
            if (ri == i)
                return b;
            i++;
        }
        return null;
    }

    public Vector2 suggestedStartPoint() {
        if (boxes.size() == 0)
            return randomPosition();

        return randomBox().randomPoint();
    }
}
