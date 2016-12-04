package com.zombies.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Modelable;
import com.zombies.interfaces.Overlappable;
import com.zombies.util.Geometry;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Wall;
import com.zombies.Zone;

import java.util.ArrayList;
import java.util.LinkedList;

public class HallwaySegment implements Overlappable, Loadable, HasZone {
    private ArrayList<Vector2> corners = new ArrayList<>();
    private static int DRAWABLE_LAYER = 1;
    public Vector2 position, center;
    private Vector2 w1p1, w1p2, w2p1, w2p2;
    public float diameter, radius, width, height;
    private Zone zone;
    private LinkedList<Wall> walls = new LinkedList<Wall>();
    private HallwayAxis pAxis, axis, nAxis;
    private com.zombies.interfaces.Modelable modelable;

    public HallwaySegment(HallwayAxis pAxis, HallwayAxis axis, HallwayAxis nAxis, float diameter, Modelable m) {
        this.pAxis = pAxis;
        this.axis  = axis;
        this.nAxis = nAxis;
        this.diameter = diameter;
        this.radius   = diameter / 2;
        this.modelable = m;

        calculateInfo();
    }

    private void setCorners() {
        corners.add(new Vector2(position.x + width, position.y + height));
        corners.add(new Vector2(position.x, position.y + height));
        corners.add(new Vector2(position.x, position.y));
        corners.add(new Vector2(position.x + width, position.y));
    }

    public void materialize() {
        calculateInfo(); // do this a second time
        setCorners();
        createWalls();
        Zone.getZone(getCenter()).addObject(this);
    }

    private void createWalls() {
        // p1aa = Point 1 Angle Average
        double p1aa = (pAxis.theta + axis.theta) / 2,
                p2aa = (axis.theta + nAxis.theta) / 2;

        // Wall 1 is on the left
        // Wall 2 is on the right
        // Point 1 is at the beginning
        // Point 2 is at the end
        double w1p1a = p1aa + Math.PI / 2;
        double w1p2a = p2aa + Math.PI / 2;
        double w2p1a = p1aa - Math.PI / 2;
        double w2p2a = p2aa - Math.PI / 2;

        float p1r = Math.abs(pAxis.theta - axis.theta) > 0 ? (float)Math.sqrt(radius*radius+radius*radius) : (float)radius;
        float p2r = Math.abs(pAxis.theta - axis.theta) > 0 ? (float)Math.sqrt(radius*radius+radius*radius) : (float)radius;
        w1p1 = new Vector2(axis.point.cpy().add((float)(p1r * Math.cos(w1p1a)), (float)(p1r * Math.sin(w1p1a)))); // starting point of the wall
        w1p2 = new Vector2(nAxis.point.cpy().add((float)(p2r * Math.cos(w1p2a)), (float)(p2r * Math.sin(w1p2a)))); // simply used for calculating the length of the wall
        w2p1 = new Vector2(axis.point.cpy().add((float)(p1r * Math.cos(w2p1a)), (float)(p1r * Math.sin(w2p1a))));
        w2p2 = new Vector2(nAxis.point.cpy().add((float)(p2r * Math.cos(w2p2a)), (float)(p2r * Math.sin(w2p2a))));

        walls.add(new Wall(w1p1, w1p2, modelable));
        walls.add(new Wall(w2p1, w2p2, modelable));

        if (C.DEBUG) {
            GameView.gv.addDebugDots(axis.point, Color.GREEN);
            GameView.gv.addDebugDots(nAxis.point, Color.RED);
        }
    }

    private void calculateInfo() {
        position = new Vector2(
                Math.min(axis.point.x, nAxis.point.x) - radius,
                Math.min(axis.point.y, nAxis.point.y) - radius);

        // calculate width and height
        width = Math.abs(axis.point.x - nAxis.point.x) + diameter;
        height = Math.abs(axis.point.y - nAxis.point.y) + diameter;

        center = position.cpy().add(width / 2, height / 2);
    }

    public Vector2 getCenter() {
        return center;
    }

    public Vector2 getP1() {return axis.point;}
    public Vector2 getP2() {return nAxis.point;}
    public LinkedList<Wall> getWalls() { return walls; }

    public void buildWallMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        for (Wall wall: walls)
            wall.buildWallMesh(builder, modelCenter);
    }

    public void buildFloorMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        Vector2 relp = new Vector2(position.x - modelCenter.x, position.y - modelCenter.y);

        builder.setUVRange(0, 0, width / C.BOX_SIZE, height / C.BOX_SIZE);
        builder.rect(relp.x, relp.y, -0.1f,
                relp.x + width, relp.y, -0.1f,
                relp.x + width, relp.y + height, -0.1f,
                relp.x, relp.y + height, -0.1f,
                1, 1, 1);
    }

    @Override
    public String className() { return "HallwaySegment"; }
    @Override
    public ArrayList<Vector2> getCorners() { return corners; }
    @Override
    public boolean overlaps(float x, float y, float w, float h) {
        return Geometry.rectOverlap(x, y, w, h, position.x, position.y, width, height);
    }
    @Override
    public boolean contains(float x, float y) { return Geometry.rectContains(x, y, position, width, height); }
    @Override
    public float edge(int direction) {
        switch(direction) {
            case 0:
                return position.x + width;
            case 90:
                return position.y + height;
            case 180:
                return position.x;
            case 270:
                return position.y;
        }
        throw new  IllegalArgumentException();
    }

    @Override
    public float oppositeEdge(int direction) {
        return edge((direction + 180) % 360);
    }

    @Override
    public Vector2 intersectPointOfLine(Vector2 p1, Vector2 p2) { return Geometry.edgeIntersection(p1, p2, this); }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void load() {
        for (Wall w: walls)
            w.load();
    }
    @Override
    public void unload() {
        for (Wall w: walls)
            w.unload();
    }
    @Override
    public Zone getZone() {
        return zone;
    }
    @Override
    public void setZone(Zone z) {
        zone = z;
    }
}
