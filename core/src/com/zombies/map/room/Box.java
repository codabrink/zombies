package com.zombies.map.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Crate;
import com.zombies.GameView;
import com.zombies.Survivor;
import com.zombies.Unit;
import com.zombies.Zombie;
import com.zombies.Zone;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.map.MapGen;
import com.zombies.map.data.join.JoinOverlappableOverlappable;
import com.zombies.powerups.Powerup;

public class Box extends Overlappable {
    private ArrayList<Unit> zombies = new ArrayList<>();
    private ArrayList<Unit> survivors = new ArrayList<Unit>();
    private ArrayList<Crate> crates = new ArrayList<Crate>();
    private ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    private HashMap<Integer, Box> adjBoxes = new HashMap<Integer, Box>();
    private Room room;
    private GameView view;
    private Random random = new Random();

    public HashSet<JoinOverlappableOverlappable> joinOverlappableOverlappables = new HashSet<>();

    public String BMKey;

    public Box(Vector2 p) {
        this(p.x, p.y);
    }
    public Box(float x, float y) {
        height = C.BOX_DIAMETER;
        width  = C.BOX_DIAMETER;
        position = new Vector2(x, y);
        setCorners();
        view = GameView.gv;
    }

    private void setCorners() {
        corners[0] = new Vector2(position.x + width, position.y + height);
        corners[1] = new Vector2(position.x, position.y + height);
        corners[2] = new Vector2(position.x, position.y);
        corners[3] = new Vector2(position.x + width, position.y);
    }

    // detect where this box should have walls, but don't create them yet.
    public ArrayList<Vector2[]> proposeWallPositions() {
        ArrayList<Vector2[]> proposedPositions = new ArrayList<>();

        Vector2[] points;
        if (adjBoxes.get(90) == null) {
            points = new Vector2[2];
            points[0] = new Vector2(position.cpy().add(0, height));
            points[1] = new Vector2(position.cpy().add(width, height));
            proposedPositions.add(points); // top wall
        }
        if (adjBoxes.get(0) == null) {
            points = new Vector2[2];
            points[0] = new Vector2(position.cpy().add(width, 0));
            points[1] = new Vector2(position.cpy().add(width, height));
            proposedPositions.add(points); // right wall
        }
        if (adjBoxes.get(270) == null) {
            points = new Vector2[2];
            points[0] = new Vector2(position.cpy());
            points[1] = new Vector2(position.cpy().add(width, 0));
            proposedPositions.add(points); // bottom wall
        }
        if (adjBoxes.get(180) == null) {
            points = new Vector2[2];
            points[0] = new Vector2(position.cpy());
            points[1] = new Vector2(position.cpy().add(0, height));
            proposedPositions.add(points); // left wall
        }

        return proposedPositions;
    }

    public float x() {return position.x;}
    public float y() {return position.y;}

    public ArrayList<Powerup> getPowerups() {
        return powerups;
    }

    public ArrayList<Integer> getOpenDirections() {
        ArrayList<Integer> openDirections = new ArrayList<Integer>();
        for (int i: MapGen.DIRECTIONS) {
            if (adjBoxes.get(i) == null) {
                openDirections.add(openDirections.size(), i);
            }
        }
        return openDirections;
    }
    public int getRandomOpenDirection() {
        ArrayList<Integer> openDirections = getOpenDirections();
        if (openDirections.size() > 0)
            return openDirections.get(random.nextInt(openDirections.size()));
        else
            return ' ';
    }

    public Survivor addSurvivor() {
        Survivor s = new Survivor(this.randomPoint());
        survivors.add(s);
        return s;
    }

    public boolean removeUnit(Unit u) {
        if (u instanceof Zombie)
            return zombies.remove((Zombie)u);
        else if (u instanceof Survivor)
            return survivors.remove((Survivor)u);
        throw new Error("Removal of class " + u.getClass() + " from box is not supported.");
    }

    public void addZombie() {
        if (C.POPULATE_ZOMBIES) {
            zombies.add(new Zombie(view, this, this.randomPoint()));
        }
    }

    public void addZombie(Unit u) {
        zombies.add(u);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getPosition(int i) {
        switch (i) {
        case 1:
            return position;
        case 2:
            return position.cpy().add(C.BOX_DIAMETER, 0);
        case 3:
            return position.cpy().add(0, C.BOX_DIAMETER);
        case 4:
            return position.cpy().add(C.BOX_DIAMETER, C.BOX_DIAMETER);
        }
        return new Vector2();
    }

    public Room getRoom() {
        return room;
    }

    public ArrayList<Unit> getSurvivorList() {
        return survivors;
    }

    public ArrayList<Unit> getUnits() {
        return zombies;
    }

    public Vector2 randomPoint() {
        return position.cpy().add(random.nextFloat() * C.BOX_DIAMETER, random.nextFloat() * C.BOX_DIAMETER);
    }

    public Unit randomZombie() {
        if (zombies.isEmpty() || zombies.size() == 1) {
            return null;
        }
        Unit u = zombies.get(random.nextInt(zombies.size()));
        return u;
    }

    public Box setRoom(Room room) {
        this.room = room;
        this.zone = room.getZone();
        return this;
    }

    public void setAdjBox(int direction, Box box) { adjBoxes.put(direction, box); }
    public Box getAdjBox(int direction) {
        return adjBoxes.get(direction);
    }
    public HashMap<Integer, Box> getAdjBoxes() { return adjBoxes; }
    public boolean isAdjacent(Box b) {
        for (Box bb: adjBoxes.values()) {
            if (b == bb)
                return true;
        }
        return false;
    }

    public void buildFloorMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        Vector2 relp = new Vector2(position.x - modelCenter.x, position.y - modelCenter.y);
        builder.rect(relp.x, relp.y, 0,
                relp.x + width, relp.y, 0,
                relp.x + width, relp.y + height, 0,
                relp.x, relp.y + height, 0,
                1, 1, 1);
    }

    // Box Map Location - used during room generation in MapGen.java
    public int[] getBMLocation() {
        String[] stringLocations = BMKey.split(",");
        int[] locations = {Integer.parseInt(stringLocations[0]), Integer.parseInt(stringLocations[1])};
        return locations;
    }

    @Override
    public void setZone(Zone z) {
        // Zone is set in setRoom
    }

    @Override
    public String className() {
        return "Box";
    }


    @Override
    public void load() {

    }
    @Override
    public void unload() {

    }
}
