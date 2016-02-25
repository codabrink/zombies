package com.zombies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;

public class Room {

	private int size;
	private ArrayList<Box> boxes = new ArrayList<Box>();
	private ArrayList<Room> adjRooms = new ArrayList<Room>();
	private Random random = new Random();
	private boolean alarmed = false;
	private float alpha = 0;
	private GameView view;
	private C c;
    private boolean loaded = false;

	public Room(GameView view, ArrayList<Box> boxes) {
		this.boxes = boxes;
		for (Box b: boxes) {
			b.setRoom(this);
		}
		this.removeWalls();
		
		//set references
		c = view.c;
		this.view = view;
	}

    public void doorsTo(Room room) {
        if (!adjRooms.contains(room)) {
            //TODO further path finding to that room
            return;
        }

    }

	public void currentRoom() {
		load(); // load self
		for (Room r : (LinkedList<Room>)view.loadedRooms.clone()) {
			if (!adjRooms.contains(r)) {
				r.unload();
			}
		}
		for (Room r : adjRooms) {
			r.load();
		}
	}

	public void load() {
		if (view.loadedRooms.contains(this))
			return;
		for (Box b : boxes) {
			b.load();
		}
		view.loadedRooms.push(this);
        loaded = true;
	}

	public void unload() {
		for (Box b: boxes) {
			b.unload();
		}
		view.loadedRooms.remove(this);
        loaded = false;
	}

	public Room(GameView view, Box box) {
		c = view.c;
		size = random.nextInt(c.MAX_ROOM_SIZE - c.MIN_ROOM_SIZE) + c.MIN_ROOM_SIZE;
		boxes.add(box.setRoom(this));
		while (boxes.size() < size) {
			ArrayList<Box> tempList = new ArrayList<Box>();
			for (Box b: boxes) {
				for (Box bb: b.getBoxes()){
					if (bb != null && !bb.isTouched()) {
						tempList.add(bb);
					}
				}
			}
			if (tempList.size() == 0) { //no available boxes to add to room
				break;
			}
			boxes.add(tempList.get(random.nextInt(tempList.size())).setRoom(this));
		}
		this.removeWalls();
		
		this.view = view;
	}
	
	public void addAdjRoom(Room r) {
		if (!adjRooms.contains(r)) {
			adjRooms.add(r);
		}
	}
	
	public void alarm(Unit victim) {
		if (!alarmed) {
			for (Box b: boxes) {
				for (Unit u: b.getUnits()) {
					if (random.nextBoolean()) {
						u.sick(victim);
					}
				}
			}
			alarmed = true;
		}
	}

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        for (Box b : boxes) {
            b.drawFloor(spriteBatch, shapeRenderer);
        }
        for (Box b: boxes) {
            b.drawBox(spriteBatch, shapeRenderer);
        }
    }

    public void drawAdjacentRooms(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        for (Room r : adjRooms){
            r.draw(spriteBatch, shapeRenderer);
        }
    }

	public void drawWalls(int frame) {
		for (Box b: boxes) {
			b.drawWalls();
			for (Unit z: b.getUnits()) {
				z.update(frame);
			}
		}
	}
	
	public Unit findUnit(Body b) {
		for (Box box: boxes) {
			for (Unit u: box.getUnits()) {
				if (u.getBody() == b)
					return u;
			}
		}
		return null;
	}
	
	public Wall findWall(Body b) {
		for (Box box: boxes) {
			for (Wall w: box.getWalls()) {
				if (w != null && w.getBody().getPosition().x == b.getPosition().x && w.getBody().getPosition().y == b.getPosition().y) {
					return w;
				}
			}
		}
		return null;
	}
	
	public void flood(int number) {
		long i = 0l;
		do {
			for (Box b: boxes) {
				for (Wall w: b.getWalls()) {
					if (w != null && w.isDoor()) {
						int l = random.nextInt(7) + 1;
						for (int k=1; k<=l; k++) {
							view.addPostZombie(new PostponedZombie(view, b, w.doorPosition(), w.getVector(), view.getPlayer(), i));
						}
						number -= l;
						i += 100l;
						if (number <= 0) {
							return;
						}
					}
				}
			}
		} while (number > 0);
	}
	
	public ArrayList<Box> getBoxes() {
		return boxes;
	}
	
	public boolean isAlarmed() {
		return alarmed;
	}
	
	public boolean isEmpty() {
		LinkedList<Unit> zList = new LinkedList<Unit>();
		for (Box b: boxes) {
			for (Unit u: b.getUnits()) {
				zList.add(u);
			}
		}
		if (zList.size() > 1) {
			return false;
		}
		return true;
	}
	
	public Box randomBox() {
		if (!boxes.isEmpty()) {
			return boxes.get(random.nextInt(boxes.size()));
		}
		return null;
	}
	
	public void removeWalls() {
		//remove walls
		for (Box b: boxes) {
			for (Box bb: boxes) {
				b.removeWall(bb);
			}
		}
	}
	
	public void findAdjacentRooms() {
		for (Box b: boxes) {
			for (Box bb: b.getBoxes()) {
				if (bb != null && bb.getRoom() != null)
					bb.getRoom().addAdjRoom(this);
			}
		}
	}

    public void update(int frame) {
        if (!loaded)
            return;
        for (Box b: boxes) b.update(frame);
    }

	public void updateAlpha() {
		if (view.getPlayer().getRoom() == this) {
			if (alpha < 255) {
				alpha += c.ROOM_ALPHA_RATE;
			}
			if (alpha > 255) {
				alpha = 255;
			}
		}
		else {
			if (alpha > 0) {
				alpha -= c.ROOM_ALPHA_RATE;
			}
			if (alpha <= 0) {
				view.getPlayer().clearOldRoom();
			}
		}
		for (Box b: boxes) {
			for (Wall w: b.getWalls()) {
				if (w != null) {
					//TODO: udpate alpha
				}
			}
		}
	}

    public LinkedList<Unit> getAliveUnits() {
        LinkedList<Unit> units = new LinkedList<Unit>();
        for (Box b: boxes) {
            units.addAll((Collection)b.getAliveUnits());
        }
        return units;
    }

}
