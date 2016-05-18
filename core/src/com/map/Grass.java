package com.map;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.interfaces.Drawable;
import com.interfaces.HasZone;
import com.util.Assets;
import com.zombies.GameView;
import com.zombies.Zone;

public class Grass implements Drawable, HasZone {
    private Model model;
    private ModelInstance modelInstance;
    private float width, height;
    private Vector2 position;
    private Zone zone;

    public Grass(Vector2 p, float w, float h) {
        position = p;
        width = w;
        height = h;

        Assets.modelBuilder.begin();
        MeshPartBuilder builder = Assets.modelBuilder.part("grass",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(Assets.wildGrassTextureDiffuse));
        builder.rect(0, 0, -0.1f,
                width, 0, -0.1f,
                width, height, -0.1f,
                0, height, -0.1f,
                1, 1, 1);
        model = Assets.modelBuilder.end();
        modelInstance = new ModelInstance(model);
        modelInstance.transform.setTranslation(position.x, position.y, 0);
    }

    @Override
    public String className() {
        return "Grass";
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        modelBatch.begin(GameView.gv.getCamera());;
        modelBatch.render(modelInstance, GameView.outsideEnvironment);
        modelBatch.end();
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        zone = z;
    }
}
