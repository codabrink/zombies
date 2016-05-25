package com.zombies.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Overlappable;
import com.zombies.util.Geometry;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Wall;
import com.zombies.Zone;

import java.util.ArrayList;
import java.util.LinkedList;

public class HallwaySegment implements Overlappable, Loadable, HasZone {
    private static int DRAWABLE_LAYER = 1;
    public Vector2 p1, p2, position, center;
    private Vector2 w1p1, w1p2, w2p1, w2p2;
    public float diameter, radius, width, height;
    private char direction;
    private Zone zone;
    private LinkedList<Wall> walls = new LinkedList<Wall>();
    private double angle, previousSegmentAngle, nextSegmentAngle = 0; // change in angle from the last hallway segment
    private com.zombies.interfaces.Modelable modelable;

    // only handles modulus 90 degree angles
    public HallwaySegment(Vector2 p1, Vector2 p2, float diameter, double previousSegmentAngle, com.zombies.interfaces.Modelable m) {
        this.p1 = p1;
        this.p2 = p2;
        this.angle = Geometry.getAngleFromPoints(p1, p2);
        this.nextSegmentAngle = this.angle;
        this.previousSegmentAngle = previousSegmentAngle;
        this.diameter = diameter;
        radius = diameter / 2;
        modelable = m;

        calculateInfo();
    }

    public void materialize() {
        calculateInfo(); // do this a second time
        createWalls();
        Zone.getZone(getCenter()).addObject(this);
    }

    private void createWalls() {
        GameView.gv.addDebugDots(p1, Color.GREEN);
        GameView.gv.addDebugDots(p2, Color.RED);

        // p1aa = Point 1 Angle Average
        double p1aa = (previousSegmentAngle + angle) / 2,
                p2aa = (nextSegmentAngle + angle) / 2;

        // Wall 1 is on the left
        // Wall 2 is on the right
        // Point 1 is at the beginning
        // Point 2 is at the end
        double w1p1a = p1aa + Math.PI / 2;
        double w1p2a = p2aa + Math.PI / 2;
        double w2p1a = p1aa - Math.PI / 2;
        double w2p2a = p2aa - Math.PI / 2;

        float p1r = Math.abs(previousSegmentAngle - angle) > 0 ? (float)Math.sqrt(radius*radius+radius*radius) : (float)radius;
        float p2r = Math.abs(nextSegmentAngle - angle) > 0 ? (float)Math.sqrt(radius*radius+radius*radius) : (float)radius;
        w1p1 = new Vector2(p1.cpy().add((float)(p1r * Math.cos(w1p1a)), (float)(p1r * Math.sin(w1p1a)))); // starting point of the wall
        w1p2 = new Vector2(p2.cpy().add((float)(p2r * Math.cos(w1p2a)), (float)(p2r * Math.sin(w1p2a)))); // simply used for calculating the length of the wall
        w2p1 = new Vector2(p1.cpy().add((float)(p1r * Math.cos(w2p1a)), (float)(p1r * Math.sin(w2p1a))));
        w2p2 = new Vector2(p2.cpy().add((float)(p2r * Math.cos(w2p2a)), (float)(p2r * Math.sin(w2p2a))));

        walls.add(new Wall(w1p1, w1p2, modelable));
        walls.add(new Wall(w2p1, w2p2, modelable));
    }

    private void calculateInfo() {
        // calculate position
        if (p1.x < p2.x || p1.y < p2.y) {
            position = new Vector2(p1.x - radius, p1.y - radius);
        } else {
            position = new Vector2(p2.x - radius, p2.y - radius);
        }

        center = position.cpy().add(width / 2, height / 2);

        // calculate width and height
        width = Math.abs(p1.x - p2.x) + diameter;
        height = Math.abs(p1.y - p2.y) + diameter;

        // calculate direction
        if (p1.x < p2.x)
            direction = 'e';
        else if (p1.x > p2.x)
            direction = 'w';
        else if (p1.y < p2.y)
            direction = 'n';
        else if (p1.y > p2.y)
            direction = 's';
    }

    public Vector2 getCenter() {
        return center;
    }

    public Vector2 getP1() {return p1;}
    public Vector2 getP2() {return p2;}
    public LinkedList<Wall> getWalls() { return walls; }

    public void setNextSegmentAngle(double nextSegmentAngle) {this.nextSegmentAngle = nextSegmentAngle;}

    @Override
    public String className() {
        return "HallwaySegment";
    }


    @Override
    public boolean overlaps(float x, float y, float w, float h) {
        return Geometry.rectOverlap(position.x, position.y, width, height, x, y, w, h);
    }

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
    public float edge(char direction) {
        switch(direction) {
            case 'n':
                return position.y + height;
            case 'e':
                return position.x + width;
            case 's':
                return position.y;
            case 'w':
                return position.x;
        }
        return 0;
    }

    @Override
    public float oppositeEdge(char direction) {
        switch(direction) {
            case 'n':
                return edge('s');
            case 'e':
                return edge('w');
            case 's':
                return edge('n');
            case 'w':
                return edge('e');
        }
        return 0;
    }

    @Override
    public Vector2 intersectPointOfLine(Vector2 p1, Vector2 p2) {
        // left line
        Vector2 i = Geometry.intersectPoint(position.x, position.y, position.x, position.y + height, p1.x, p1.y, p2.x, p2.y);
        if (i == null) // top line
            i = Geometry.intersectPoint(position.x, position.y + height, position.x + width, position.y + height, p1.x, p1.y, p2.x, p2.y);
        if (i == null) // right line
            i = Geometry.intersectPoint(position.x + width, position.y + height, position.x + width, position.y, p1.x, p1.y, p2.x, p2.y);
        if (i == null) // bottom line
            i = Geometry.intersectPoint(position.x, position.y, position.x + width, position.y, p1.x, p1.y, p2.x, p2.y);

        return i;
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
