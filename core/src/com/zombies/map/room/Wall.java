package com.zombies.map.room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.zombies.BodData;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.interfaces.Collideable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.ModelMeCallback;
import com.zombies.interfaces.ZCallback;
import com.zombies.util.Assets.MATERIAL;
import com.zombies.util.G;

public class Wall implements Collideable, Loadable, HasZone {
    private Vector2 p1, p2, center;
    protected double angle;
    private Body body;
    private MATERIAL leftMaterial, rightMaterial;
    private Zone zone;

    protected LinkedList<WallPoint>   points   = new LinkedList<>();
    protected LinkedList<WallSegment> rightSegments, leftSegments;

    private ModelMeCallback modelLeft = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildRightMesh(builder, center);
        }
    };
    private ModelMeCallback modelRight = new ModelMeCallback() {
        @Override
        public void buildModel(MeshPartBuilder builder, Vector2 center) {
            buildLeftMesh(builder, center);
        }
    };

    public Wall(Vector2 p1, Vector2 p2, MATERIAL leftMaterial, MATERIAL rightMaterial) {
        this.p1 = p1;
        this.p2 = p2;

        this.leftMaterial = leftMaterial;
        this.rightMaterial = rightMaterial;

        angle = G.getAngle(p1, p2);
        center = G.center(p1, p2);

        zone = Zone.getZone(center);
    }

    public void compile() {
        genSegmentsFromPoints();
        zone.addModelingCallback(leftMaterial, modelLeft);
        zone.addModelingCallback(rightMaterial, modelRight);
    }

    private void flushPoints() {
        if (points.size() < 2)
            return;

        Iterator<WallPoint> itr = points.iterator();
        WallPoint prevPoint = itr.next(), currPoint;

        while (itr.hasNext()) {
            currPoint = itr.next();
            if (currPoint.point.dst(p1) < prevPoint.point.dst(p1))
                itr.remove();
            else
                prevPoint = currPoint;
        }
    }

    public void genSegmentsFromPoints() {
        if (body != null)
            D.world.destroyBody(body);

        flushPoints();

        rightSegments = new LinkedList<>();
        leftSegments  = new LinkedList<>();
        Iterator<WallPoint> itr = points.iterator();
        WallPoint prevPoint = itr.next(), currPoint;

        while (itr.hasNext()) {
            currPoint = itr.next();
            rightSegments.add(new WallSegment(
                    prevPoint.point,
                    currPoint.point,
                    prevPoint.height,
                    rightMaterial));
            leftSegments.add(new WallSegment(
                    currPoint.point,
                    prevPoint.point,
                    prevPoint.height,
                    leftMaterial));
            prevPoint = currPoint;
        }

        final Wall wall = this;
        GameView.gv.addCallback(new ZCallback() {
            @Override
            public void call() {
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                body = D.world.createBody(bodyDef);
                body.setTransform(p1, (float)angle);
                body.setUserData(new BodData("wall", wall));

                for (WallSegment ws : rightSegments)
                    ws.genShapes(body);
            }
        });
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

    public void buildLeftMesh(MeshPartBuilder buidler, Vector2 center) {
        for (WallSegment ws : leftSegments)
            ws.buildMesh(buidler, center);
    }

    public void buildRightMesh(MeshPartBuilder builder, Vector2 center) {
        for (WallSegment ws : rightSegments)
            ws.buildMesh(builder, center);
    }

    public void dispose() {

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

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {}
}
