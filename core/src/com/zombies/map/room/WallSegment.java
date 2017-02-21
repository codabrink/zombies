package com.zombies.map.room;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.zombies.C;
import com.zombies.util.FixedBoxShapeBuilder;

public class WallSegment {
	private Vector2 p1, p2;
    private double angle;
    private float height;

	public WallSegment(Vector2 p1, Vector2 p2, float height) {
        this.p1 = p1;
        this.p2 = p2;
        this.height = height;
        angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
	}

    public void buildMesh(MeshPartBuilder wallBuilder, Vector2 modelCenter) {
        if (height == 0)
            return;

        BoundingBox bounds;
        Vector3 min, max;

        if (height < 0)
            System.out.println("yeah");

        min = new Vector3(0, 0, (height < 0 ? C.BOX_DEPTH * Math.abs(height) : 0));
        max = new Vector3(p1.dst(p2), 0.1f, (height > 0 ? C.BOX_DEPTH * height : C.BOX_DEPTH));
        bounds = new BoundingBox(min, max);

        Matrix4 mtrans = new Matrix4();
        mtrans.translate(p1.x - modelCenter.x, p1.y - modelCenter.y, 0);
        mtrans.rotate(Vector3.Z, (float)Math.toDegrees(angle));
        bounds.mul(mtrans);

        FixedBoxShapeBuilder.build(wallBuilder, bounds);
    }
}
