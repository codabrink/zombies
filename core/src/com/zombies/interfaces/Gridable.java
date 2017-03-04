package com.zombies.interfaces;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;

public interface Gridable {
    public void buildWallMesh(MeshPartBuilder builder, Vector2 center);
    public void buildFloorMesh(MeshPartBuilder builder, Vector2 center);
}
