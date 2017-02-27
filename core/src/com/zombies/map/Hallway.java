package com.zombies.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Modelable;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.WallDoor;
import com.zombies.util.Geometry;

import java.util.ArrayList;
import java.util.Random;

public class Hallway implements Drawable, HasZone {
    public ArrayList<HallwaySegment> segments = new ArrayList<>();
    private Random r;
    private Box box;

    private float diameter;
    private Model model;
    private ModelInstance modelInstance;
    private Vector2 center;
    private Zone zone;

    public Hallway(Box b, int[] key) {
        r = GameView.gv.random;
        box = b;

        // create a door
        String wallKey = Building.wallKeyBetweenBoxes(b.getKey(), key);
        Vector2[] wallPosition = b.getBuilding().wallPositionOf(wallKey);
        b.getBuilding().putWallMap(wallKey, new WallDoor(wallPosition[0], wallPosition[1], b.getBuilding()));

        // add an initial segment
        Vector2 p = b.getPosition().cpy();
        p.add((key[1] != b.getKey()[1] ? C.BOX_RADIUS : 0),
                (key[0] != b.getKey()[0] ? C.BOX_RADIUS : 0));
        p.add((key[0] > b.getKey()[0] ? C.BOX_DIAMETER : 0),
                (key[1] > b.getKey()[1] ? C.BOX_DIAMETER : 0));

        segments.add(new HallwaySegment(this, p));

        double angle = (Geometry.getAngle(p, b.getCenter()) + Math.PI) % Math.PI;
        Vector2 p2 = Geometry.projectVector(p, angle, C.BOX_DIAMETER);
        segments.add(new HallwaySegment(this, p2));

        for (HallwaySegment s : segments)
            s.materialize();

        b.getBuilding().getHallways().add(this);

        b.getBuilding().rebuildModel();
    }

    public Modelable getModelable() {
        return box.getBuilding();
    }

    public Box getBox() { return box; }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        modelBatch.begin(GameView.gv.getCamera());
        modelBatch.render(modelInstance, GameView.environment);
        modelBatch.end();
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        zone = z;
    }

    public void rebuildModel(Vector2 modelCenter) {
        MeshPartBuilder builder = com.zombies.util.Assets.modelBuilder.part("floor",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(com.zombies.util.Assets.floor1Diffuse));
        for (HallwaySegment s : segments)
            s.buildFloorMesh(builder, modelCenter);

        builder = com.zombies.util.Assets.modelBuilder.part("walls",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        for (HallwaySegment s : segments)
            s.buildWallModels(builder, modelCenter);
    }
}
