package com.zombies;

import com.HUD.DebugText;
import com.badlogic.gdx.math.Vector2;
import com.interfaces.Collideable;
import com.interfaces.Drawable;
import com.interfaces.HasZone;
import com.interfaces.Loadable;
import com.interfaces.Overlappable;
import com.interfaces.Updateable;
import com.map.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by coda on 2/27/2016.
 */
public class Zone implements Loadable {
    private Vector2 position;
    private int frame, fsAdjCheck=0, layer;
    private static Random r;
    public boolean loaded = false;

    private ArrayList<Zone> adjZones = new ArrayList<Zone>();
    private ArrayList objects = new ArrayList();
    private ArrayList<Box> boxes = new ArrayList<Box>();
    private ArrayList<Room> rooms = new ArrayList<Room>();

    private ArrayList<ArrayList<Drawable>> drawablesList = new ArrayList<ArrayList<Drawable>>();

    public int numRooms = 2; // number of rooms that are supposed to exist in the zone
    public int roomGenFailureCount = 0; // number of rooms that failed to generate

    public Zone(float x, float y) {
        r = GameView.gv.random;
        position = new Vector2(x, y);
        for (int i=0;i<=C.DRAW_LAYERS;i++) {
            drawablesList.add(new ArrayList<Drawable>());
        }
        numRooms = r.nextInt(numRooms);
    }

    public void generate() {
        MapGen.fillZone(this);
    }

    public void draw(int frame, int limit, int layer) {
        if (this.frame == frame && this.layer == layer)
            return;
        this.frame = frame;
        this.layer = layer;

        for (Drawable d: drawablesList.get(layer)) {
            d.draw(GameView.gv.spriteBatch, GameView.gv.shapeRenderer, GameView.gv.modelBatch);
        }

        if (limit > 0)
            for (Zone z: adjZones) {
                z.draw(frame, limit - 1, layer);
            }
    }

    public void update(int frame, int limit) {
        if (this.frame == frame)
            return;
        this.frame = frame;

        MapGen.update(this); // generate the map

        for (Object o: objects)
            if (o instanceof Updateable)
                ((Updateable) o).update();

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
        fsAdjCheck = 0;
    }

    @Override
    public void load() {
        for (Object o: objects)
            if (o instanceof Loadable)
                ((Loadable) o).load();
        loaded = true;
    }

    @Override
    public void unload() {
        for (Object o: objects)
            if (o instanceof Loadable)
                ((Loadable) o).unload();
        for (Zone z: adjZones)
            if (z.loaded)
                z.unload();
        loaded = false;
    }

    public static Zone getZone(float x, float y) {
        int indX = (int)Math.floor(x / C.ZONE_SIZE);
        int indY = (int)Math.floor(y / C.ZONE_SIZE);

        Zone z = GameView.gv.zones.get("row"+indY+"column"+indX);
        if (z == null) {
            z = new Zone(indX * C.ZONE_SIZE, indY * C.ZONE_SIZE);
            GameView.gv.zones.put("row"+indY+"column"+indX, z);
        }
        return z;
    }
    public static Zone getZone(Vector2 v) {
        return getZone(v.x, v.y);
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

        objects.add(o);

        // KEEP RECORDS
        if (o instanceof Room)
            addRoom((Room) o);
        if (o instanceof Box)
            addBox((Box) o);
    }
    public boolean removeObject(HasZone o) {
        o.setZone(null);

        // KEEP RECORDS
        if (o instanceof Room)
            removeRoom((Room) o);
        if (o instanceof Box)
            removeBox((Box) o);

        drawablesList.remove(o);
        return objects.remove(o);
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

    public void addDrawable(Drawable d, int layer) {
        ArrayList<Drawable> drawables = drawablesList.get(layer);
        if (drawables.indexOf(d) == -1)
            drawables.add(d);
    }
    public void addDrawableNoCheck(Drawable d, int layer) {
        ArrayList<Drawable> drawables = drawablesList.get(layer);
        drawables.add(d);
    }

    public Overlappable checkOverlap(float x, float y, float w, float h, int limit, LinkedList<Overlappable> ignore) {
        for (Object oo: objects) {
            if (!(oo instanceof Overlappable))
                continue;
            Overlappable o = (Overlappable) oo;

            if (o.overlaps(x, y, w, h)) {
                if (ignore != null) {
                    boolean shouldIgnore = false;
                    for (Overlappable ig : ignore) {
                        if (ig == o) {
                            shouldIgnore = true;
                            break;
                        }
                    }
                    if (!shouldIgnore)
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
    public Overlappable checkOverlap(Vector2 v, float w, float h, int limit, LinkedList<Overlappable> ignore) {
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
