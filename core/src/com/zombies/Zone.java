package com.zombies;

import com.HUD.DebugText;
import com.badlogic.gdx.math.Vector2;
import com.interfaces.Drawable;
import com.map.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by coda on 2/27/2016.
 */
public class Zone {
    private Vector2 position;
    private int frame, fsAdjCheck=0;
    private static Random r = new Random();
    private ArrayList<Zone> adjZones = new ArrayList<Zone>();
    private ArrayList<Survivor> survivors = new ArrayList<Survivor>();
    private ArrayList<Zombie> zombies = new ArrayList<Zombie>();
    private ArrayList<Box> boxes = new ArrayList<Box>();
    private ArrayList<Room> rooms = new ArrayList<Room>();
    private ArrayList<Drawable> renderables = new ArrayList<Drawable>();

    public int numRooms = 20; // number of rooms that are supposed to exist in the zone
    public int roomGenFailureCount = 0; // number of rooms that failed to generate

    public Zone(float x, float y) {
        position = new Vector2(x, y);
    }

    public void generate() {
        MapGen.fillZone(this);
    }

    public void update(int frame, int limit) {
        if (this.frame == frame)
            return;
        this.frame = frame;

        MapGen.update(this); // generate the map

        if (adjZones.size() < 8) {
            fsAdjCheck++;
            if (fsAdjCheck > 20)
                findAdjZones();
        }

        for (Box b: boxes) {
            b.drawBox(GameView.gv.spriteBatch, GameView.gv.shapeRenderer);
        }
        for (Drawable r: renderables) {
            r.draw(GameView.gv.spriteBatch, GameView.gv.shapeRenderer);
        }

        DebugText.addMessage("rooms", "Rooms in zone: " + rooms.size());

        if (limit > 0)
            for (Zone z: adjZones) {
                z.update(frame, limit - 1);
            }
    }

    public void findAdjZones() {
        // java is the absolute freaking worst...
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

    public void load() {
        for (Zombie z: (ArrayList<Zombie>)zombies.clone()) {
            z.load();
        }
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

    public void addUnit(Unit u) {
        if (u.zone != null)
            u.zone.removeUnit(u);
        u.zone = this;

        if (u instanceof Zombie) {
            Zombie z = (Zombie)u;
            if (zombies.indexOf(z) == -1)
                zombies.add(z);
            return;
        } else if (u instanceof Survivor) {
            Survivor s = (Survivor)u;
            if (survivors.indexOf(s) == -1)
                survivors.add(s);
            return;
        }
        throw new Error("Addition of class " + u.getClass() + " to zone is not supported.");
    }
    public boolean removeUnit(Unit u) {
        if (u instanceof Zombie)
            return zombies.remove((Zombie)u);
        else if (u instanceof Survivor)
            return survivors.remove((Survivor)u);
        throw new Error("Removal of class " + u.getClass() + " from zone is not supported.");
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
    public void addRoom(Room r) {
        if (rooms.indexOf(r) == -1)
            rooms.add(r);
        for (Box b : r.getBoxes()) {
            if (boxes.indexOf(b) == -1)
                boxes.add(b);
        }
    }
    public void addRenderable(Drawable r) {
        if (renderables.indexOf(r) == -1)
            renderables.add(r);
    }
    public Vector2 randomPosition() { return position.cpy().add(r.nextFloat() * C.ZONE_SIZE, r.nextFloat() * C.ZONE_SIZE); }
    public Box randomBox() {
        if (boxes.size() > 0)
            return boxes.get(r.nextInt(boxes.size()));
        else
            return null;
    }
}
