package com.zombies.map;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.interfaces.IGridable;
import com.zombies.interfaces.Loadable;
import com.zombies.map.building.Building;
import com.zombies.map.building.BuildingGridable;
import com.zombies.map.building.WallWall;
import com.zombies.lib.Assets.MATERIAL;
import com.zombies.lib.math.M;
import com.zombies.C;
import com.zombies.map.building.Wall;
import com.zombies.Zone;

import java.util.ArrayList;
import java.util.HashSet;

public class HallwaySegment extends BuildingGridable implements Loadable {
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
        setCorners(building.cornersOf(position));

        diameter   = C.HALLWAY_WIDTH;
        radius     = diameter / 2;
        this.key   = key;
        sKey       = key[0] + "," + key[1];

        halfWidth  = width / 2;
        halfHeight = height / 2;
    }
    public HallwaySegment(Hallway h, int[] key) {
        super(h.getBuilding(), key);
        width      = C.GRIDSIZE;
        height     = C.GRIDSIZE;
        setInfo(h, key);
    }

    public void compile() {
        HashSet<IGridable> adj = new HashSet<>();
        IGridable g;
        int[][] adjGridKeys = Building.getAdjBMKeys(key);
        for (int i = 0; i < adjGridKeys.length; i++)
            connections[i] = (building.gridMapGet(adjGridKeys[i]) instanceof HallwaySegment);

        Zone.getZone(getCenter()).addPendingObject(this);
    }

    // TODO: build rotation into this to reduce redundant code
    public void buildWallMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        Vector2 center = getCenter(), c;
        // right
        if (connections[0]) {
            // right hallway, top
            c = center.cpy();
            walls.add(new WallWall(c.add(radius, radius), c.cpy().add(halfWidth - radius, 0), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
            // right hallway, bottom
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, -radius), c.cpy().add(halfWidth - radius, 0), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
        } else {
            // cap off right side
            c = center.cpy();
            walls.add(new WallWall(c.add(radius, radius), c.cpy().sub(0, diameter), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
        }

        // top
        if (connections[1]) {
            // top hallway, left
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, radius), c.cpy().add(0, halfHeight - radius), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
            // top hallway, right
            c = center.cpy();
            walls.add(new WallWall(c.add(radius, radius), c.cpy().add(0, halfHeight - radius), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
        } else {
            // cap off top side
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, radius), c.cpy().add(diameter, 0), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
        }

        // left
        if (connections[2]) {
            // left hallway, top
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, radius), c.cpy().sub(halfWidth - radius, 0), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
            // left hallway, bottom
            c = center.cpy();
            walls.add(new WallWall(c.sub(radius,radius), c.cpy().sub(halfWidth - radius, 0), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
        } else {
            // cap off left
            c = center.cpy();
            walls.add(new WallWall(c.add(-radius, radius), c.cpy().sub(0, diameter), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
        }

        // bottom
        if (connections[3]) {
            // bottom hallway, left
            c = center.cpy();
            walls.add(new WallWall(c.sub(radius, radius), c.cpy().sub(0, halfHeight - radius), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
            // bottom hallway, right
            c = center.cpy();
            walls.add(new WallWall(c.add(radius, -radius), c.cpy().sub(0, halfHeight - radius), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
        } else {
            // capp off bottom
            c = center.cpy();
            walls.add(new WallWall(c.sub(radius, radius), c.cpy().add(diameter, 0), MATERIAL.WALL_WHITE_WALLPAPER, MATERIAL.WALL_WHITE_WALLPAPER));
        }

        for (Wall wall : walls) {
            wall.buildRightMesh(builder, modelCenter);
        }
    }

    @Override
    public Vector2[] getCorners() { return corners; }
    @Override
    public boolean contains(float x, float y) { return M.rectContains(x, y, position, width, height); }

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

    public void buildFloorMesh(MeshPartBuilder builder, Vector2 center) {
        Vector2 relp = new Vector2(position.x - center.x, position.y - center.y);

        builder.setUVRange(0, 0, width / C.GRIDSIZE, height / C.GRIDSIZE);
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
