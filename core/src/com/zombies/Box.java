package com.zombies;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.powerups.HealthPickup;
import com.powerups.PistolPickup;
import com.powerups.ShotgunPickup;

import com.badlogic.gdx.math.Vector2;
import com.zombies.zombie.Carcass;

public class Box {
	private ArrayList<Box> adjBoxes = new ArrayList<Box>();
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Unit> zombies = new ArrayList<Unit>();
	private ArrayList<Carcass> carcasses = new ArrayList<Carcass>();
	private ArrayList<Unit> survivors = new ArrayList<Unit>();
	private ArrayList<Crate> crates = new ArrayList<Crate>();
	private LinkedList<Powerup> powerups = new LinkedList<Powerup>();
	private boolean touched = false;
	private boolean pathed = false;
    private Vector2 position;
    private int indexX, indexY;
	private Room room;
	private GameView view;
	private Random random = new Random();
	private Floor floor;
	
	public Box(float x, float y, int indexX, int indexY) {
        position = new Vector2(x, y);
		this.view = GameView.m;
		this.floor = new Floor(view, this);
        this.indexX = indexX;
        this.indexY = indexY;

		//create walls
		walls.add(new Wall(this, 0, 0, C.BOX_WIDTH, 0, 0)); //top wall
		walls.add(new Wall(this, C.BOX_WIDTH, 0, C.BOX_WIDTH, C.BOX_HEIGHT, 1)); //right wall
		walls.add(new Wall(this, 0, C.BOX_HEIGHT, C.BOX_WIDTH, C.BOX_HEIGHT, 2)); //bottom wall
		walls.add(new Wall(this, 0, 0, 0, C.BOX_HEIGHT, 3)); //left wall
		
		this.populateBox();

        Zone.getZone(x, y).addBox(this);
        Zone.getZone(x + C.BOX_WIDTH, y).addBox(this);
        Zone.getZone(x + C.BOX_WIDTH, y + C.BOX_HEIGHT).addBox(this);
        Zone.getZone(x, y + C.BOX_HEIGHT).addBox(this);
    }

	public void load() {
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
			survivors.add(new Survivor(this, this.randomPoint()));
		}
		if (C.ENABLE_SHOTGUN && random.nextFloat() < C.SHOTGUN_CHANCE) {
			powerups.add(new ShotgunPickup(view, this));
		}
		if (C.ENABLE_PISTOL && random.nextFloat() < C.PISTOL_CHANCE) {
			powerups.add(new PistolPickup(view, this));
		}
		if (C.ENABLE_HEALTH && random.nextFloat() < C.HEALTH_CHANCE) {
			powerups.add(new HealthPickup(view, this));
		}
	}

	public void addAdjBox(Box box) {
		adjBoxes.add(box);
	}
    public boolean isAdjacent(Box box) { return adjBoxes.contains(box); }
    public int adjDirection(Box box) {
        int dX = box.getIndexX() - this.indexX;
        int dY = box.getIndexY() - this.indexY;
        if (dX != 0 && dY != 0 || box == this) { return -1; }
        if (dX == -1)
            return 4;
        if (dX == 1)
            return 2;
        if (dY == -1)
            return 1;
        if (dY == 1)
            return 3;
        return -1;
    }

    public int getIndexX() {return indexX;}
    public int getIndexY() {return indexY;}
    public float getX() {return position.x;}
    public float getY() {return position.y;}


	public LinkedList<Powerup> getPowerups() {
		return powerups;
	}
	
	public Survivor addSurvivor() {
		Survivor s = new Survivor(this, this.randomPoint());
		survivors.add(s);
		return s;
	}

	public void addSurvivor(Unit u) {
		survivors.add(u);
	}
    public void removeSurvivor(Unit u) {survivors.remove(u);}
	
	public void addZombie() {
        if (C.POPULATE_ZOMBIES) {
            zombies.add(new Zombie(view, this, this.randomPoint()));
        }
	}

	public void addZombie(Unit u) {
		zombies.add(u);
	}
	
	public void createDoor(Box box) {
		if (!adjBoxes.contains(box)) return;
		int i = adjBoxes.indexOf(box);
		if (walls.get(i) != null)
			walls.get(i).makeDoor();
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
	
	public Box getBox(int i) {
		return adjBoxes.get(i);
	}
	
	public ArrayList<Box> getBoxes() {
		return adjBoxes;
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
				box = adjBoxes.get(i);
			}
			createDoor(box);
			box.createDoor(this);
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
	
	public void removeWall(Box box){
		if (!adjBoxes.contains(box)) return;
		int i = adjBoxes.indexOf(box);
		walls.get(i).removeWall();
		walls.set(i, null);
	}
	
	public void setBorder(Box box, int i) {
		adjBoxes.add(i, box);
	}

	public Box setRoom(Room room) {
		this.room = room;
		touched = true;
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
			if (w != null)
				w.update();
		}
	}
	
	public void updatePlayerRecords() {
		Player player = view.getPlayer();
		//too far right
		if (player.getX() > position.x + C.BOX_WIDTH) {
			if (adjBoxes.get(1) != null) {
				player.setBox(adjBoxes.get(1));
			}
		}
		//too far left
		if (player.getX() < position.x) {
			if (adjBoxes.get(3) != null) {
				player.setBox(adjBoxes.get(3));
			}
		}
		//too far below
		if (player.getY() > position.y + C.BOX_HEIGHT) {
			if (adjBoxes.get(2) != null) {
				player.setBox(adjBoxes.get(2));
			}
		}
		//too far above
		if (player.getY() < position.y) {
			if (adjBoxes.get(0) != null) {
				player.setBox(adjBoxes.get(0));
			}
		}
	}
}
