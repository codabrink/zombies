package com.zombies.map.building.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.zombies.Zone;
import com.zombies.map.building.Box;
import com.zombies.lib.Assets;
import com.zombies.lib.math.M;

public class Fridge {
    public static Model model;
    static {
        model = Assets.a.get("data/models/fridge.g3dj", Model.class);
        model.nodes.get(0).scale.set(new Vector3(3, 3, 1));
    }

    private ModelInstance modelInstance;

    public Fridge(Box b) {
        Vector2 p = b.getCenter();

        modelInstance = new ModelInstance(model);
        modelInstance.transform.setToRotationRad(Vector3.X, (float) M.PIHALF);
        modelInstance.transform.rotateRad(Vector3.Y, (float) -M.PIHALF);

        modelInstance.transform.setTranslation(p.x, p.y, 0);

        Zone.getZone(p).addPendingObject(modelInstance);
    }
}
