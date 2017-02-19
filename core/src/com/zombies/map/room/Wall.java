package com.zombies.map.room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.zombies.BodData;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.interfaces.Collideable;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Modelable;

public class Wall implements Collideable, Loadable {
    private Vector2 p1, p2, center;
    private double angle;
    private Body body;
    private HashMap<Float, Float> holes = new HashMap<Float, Float>();

    private ArrayList<WallPoint> points     = new ArrayList<>();
    private ArrayList<WallSegment> segments = new ArrayList<>();

    private GameView view;
    private Modelable modelable;

    public Wall(Vector2 p1, Vector2 p2, Modelable m) {
        view = GameView.gv;

        HashSet<Zone> zonesOnLine = Zone.zonesOnLine(p1, p2);
        // Do not duplicate walls.
        for (Zone z : zonesOnLine)
            for (Wall w : z.getWalls())
                if (w.similar(p1, p2))
                    return;

        for (Zone z : zonesOnLine)
            z.addWall(this);

        this.p1 = p1;
        this.p2 = p2;
        angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        center = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        modelable = m;

        //set up physics
        EdgeShape shape = new EdgeShape();
        shape.set(new Vector2(0, 0), new Vector2(p1.dst(p2), 0));
        body = view.getWorld().createBody(new BodyDef());
        body.createFixture(shape, 0);
        body.setTransform(p1, (float)angle);
        body.setUserData(new BodData("wall", this));

        points.add(new WallPoint(p1, 1));
        points.add(new WallPoint(p2, 0));

        genSegmentsFromPoints();
    }

    private void genSegmentsFromPoints() {
        segments = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (i == points.size() - 1)
                break;

            segments.add(new WallSegment(
                    points.get(i).getPoint(),
                    points.get(i + 1).getPoint(),
                    points.get(i).getHeight()));
        }
    }

    // Check if two lines are very close
    public boolean similar(Vector2 p1, Vector2 p2) {
        final float dstTolerance = 0.1f;
        if (this.p1.dst(p1) < dstTolerance && this.p2.dst(p2) < dstTolerance)
            return true;
        return false;
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
        segments = new ArrayList<WallSegment>();

        // if holePosition is not on line, this function will
        // swing the vector2 onto the line using p1 as the axis
        holes.put(dst, holeSize);
        consolidateHoles();

        ArrayList<Float> holePositions = new ArrayList<Float>(holes.keySet());

        Collections.sort(holePositions);

        // unit vector in the same direction as the wall.
        Vector2 vo = p2.cpy().sub(p1).scl(1 / p2.cpy().sub(p1).len());
        Vector2 v1, v2;

        // System.out.println("vo: " + vo);

        for (int i = 0; i <= holePositions.size(); i++) {

            // the start and end positions of this wall segment, relative to the wall position.
            v1 = (i == 0 ? new Vector2(0, 0) : vo.cpy().scl(holePositions.get(i - 1) + holes.get(holePositions.get(i - 1)) / 2));
            v2 = (i == holePositions.size() ? p2.cpy().sub(p1) : vo.cpy().scl(holePositions.get(i) - holes.get(holePositions.get(i)) / 2));

            // System.out.println("v1: " + v1);
            // System.out.println("v2: " + v2);

            // create the segment only if it has nonzero length, and is in the same direction as
            // the wall unit vector (second requirement is false if the last/first hole extends past
            // the wall, in which case this seg is not needed).
            if (v2.cpy().sub(v1).len() > 0 && v2.cpy().sub(v1).dot(vo) > 0.0) {
                EdgeShape shape = new EdgeShape();
                shape.set(v1, v2);
                segments.add(new WallSegment(p1.cpy().add(v1), p1.cpy().add(v2), 1));
                body.createFixture(shape, 0);
            }
        }
        modelable.rebuildModel();
    }

    public void buildWallMesh(MeshPartBuilder wallBuilder, Vector2 modelCenter) {
        for (WallSegment ws: segments)
            ws.buildMesh(wallBuilder, modelCenter);
    }

    @Override
    public void handleCollision(Fixture f) {
        if (C.ENABLE_WALL_DESTRUCTION) {
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
