package com.zombies;

import com.HUD.DebugText;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by coda on 2/27/2016.
 */
public class Zone {
    private int x, y, frame, fsAdjCheck=0;
    private ArrayList<Zone> adjZones = new ArrayList<Zone>();
    private ArrayList<Survivor> survivors = new ArrayList<Survivor>();
    private ArrayList<Zombie> zombies = new ArrayList<Zombie>();
    private ArrayList<Box> boxes = new ArrayList<Box>();
    private ArrayList<Room> rooms = new ArrayList<Room>();

    public Zone(int x, int y) {
        this.x = x;this.y = y;
    }

    public void update(int frame, int limit) {
        if (this.frame == frame)
            return;
        this.frame = frame;

        if (adjZones.size() < 8) {
            fsAdjCheck++;
            if (fsAdjCheck > 20)
                checkNearbyZones();
        }

        DebugText.addMessage("rooms", "Rooms in zone: "+rooms.size());

        if (limit > 0)
            for (Zone z: adjZones) {
                z.update(frame, limit - 1);
            }
    }

    private void checkNearbyZones() {
        for (int i = y-1; i <= y+1; i++) {
            for (int j = x-1; j <= x+1; j++) {
                Zone z = Zone.getZone(j, i);
                if (z != this && adjZones.indexOf(z) != -1) {
                    adjZones.add(z);
                }
            }
        }
        fsAdjCheck = 0;
    }

    public void load() {
        for (Zombie z: (ArrayList<Zombie>)zombies.clone()) {
            z.load();
        }
    }

    public void addBox(Box b) {
        if (boxes.indexOf(b) == -1)
            boxes.add(b);
    }
    public void addRoom(Room r) {
        if (rooms.indexOf(r) == -1)
            rooms.add(r);
    }

    public static Zone getZone(float x, float y) {
        int indX = (int)(x / C.ZONE_SIZE);
        int indY = (int)(y / C.ZONE_SIZE);

        // get x row
        ArrayList<Zone> rowX;
        try {
            rowX = GameView.zones.get(indX);
        } catch (IndexOutOfBoundsException e) {
            rowX = new ArrayList<Zone>();
            GameView.zones.add(indX, rowX);
        }

        // get zone
        try {
            return rowX.get(indY);
        } catch (IndexOutOfBoundsException e) {
            Zone z = new Zone(indX, indY);
            rowX.add(indY, z);
            return z;
        }
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
}
