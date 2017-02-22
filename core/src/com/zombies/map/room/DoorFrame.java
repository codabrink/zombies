package com.zombies.map.room;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.zombies.C;
import com.zombies.interfaces.Modelable;

public class DoorFrame {
    private static BoxShapeBuilder boxShapeBuilder = new BoxShapeBuilder();
    private Vector2 p1, p2;
    private Modelable modelable;
    private double angle;

    public DoorFrame(Vector2 p1, Vector2 p2, Modelable m) {
        this.p1 = p1;
        this.p2 = p2;
        modelable = m;
        angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
    }

    public void buildMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        BoundingBox bounds;
        Vector3 min, max;

        float frameTop = C.BOX_DEPTH * 0.9f;

        min = new Vector3(0, -0.2f, 0);
        max = new Vector3(p1.dst(p2), 0.2f, 0.4f);
        bounds = new BoundingBox(min, max);

        Matrix4 mtrans = new Matrix4();
        mtrans.translate(p1.x - modelCenter.x, p1.y - modelCenter.y, frameTop);
        mtrans.rotate(Vector3.Z, (float)Math.toDegrees(angle));
        bounds.mul(mtrans);

        boxShapeBuilder.build(builder, bounds);
    }
}
