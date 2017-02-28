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
import com.zombies.interfaces.Gridable;
import com.zombies.interfaces.Modelable;
import com.zombies.powerups.Powerup;

public class Box extends Overlappable implements Gridable {
    public static int numBoxes = 0;

    private ArrayList<Unit> zombies = new ArrayList<>();
    private ArrayList<Unit> survivors = new ArrayList<Unit>();
    private ArrayList<Crate> crates = new ArrayList<Crate>();
    private ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    private HashMap<String, Gridable> gridMap;
    private Random random = new Random();

    private Building building;
    private Room room;

    private int id;
    private int[] key;
    private String sKey;

    public Box(Room room, int[] key) {
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
        height   = C.GRID_SIZE;
        width    = C.GRID_SIZE;

        corners = building.cornersOf(position);
        setZones();
    }

    private void setZones() {
        zone = Zone.getZone(getCenter());
        Zone z;
        for (Vector2 v : corners) {
            z = Zone.getZone(v);
            z.addObject(this);
            z.addObject(room);
        }
    }

    public void setAdjWallMap() {
        Gridable n = gridMap.get(key[0] + "," + (key[1] + 1));
        Gridable s = gridMap.get(key[0] + "," + (key[1] - 1));
        Gridable e = gridMap.get(key[0] + 1 + "," + key[1]);
        Gridable w = gridMap.get(key[0] - 1 + "," + key[1]);

        String key = this.key[0]+","+(this.key[1]+1)+",h";
        if (!(n instanceof Box) || ((Box)n).getRoom() != room) {
            putWall(key,
                    position.cpy().add(0, height),
                    position.cpy().add(width, height),
                    building);
        } else { clearWall(key); }

        key = (this.key[0]+1)+","+ this.key[1]+",v";
        if (!(e instanceof Box) || ((Box)e).getRoom() != room) {
            putWall(key,
                    position.cpy().add(width, 0),
                    position.cpy().add(width, height),
                    building);
        } else { clearWall(key); }

        key = sKey+",h";
        if (!(s instanceof Box) || ((Box)s).getRoom() != room) {
            putWall(key,
                    position.cpy(),
                    position.cpy().add(width, 0),
                    building);
        } else { clearWall(key); }

        key = sKey+",v";
        if (!(w instanceof Box) || ((Box)w).getRoom() != room) {
            putWall(key,
                    position.cpy(),
                    position.cpy().add(0, height),
                    building);
        } else { clearWall(key); }
    }

    private void putWall(String key, Vector2 p1, Vector2 p2, Modelable m) {
        building.putWallMap(key, new WallWall(p1, p2, m));
    }
    private void clearWall(String key) {
        if (building.wallMap.get(key) != null)
            building.wallMap.get(key).destroy();
    }

    public float x() {return position.x;}
    public float y() {return position.y;}

    public ArrayList<Powerup> getPowerups() {
        return powerups;
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
            return position.cpy().add(C.GRID_SIZE, 0);
        case 3:
            return position.cpy().add(0, C.GRID_SIZE);
        case 4:
            return position.cpy().add(C.GRID_SIZE, C.GRID_SIZE);
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
        return position.cpy().add(random.nextFloat() * C.GRID_SIZE, random.nextFloat() * C.GRID_SIZE);
    }

    public Unit randomZombie() {
        if (zombies.isEmpty() || zombies.size() == 1) {
            return null;
        }
        Unit u = zombies.get(random.nextInt(zombies.size()));
        return u;
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
    public HashSet<int[]> getOpenAdjKeys() {
        HashSet<int[]> adjKeys = new HashSet<>();
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

    @Override
    public void setZone(Zone z) {
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

    public String giveKey(Box b) {
        return Math.min(id, b.getId()) + "," + Math.max(id, b.getId());
    }
}
