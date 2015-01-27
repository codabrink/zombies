package com.zombies;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import com.powerups.HealthPickup;
import com.powerups.PistolPickup;
import com.powerups.ShotgunPickup;

import com.badlogic.gdx.math.Vector2;

public class Box {

	private ArrayList<Box> borders = new ArrayList<Box>();
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Unit> zombies = new ArrayList<Unit>();
	private ArrayList<Unit> survivors = new ArrayList<Unit>();
	private ArrayList<Unit> dumpList = new ArrayList<Unit>();
	private ArrayList<Unit> addList = new ArrayList<Unit>();
	private ArrayList<Crate> crates = new ArrayList<Crate>();
	private LinkedList<Powerup> powerups = new LinkedList<Powerup>();
	private boolean touched = false;
	private boolean pathed = false;
	private int x=0, y=0;
	private Room room;
	private GameView view;
	private Random random = new Random();
	private C c;
	private Floor floor;
	
	public Box(GameView view, int x, int y) {
		this.c = view.c;
		this.x = x;
		this.y = y;
		this.view = view;
		this.floor = new Floor(view, this);
		
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
	
	public void addBorder(Box box) {
		borders.add(box);
	}

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
		zombies.add(new Zombie(view, this, this.randomPoint()));
	}
	
	public void addZombie(Unit u) {
		addList.add(u);
	}
	
	public void createDoor(Box box) {
		if (!borders.contains(box)) return;
		int i = borders.indexOf(box);
		if (walls.get(i) != null)
			walls.get(i).makeDoor();
	}
	
	public void drawFloor() {
		floor.draw();
	}
	
	public Vector2 getPosition() {
		return new Vector2(x, y);
	}
	
	
	public Vector2 getPosition(int i) {
		switch (i) {
		case 1:
			return new Vector2(x, y);
		case 2:
			return new Vector2(x + c.BOX_WIDTH, y);
		case 3:
			return new Vector2(x, y + c.BOX_HEIGHT);
		case 4:
			return new Vector2(x + c.BOX_WIDTH, y + c.BOX_HEIGHT);
		}
		return new Vector2();
	}
	
	public void drawBox() {
		for (Unit u: zombies) {
			if (u != view.getPlayer()) {
				u.draw();
			}
		}
		for (Unit u: survivors) {
			u.draw();
		}
		for (Crate c: crates) {
			c.draw();
		}
		for (Powerup p: powerups) {
			p.draw();
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
		return borders.get(i);
	}
	
	public ArrayList<Box> getBoxes() {
		return borders;
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
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
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
				box = borders.get(i);
			}
			createDoor(box);
			box.createDoor(this);
			box.path(level - 1);
		}
	}
	
	public Vector2 randomPoint() {
		return new Vector2(x + random.nextFloat() * c.BOX_WIDTH, y + random.nextFloat() * c.BOX_HEIGHT);
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
		if (!borders.contains(box)) return;
		int i = borders.indexOf(box);
		walls.get(i).removeWall();
		walls.set(i, null);
	}
	
	public void setBorder(Box box, int i) {
		borders.add(i, box);
	}

	public Box setRoom(Room room) {
		this.room = room;
		touched = true;
		return this;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
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
		if (player.getX() > x + c.BOX_WIDTH) {
			if (borders.get(1) != null) {
				player.setBox(borders.get(1));
			}
		}
		//too far left
		if (player.getX() < x) {
			if (borders.get(3) != null) {
				player.setBox(borders.get(3));
			}
		}
		//too far below
		if (player.getY() > y + c.BOX_HEIGHT) {
			if (borders.get(2) != null) {
				player.setBox(borders.get(2));
			}
		}
		//too far above
		if (player.getY() < y) {
			if (borders.get(0) != null) {
				player.setBox(borders.get(0));
			}
		}
	}
	
	public void updateSurvivorRecords(Unit p) {
		//too far right
		if (p.getX() > x + c.BOX_WIDTH) {
			if (borders.get(1) != null) {
				p.setBox(borders.get(1));
				dumpList.add(p);
				borders.get(1).addSurvivor(p);
			}
		}
		//too far left
		if (p.getX() < x) {
			if (borders.get(3) != null) {
				p.setBox(borders.get(3));
				dumpList.add(p);
				borders.get(3).addSurvivor(p);
			}
		}
		//too far below
		if (p.getY() > y + c.BOX_HEIGHT) {
			if (borders.get(2) != null) {
				p.setBox(borders.get(2));
				dumpList.add(p);
				borders.get(2).addSurvivor(p);
			}
		}
		//too far above
		if (p.getY() < y) {
			if (borders.get(0) != null) {
				p.setBox(borders.get(0));
				dumpList.add(p);
				borders.get(0).addSurvivor(p);
			}
		}
	}
	
	public void updateZombieRecords(Unit p) {
		//too far right
		if (p.getX() > x + c.BOX_WIDTH) {
			if (borders.get(1) != null) {
				p.setBox(borders.get(1));
				dumpList.add(p);
				borders.get(1).addZombie(p);
			}
		}
		//too far left
		if (p.getX() < x) {
			if (borders.get(3) != null) {
				p.setBox(borders.get(3));
				dumpList.add(p);
				borders.get(3).addZombie(p);
			}
		}
		//too far below
		if (p.getY() > y + c.BOX_HEIGHT) {
			if (borders.get(2) != null) {
				p.setBox(borders.get(2));
				dumpList.add(p);
				borders.get(2).addZombie(p);
			}
		}
		//too far above
		if (p.getY() < y) {
			if (borders.get(0) != null) {
				p.setBox(borders.get(0));
				dumpList.add(p);
				borders.get(0).addZombie(p);
			}
		}
	}
	
}
