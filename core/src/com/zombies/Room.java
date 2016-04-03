package com.zombies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;

public class Room {
    private int size;
    private ArrayList<Box> boxes = new ArrayList<Box>();
    private ArrayList<Room> adjRooms = new ArrayList<Room>();
    private Random random = new Random();
    private boolean alarmed = false;
    private float alpha = 0;
    private GameView view;
    private boolean loaded = false;
    private int frame;
    private ArrayList<Zone> zones = new ArrayList<Zone>();
    private ArrayList<Box> outerBoxes = new ArrayList<Box>();

    public Room(Collection<Box> boxes) {
        view = GameView.gv;
        this.boxes = new ArrayList<Box>(boxes);
        for (Box b: boxes) {
            b.setRoom(this);
            Zone.getZone(b.getPosition()).addDrawable(b, 0);
            if (b.getAdjBoxes().size() < 4)
                outerBoxes.add(b);
        }
    }

    public void doorsTo(Room room) {
        if (!adjRooms.contains(room)) {
            //TODO further path finding to that room
            return;
        }
    }

    public Zone addZone(Zone z) {
        if (zones.indexOf(z) == -1)
            zones.add(z);
        z.addRoom(this);
        return z;
    }
    public void registerOverlappable() {
        for (Zone z: zones) {
            for (Box b: boxes) {
                z.addOverlappable(b);
            }
        }
    }

    public void currentRoom() {
        load(); // load self
    }

    public void load() {
        for (Box b : boxes) {
            b.load();
        }
        loaded = true;
    }

    public void unload() {
        for (Box b: boxes) {
            b.unload();
        }
        loaded = false;
    }

    public void alarm(Unit victim) {
        if (!alarmed) {
            for (Box b: boxes) {
                for (Unit u: b.getUnits()) {
                    if (random.nextBoolean()) {
                        u.sick(victim);
                    }
                }
            }
            alarmed = true;
        }
    }

    public void genOuterWalls() {
        for (Box b: boxes) {
            b.genOuterWalls();
        }
    }

    public Unit findUnit(Body b) {
        for (Box box: boxes) {
            for (Unit u: box.getUnits()) {
                if (u.getBody() == b)
                    return u;
            }
        }
        return null;
    }

    public Wall findWall(Body b) {
        for (Box box: boxes) {
            for (Wall w: box.getWalls()) {
                if (w != null && w.getBody().getPosition().x == b.getPosition().x && w.getBody().getPosition().y == b.getPosition().y) {
                    return w;
                }
            }
        }
        return null;
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    public boolean isAlarmed() {
        return alarmed;
    }

    public boolean isEmpty() {
        LinkedList<Unit> zList = new LinkedList<Unit>();
        for (Box b: boxes) {
            for (Unit u: b.getUnits()) {
                zList.add(u);
            }
        }
        if (zList.size() > 1) {
            return false;
        }
        return true;
    }

    public Box getRandomBox() {
        if (!boxes.isEmpty()) {
            return boxes.get(random.nextInt(boxes.size()));
        }
        return null;
    }

    public void update(int frame, int distance) {
        if (!loaded || this.frame == frame)
            return;
        this.frame = frame;

        if (distance > 0) {
            for (Room r : adjRooms) {
                r.update(frame, distance - 1);
            }
        }

        for (Box b: boxes) b.update(frame);
    }

    public LinkedList<Unit> getAliveUnits() {
        LinkedList<Unit> units = new LinkedList<Unit>();
        for (Box b: boxes) {
            units.addAll((Collection)b.getUnits());
        }
        return units;
    }

    public ArrayList<Box> getOuterBoxes() {
        return outerBoxes;
    }
}
