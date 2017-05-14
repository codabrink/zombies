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

    private final boolean cacheActive = false;

    public Models() {
        m = this;
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    }

    private LinkedHashSet<ModelInstance> inactiveModels = new LinkedHashSet<>();
    private LinkedHashSet<ModelInstance> activeModels   = new LinkedHashSet<>();
    private boolean rebuildInactive = false;
    private boolean rebuildActive   = false;

    public static void render(ModelBatch modelBatch) {
        if (m.cacheActive) {
            m.rebuildActiveModelCache();
            modelBatch.render(m.activeCache, m.environment);
        } else {
            if (m.rebuildActive) m.rebuildActiveModelCache();
            for (ModelInstance mi : m.activeModels)
                modelBatch.render(mi, m.environment);
        }
        if (m.rebuildInactive) m.rebuildInactiveModelCache();
        modelBatch.render(m.inactiveCache, m.environment);
    }

    // ACTIVE
    public static void addActiveModel(ModelInstance modelInstance, ModelInstance replacing) {
        m.activeModels.remove(replacing);
        m.addActiveModel(modelInstance);
    }
    public static void addActiveModel(ModelInstance modelInstance) {
        m.activeModels.add(modelInstance);
        m.rebuildActive = true;
    }
    public static void removeActiveModel(ModelInstance modelInstance) {
        m.activeModels.remove(modelInstance);
        m.rebuildActive = true;
    }
    private void rebuildActiveModelCache() {
        rebuildActive = false;
        activeCache.begin();
        for (ModelInstance mi : activeModels)
            activeCache.add(mi);
        activeCache.end();
    }

    // INACTIVE
    public static void addInactiveModel(ModelInstance modelInstance, ModelInstance replacing) {
        m.inactiveModels.remove(replacing);
        m.addInactiveModel(modelInstance);
    }
    public static void addInactiveModel(ModelInstance modelInstance) {
        m.inactiveModels.add(modelInstance);
        m.rebuildInactive = true;
    }
    public static void removeInactiveModel(ModelInstance modelInstance) {
        m.inactiveModels.remove(modelInstance);
        m.rebuildInactive = true;
    }
    private void rebuildInactiveModelCache() {
        rebuildInactive = false;
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
