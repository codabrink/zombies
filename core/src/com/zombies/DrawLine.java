package com.zombies;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.zombies.util.FixedBoxShapeBuilder;

public class DrawLine {
	private Vector2 p1, p2;
    private double angle;

	public DrawLine(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
	}

    public void buildMesh(MeshPartBuilder wallBuilder, Vector2 modelCenter) {
        BoundingBox bounds;
        Vector3 min, max;

        min = new Vector3(0, 0, 0);
        max = new Vector3(p1.dst(p2), 0.1f, C.BOX_DEPTH);
        bounds = new BoundingBox(min, max);

        MeshBuilder builder = new MeshBuilder();
        builder.begin(Usage.Position | Usage.Normal | Usage.TextureCoordinates, GL20.GL_TRIANGLES);
        FixedBoxShapeBuilder.build(builder, bounds);
        Mesh mesh = builder.end();

        Matrix4 mtrans = new Matrix4();
        mtrans.translate(p1.x - modelCenter.x, p1.y - modelCenter.y, 0);
        mtrans.rotate(Vector3.Z, (float)Math.toDegrees(angle));

        mesh.transform(mtrans);

        wallBuilder.addMesh(mesh);
    }
}
