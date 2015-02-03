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

public class Box {

	private ArrayList<Box> adjBoxes = new ArrayList<Box>();
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Unit> zombies = new ArrayList<Unit>();
	private ArrayList<Unit> survivors = new ArrayList<Unit>();
	private ArrayList<Unit> dumpList = new ArrayList<Unit>();
	private ArrayList<Unit> addList = new ArrayList<Unit>();
	private ArrayList<Crate> crates = new ArrayList<Crate>();
	private LinkedList<Powerup> powerups = new LinkedList<Powerup>();
	private boolean touched = false;
	private boolean pathed = false;
    private Vector2 position;
    private int indexX, indexY;
	private Room room;
	private GameView view;
	private Random random = new Random();
	private C c;
	private Floor floor;
	
	public Box(GameView view, float x, float y, int indexX, int indexY) {
		this.c = view.c;
        position = new Vector2(x, y);
		this.view = view;
		this.floor = new Floor(view, this);
        this.indexX = indexX;
        this.indexY = indexY;

		//create walls
		walls.add(new Wall(this.view, this, 0, 0, c.BOX_WIDTH, 0, 0)); //top wall
		walls.add(new Wall(this.view, this, c.BOX_WIDTH, 0, c.BOX_WIDTH, c.BOX_HEIGHT, 1)); //right wall
		walls.add(new Wall(this.view, this, 0, c.BOX_HEIGHT, c.BOX_WIDTH, c.BOX_HEIGHT, 2)); //bottom wall
		walls.add(new Wall(this.view, this, 0, 0, 0, c.BOX_HEIGHT, 3)); //left wall
		
		this.populateBox();
	}
	
	private void populateBox() {
		if (c.ENABLE_CRATES && random.nextFloat() < c.CRATE_CHANCE) {
			crates.add(new Crate(view, this.randomPoint()));
		}
		if (c.ENABLE_SURVIVORS && random.nextFloat() < c.SURVIVOR_CHANCE) {
			survivors.add(new Survivor(view, this, this.randomPoint()));
		}
		if (c.ENABLE_SHOTGUN && random.nextFloat() < c.SHOTGUN_CHANCE) {
			powerups.add(new ShotgunPickup(view, this));
		}
		if (c.ENABLE_PISTOL && random.nextFloat() < c.PISTOL_CHANCE) {
			powerups.add(new PistolPickup(view, this));
		}
		if (c.ENABLE_HEALTH && random.nextFloat() < c.HEALTH_CHANCE) {
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
	
	public void addDumpList(Unit u) {
		dumpList.add(u);
	}

	public Survivor addSurvivor() {
		Survivor s = new Survivor(view, this, this.randomPoint());
		survivors.add(s);
		return s;
	}

	public void addSurvivor(Unit u) {
		survivors.add(u);
	}
	
	public void addZombie() {
        if (c.POPULATE_ZOMBIES) {
            zombies.add(new Zombie(view, this, this.randomPoint()));
        }
	}
	
	public void addZombie(Unit u) {
		addList.add(u);
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
			return position.cpy().add(c.BOX_WIDTH, 0);
		case 3:
			return position.cpy().add(0, c.BOX_HEIGHT);
		case 4:
			return position.cpy().add(c.BOX_WIDTH, c.BOX_HEIGHT);
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
    public ArrayList<Unit> getAliveUnits() {
        ArrayList<Unit> units = new ArrayList<Unit>();
        for (Unit u: zombies) {
            if (!u.dead) {
                units.add(u);
            }
        }
        return units;
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
		return position.cpy().add(random.nextFloat() * c.BOX_WIDTH, random.nextFloat() * c.BOX_HEIGHT);
	}
	
	public Unit randomZombie() {
		if (zombies.isEmpty() || zombies.size() == 1) {
			return null;
		}
		Unit u = zombies.get(random.nextInt(zombies.size()));
		if (u == view.getPlayer()) {
			return randomZombie();
		}
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
	
	public void update() {
		for (Unit u: zombies) {
			if (u != view.getPlayer()) {
				u.update();
				updateZombieRecords(u);
			}
		}
		for (Unit u: survivors) {
			u.update();
			updateSurvivorRecords(u);
		}
		
		for (Crate c: crates) {
			c.update();
		}
		
		for (Unit u: dumpList) {
			zombies.remove(u);
			survivors.remove(u);
		}
		dumpList.clear();
		for (Unit u: addList) {
			zombies.add(u);
		}
		addList.clear();
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
		if (player.getX() > position.x + c.BOX_WIDTH) {
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
		if (player.getY() > position.y + c.BOX_HEIGHT) {
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
	
	public void updateSurvivorRecords(Unit p) {
		//too far right
		if (p.getX() > position.x + c.BOX_WIDTH) {
			if (adjBoxes.get(1) != null) {
				p.setBox(adjBoxes.get(1));
				dumpList.add(p);
				adjBoxes.get(1).addSurvivor(p);
			}
		}
		//too far left
		if (p.getX() < position.x) {
			if (adjBoxes.get(3) != null) {
				p.setBox(adjBoxes.get(3));
				dumpList.add(p);
				adjBoxes.get(3).addSurvivor(p);
			}
		}
		//too far below
		if (p.getY() > position.y + c.BOX_HEIGHT) {
			if (adjBoxes.get(2) != null) {
				p.setBox(adjBoxes.get(2));
				dumpList.add(p);
				adjBoxes.get(2).addSurvivor(p);
			}
		}
		//too far above
		if (p.getY() < position.y) {
			if (adjBoxes.get(0) != null) {
				p.setBox(adjBoxes.get(0));
				dumpList.add(p);
				adjBoxes.get(0).addSurvivor(p);
			}
		}
	}
	
	public void updateZombieRecords(Unit p) {
        if (p.dead) return;

		//too far right
		if (p.getX() > position.x + c.BOX_WIDTH) {
			if (adjBoxes.get(1) != null) {
				p.setBox(adjBoxes.get(1));
				dumpList.add(p);
				adjBoxes.get(1).addZombie(p);
			}
		}
		//too far left
		if (p.getX() < position.x) {
			if (adjBoxes.get(3) != null) {
				p.setBox(adjBoxes.get(3));
				dumpList.add(p);
				adjBoxes.get(3).addZombie(p);
			}
		}
		//too far below
		if (p.getY() > position.y + c.BOX_HEIGHT) {
			if (adjBoxes.get(2) != null) {
				p.setBox(adjBoxes.get(2));
				dumpList.add(p);
				adjBoxes.get(2).addZombie(p);
			}
		}
		//too far above
		if (p.getY() < position.y) {
			if (adjBoxes.get(0) != null) {
				p.setBox(adjBoxes.get(0));
				dumpList.add(p);
				adjBoxes.get(0).addZombie(p);
			}
		}
	}
}
