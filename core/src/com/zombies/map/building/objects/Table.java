package com.zombies.map.building.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.zombies.Zone;
import com.zombies.util.Assets;
import com.zombies.util.G;

public class Table {
    public static Model model;
    static {
        model = Assets.a.get("data/models/wood-table.g3dj", Model.class);
        model.nodes.get(0).scale.set(new Vector3(1, 1, .7f));
    }

    private ModelInstance modelInstance;

    public Table(Vector2 p, float angle) {
        modelInstance = new ModelInstance(model);
        modelInstance.transform.setToRotationRad(Vector3.X, (float) G.PIHALF);
        modelInstance.transform.rotateRad(Vector3.Y, angle);
        modelInstance.transform.setTranslation(p.x, p.y, 0);

        Zone.getZone(p).addPendingObject(modelInstance);
    }
}
