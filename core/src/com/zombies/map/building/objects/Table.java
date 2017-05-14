package com.zombies.map.building.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.zombies.Zone;
import com.zombies.interfaces.Loadable;
import com.zombies.lib.Assets;
import com.zombies.lib.Models;
import com.zombies.lib.math.M;

public class Table implements Loadable {
    public static Model model;
    private Zone zone;

    static {
        model = Assets.a.get("data/models/wood-table.g3dj", Model.class);
        model.nodes.get(0).scale.set(new Vector3(1, 1, .7f));
    }

    private ModelInstance modelInstance;

    public Table(Vector2 p, float angle) {
        modelInstance = new ModelInstance(model);
        modelInstance.transform.setToRotationRad(Vector3.X, (float) M.PIHALF);
        modelInstance.transform.rotateRad(Vector3.Y, angle);
        modelInstance.transform.setTranslation(p.x, p.y, 0);

        zone = Zone.getZone(p);
        zone.addPendingObject(this);
    }

    @Override
    public void load() {
        Models.addActiveModel(modelInstance);
    }

    @Override
    public void unload() {
        Models.removeActiveModel(modelInstance);
    }
}
