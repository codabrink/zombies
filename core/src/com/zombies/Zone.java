package com.zombies;

import com.zombies.HUD.DebugText;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Overlappable;
import com.zombies.interfaces.Updateable;
import com.zombies.map.Hallway;
import com.zombies.map.HallwaySegment;
import com.zombies.map.Grass;
import com.zombies.map.MapGen;
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
    // Collections

    private HashSet<Zone> adjZones = new HashSet<Zone>();
    private HashSet<Overlappable> overlappables = new HashSet<Overlappable>();
    private HashSet<Updateable> updateables = new HashSet<Updateable>();
    private HashSet<Box> boxes = new HashSet<Box>();
    private HashSet<Room> rooms = new HashSet<Room>();
    private ArrayList<Hallway> hallways = new ArrayList<Hallway>();
    private HashSet<Loadable> loadables = new HashSet<Loadable>();
    private HashSet<Drawable> drawables = new HashSet<Drawable>();
    private HashSet<Drawable> debugLines = new HashSet<Drawable>();

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

        addObject(new Grass(position, C.ZONE_SIZE, C.ZONE_SIZE));
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

    public Zone adjZoneByDirection(Integer direction) {
        Zone z = null;

        switch (direction) {
            case 1:
                z = Zone.getZone(this.position.cpy().add(0, C.ZONE_SIZE));
                break;
            case 2:
                z = Zone.getZone(this.position.cpy().add(C.ZONE_SIZE, C.ZONE_SIZE));
                break;
            case 3:
                z = Zone.getZone(this.position.cpy().add(C.ZONE_SIZE, 0));
                break;
            case 4:
                z = Zone.getZone(this.position.cpy().add(C.ZONE_SIZE, -C.ZONE_SIZE));
                break;
            case 5:
                z = Zone.getZone(this.position.cpy().add(0, -C.ZONE_SIZE));
                break;
            case 6:
                z = Zone.getZone(this.position.cpy().add(-C.ZONE_SIZE, -C.ZONE_SIZE));
                break;
            case 7:
                z = Zone.getZone(this.position.cpy().add(-C.ZONE_SIZE, 0));
                break;
            case 8:
                z = Zone.getZone(this.position.cpy().add(-C.ZONE_SIZE, C.ZONE_SIZE));
                break;
            default:
                break;
        }

        return z;
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

    public ArrayList<Zone> infringedAdjZones(Vector2 circleCenter, Float circleRadius, ArrayList<Zone> infringedZones) {
        Zone zoneToCheck;

        for (int i = 1; i < 9; i++) {
            zoneToCheck = this.adjZoneByDirection(i);
            if (zoneToCheck.pointToZoneDistance(circleCenter) < circleRadius) {
                if (!infringedZones.contains(zoneToCheck)) {
                    infringedZones.add(zoneToCheck);
                    infringedZones = zoneToCheck.infringedAdjZones(circleCenter, circleRadius, infringedZones);
                }
            }
        }

        return infringedZones;
    }

    public static ArrayList<Zone> getOverlappedZones(Vector2 circleCenter, Float circleRadius) {
        ArrayList<Zone> overlappedZones = new ArrayList<Zone>();
        Zone originZone = getZone(circleCenter);

        overlappedZones.add(originZone);
        overlappedZones = originZone.infringedAdjZones(circleCenter, circleRadius, overlappedZones);

        return overlappedZones;
    }

    public static void createHole(Vector2 blastCenter, Float blastRadius) {
        Zone startingZone = getZone(blastCenter);
        ArrayList<Zone> damageZones = Zone.getOverlappedZones(blastCenter, blastRadius);

        System.out.println("Affected zones: " + damageZones.size());

        ArrayList<Wall> wallsToCheck = new ArrayList<Wall>();

        // TODO: sometimes a wall is in a zone, but belongs to a different zone and so does not explode.
        // how can these walls be counted?
        for (Zone z: damageZones)
            wallsToCheck.addAll(z.getWalls());

        for (Wall w: wallsToCheck) {

            Float m = w.getEnd().cpy().sub(w.getStart()).y / w.getEnd().cpy().sub(w.getStart()).x;
            Float d, a, b, c, square, xi1, yi1, xi2, yi2;

            // a variation of the formula has to be used for vertical lines.
            if (m == Float.POSITIVE_INFINITY || m == Float.NEGATIVE_INFINITY) {
                d = w.getStart().x;
                a = 1.0f;
                b = -2 * blastCenter.y;
                c = (float)Math.pow(blastCenter.x, 2) + (float)Math.pow(blastCenter.y, 2) - (float)Math.pow(blastRadius, 2) - 2 * blastCenter.x * d + (float)Math.pow(d, 2);

                square = (float)Math.pow(b, 2) - 4 * a * c;

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
                b = 2 * (m * d - m * blastCenter.y - blastCenter.x);
                c = (float) Math.pow(blastCenter.y, 2) - (float) Math.pow(blastRadius, 2) + (float) Math.pow(blastCenter.x, 2) - 2 * blastCenter.y * d + (float) Math.pow(d, 2);

                square = (float)Math.pow(b, 2) - 4 * a * c;

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
        if (o instanceof Hallway)
            addHallway((Hallway) o);
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
        if (o instanceof Hallway)
            removeHallway((Hallway) o);
        if (o instanceof Overlappable)
            removeOverlappable((Overlappable) o);
        if (o instanceof Loadable)
            removeLoadable((Loadable) o);
        if (o instanceof Updateable)
            removeUpdateable((Updateable) o);
        if (o instanceof Drawable)
            removeDrawable((Drawable) o);
    }

    public Vector2 getPosition() { return position; }
    public HashSet<Box> getBoxes() { return boxes; }
    public HashSet<Room> getRooms() { return rooms; }
    public HashSet<Zone> getAdjZones() { return adjZones; }
    public HashSet<Zone> getAdjZonesPlusSelf() {
        HashSet<Zone> allZones = (HashSet<Zone>)adjZones.clone();
        allZones.add(this);
        return allZones;
    }

    private void addRoom(Room r) {
        rooms.add(r);
        for (Box b : r.getBoxes())
            addObject(b);
    }
    private void addHallway(Hallway h) {
        if (hallways.indexOf(h) == -1)
            hallways.add(h);
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
    private void removeBox(Box b) {
        boxes.remove(b);
    }
    private void removeHallway(Hallway h) {
        hallways.remove(h);
    }
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

    private ArrayList<Wall> getWalls() {
        ArrayList<Wall> walls = new ArrayList<Wall>();

        for (Room r: rooms)
            walls.addAll(r.getWalls());

        for (Hallway h: hallways)
            for (HallwaySegment hs: h.getHallwaySegments())
                walls.addAll(hs.getWalls());

        return walls;
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
        int ri = GameView.r.nextInt(boxes.size()), i = 0;
        for (Box b: boxes) {
            if (ri == i)
                return b;
            i++;
        }
        return null;
    }
}
