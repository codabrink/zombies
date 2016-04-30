package com.zombies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.interfaces.HasZone;
import com.interfaces.Loadable;

public class Room implements Loadable, HasZone {
    private int size;
    private ArrayList<Box> boxes = new ArrayList<Box>();
    private ArrayList<Room> adjRooms = new ArrayList<Room>();
    private Random random = new Random();
    private boolean alarmed = false;
    private float alpha = 0;
    private GameView view;
    private boolean loaded = false;
    private int frame;
    private Zone zone;
    private ArrayList<Box> outerBoxes = new ArrayList<Box>();

    public Room(Collection<Box> boxes) {
        view = GameView.gv;
        this.boxes = new ArrayList<Box>(boxes);
        Zone.getZone(calculateMedian()).addObject(this);

        for (Box b: boxes) {
            Zone.getZone(b.getPosition()).addDrawableNoCheck(b, 0);
            if (b.getAdjBoxes().size() < 4)
                outerBoxes.add(b);
        }
    }

    // calculates the median position of all of the boxes
    private Vector2 calculateMedian() {
        Vector2 center = new Vector2(0, 0);
        for (Box b: boxes) {
            center.add(b.getCenter());
        }
        return new Vector2(center.x / boxes.size(), center.y / boxes.size());
    }

    public void doorsTo(Room room) {
        if (!adjRooms.contains(room)) {
            //TODO further path finding to that room
            return;
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

    public ArrayList<Wall> getWalls() {
        ArrayList<Wall> roomWalls = new ArrayList<Wall>();

        for (Box box: boxes) {
            for (Wall wall: box.getWalls()) {
                roomWalls.add(wall);
            }
        }

        return roomWalls;
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

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        zone = z;
    }
}
