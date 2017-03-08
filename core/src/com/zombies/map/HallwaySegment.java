package com.zombies.map;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.interfaces.Gridable;
import com.zombies.map.room.Building;
import com.zombies.map.room.WallWall;
import com.zombies.util.Geometry;
import com.zombies.C;
import com.zombies.map.room.Wall;
import com.zombies.Zone;

import java.util.ArrayList;
import java.util.HashSet;

public class HallwaySegment extends Overlappable implements Gridable {
    public float diameter, radius;
    private HashSet<Wall> walls = new HashSet<>();
    private boolean[] connections = new boolean[4];
    private boolean[] connectionOverride = new boolean[4];
    private float halfWidth, halfHeight;

    private Hallway hallway;
    private Building building;

    private int[] key;
    private String sKey;

    private void setInfo(Hallway h, int[] key) {
        hallway  = h;
        building = h.getBuilding();
        position = building.positionOf(key);
        corners  = building.cornersOf(position);

        diameter   = C.HALLWAY_WIDTH;
        radius     = diameter / 2;
        this.key   = key;
        sKey       = key[0] + "," + key[1];

        halfWidth  = width / 2;
        halfHeight = height / 2;
    }
    public HallwaySegment(Hallway h, int[] key, float width, float height) {
        this.width      = width;
        this.height     = height;
        setInfo(h, key);
    }
    public HallwaySegment(Hallway h, int[] key) {
        width      = C.GRID_SIZE;
        height     = C.GRID_SIZE;
        setInfo(h, key);
    }

    public void compile() {
        HashSet<Gridable> adj = new HashSet<>();
        Gridable g;
        int[][] adjGridKeys = Building.getAdjBMKeys(key);
        for (int i = 0; i < adjGridKeys.length; i++)
            connections[i] = (building.gridMapGet(adjGridKeys[i]) instanceof HallwaySegment);

        Zone.getZone(getCenter()).addObject(this);
    }

    public ArrayList<int[]> getOpenAdjKeys() {
        ArrayList<int[]> adjKeys = new ArrayList<>();
        for (int[] k : Building.getAdjBMKeys(key)) {
            if (building.gridMap.get(k[0] + "," + k[1]) == null)
                adjKeys.add(k);
        }
        return adjKeys;
    }

    @Override
    public void buildWallMesh(MeshPartBuilder builder, Vector2 center) {
        for (Wall w : walls)
            w.buildWallMesh(builder, center);
    }

    // TODO: build rotation into this to reduce redundant code
    private void buildWalls(MeshPartBuilder builder, Vector2 modelCenter) {
        Vector2 center = getCenter(), c;
        // right
        if (connections[0]) {
            // right hallway, top
            c = center.cpy();
            walls.add(new WallWall(c.add(radius, radius), c.cpy().add(halfWidth - radius, 0), building));
            // right hallway, bottom
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, -radius), c.cpy().add(halfWidth - radius, 0), building));
        } else {
            // cap off right side
            c = center.cpy();
            walls.add(new WallWall(c.add(radius, radius), c.cpy().sub(0, diameter), building));
        }

        // top
        if (connections[1]) {
            // top hallway, left
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, radius), c.cpy().add(0, halfHeight - radius), building));
            // top hallway, right
            c = center.cpy();
            walls.add(new WallWall(c.add(radius, radius), c.cpy().add(0, halfHeight - radius), building));
        } else {
            // cap off top side
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, radius), c.cpy().add(diameter, 0), building));
        }

        // left
        if (connections[2]) {
            // left hallway, top
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, radius), c.cpy().sub(halfWidth - radius, 0), building));
            // left hallway, bottom
            c = center.cpy();
            walls.add(new WallWall(c.sub(radius,radius), c.cpy().sub(halfWidth - radius, 0), building));
        } else {
            // cap off left
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, radius), c.cpy().sub(0, diameter), building));
        }

        // bottom
        if (connections[3]) {
            // bottom hallway, left
            c = center.cpy();
            walls.add(new WallWall(c.sub(radius, radius), c.cpy().sub(0, halfHeight - radius), building));
            // bottom hallway, right
            c = center.cpy();
            walls.add(new WallWall(c.add(radius, -radius), c.cpy().sub(0, halfHeight - radius), building));
        } else {
            // capp off bottom
            c = center.cpy();
            walls.add(new WallWall(c.sub(radius, radius), c.cpy().add(diameter, 0), building));
        }

        for (Wall wall : walls) {
            wall.genSegmentsFromPoints();
            wall.buildWallMesh(builder, modelCenter);
        }
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

    @Override
    public void buildFloorMesh(MeshPartBuilder builder, Vector2 center) {
        Vector2 relp = new Vector2(position.x - center.x, position.y - center.y);

        builder.setUVRange(0, 0, width / C.GRID_SIZE, height / C.GRID_SIZE);
        builder.rect(relp.x, relp.y, -0.1f,
                relp.x + width, relp.y, -0.1f,
                relp.x + width, relp.y + height, -0.1f,
                relp.x, relp.y + height, -0.1f,
                1, 1, 1);
    }

    @Override
    public Building getBuilding() {
        return building;
    }

    @Override
    public int[] getKey() {
        return key;
    }
}
