package com.zombies.map.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Survivor;
import com.zombies.Unit;
import com.zombies.Zombie;
import com.zombies.Zone;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.interfaces.Gridable;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.util.U;

public class Box extends Overlappable implements Gridable {
    public static int numBoxes = 0;

    private HashSet<Unit> zombies = new HashSet<>();
    private HashSet<Unit> survivors = new HashSet<Unit>();
    public HashSet<DoorContainer> doors = new HashSet<>();
    private HashMap<String, Gridable> gridMap;
    private Random random = new Random();
    private Zone zone;

    private Building building;
    private Room room;
    private ModelMeCallback modelFloorCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildFloorMesh(builder, center);
        }
    };

    private Wall[] walls = new Wall[4];

    private int id;
    private int[] key;
    private String sKey;

    public static Box createBox(Room room, int[] key) {
        Overlappable o = room.getBuilding().checkOverlap(key);
        if (room.getBuilding().checkOverlap(key) != null)
            return null;
        return new Box(room, key);
    }

    protected Box(Room room, int[] key) {
        id = numBoxes;
        numBoxes++;

        this.building = room.getBuilding();
        this.room     = room;

        this.key      = key;
        this.sKey     = key[0]+","+key[1];

        gridMap = building.gridMap;

        building.putBoxMap(this.key, this);
        room.boxes.add(this);

        position = building.positionOf(key);
        height   = C.GRIDSIZE;
        width    = C.GRIDSIZE;

        setCorners(building.cornersOf(position));

        zone = Zone.getZone(getCenter());
        zone.addPendingObject(this);
        zone.addPendingObject(room);
        zone.addModelingCallback(room.roomType.floorMaterial, modelFloorCallback);
    }

    public void compile() {
        buildWalls();
        for (Wall wall : walls)
            if (wall != null)
                wall.compile();
    }

    public void buildWalls() {
        Gridable n = gridMap.get(key[0] + "," + (key[1] + 1));
        Gridable s = gridMap.get(key[0] + "," + (key[1] - 1));
        Gridable e = gridMap.get(key[0] + 1 + "," + key[1]);
        Gridable w = gridMap.get(key[0] - 1 + "," + key[1]);

        if (!(e instanceof Box) || ((Box)e).getRoom() != room)
            walls[0] = new WallWall(position.cpy().add(width, height), position.cpy().add(width, 0), room.roomType.wallMaterial);
        if (!(n instanceof Box) || ((Box)n).getRoom() != room)
            walls[1] = new WallWall(position.cpy().add(0, height), position.cpy().add(width, height), room.roomType.wallMaterial);
        if (!(w instanceof Box) || ((Box)w).getRoom() != room)
            walls[2] = new WallWall(position.cpy(), position.cpy().add(0, height), room.roomType.wallMaterial);
        if (!(s instanceof Box) || ((Box)s).getRoom() != room)
            walls[3] = new WallWall(position.cpy().add(width, 0), position.cpy(), room.roomType.wallMaterial);
    }

    public float x() {return position.x;}
    public float y() {return position.y;}

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
            return position.cpy().add(C.GRIDSIZE, 0);
        case 3:
            return position.cpy().add(0, C.GRIDSIZE);
        case 4:
            return position.cpy().add(C.GRIDSIZE, C.GRIDSIZE);
        }
        return new Vector2();
    }

    public HashSet<Unit> getUnits() {
        return zombies;
    }

    public Vector2 randomPoint() {
        return position.cpy().add(random.nextFloat() * C.GRIDSIZE, random.nextFloat() * C.GRIDSIZE);
    }

    public Unit randomZombie() {
        return (Unit)U.random(zombies);
    }

    public int getId() { return id; }
    public Building getBuilding() { return building; }
    public Room getRoom() { return room; }
    public int[] getKey() { return key; }
    public String getSKey() { return sKey; }
    public HashSet<Box> getAdjBoxes() {
        HashSet<Box> adjBoxes = new HashSet<>();
        Gridable g;
        for (int[] k : Building.getAdjBMKeys(key)) {
            g = building.gridMapGet(k);
            if (g instanceof Box)
                adjBoxes.add((Box)g);
        }
        return adjBoxes;
    }
    public ArrayList<int[]> getOpenAdjKeys() {
        ArrayList<int[]> adjKeys = new ArrayList<>();
        for (int[] k : Building.getAdjBMKeys(key)) {
            if (gridMap.get(k[0] + "," + k[1]) == null)
                adjKeys.add(k);
        }
        return adjKeys;
    }

    public void buildFloorMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        Vector2 relp = new Vector2(position.x - modelCenter.x, position.y - modelCenter.y);
        builder.rect(relp.x, relp.y, 0,
                relp.x + width, relp.y, 0,
                relp.x + width, relp.y + height, 0,
                relp.x, relp.y + height, 0,
                1, 1, 1);
    }

    public void dispose() {
        zone.removeModelingCallback(room.roomType.floorMaterial, modelFloorCallback);
        zone.removeObject(this);
    }

    public String giveKey(Box b) {
        return Math.min(id, b.getId()) + "," + Math.max(id, b.getId());
    }
}
