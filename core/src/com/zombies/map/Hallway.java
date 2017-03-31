package com.zombies.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.DoorWall;

import java.util.ArrayList;
import java.util.HashSet;

public class Hallway implements Drawable, HasZone {
    public ArrayList<HallwaySegment> segments = new ArrayList<>();
    private Box box;

    public Building start, end;

    private float diameter;
    private Model model;
    private ModelInstance modelInstance;
    private Vector2 center;
    private Zone zone;
    private Building building;
    int[] firstKey;

    public Hallway(Box b, int[] key) {
        box      = b;
        building = box.getBuilding();
        firstKey = key;

        start = b.getBuilding();
        if (start.checkOverlap(firstKey) != null)
            return;
        start.getHallways().add(this);

        HallwaySegment segment = new HallwaySegment(this, key);
        b.getBuilding().gridMapPut(key, segment);
        segments.add(segment);
    }

    public void compile() {
        // create a door
        String wallKey = Building.wallKeyBetweenGridables(box.getKey(), firstKey);
        Vector2[] wallPosition = box.getBuilding().wallPositionOf(wallKey);

        for (HallwaySegment hs : segments)
            hs.compile();
    }

    public HashSet<HallwaySegment> getOuterSegments() {
        HashSet<HallwaySegment> outerSegments = new HashSet<>();
        // TODO: expensive
        for (HallwaySegment segment : segments) {
            if (segment.getOpenAdjKeys().size() > 0)
                outerSegments.add(segment);
        }
        return outerSegments;
    }

    public Box getBox() { return box; }
    public Building getBuilding() { return building; }

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
}
