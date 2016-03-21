package com.zombies;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.powerups.HealthPickup;
import com.powerups.PistolPickup;
import com.powerups.Powerup;
import com.powerups.ShotgunPickup;
import com.zombies.zombie.Carcass;

public class Box {
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private ArrayList<Unit> zombies = new ArrayList<Unit>();
    private ArrayList<Carcass> carcasses = new ArrayList<Carcass>();
    private ArrayList<Unit> survivors = new ArrayList<Unit>();
    private ArrayList<Crate> crates = new ArrayList<Crate>();
    private ArrayList<Powerup> powerups = new ArrayList<Powerup>();
    private boolean touched = false;
    private boolean pathed = false;
    private Vector2 position;
    private int indexX, indexY;
    private Room room;
    private GameView view;
    private Random random = new Random();
    private Floor floor;
    public float height = C.BOX_SIZE, width = C.BOX_SIZE;

    public Box(float x, float y, int indexX, int indexY) {
        position = new Vector2(x, y);
        this.view = GameView.gv;
        this.floor = new Floor(view, this);
        this.indexX = indexX;
        this.indexY = indexY;

        //create walls
//        walls.add(new Wall(this, 0, 0, C.BOX_WIDTH, 0, 0)); //top wall
//        walls.add(new Wall(this, C.BOX_WIDTH, 0, C.BOX_WIDTH, C.BOX_HEIGHT, 1)); //right wall
//        walls.add(new Wall(this, 0, C.BOX_HEIGHT, C.BOX_WIDTH, C.BOX_HEIGHT, 2)); //bottom wall
//        walls.add(new Wall(this, 0, 0, 0, C.BOX_HEIGHT, 3)); //left wall

        this.populateBox();
    }

    public Box(float x, float y) {
        position = new Vector2(x, y);
        this.view = GameView.gv;
        this.floor = new Floor(view, this);

        walls.add(new Wall(this, 0,     height, width,  0 )); // top wall
        walls.add(new Wall(this, width, 0,      height, 90)); // right wall
        walls.add(new Wall(this, 0,     0,      width,  0 )); // bottom wall
        walls.add(new Wall(this, 0,     0,      height, 90)); // left wall
        //this.populateBox();
    }

    public void load() {
        for (Unit z : zombies) {
            z.load();
        }
    }

    public void unload() {
    }

    public boolean insideBox(float x, float y) {
        return (x > position.x && x < position.x + C.BOX_WIDTH && y > position.y && y < position.y + C.BOX_HEIGHT);
    }

    private void populateBox() {
        if (C.ENABLE_CRATES && random.nextFloat() < C.CRATE_CHANCE) {
            crates.add(new Crate(view, this.randomPoint()));
        }
        if (C.ENABLE_SURVIVORS && random.nextFloat() < C.SURVIVOR_CHANCE) {
            survivors.add(new Survivor(this.randomPoint()));
        }
        if (C.ENABLE_SHOTGUN && random.nextFloat() < C.SHOTGUN_CHANCE) {
            powerups.add(new ShotgunPickup(this));
        }
        if (C.ENABLE_PISTOL && random.nextFloat() < C.PISTOL_CHANCE) {
            powerups.add(new PistolPickup(this));
        }
        if (C.ENABLE_HEALTH && random.nextFloat() < C.HEALTH_CHANCE) {
            powerups.add(new HealthPickup(this));
        }
    }

    public float getX() {return position.x;}
    public float getY() {return position.y;}

    public ArrayList<Powerup> getPowerups() {
        return powerups;
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

    public void createDoor(Box box) {
        Wall[] walls = adjWalls(box);
        if (walls instanceof Wall[]) {
            walls[0].makeDoor();
            walls[1].makeDoor();
        }
    }

    public void drawFloor(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        floor.draw(spriteBatch, shapeRenderer);
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

    public void drawBox(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        for (Unit u: zombies) {
            if (u != view.getPlayer()) {
                u.draw(spriteBatch, shapeRenderer);
            }
        }
        for (Unit u: survivors) {
            u.draw(spriteBatch, shapeRenderer);
        }
        for (com.powerups.Powerup p: powerups) {
            p.draw(spriteBatch, shapeRenderer);
        }
        for (Crate c: crates) {
            c.draw(spriteBatch, shapeRenderer);
        }
        for (Powerup p: powerups) {
            p.draw(spriteBatch, shapeRenderer);
        }
        drawWalls();
    }

    public void drawWalls() {
        for (Wall w: walls) {
            if (w != null)
                w.draw();
        }
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

    public boolean isPathed() {
        return pathed;
    }

    public boolean isTouched() {
        return touched;
    }

    public void path(int level) {
        if (level > 0) {
            pathed = true;
            Box box = null;
            int i = 0;
            while (box == null) {
                i = random.nextInt(3);
                // TODO actually path
            }
            createDoor(box);
            box.path(level - 1);
        }
    }

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

    public void removeWall(Wall w) {
        w.removeWall();
        walls.remove(w);
    }

    // new
    public void removePotentialWall(Box box) {
        Wall[] walls = adjWalls(box);
        if (walls instanceof Wall[]) {
            new Color();
            Color c = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f);
            walls[0].setColor(c);
            walls[1].setColor(c);
            //removeWall(walls[0]);
            //box.removeWall(walls[1]);
        }
    }

    private Wall[] adjWalls(Box box) {
        for (Wall w : (ArrayList<Wall>)walls.clone()) {
            for (Wall ww : (ArrayList<Wall>)box.getWalls().clone()) {
                if (w.samePositionAs(ww))
                    return new Wall[]{w, ww};
            }
        }
        return null;
    }

    public Box setRoom(Room room) {
        this.room = room;
        touched = true;

        room.addZone(Zone.getZone(position.x, position.y));
        room.addZone(Zone.getZone(position.x + C.BOX_WIDTH, position.y));
        room.addZone(Zone.getZone(position.x + C.BOX_WIDTH, position.y + C.BOX_HEIGHT));
        room.addZone(Zone.getZone(position.x, position.y + C.BOX_HEIGHT));

        return this;
    }

    public void touch(){
        touched = true;
    }

    public void update(int frame) {
        for (Crate c: crates) {
            c.update();
        }
        updateWalls();
    }

    private void updateWalls() {
        for (Wall w: walls) {
            w.update();
        }
    }
}
