package com.zombies.map.building;

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
import com.zombies.overlappable.Overlappable;
import com.zombies.overlappable.PolygonOverlappable;
import com.zombies.interfaces.*;
import com.zombies.map.building.door.DoorContainer;
import com.zombies.map.building.door.DoorWall;
import com.zombies.map.building.room.Room;
import com.zombies.map.building.window.WindowContainer;
import com.zombies.map.building.window.WindowWall;
import com.zombies.lib.Assets.MATERIAL;

public class Box extends BuildingGridable {
    public static int numBoxes = 0;

    private HashSet<Unit> zombies = new HashSet<>();
    private HashSet<Unit> survivors = new HashSet<Unit>();

    private HashMap<String, BuildingGridable> gridMap;
    private Random random = new Random();
    private Zone zone;

    private Room room;
    private ModelMeCallback modelFloorCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildFloorMesh(builder, center);
        }
    };

    private Wall[]           walls        = new Wall[4];
    public DoorContainer[]   doors        = new DoorContainer[4];
    public WindowContainer[] windows      = new WindowContainer[4];
    private Vector2[]        outerCorners = new Vector2[4];
    private Wall[]           outerWalls   = new Wall[4];

    private int id;

    public static Box createBox(Room room, int[] key) {
        if (room.getBuilding().checkOverlap(key) != null)
            return null;
        return new Box(room, key);
    }

    protected Box(Room room, int[] key) {
        super(room.getBuilding(), key);
        id = numBoxes;
        numBoxes++;

        this.room     = room;

        gridMap = building.gridMap;

        room.boxes.add(this);

        setCorners(building.cornersOf(position));

        zone = Zone.getZone(getCenter());
        zone.addPendingObject(this);
        zone.addPendingObject(room);
        zone.addModelingCallback(room.type.floorMaterial, modelFloorCallback);
    }

    public void compile() {
        buildWalls();
        for (com.zombies.map.building.Wall wall : walls)
            if (wall != null)
                wall.compile();
        for (com.zombies.map.building.Wall wall : outerWalls)
            if (wall != null)
                wall.compile();
    }

    private void buildWalls() {
        BuildingGridable n = gridMap.get(key[0] + "," + (key[1] + 1));
        BuildingGridable s = gridMap.get(key[0] + "," + (key[1] - 1));
        BuildingGridable e = gridMap.get(key[0] + 1 + "," + key[1]);
        BuildingGridable w = gridMap.get(key[0] - 1 + "," + key[1]);

        processWall(e, 0, 0, 3);
        processWall(n, 1, 1, 0);
        processWall(w, 2, 2, 1);
        processWall(s, 3, 3, 2);
    }

    private void processWall(BuildingGridable g, int i, int a, int b) {
        if (g == null) {
            if (building.outsideDoorCount == 0 || random.nextFloat() < 0.1f) {
                createDoor(i, a, b, building.type.outerWallMaterial, room.type.wallMaterial);
                building.outsideDoorCount++;
                return;
            } else if (random.nextFloat() < 0.2f) {
                createWindow(i, a, b, building.type.outerWallMaterial, room.type.wallMaterial);
                return;
            }
            createWall(i, a, b, building.type.outerWallMaterial, room.type.wallMaterial);
            return;
        }

        if (g instanceof Box && ((Box) g).getRoom() != room) {
            Box box = (Box) g;
            Room room = box.getRoom();
            if (this.room != room && (!(this.room.connections.contains(room)) || random.nextFloat() < 0.05f)) {
                createDoor(g, a, b, room.type.wallMaterial, this.room.type.wallMaterial);

                room.connections.add(this.room);
                this.room.connections.add(room);

                this.room.connected = this.room.connected && room.connected;
                room.connected      = this.room.connected;

                return;
            }

            createWall(g, a, b, room.type.wallMaterial, this.room.type.wallMaterial);
            return;
        }
    }


    private void createDoor(int i, int a, int b, MATERIAL lm, MATERIAL rm) {
        building.putWall(this, i, new DoorWall(corners[a], corners[b], building, lm, rm));
    }
    private void createDoor(BuildingGridable g, int a, int b, MATERIAL lm, MATERIAL rm) {
        building.putWall(this, g, new DoorWall(corners[a], corners[b], building, lm, rm));
    }
    private void createWindow(int i, int a, int b, MATERIAL lm, MATERIAL rm) {
        building.putWall(this, i, new WindowWall(corners[a], corners[b], building, lm, rm));
    }
    private void createWindow(BuildingGridable g, int a, int b, MATERIAL lm, MATERIAL rm) {
        building.putWall(this, g, new WindowWall(corners[a], corners[b], building, lm, rm));
    }
    private void createWall(int i, int a, int b, MATERIAL lm, MATERIAL rm) {
        building.putWall(this, i, new WallWall(corners[a], corners[b], lm, rm));
    }
    private void createWall(BuildingGridable g, int a, int b, MATERIAL lm, MATERIAL rm) {
        building.putWall(this, g, new WallWall(corners[a], corners[b], lm, rm));
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

    public int getId() { return id; }
    public Building getBuilding() { return building; }
    public Room getRoom() { return room; }
    public int[] getKey() { return key; }
    public String getSKey() { return sKey; }
    public int directionOf(Box b) {
        return Building.bmKeyToDirection(key, b.key);
    }
    public HashSet<Box> getAdjBoxes() {
        HashSet<Box> adjBoxes = new HashSet<>();
        BuildingGridable g;
        for (int[] k : Building.getAdjBMKeys(key)) {
            g = building.gridMapGet(k);
            if (g instanceof Box)
                adjBoxes.add((Box)g);
        }
        return adjBoxes;
    }

    public void buildFloorMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        Vector2 relp = new Vector2(position.x - modelCenter.x, position.y - modelCenter.y);
        builder.rect(relp.x, relp.y, 0,
                relp.x + C.GRIDSIZE, relp.y, 0,
                relp.x + C.GRIDSIZE, relp.y + C.GRIDSIZE, 0,
                relp.x, relp.y + C.GRIDSIZE, 0,
                1, 1, 1);
    }

    public void dispose() {
        zone.removeModelingCallback(room.type.floorMaterial, modelFloorCallback);
        zone.removeObject(this);
    }

    public String giveKey(Box b) {
        return Math.min(id, b.getId()) + "," + Math.max(id, b.getId());
    }
}
