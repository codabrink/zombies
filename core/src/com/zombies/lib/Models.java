package com.zombies.lib;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Disposable;

import java.util.LinkedHashSet;

public class Models implements Disposable {
    private static Models m;
    Environment environment  = new Environment();

    ModelCache inactiveCache = new ModelCache();
    ModelCache activeCache   = new ModelCache();

    public Models() {
        m = this;
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    }

    private LinkedHashSet<ModelInstance> inactiveModels = new LinkedHashSet<>();
    private LinkedHashSet<ModelInstance> activeModels   = new LinkedHashSet<>();

    public static void render(ModelBatch modelBatch) {
        modelBatch.render(m.inactiveCache, m.environment);
        modelBatch.render(m.activeCache, m.environment);
    }

    public static void addInactiveModel(ModelInstance modelInstance, ModelInstance replacing) {
        m.inactiveModels.remove(modelInstance);
        m.addInactiveModel(modelInstance);
    }
    public static void addInactiveModel(ModelInstance modelInstance) {
        m.inactiveModels.add(modelInstance);
        m.rebuildInactiveModelCache();
    }
    public static void removeInactiveModel(ModelInstance modelInstance) {
        m.inactiveModels.remove(modelInstance);
        m.rebuildInactiveModelCache();
    }
    private void rebuildInactiveModelCache() {
        inactiveCache.begin();
        for (ModelInstance mi : inactiveModels)
            inactiveCache.add(mi);
        inactiveCache.end();
    }

    @Override
    public void dispose() {
        inactiveCache.dispose();
        activeCache.dispose();
    }
}
