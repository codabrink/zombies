package com.zombies.map.building;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.zombies.C;
import com.zombies.lib.Assets.MATERIAL;
import com.zombies.lib.math.M;

public class WallSegment {
	private Vector2 p1, p2;
    private double angle;
    private float height;
    private float dst;
    float top, bottom;
    private WallTop wallTop;

	public WallSegment(Vector2 p1, Vector2 p2, float height, MATERIAL material) {
        this.p1 = p1;
        this.p2 = p2;
        this.height = height;
        this.dst    = p1.dst(p2);
        angle       = M.getAngle(p1, p2);

        top = Wall.getTop(height);
        bottom = Wall.getBottom(height);
    }

	public void genShapes(Body body) {
        if (height > 0) {
            EdgeShape shape = new EdgeShape();
            Vector2 p0 = body.getPosition();
            shape.set(new Vector2(p0.dst(p1), 0), new Vector2(p0.dst(p2), 0));
            body.createFixture(shape, 0);
        }
    }

    public WallSegment addTop(Vector2 c1, Vector2 c2, Vector2 c3, Vector2 c4) {
        wallTop = new WallTop(c1, c2, c3, c4, top);
        return this;
    }

    public void buildMesh(MeshPartBuilder builder, Vector2 center) {
        if (height == 0)
            return;

        Vector2 a = new Vector2(p1.x - center.x, p1.y - center.y);
        Vector2 b = p2.cpy().sub(p1).setAngleRad((float) angle).add(a);

        builder.setUVRange(0, 0, dst / C.GRIDSIZE, Math.abs(height));

        builder.rect(
                a.x, a.y, bottom,
                b.x, b.y, bottom,
                b.x, b.y, top,
                a.x, a.y, top,
                1, 1, 1);
    }
}
