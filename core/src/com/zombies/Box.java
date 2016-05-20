package com.zombies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Overlappable;
import com.zombies.map.MapGen;
import com.zombies.powerups.HealthPickup;
import com.zombies.powerups.PistolPickup;
import com.zombies.powerups.Powerup;
import com.zombies.powerups.ShotgunPickup;
import com.zombies.util.Geometry;

public class Box implements Overlappable, Loadable, HasZone {
    private ArrayList<Unit> zombies = new ArrayList<Unit>();
    private ArrayList<Unit> survivors = new ArrayList<Unit>();
    private ArrayList<Crate> crates = new ArrayList<Crate>();
    private ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    private HashMap<Character, Box> adjBoxes = new HashMap<Character, Box>();
    private Vector2 position;
    private Room room;
    private GameView view;
    private Random random = new Random();
    public float height = C.BOX_SIZE, width = C.BOX_SIZE;
    private Zone z;

    public String BMKey;

    public Box(float x, float y) {
        position = new Vector2(x, y);
        this.view = GameView.gv;
    }

    public boolean insideBox(float x, float y) {
        return (x > position.x && x < position.x + C.BOX_SIZE && y > position.y && y < position.y + C.BOX_SIZE);
    }

    private void populateBox() {
        if (C.ENABLE_CRATES && random.nextFloat() < C.CRATE_CHANCE)
            crates.add(new Crate(view, this.randomPoint()));
        if (C.ENABLE_SURVIVORS && random.nextFloat() < C.SURVIVOR_CHANCE)
            survivors.add(new Survivor(this.randomPoint()));
        if (C.ENABLE_SHOTGUN && random.nextFloat() < C.SHOTGUN_CHANCE)
            powerups.add(new ShotgunPickup(this));
        if (C.ENABLE_PISTOL && random.nextFloat() < C.PISTOL_CHANCE)
            powerups.add(new PistolPickup(this));
        if (C.ENABLE_HEALTH && random.nextFloat() < C.HEALTH_CHANCE)
            powerups.add(new HealthPickup(this));
    }

    // detect where this box should have walls, but don't create them yet.
    public ArrayList<ArrayList<Vector2>> proposeWallPositions() {

        ArrayList<ArrayList<Vector2>> proposedPositions = new ArrayList<ArrayList<Vector2>>();

        if (adjBoxes.get('n') == null) {
            ArrayList<Vector2> points = new ArrayList<Vector2>();
            points.add(new Vector2(position.cpy().add(0, height)));
            points.add(new Vector2(position.cpy().add(width, height)));
            proposedPositions.add(points); // top wall
        }
        if (adjBoxes.get('e') == null) {
            ArrayList<Vector2> points = new ArrayList<Vector2>();
            points.add(new Vector2(position.cpy().add(width, 0)));
            points.add(new Vector2(position.cpy().add(width, height)));
            proposedPositions.add(points); // right wall
        }
        if (adjBoxes.get('s') == null) {
            ArrayList<Vector2> points = new ArrayList<Vector2>();
            points.add(new Vector2(position.cpy()));
            points.add(new Vector2(position.cpy().add(width, 0)));
            proposedPositions.add(points); // bottom wall
        }
        if (adjBoxes.get('w') == null) {
            ArrayList<Vector2> points = new ArrayList<Vector2>();
            points.add(new Vector2(position.cpy()));
            points.add(new Vector2(position.cpy().add(0, height)));
            proposedPositions.add(points); // left wall
        }

        return proposedPositions;
    }

    public float x() {return position.x;}
    public float y() {return position.y;}

    public ArrayList<Powerup> getPowerups() {
        return powerups;
    }

    public ArrayList<Character> getOpenDirections() {
        ArrayList<Character> openDirections = new ArrayList<Character>();
        for (char c: MapGen.DIRECTIONS) {
            if (adjBoxes.get(c) == null) {
                openDirections.add(openDirections.size(), c);
            }
        }
        return openDirections;
    }
    public char getRandomOpenDirection() {
        ArrayList<Character> openDirections = getOpenDirections();
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
            return position.cpy().add(C.BOX_SIZE, 0);
        case 3:
            return position.cpy().add(0, C.BOX_SIZE);
        case 4:
            return position.cpy().add(C.BOX_SIZE, C.BOX_SIZE);
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
        return position.cpy().add(random.nextFloat() * C.BOX_SIZE, random.nextFloat() * C.BOX_SIZE);
    }

    public Unit randomZombie() {
        if (zombies.isEmpty() || zombies.size() == 1) {
            return null;
        }
        Unit u = zombies.get(random.nextInt(zombies.size()));
        return u;
    }

    public Vector2 getCenter() {
        return position.cpy().add(width / 2, height / 2);
    }

    public Box setRoom(Room room) {
        this.room = room;
        return this;
    }

    public void setAdjBox(Character direction, Box box) {
        adjBoxes.put(direction, box);
    }
    public Box getAdjBox(Character direction) {
        return adjBoxes.get(direction);
    }
    public HashMap<Character, Box> getAdjBoxes() {return adjBoxes;}
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
        int[] locations = new int[2];
        locations[0] = Integer.parseInt(stringLocations[0]);
        locations[1] = Integer.parseInt(stringLocations[1]);
        return locations;
    }

    @Override
    public String className() {
        return "Box";
    }

    @Override
    public boolean overlaps(float x, float y, float w, float h) {
        return Geometry.rectOverlap(position.x, position.y, width, height, x, y, w, h);
    }
    @Override
    public float edge(char direction) {
        switch(direction) {
            case 'n':
                return position.y + height;
            case 'e':
                return position.x + width;
            case 's':
                return position.y;
            case 'w':
                return position.x;
        }
        return 0;
    }
    @Override
    public float oppositeEdge(char direction) {
        switch(direction) {
            case 'n':
                return edge('s');
            case 'e':
                return edge('w');
            case 's':
                return edge('n');
            case 'w':
                return edge('e');
        }
        return 0;
    }

    @Override
    public Vector2 intersectPointOfLine(Vector2 p1, Vector2 p2) {
        // left line
        Vector2 ip = Geometry.intersectPoint(position.x, position.y, position.x, position.y + height, p1.x, p1.y, p2.x, p2.y);
        if (ip == null) // top line
            ip = Geometry.intersectPoint(position.x, position.y + height, position.x + width, position.y + height, p1.x, p1.y, p2.x, p2.y);
        if (ip == null) // right line
            ip = Geometry.intersectPoint(position.x + width, position.y + height, position.x + width, position.y, p1.x, p1.y, p2.x, p2.y);
        if (ip == null) // bottom line
            ip = Geometry.intersectPoint(position.x, position.y, position.x + width, position.y, p1.x, p1.y, p2.x, p2.y);

        return ip;
    }

    @Override
    public void load() {

    }
    @Override
    public void unload() {

    }
    @Override
    public Zone getZone() {
        return z;
    }
    @Override
    public void setZone(Zone z) {
        this.z = z;
    }
}
