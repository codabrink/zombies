package com.zombies.interfaces;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.map.room.Building;

import java.util.ArrayList;
import java.util.HashMap;

public interface Gridable {
    public void buildWallMesh(Vector2 center);
    public void buildFloorMesh(MeshPartBuilder builder, Vector2 center);
    public Building getBuilding();
    public ArrayList<int[]> getOpenAdjKeys();
    public int[] getKey();
}
