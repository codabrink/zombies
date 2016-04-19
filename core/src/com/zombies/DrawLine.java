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
        float dx = p2.x - p1.x, dy = p2.y - p1.y;
        double angle = Math.atan2(dy, dx);

        model = Assets.mb.createBox(Math.max(p2.x - p1.x, 0.1f), Math.max(p2.y - p1.y, 0.1f), C.BOX_HEIGHT,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                Usage.Position | Usage.Normal);
        instance = new ModelInstance(model);

        System.out.println(Math.cos(angle));
        dx = (float)(p1.dst(p2) * Math.cos(angle) / 2);
        dy = (float)(p1.dst(p2) * Math.sin(angle) / 2);
        instance.transform.setTranslation(p1.x + dx, p1.y + dy, C.BOX_HEIGHT / 2);
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
