package com.zombies.map.room;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.zombies.C;
import com.zombies.util.Assets.MATERIAL;
import com.zombies.util.G;

public class WallSegment {
	private Vector2 p1, p2;
    private double angle;
    private float height;
    private MATERIAL material;

	public WallSegment(Vector2 p1, Vector2 p2, float height, MATERIAL material) {
        this.p1 = p1;
        this.p2 = p2;
        this.height = height;
        this.material = material;
        angle = G.getAngle(p1, p2);
    }

	public void genShapes(Body body) {
        if (height > 0) {
            EdgeShape shape = new EdgeShape();
            Vector2 p0 = body.getPosition();
            shape.set(new Vector2(p0.dst(p1), 0), new Vector2(p0.dst(p2), 0));
            body.createFixture(shape, 0);
        }
    }

    public void buildMesh(MeshPartBuilder builder, Vector2 center) {
        if (height == 0)
            return;

        Vector2 a = new Vector2(p1.x - center.x, p1.y - center.y);
        Vector2 b = p2.cpy().sub(p1).setAngleRad((float) angle).add(a);

        float lowZ = (height < 0 ? C.BOX_DEPTH - C.BOX_DEPTH * Math.abs(height) : 0);
        float highZ = (height > 0 ? C.BOX_DEPTH * height : C.BOX_DEPTH);

        builder.rect(
                a.x, a.y, lowZ,
                b.x, b.y, lowZ,
                b.x, b.y, highZ,
                a.x, a.y, highZ,
                1, 1, 1);
    }
}
