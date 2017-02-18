package com.zombies.map.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Unit;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Modelable;
import com.zombies.util.Assets;

public class Room implements Loadable, HasZone, Drawable, Modelable {
    public  HashSet<Box> boxes = new HashSet<>();
    private boolean finalized = false;
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private HashSet<DoorContainer> doorContainers = new HashSet<>();
    private Random random = new Random();
    private boolean alarmed = false;
    private Zone zone;
    private Vector2 center;

    private Building building;
    private HashMap<String, ArrayList<Box[]>> doors = new HashMap<>();

    private Model wallModel, floorModel;
    private ModelInstance wallModelInstance, floorModelInstance;

    public Room(Building building) {
        this.building = building;
    }

    public void finalize() {
        center = calculateMedian();
        zone = Zone.getZone(center);
        zone.addObject(this);

        for (Box b: boxes)
            b.setRoom(this);

        building.associateBoxes();

        buildFloorModel();
        rasterizeWalls();
        handleZoning();

        building.refresh();

        finalized = true;
    }

    private void handleZoning() {
        HashSet<Zone> zones = new HashSet<>();
        for (Box b : getBoxes())
            for (Vector2 v : b.getCorners())
                zones.add(Zone.getZone(v).addObject(b));
        for (Zone z : zones)
            z.addObject(this);
    }

    // calculates the median position of all of the boxes
    private Vector2 calculateMedian() {
        Vector2 center = new Vector2(0, 0);
        for (Box b: boxes)
            center.add(b.getCenter());
        return new Vector2(center.x / boxes.size(), center.y / boxes.size());
    }

    public void currentRoom() {
        load(); // load self
    }

    public void load() {
        for (Box b : boxes)
            b.load();
        for (Wall w: walls)
            w.load();
    }

    public void unload() {
        for (Box b: boxes) {
            b.unload();
        }
        for (Wall w: walls)
            w.unload();
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

    public void rasterizeWalls() {
        // proposedPositions are sets of points where walls could be placed.
        ArrayList<Vector2[]> proposedPositions = new ArrayList<>();

        // propose positions for each box in the room.
        for (Box b: boxes) {
            proposedPositions.addAll(b.proposeWallPositions());
        }
        
        proposedPositions = consolidateWallPositions(proposedPositions);

        for (Vector2[] pstn: proposedPositions) {
            walls.add(new Wall(pstn[0], pstn[1], this));
        }

        buildWallModel();
    }
    
    // consolidate the proposed walls into as few as possible.
    public ArrayList<Vector2[]> consolidateWallPositions(ArrayList<Vector2[]> proposedPositions) {

        ArrayList<Vector2[]> iteratedPositions = new ArrayList<>(proposedPositions);

        for (Vector2[] pstn1: iteratedPositions) {
            for (Vector2[] pstn2: iteratedPositions) {

                // if the first wall's end meets the other wall's start and they have the same
                // angle...
                if (pstn1[1].equals(pstn2[0]) && Math.abs(pstn1[1].cpy().sub(pstn1[0]).angle() - (pstn2[1].cpy().sub(pstn2[0]).angle())) < 0.0001) {
                    Vector2[] points = new Vector2[2];
                    points[0] = pstn1[0];
                    points[1] = pstn2[1];

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
    public HashSet<Box> getBoxes() {
        return boxes;
    }
    public Building getBuilding() { return building; }

    public void buildWallModel() {
        Assets.modelBuilder.begin();
        MeshPartBuilder wallBuilder = Assets.modelBuilder.part("Walls",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        for (Wall w: walls)
            w.buildWallMesh(wallBuilder, center);
        MeshPartBuilder frameBuilder = Assets.modelBuilder.part("DoorFrames",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)));
        for (DoorContainer dc : doorContainers)
            dc.getDoorFrame().buildMesh(frameBuilder, center);
        wallModel = Assets.modelBuilder.end();
        wallModelInstance = new ModelInstance(wallModel);
        wallModelInstance.transform.setTranslation(center.x, center.y, 0);
    }

    public void buildFloorModel() {
        Assets.modelBuilder.begin();
        MeshPartBuilder floorBuilder = Assets.modelBuilder.part("floor",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(Assets.floor1Diffuse));
        for (Box b: boxes) {
            b.buildFloorMesh(floorBuilder, center);
        }
        floorModel = Assets.modelBuilder.end();
        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setTranslation(center.x, center.y, 1);
    }

    public HashSet<Box> getOuterBoxes() {
        HashSet<Box> outerBoxes = new HashSet<>();
        // TODO: expensive
        for (Box b : boxes) {
            if (b.getOpenAdjKeys().size() > 0)
                outerBoxes.add(b);
        }
        return outerBoxes;
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        // Zone is set in the constructor
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        modelBatch.begin(GameView.gv.getCamera());
        modelBatch.render(floorModelInstance, GameView.environment);
        modelBatch.render(wallModelInstance, GameView.environment);
        modelBatch.end();

        if (C.DEBUG) {
            BitmapFont f = Zombies.getFont("sans-reg:8:white");
            String s = "";

            spriteBatch.begin();
            for (Box b : boxes) {
                if (C.DEBUG_SHOW_BOXMAP)
                    //s = b.getBMLocation();
                if (C.DEBUG_SHOW_ADJBOXCOUNT)
                    s = b.getAdjBoxes().size() + "";
                f.draw(spriteBatch, s, b.getPosition().x + C.BOX_DIAMETER / 2, b.getPosition().y + C.BOX_DIAMETER / 2);
            }
            spriteBatch.end();
        }
    }

    @Override
    public void rebuildModel() {
        buildWallModel();
    }
}
