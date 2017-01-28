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
import com.zombies.abstract_classes.Overlappable;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Modelable;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.data.join.JoinOverlappableOverlappable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Hallway implements Drawable, HasZone, Modelable {
    public static int MAX_HALLWAY_SEGMENTS = 2;

    ArrayList<HallwayAxis> axes = new ArrayList<>();
    private Random r;
    private ArrayList<Overlappable> hallwaySegments = new ArrayList<>();

    private Box originBox;
    private Overlappable endOverlappable;

    private float diameter;
    private Model model;
    private ModelInstance modelInstance;
    private Vector2 center;
    private Zone zone;

    public HashSet<JoinOverlappableOverlappable> joinOverlappableOverlappables = new HashSet<>();

    public Hallway(Box b, int direction, float width) {
        r = GameView.gv.random;
        originBox = b;
        diameter = width;

        double theta = Math.toRadians(direction);

        axes.add(
                new HallwayAxis(
                        theta,
                        new Vector2(
                                (float)(b.getCenter().x + C.BOX_DIAMETER / 2 * Math.cos(theta)),
                                (float)(b.getCenter().y + C.BOX_DIAMETER / 2 * Math.sin(theta)))));
        move(theta);
    }

    private float hallwayLength() { return C.BOX_DIAMETER - 0.001f; }

    private Vector2 calculateNewAxis(double angle) {
        Vector2 axis = axes.get(axes.size() - 1).point.cpy();
        float length = hallwayLength();
        return axis.add((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }

    private void move(double theta) {
        HallwayAxis lastAxis = axes.get(axes.size() - 1);
        Vector2 newPoint = new Vector2(
                (float)(lastAxis.point.x + hallwayLength() * Math.cos(theta)),
                (float)(lastAxis.point.y + hallwayLength() * Math.sin(theta)));

        HashSet<Overlappable> overlappables = Zone.getZone(newPoint).checkOverlap(newPoint.x, newPoint.y, 1);
        for (Overlappable o : overlappables) {
            Vector2 p = o.intersectPointOfLine(lastAxis.point, newPoint);
            if (p != null) {
                newPoint = p;
                endOverlappable = o;
            }
        }

        axes.add(
                new HallwayAxis(
                        theta,
                        newPoint));

        materialize();
    }

    private void materialize() {
        center = new Vector2();



        for (int i = 0; i < axes.size() - 1; i++) {
            hallwaySegments.add(new HallwaySegment(
                    axes.get(Math.max(i - 1, 0)),
                    axes.get(i),
                    axes.get(Math.min(i + 1, axes.size() - 1)),
                    diameter,
                    this));
        }

        new JoinOverlappableOverlappable(axes.get(0).point, (Overlappable)originBox, (Overlappable)hallwaySegments.get(0));

        for (Overlappable hs: hallwaySegments) {
            center.add(hs.getCenter());
            ((HallwaySegment)hs).materialize();
        }
        center = new Vector2(center.x / hallwaySegments.size(), center.y / hallwaySegments.size());

        Zone.createHole(axes.get(0).point, diameter / 2);
        Zone.createHole(axes.get(axes.size()-1).point, diameter / 2);

        buildModel();
        Zone.getZone(center).addObject(this);
    }

    public void buildModel() {
        com.zombies.util.Assets.modelBuilder.begin();
        MeshPartBuilder builder = com.zombies.util.Assets.modelBuilder.part("walls",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        for (Overlappable hs: hallwaySegments) {
            ((HallwaySegment)hs).buildWallMesh(builder, center);
        }
        builder = com.zombies.util.Assets.modelBuilder.part("floor",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(com.zombies.util.Assets.floor1Diffuse));
        for (Overlappable hs: hallwaySegments) {
            ((HallwaySegment)hs).buildFloorMesh(builder, center);
        }

        model = com.zombies.util.Assets.modelBuilder.end();
        modelInstance = new ModelInstance(model);
        modelInstance.transform.setTranslation(center.x, center.y, 0);
    }

    private Zone originBoxZone() {
        return Zone.getZone(originBox.getPosition());
    }

    private float horizBoxRange(Box b, float width) {
        return b.getPosition().x + r.nextFloat() * (b.width - width) + width / 2;
    }
    private float vertBoxRange(Box b, float width) {
        return b.getPosition().y + r.nextFloat() * (b.height - width) + width / 2;
    }

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

    @Override
    public void rebuildModel() {
        buildModel();
    }
}
