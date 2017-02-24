package com.zombies.map;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.map.room.WallWall;
import com.zombies.util.Geometry;
import com.zombies.C;
import com.zombies.map.room.Wall;
import com.zombies.Zone;

import java.util.HashSet;

public class HallwaySegment extends Overlappable {
    public float diameter, radius;
    private HashSet<Wall> walls = new HashSet<>();

    private Hallway hallway;
    private Vector2 point;

    public HallwaySegment(Hallway h, Vector2 p) {
        hallway  = h;
        point    = p;
        diameter = C.HALLWAY_WIDTH;
        radius   = diameter / 2;
    }

    private void setCorners() {
        corners[0] = new Vector2(position.x + width, position.y + height);
        corners[1] = new Vector2(position.x, position.y + height);
        corners[2] = new Vector2(position.x, position.y);
        corners[3] = new Vector2(position.x + width, position.y);
    }

    public void materialize() {
        HallwaySegment nextHallwaySegment = nextHallwaySegment();
        if (nextHallwaySegment == null)
            return;

        calculateInfo(); // do this a second time
        setCorners();
        createWalls();
        Zone.getZone(getCenter()).addObject(this);
    }

    private void calculateInfo() {
        Vector2 nPos = nextHallwaySegment().point;
        position = new Vector2(
                Math.min(point.x, nPos.x) - radius,
                Math.min(point.y, nPos.y) - radius);

        // calculate width and height
        width = Math.abs(point.x - nPos.x) + diameter;
        height = Math.abs(point.y - nPos.y) + diameter;
    }

    public double[] getAdjAngles() {
        int index = hallway.segments.indexOf(this);

        Vector2 prev = (index == 0 ? hallway.getBox().getCenter() : hallway.segments.get(index - 1).getPoint());
        double  prevAngle = Geometry.getAngle(point, prev);
        if (index == hallway.segments.size() - 1)
            return new double[]{prevAngle, prevAngle - Math.PI};
        return new double[]{prevAngle, Geometry.getAngle(point, hallway.segments.get(index + 1).getPoint())};
    }

    public Vector2[] getCornerPoints() {
        double[] adjAngles = getAdjAngles();
        double leftAngle = ((adjAngles[0] + adjAngles[1]) / 2) % Math.PI;
        double rightAngle = (leftAngle + Math.PI) % Math.PI;
        return new Vector2[]{
                Geometry.projectVector(point, leftAngle, 1),
                Geometry.projectVector(point, rightAngle, 1)};
    }

    public HallwaySegment nextHallwaySegment() {
        int index = hallway.segments.indexOf(this);
        if (index == hallway.segments.size() - 1)
            return null;
        return hallway.segments.get(index + 1);
    }

    private void createWalls() {
        HallwaySegment nextHallwaySegment = nextHallwaySegment();
        if (nextHallwaySegment == null)
            return;

        Vector2[] cornerPoints = getCornerPoints();
        Vector2[] nextCornerPoints = nextHallwaySegment.getCornerPoints();

        walls.add(new WallWall(cornerPoints[0], nextCornerPoints[0], hallway.getModelable()));
        walls.add(new WallWall(cornerPoints[1], nextCornerPoints[1], hallway.getModelable()));
    }

    public Vector2 getPoint() { return point; }

    public void buildWallModels(MeshPartBuilder builder, Vector2 modelCenter) {
        for (Wall wall: walls) {
            wall.genSegmentsFromPoints();
            wall.buildWallMesh(builder, modelCenter);
        }
    }
    public void buildFloorModel(MeshPartBuilder builder, Vector2 modelCenter) {
        if (nextHallwaySegment() == null)
            return;

        Vector2 relp = new Vector2(position.x - modelCenter.x, position.y - modelCenter.y);

        builder.setUVRange(0, 0, width / C.BOX_DIAMETER, height / C.BOX_DIAMETER);
        builder.rect(relp.x, relp.y, -0.1f,
                relp.x + width, relp.y, -0.1f,
                relp.x + width, relp.y + height, -0.1f,
                relp.x, relp.y + height, -0.1f,
                1, 1, 1);
    }

    @Override
    public String className() { return "HallwaySegment"; }
    @Override
    public Vector2[] getCorners() { return corners; }
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
