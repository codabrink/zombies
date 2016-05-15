package com.zombies;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class DrawLine {
	private Vector2 p1, p2;
    private double angle;

	public DrawLine(Vector2 p1, Vector2 p2) {
        this.p1 = p1;
        this.p2 = p2;
        angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
	}

    public void buildMesh(MeshPartBuilder wallBuilder, Vector2 modelCenter) {
        float dx = (float)(p1.dst(p2) * Math.cos(angle) / 2);
        float dy = (float)(p1.dst(p2) * Math.sin(angle) / 2);

        BoundingBox bounds;
        Vector3 min, max;

        min = new Vector3(p1.x + dx - modelCenter.x, p1.y + dy - modelCenter.y, 0);
        max = min.cpy().add(p1.dst(p2), 0.1f, C.BOX_HEIGHT);
        bounds = new BoundingBox(min, max);
        Matrix4 mrot = new Matrix4();
        mrot.rotate(Vector3.Z, (float)Math.toDegrees(angle));
        bounds.mul(mrot);

        BoxShapeBuilder.build(wallBuilder, bounds);
    }
}
