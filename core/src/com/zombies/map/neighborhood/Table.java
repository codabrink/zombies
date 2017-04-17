package com.zombies.map.neighborhood;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.zombies.Zone;
import com.zombies.util.Assets;

public class Table {
    public static Model model;
    static {
        model = Assets.loader.loadModel(Gdx.files.internal("data/models/table.g3db"));
    }

    private ModelInstance modelInstance;

    public Table(Vector2 p, float angle) {
        modelInstance = new ModelInstance(model);
        modelInstance.transform.setTranslation(p.x, p.y, 1);
        modelInstance.transform.setToRotation(Vector3.Z, angle);

        Zone.getZone(p).addPendingObject(modelInstance);
    }
}
