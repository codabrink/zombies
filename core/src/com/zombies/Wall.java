package com.zombies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.interfaces.Collideable;
import com.interfaces.Loadable;
import com.interfaces.Modelable;
import com.util.Geometry;

public class Wall implements Collideable, Loadable {
    private Vector2 p1, p2, center;
    private double angle;
    private Body body;
    private HashMap<Float, Float> holes = new HashMap<Float, Float>();
    private ArrayList<DrawLine> lines;
    private GameView view;
    private Modelable modelable;

    public Wall(Vector2 p1, Vector2 p2, Modelable m) {
        view = GameView.gv;
        this.p1 = p1;
        this.p2 = p2;
        center = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        lines = new ArrayList<DrawLine>();
        angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        modelable = m;

        //set up physics
        EdgeShape shape = new EdgeShape();
        shape.set(new Vector2(0, 0), new Vector2(p1.dst(p2), 0));
        body = view.getWorld().createBody(new BodyDef());
        body.createFixture(shape, 0);
        body.setTransform(p1, (float)angle);
        body.setUserData(new BodData("wall", this));

        lines.add(new DrawLine(p1, p2));
        //Zone.getZone((p1.x + p2.x) / 2, (p1.y + p2.y) / 2).addDrawableNoCheck(this, 1);
    }

    public Double getAngle() { return angle; }
    public Vector2 getStart() { return p1; }
    public Vector2 getEnd() { return p2; }

    public Body getBody() {return body;}

    public void consolidateHoles() {
        ArrayList<Float> holePositions = new ArrayList<Float>(holes.keySet());
        Collections.sort(holePositions);
        for (int i = 0; i < holePositions.size() - 1; i++) {
            Float holePosition, nextHolePosition, holeRadius, nextHoleRadius;
            holePosition = holePositions.get(i);
            holeRadius = holes.get(holePosition) / 2;
            nextHolePosition = holePositions.get(i + 1);
            nextHoleRadius = holes.get(nextHolePosition) / 2;

            if (holePosition + holeRadius > nextHolePosition - nextHoleRadius) {
                float newHolePosition, newHoleSize;
                newHolePosition = ((nextHolePosition + nextHoleRadius) + (holePosition - holeRadius)) / 2;
                newHoleSize = ((nextHolePosition + nextHoleRadius) - (holePosition - holeRadius));

                // consolidate the two holes
                holes.remove(holePosition);
                holes.remove(nextHolePosition);
                holes.put(newHolePosition, newHoleSize);

                consolidateHoles(); // rinse and repeat
                return;
            }
        }
    }

    public void createHole(Vector2 holePoint, float holeSize) {
        float dst = p1.dst(holePoint);
        if (dst > p1.dst(p2))
            return; // this is beyond the scope of the wall

        view.getWorld().destroyBody(body);
        body = view.getWorld().createBody(new BodyDef());
        body.setTransform(p1, body.getAngle());
        body.setUserData(new BodData("wall", this));
        lines = new ArrayList<DrawLine>();

        // if holePosition is not on line, this function will
        // swing the vector2 onto the line using p1 as the axis
        holes.put(dst, holeSize);
        consolidateHoles();

        ArrayList<Float> holePositions = new ArrayList<Float>(holes.keySet());

        Collections.sort(holePositions);
        Vector2 v1, v2;
        float length;

        for (int i = 0; i < holePositions.size(); i++) {
            // v1 describes the wall segment before this hole.
            // the position of the end of the last hole (or the start of the wall if there isn't one),
            // the distance between the start of the last hole and the beginning of this one, the wall angle.
            float holePosition = holePositions.get(i);
            float holeDiameter = holes.get(holePosition), holeRadius = holeDiameter / 2;

            if (i == 0) {
                v1 = new Vector2(0, 0);
                length = Math.max(holePosition - holeRadius, 0);
            } else {
                float previousHolePosition = holePositions.get(i - 1);
                float previousHoleDiameter = holes.get(previousHolePosition), previousHoleRadius = previousHoleDiameter / 2;

                v1 = new Vector2((float)((previousHolePosition + previousHoleRadius) * Math.cos(angle)),
                        (float)((previousHolePosition + previousHoleRadius) * Math.sin(angle)));
                length = Math.max((holePosition - holeRadius) - (previousHolePosition + previousHoleRadius), 0);
            }

            if (length > 0) {
                EdgeShape shape = new EdgeShape();
                shape.set(v1, Geometry.projectVector(v1, angle, length));
                body.createFixture(shape, 0);
                v1.add(body.getPosition());
                lines.add(new DrawLine(v1, Geometry.projectVector(v1, angle, length)));
            }

            // if this is the last hole in the wall, draw the wall segment after it too.
            if (i == holePositions.size() - 1) {
                // v2 describes the wall segment after this wall.
                v2 = new Vector2((float)((holePosition + holeDiameter) * Math.cos(angle)),
                        (float)((holePosition + holeDiameter) * Math.sin(angle)));
                length = Math.max(p1.dst(p2) - (holePosition + holeDiameter), 0);

                if (length > 0) {
                    EdgeShape shape2 = new EdgeShape();
                    shape2.set(v2, Geometry.projectVector(v2, angle, length));
                    body.createFixture(shape2, 0);
                    v2.add(body.getPosition());
                    lines.add(new DrawLine(v2, Geometry.projectVector(v2, angle, length)));
                }
            }
        }
        modelable.rebuildModel();
    }

    public void buildWallMesh(MeshPartBuilder wallBuilder, Vector2 modelCenter) {
        for (DrawLine dl: lines)
            dl.buildMesh(wallBuilder, modelCenter);
    }

    @Override
    public void handleCollision(Fixture f) {
        if (C.ENABLE_WALL_DESRUCTION) {
            //createHole(f.getBody().getPosition(), 5f);
        }
    }

    @Override
    public void load() {
        body.setActive(true);
    }

    @Override
    public void unload() {
        body.setActive(false);
    }
}
