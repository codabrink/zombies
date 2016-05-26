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
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Overlappable;
import com.zombies.Box;
import com.zombies.GameView;
import com.zombies.Wall;
import com.zombies.Zone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Hallway implements com.zombies.interfaces.Drawable, HasZone, com.zombies.interfaces.Modelable {
    public static int MAX_HALLWAY_SEGMENTS = 1;

    ArrayList<Vector2> axes = new ArrayList<Vector2>();
    private Random r;
    private ArrayList<HallwaySegment> hallwaySegments = new ArrayList<HallwaySegment>();
    private Box originBox;
    private Wall originWall;
    private float diameter;
    private double totalAngle = 0;
    private Model model, floorModel;
    private ModelInstance modelInstance, floorModelInstance;
    private Vector2 center;
    private Zone zone;

    public Hallway(Box b, char direction, float width) {
        r = GameView.gv.random;
        originBox = b;
        diameter = width;
        double angle = 0;
        switch(direction) {
            case 'n':
                angle = Math.PI / 2;
                axes.add(new Vector2(horizBoxRange(b, width), b.getPosition().y + b.height));
                break;
            case 'e':
                angle = 0;
                axes.add(new Vector2(b.getPosition().x + b.width, vertBoxRange(b, width)));
                break;
            case 's':
                angle = Math.PI * 1.5;
                axes.add(new Vector2(horizBoxRange(b, width), b.getPosition().y));
                break;
            case 'w':
                angle = Math.PI;
                axes.add(new Vector2(b.getPosition().x, vertBoxRange(b, width)));
                break;
        }
        tryToMove(angle, angle);
    }

    private float hallwayLength() { return r.nextFloat() * 10 + 15; }

    private Vector2 calculateNewAxis(double angle) {
        Vector2 a = axes.get(axes.size() - 1).cpy();
        float length = hallwayLength();
        return a.add((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }

    private void tryToMove(double angle, double previousSegmentAngle) {
        Vector2 newAxis = calculateNewAxis(angle);
        HallwaySegment hs = new HallwaySegment(this, axes.get(axes.size() - 1), newAxis, diameter);
        Overlappable o = Zone.getZone(hs.center).checkOverlap(hs.position, hs.width, hs.height, 1);

        if (o == null)
            o = com.zombies.util.Geometry.checkOverlap(hs.position.x, hs.position.y, hs.width, hs.height, hallwaySegments);

        // I would love to use lambdas here to reduce redundancy, but that's not introduced until Java 8, so... yeah
        if (o != null) {
            if (axes.size() > 1) { // is fist segment
                if (o instanceof Box && o != originBox && !originBox.isAdjacent((Box)o)) { // is not an origin box or adjacent box
                    Vector2 ip = o.intersectPointOfLine(hs.p1, hs.p2); // set to a variable for debugging..
                    hs.p2.set(ip);
                    addHallwaySegment(hs);
                    materialize();
                } else { // collision is an origin box or adjacent box
                    addHallwaySegment(hs);
                }
            } else { // is a second or more segment
                Vector2 ip = o.intersectPointOfLine(hs.p1, hs.p2); // set to a variable for debugging...
                hs.p2.set(ip);
                addHallwaySegment(hs);
                materialize();
            }
         } else { // in the clear, just add the segment
            addHallwaySegment(hs);
        }

        if (hallwaySegments.size() - 1 < MAX_HALLWAY_SEGMENTS) {
            double nextDeltaAngle = 0;
            switch (r.nextInt(2)) {
                case 0: nextDeltaAngle = -Math.PI / 2; break;
                case 1: nextDeltaAngle = 0; break;
                case 2: nextDeltaAngle = Math.PI / 2; break;
            }
            ((HallwaySegment)hallwaySegments.get(hallwaySegments.size() - 1)).setNextSegmentAngle(angle + nextDeltaAngle);
            tryToMove(angle + nextDeltaAngle, angle);
        } else {
            materialize();
        }
    }

    private void addHallwaySegment(HallwaySegment hs) {
        hallwaySegments.add(hs);
        axes.add(hs.getP2());
    }

    private void materialize() {
        center = new Vector2();

        for (Overlappable hs: hallwaySegments) {
            center.add(((HallwaySegment)hs).getCenter());
            ((HallwaySegment)hs).materialize();
        }
        center = new Vector2(center.x / hallwaySegments.size(), center.y / hallwaySegments.size());

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

    public ArrayList<HallwaySegment> getSegments() {
        return hallwaySegments;
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
