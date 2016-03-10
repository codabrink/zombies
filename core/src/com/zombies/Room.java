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

    public Room(ArrayList<Box> boxes) {
        view = GameView.gv;
        this.boxes = boxes;
        for (Box b: boxes) {
            b.setRoom(this);
        }
        this.removeWalls();

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

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, int frame, int distance) {
        if (this.frame == frame)
            return;
        this.frame = frame;
        for (Box b : boxes) {
            b.drawFloor(spriteBatch, shapeRenderer);
        }
        for (Box b: boxes) {
            b.drawBox(spriteBatch, shapeRenderer);
        }

        if (distance > 0) {
            for (Room r : adjRooms) {
                r.draw(spriteBatch, shapeRenderer, frame, distance - 1);
            }
        }
    }

    public void drawWalls(int frame) {
        for (Box b: boxes) {
            b.drawWalls();
            for (Unit z: b.getUnits()) {
                z.update(frame);
            }
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

    public void flood(int number) {
        long i = 0l;
        do {
            for (Box b: boxes) {
                for (Wall w: b.getWalls()) {
                    if (w != null && w.isDoor()) {
                        int l = random.nextInt(7) + 1;
                        for (int k=1; k<=l; k++) {
                            view.addPostZombie(new PostponedZombie(view, b, w.doorPosition(), w.getVector(), view.getPlayer(), i));
                        }
                        number -= l;
                        i += 100l;
                        if (number <= 0) {
                            return;
                        }
                    }
                }
            }
        } while (number > 0);
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

    public Box randomBox() {
        if (!boxes.isEmpty()) {
            return boxes.get(random.nextInt(boxes.size()));
        }
        return null;
    }

    public void removeWalls() {
        //remove walls
        for (Box b: boxes) {
            for (Box bb: boxes) {
                if (b != bb)
                    b.removePotentialWall(bb);
            }
        }
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
        draw(view.getSpriteBatch(), view.getShapeRenderer(), frame, distance);
    }

    public LinkedList<Unit> getAliveUnits() {
        LinkedList<Unit> units = new LinkedList<Unit>();
        for (Box b: boxes) {
            units.addAll((Collection)b.getUnits());
        }
        return units;
    }

}
