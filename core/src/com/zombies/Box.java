package com.zombies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.interfaces.HasZone;
import com.interfaces.Loadable;
import com.interfaces.Overlappable;
import com.interfaces.Drawable;
import com.map.MapGen;
import com.powerups.HealthPickup;
import com.powerups.PistolPickup;
import com.powerups.Powerup;
import com.powerups.ShotgunPickup;
import com.util.Geometry;

public class Box implements Drawable, Overlappable, Loadable, HasZone {
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private ArrayList<Unit> zombies = new ArrayList<Unit>();
    private ArrayList<Unit> survivors = new ArrayList<Unit>();
    private ArrayList<Crate> crates = new ArrayList<Crate>();
    private ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    private HashMap<Character, Box> adjBoxes = new HashMap<Character, Box>();
    private HashMap<Character, Wall> wallsByDirection = new HashMap<Character, Wall>();
    private Vector2 position;
    private int indexX, indexY;
    private Room room;
    private GameView view;
    private Random random = new Random();
    private Floor floor;
    public float height = C.BOX_SIZE, width = C.BOX_SIZE;
    private Zone z;

    public String BMKey;
    private int[] boxMapLocation;

    public Box(float x, float y) {
        position = new Vector2(x, y);
        this.view = GameView.gv;
        this.floor = new Floor(this);
    }

    public boolean insideBox(float x, float y) {
        return (x > position.x && x < position.x + C.BOX_WIDTH && y > position.y && y < position.y + C.BOX_HEIGHT);
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

    public void genOuterWalls() {
        if (adjBoxes.get('n') == null) {
            walls.add(new Wall(position.cpy().add(0, height), width, 0)); // top wall
            wallsByDirection.put('n', walls.get(walls.size() - 1));
        }
        if (adjBoxes.get('e') == null) {
            walls.add(new Wall(position.cpy().add(width, 0), height, 90)); // right wall
            wallsByDirection.put('e', walls.get(walls.size() - 1));
        }
        if (adjBoxes.get('s') == null) {
            walls.add(new Wall(position.cpy(), width, 0)); // bottom wall
            wallsByDirection.put('s', walls.get(walls.size() - 1));
        }
        if (adjBoxes.get('w') == null) {
            walls.add(new Wall(position.cpy(), height, 90)); // left wall
            wallsByDirection.put('w', walls.get(walls.size() - 1));
        }
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

    public void addUnit(Unit u) {
        if (u.box != null)
            u.box.removeUnit(u);
        u.box = this;

        if (u instanceof Zombie) {
            Zombie z = (Zombie)u;
            if (zombies.indexOf(z) == -1)
                zombies.add(z);
            return;
        } else if (u instanceof Survivor) {
            Survivor s = (Survivor)u;
            if (survivors.indexOf(s) == -1)
                survivors.add(s);
            return;
        }
        throw new Error("Addition of class " + u.getClass() + " to box is not supported.");
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

    public void createDoor(char direction) {

    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getPosition(int i) {
        switch (i) {
        case 1:
            return position;
        case 2:
            return position.cpy().add(C.BOX_WIDTH, 0);
        case 3:
            return position.cpy().add(0, C.BOX_HEIGHT);
        case 4:
            return position.cpy().add(C.BOX_WIDTH, C.BOX_HEIGHT);
        }
        return new Vector2();
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        floor.draw(spriteBatch, shapeRenderer, modelBatch);
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

    public Wall getWall(int i) {
        return walls.get(i);
    }

    public ArrayList<Wall> getWalls() { return walls; }

    public HashMap<Character, Wall> getWallsByDirection() { return wallsByDirection; }

    public Vector2 randomPoint() {
        return position.cpy().add(random.nextFloat() * C.BOX_WIDTH, random.nextFloat() * C.BOX_HEIGHT);
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
    public void load() {
        for (Wall w: walls)
            w.load();
    }
    @Override
    public void unload() {
        for (Wall w: walls)
            w.unload();
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
