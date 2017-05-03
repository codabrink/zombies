package com.zombies.lib;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.zombies.interfaces.ThreadedModelBuilderCallback;

public class ThreadedModelBuilder extends ModelBuilder {
    // This can be run in any thread
    // 1. builds entire model in thread up until .end()
    // 2. calls .end() in main thread and receives model
    // 3. sends model back as parameter to callback given to constructor

    public enum MODELING_STATE { READY, MODELING, FINISHED, DORMANT}
    private ThreadedModelBuilderCallback callback;
    public MODELING_STATE modelingState = MODELING_STATE.DORMANT;

    public boolean setCallback(ThreadedModelBuilderCallback callback) {
        if (modelingState != MODELING_STATE.DORMANT)
            return false;
        this.callback = callback;
        modelingState = MODELING_STATE.READY;
        return true;
    }

    @Override
    public void begin() {
        if (modelingState != MODELING_STATE.READY)
            return;
        modelingState = MODELING_STATE.MODELING;
        super.begin();
    }

    public void finish() {
        modelingState = MODELING_STATE.FINISHED;
    }

    // only call in main thread
    @Override
    public Model end() {
        Model model = super.end();
        callback.response(model);
        modelingState = MODELING_STATE.DORMANT;
        return model;
    }
}
