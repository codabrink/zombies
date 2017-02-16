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
import com.zombies.powerups.Powerup;

public class Box extends Overlappable {
    private ArrayList<Unit> zombies = new ArrayList<>();
    private ArrayList<Unit> survivors = new ArrayList<Unit>();
    private ArrayList<Crate> crates = new ArrayList<Crate>();
    private ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    private HashMap<Integer, Box> adjBoxes = new HashMap<Integer, Box>();
    private HashMap<String, Box> boxMap;
    private Random random = new Random();

    private Building building;
    private Room room;

    private int[] key;
    private String sKey;

    public Box(Building building, Room room, int[] bmKey) {
        this.building = building;
        this.room     = room;

        this.key      = bmKey;
        this.sKey     = bmKey[0]+","+bmKey[1];

        boxMap = building.boxMap;

        building.putBoxMap(key, this);
        room.boxes.add(this);

        position = building.positionOf(bmKey);
        height   = C.BOX_DIAMETER;
        width    = C.BOX_DIAMETER;

        setCorners();
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

        Box n = boxMap.get(key[0] + "," + (key[1] + 1));
        Box s = boxMap.get(key[0] + "," + (key[1] - 1));
        Box e = boxMap.get(key[0] + 1 + "," + key[1]);
        Box w = boxMap.get(key[0] - 1 + "," + key[1]);

        Vector2[] points;
        if (n == null || n.getRoom() != room) {
            points = new Vector2[2];
            points[0] = new Vector2(position.cpy().add(0, height));
            points[1] = new Vector2(position.cpy().add(width, height));
            proposedPositions.add(points); // top wall
        }
        if (e == null || e.getRoom() != room) {
            points = new Vector2[2];
            points[0] = new Vector2(position.cpy().add(width, 0));
            points[1] = new Vector2(position.cpy().add(width, height));
            proposedPositions.add(points); // right wall
        }
        if (s == null || s.getRoom() != room) {
            points = new Vector2[2];
            points[0] = new Vector2(position.cpy());
            points[1] = new Vector2(position.cpy().add(width, 0));
            proposedPositions.add(points); // bottom wall
        }
        if (w == null || w.getRoom() != room) {
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

    public int[][] getOpenAdjBMAKeys() {
        ArrayList<int[]> openAdjBMAKeys = new ArrayList<>();
        int[][] adjBMAKeys = Building.getAdjBMKeys(key);
        for (int[] aKey : adjBMAKeys)
            if (building.boxMap.get(aKey[0]+","+aKey[1]) == null)
                openAdjBMAKeys.add(aKey);
        return openAdjBMAKeys.toArray(new int[openAdjBMAKeys.size()][]);
    }

    public ArrayList<Integer> getOpenDirections() {
        ArrayList<Integer> openDirections = new ArrayList<Integer>();
        for (int i: C.DIRECTIONS) {
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
            zombies.add(new Zombie(GameView.gv, this, this.randomPoint()));
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
    public Building getBuilding() { return building; }
    public Room getRoom() { return room; }
    public int[] getKey() { return key; }

    public void buildFloorMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        Vector2 relp = new Vector2(position.x - modelCenter.x, position.y - modelCenter.y);
        builder.rect(relp.x, relp.y, 0,
                relp.x + width, relp.y, 0,
                relp.x + width, relp.y + height, 0,
                relp.x, relp.y + height, 0,
                1, 1, 1);
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
