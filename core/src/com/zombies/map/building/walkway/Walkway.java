package com.zombies.map.building.walkway;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.lib.math.M;
import com.zombies.map.building.Building;

public class Walkway {
    private Building building;

    public Walkway(Building building, int[] key, float angle) {
        this.building = building;

        Vector2 start = building.positionOf(key).add(C.GRID_HALF_SIZE, C.GRID_HALF_SIZE);
        Vector2 end   = M.projectVector(start, angle, C.GRIDSIZE * 4);
        for (int[] k : building.keysOnLine(start, end)) {
            WalkwaySegment.createWalkwaySegment(this, k);
        }
    }

    public Building getBuilding() {
        return building;
    }
}
