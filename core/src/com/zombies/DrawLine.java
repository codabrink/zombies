package com.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.interfaces.Drawable;
import com.util.Assets;

public class DrawLine {
	private Vector2 p1, p2;
    private Color color;
    GameView view;

    private Mesh mesh;
    private Model model;
    private ModelInstance instance;

	public DrawLine(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        view = GameView.gv;
        color = Color.WHITE;
        float dx = p1.x - p2.x, dy = p1.y - p2.y, angle = (float)Math.atan2(dx, dy);

        model = Assets.mb.createBox(p1.dst(p2), 0.1f, 10f,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                Usage.Position | Usage.Normal);
        instance = new ModelInstance(model);
        instance.transform.rotate(Vector3.Z, angle);
        instance.transform.setTranslation(p1.x, p1.y, 0);
	}

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        modelBatch.begin(GameView.gv.getCamera());
        modelBatch.render(instance, GameView.gv.environment);
        modelBatch.end();
	}

    public void setColor(Color c) {
        color = c;
    }
}
