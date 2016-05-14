package com.zombies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.interfaces.Drawable;
import com.interfaces.HasZone;
import com.interfaces.Loadable;
import com.util.Assets;

public class Room implements Loadable, HasZone, Drawable {
    private int size;
    private ArrayList<Box> boxes = new ArrayList<Box>();
    private ArrayList<Room> adjRooms = new ArrayList<Room>();
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private Random random = new Random();
    private boolean alarmed = false;
    private float alpha = 0;
    private GameView view;
    private boolean loaded = false;
    private int frame;
    private Zone zone;
    private ArrayList<Box> outerBoxes = new ArrayList<Box>();
    private Vector2 center;

    private Model wallModel;
    private ModelInstance wallModelInstance;

    public Room(Collection<Box> boxes) {
        view = GameView.gv;
        this.boxes = new ArrayList<Box>(boxes);
        Zone.getZone(calculateMedian()).addObject(this);

        for (Box b: boxes) {
            Zone.getZone(b.getPosition()).addDrawableNoCheck(b);
            if (b.getAdjBoxes().size() < 4)
                outerBoxes.add(b);
        }

        center = calculateMedian();
        genOuterWalls();
        Zone.getZone(center).addDrawableNoCheck(this);
    }

    // calculates the median position of all of the boxes
    private Vector2 calculateMedian() {
        Vector2 center = new Vector2(0, 0);
        for (Box b: boxes) {
            center.add(b.getCenter());
        }
        return new Vector2(center.x / boxes.size(), center.y / boxes.size());
    }

    public void doorsTo(Room room) {
        if (!adjRooms.contains(room)) {
            //TODO further path finding to that room
            return;
        }
    }

    public void currentRoom() {
        load(); // load self
    }

    public void load() {
        for (Box b : boxes)
            b.load();
        for (Wall w: walls)
            w.load();
        loaded = true;
    }

    public void unload() {
        for (Box b: boxes) {
            b.unload();
        }
        for (Wall w: walls)
            w.unload();
        loaded = false;
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

    public void genOuterWalls() {
        // proposedPositions are sets of points where walls could be placed.
        ArrayList<ArrayList<Vector2>> proposedPositions = new ArrayList<ArrayList<Vector2>>();

        // propose positions for each box in the room.
        for (Box b: boxes) {
            proposedPositions.addAll(b.proposeWallPositions());
        }
        
        proposedPositions = consolidateWallPositions(proposedPositions);

        for (ArrayList<Vector2> pstn: proposedPositions) {
            walls.add(new Wall(pstn.get(0), pstn.get(1)));
        }

        buildWallsModel();
    }
    
    // consolidate the proposed walls into as few as possible.
    public ArrayList<ArrayList<Vector2>> consolidateWallPositions(ArrayList<ArrayList<Vector2>> proposedPositions) {

        ArrayList<ArrayList<Vector2>> iteratedPositions = new ArrayList<ArrayList<Vector2>>(proposedPositions);

        for (ArrayList<Vector2> pstn1: iteratedPositions) {
            for (ArrayList<Vector2> pstn2: iteratedPositions) {

                // if the first wall's end meets the other wall's start and they have the same
                // angle...
                if (pstn1.get(1).equals(pstn2.get(0)) && Math.abs(pstn1.get(1).cpy().sub(pstn1.get(0)).angle() - (pstn2.get(1).cpy().sub(pstn2.get(0)).angle())) < 0.0001) {

                    // System.out.println("Walls match up.");
                    // System.out.println("pstn1: " + pstn1);
                    // System.out.println("pstn2: " + pstn2);

                    ArrayList<Vector2> points = new ArrayList<Vector2>();
                    points.add(pstn1.get(0));
                    points.add(pstn2.get(1));

                    proposedPositions.add(points);

                    proposedPositions.remove(pstn1);
                    proposedPositions.remove(pstn2);

                    // keep going until no more matched walls are found.
                    proposedPositions = consolidateWallPositions(proposedPositions);
                    return proposedPositions;
                }
            }
        }

        return proposedPositions;
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
        for (Wall w: walls) {
            if (w != null && w.getBody().getPosition().x == b.getPosition().x && w.getBody().getPosition().y == b.getPosition().y) {
                return w;
            }
        }
        return null;
    }

    public ArrayList<Wall> getWalls() { return walls; }

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

    public Box getRandomBox() {
        if (!boxes.isEmpty()) {
            return boxes.get(random.nextInt(boxes.size()));
        }
        return null;
    }

    private void buildWallsModel() {
        Assets.modelBuilder.begin();
        for (Wall w: walls) {
            w.buildWallMesh(Assets.modelBuilder);
        }
        wallModel = Assets.modelBuilder.end();
        wallModelInstance = new ModelInstance(wallModel);
        wallModelInstance.transform.setTranslation(center.x, center.y, 0);
    }

    public LinkedList<Unit> getAliveUnits() {
        LinkedList<Unit> units = new LinkedList<Unit>();
        for (Box b: boxes) {
            units.addAll((Collection)b.getUnits());
        }
        return units;
    }

    public ArrayList<Box> getOuterBoxes() {
        return outerBoxes;
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        zone = z;
    }

    @Override
    public String className() {
        return "Room";
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        modelBatch.begin(GameView.gv.getCamera());
        modelBatch.render(wallModelInstance, GameView.gv.environment);
        modelBatch.end();
    }
}
