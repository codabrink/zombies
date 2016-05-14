package com.zombies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.util.Assets;

public class DrawLine {
	private Vector2 p1, p2;
    private Color color;
    GameView view;

    private Model model;
    private ModelInstance instance;

    private double angle;

	public DrawLine(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);

        view = GameView.gv;
        color = Color.WHITE;
        float dx = p2.x - p1.x, dy = p2.y - p1.y;
        double angle = Math.atan2(dy, dx);

        model = Assets.modelBuilder.createBox(p1.dst(p2), 0.1f, C.BOX_HEIGHT,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                Usage.Position | Usage.Normal);
        instance = new ModelInstance(model);

        dx = (float)(p1.dst(p2) * Math.cos(angle) / 2);
        dy = (float)(p1.dst(p2) * Math.sin(angle) / 2);
        instance.transform.setTranslation(p1.x + dx, p1.y + dy, C.BOX_HEIGHT / 2);
        instance.transform.rotate(Vector3.Z, (float)Math.toDegrees(angle));
	}

    public void buildMesh(ModelBuilder modelBuilder, Vector2 modelCenter) {
        float dx = p2.x - p1.x, dy = p2.y - p1.y;
        Assets.meshBuilder.begin(Usage.Position | Usage.Normal, GL20.GL_TRIANGLES);
        Assets.meshBuilder.box(p1.dst(p2), 0.1f, C.BOX_HEIGHT);
        modelBuilder.part(Integer.toString(System.identityHashCode(this)),
                Assets.meshBuilder.end(), Usage.Position | Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)))
                .mesh.transform(new Matrix4().translate(p1.x + dx - modelCenter.x, p1.y + dy - modelCenter.y, C.BOX_HEIGHT / 2)
                .rotate(Vector3.Z, (float)Math.toDegrees(angle)));
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
//        modelBatch.begin(GameView.gv.getCamera());
//        modelBatch.render(instance, GameView.gv.environment);
//        modelBatch.end();
	}

    public void setColor(Color c) {
        color = c;
    }
}
