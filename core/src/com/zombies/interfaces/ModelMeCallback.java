package com.zombies.interfaces;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;

public interface ModelMeCallback {
    public void buildModel(MeshPartBuilder builder, Vector2 center);
}
