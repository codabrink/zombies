package com.zombies.map.building;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.lib.math.M;

import java.util.LinkedList;

public class BuildingGridable extends Overlappable {
    protected Building building;

    protected String sKey;
    protected int[]  key;

    protected Vector2 position;

    public BuildingGridable(Building building, int[] key) {
        super(building.cornersOf(key));
        this.building = building;
        this.key      = key;
        this.sKey     = key[0]+","+key[1];

        position = building.positionOf(key);

        building.putGridMap(key, this);
    }

    @Override
    public boolean contains(Vector2 p) {
        return M.inRangeInclusive(p.x, position.x, position.x + C.GRIDSIZE) &&
                M.inRangeInclusive(p.y, position.y, position.y + C.GRIDSIZE);
    }

    public Building getBuilding() {
        return building;
    }
    public LinkedList<int[]> getOpenAdjKeys() {
        return building.getOpenAdjKeys(key);
    }
    public int[] getKey() {
        return key;
    }
}
