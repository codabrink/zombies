package com.zombies.map.building;

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
import com.zombies.map.building.room.Room;
import com.zombies.util.Assets.MATERIAL;

public class Box extends Overlappable implements Gridable {
    public static int numBoxes = 0;

    private HashSet<Unit> zombies = new HashSet<>();
    private HashSet<Unit> survivors = new HashSet<Unit>();
    public HashSet<DoorContainer> doors = new HashSet<>();
    private HashMap<String, Gridable> gridMap;
    private Random random = new Random();
    private Zone zone;

    private com.zombies.map.building.Building building;
    private Room room;
    private ModelMeCallback modelFloorCallback = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildFloorMesh(builder, center);
        }
    };

    private com.zombies.map.building.Wall[] walls = new com.zombies.map.building.Wall[4];
    private Vector2[] outerCorners = new Vector2[4];
    private com.zombies.map.building.Wall[] outerWalls = new com.zombies.map.building.Wall[4];

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
        zone.addModelingCallback(room.type.floorMaterial, modelFloorCallback);
    }

    @Override
    protected void setCorners(Vector2[] corners) {
        super.setCorners(corners);
        float thickness = 0.1f;
        outerCorners = new Vector2[]{
                position.cpy().add(width + thickness, height + thickness),
                position.cpy().add(0, height + thickness),
                position.cpy().add(0, -thickness),
                position.cpy().add(width + thickness, 0)
        };
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
        Gridable n = gridMap.get(key[0] + "," + (key[1] + 1));
        Gridable s = gridMap.get(key[0] + "," + (key[1] - 1));
        Gridable e = gridMap.get(key[0] + 1 + "," + key[1]);
        Gridable w = gridMap.get(key[0] - 1 + "," + key[1]);

        processWall(e, 0, 0, 3);
        processWall(n, 1, 1, 0);
        processWall(w, 2, 2, 1);
        processWall(s, 3, 3, 2);
    }

    private void processWall(Gridable g, int i, int a, int b) {
        if (g == null) {
            if (building.outsideDoorCount == 0 || random.nextFloat() < 0.1f) {
                createDoor(i, a, b, building.type.outerWallMaterial, room.type.wallMaterial);
                building.outsideDoorCount++;
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
        building.putWall(this, i, new com.zombies.map.building.DoorWall(corners[a], corners[b], building, lm, rm));
    }
    private void createDoor(Gridable g, int a, int b, MATERIAL lm, MATERIAL rm) {
        building.putWall(this, g, new com.zombies.map.building.DoorWall(corners[a], corners[b], building, lm, rm));
    }
    private void createWall(int i, int a, int b, MATERIAL lm, MATERIAL rm) {
        building.putWall(this, i, new WallWall(corners[a], corners[b], lm, rm));
    }
    private void createWall(Gridable g, int a, int b, MATERIAL lm, MATERIAL rm) {
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
    public com.zombies.map.building.Building getBuilding() { return building; }
    public Room getRoom() { return room; }
    public int[] getKey() { return key; }
    public String getSKey() { return sKey; }
    public HashSet<Box> getAdjBoxes() {
        HashSet<Box> adjBoxes = new HashSet<>();
        Gridable g;
        for (int[] k : com.zombies.map.building.Building.getAdjBMKeys(key)) {
            g = building.gridMapGet(k);
            if (g instanceof Box)
                adjBoxes.add((Box)g);
        }
        return adjBoxes;
    }
    public ArrayList<int[]> getOpenAdjKeys() {
        ArrayList<int[]> adjKeys = new ArrayList<>();
        for (int[] k : com.zombies.map.building.Building.getAdjBMKeys(key)) {
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
        zone.removeModelingCallback(room.type.floorMaterial, modelFloorCallback);
        zone.removeObject(this);
    }

    public String giveKey(Box b) {
        return Math.min(id, b.getId()) + "," + Math.max(id, b.getId());
    }
}
