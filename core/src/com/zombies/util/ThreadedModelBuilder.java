package com.zombies.util;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.zombies.GameView;
import com.zombies.interfaces.ThreadedModelBuilderCallback;

public class ThreadedModelBuilder extends ModelBuilder {
    // This can be run in any thread
    // 1. builds entire model in thread up until .end()
    // 2. calls .end() in main thread and receives model
    // 3. sends model back as parameter to callback given to constructor

    private boolean begun = false;
    private ThreadedModelBuilderCallback callback;

    public ThreadedModelBuilder(ThreadedModelBuilderCallback callback) {
        this.callback = callback;
    }

    @Override
    public void begin() {
        if (begun)
            return;
        begun = true;
        super.begin();
    }

    public void finish() {
        GameView.addEndableBuilder(this);
    }

    // only call in main thread
    @Override
    public Model end() {
        Model model = super.end();
        callback.response(model);
        return model;
    }

    public boolean isBegun() {
        return begun;
    }
}
