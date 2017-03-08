package com.zombies.map.room;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.zombies.C;
import com.zombies.interfaces.ModelingCallback;
import com.zombies.util.Assets;
import com.zombies.util.Geometry;

public class DoorFrame {
    private static BoxShapeBuilder boxShapeBuilder = new BoxShapeBuilder();
    private Vector2 p1, p2;
    private Building building;
    private double angle;

    public DoorFrame(Vector2 p1, Vector2 p2, Building b) {
        this.p1 = p1;
        this.p2 = p2;
        building = b;
        angle = Geometry.getAngle(p1, p2);

        building.modelables.get(Building.MATERIAL.FLOOR_WOOD).add(new ModelingCallback() {
            @Override
            public void buildModel(MeshPartBuilder builder, Vector2 center) {
                buildMesh(builder, center);
            }
        });
    }

    public void buildMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        BoundingBox bounds;
        Vector3 min, max;

        float frameTop = C.BOX_DEPTH * 0.8f;

        min = new Vector3(0, -0.5f, 0);
        max = new Vector3(p1.dst(p2), 0.5f, -0.4f);
        bounds = new BoundingBox(min, max);

        Matrix4 mtrans = new Matrix4();
        mtrans.translate(p1.x - modelCenter.x, p1.y - modelCenter.y, frameTop);
        mtrans.rotate(Vector3.Z, (float)Math.toDegrees(angle));
        bounds.mul(mtrans);

        boxShapeBuilder.build(builder, bounds);

        min = new Vector3(-0.4f, -0.4f, 0);
        max = new Vector3(0.4f, 0.4f, frameTop);
        bounds = new BoundingBox(min, max);

        mtrans = new Matrix4();
        mtrans.translate(p1.x - modelCenter.x, p1.y - modelCenter.y, 0);
        bounds.mul(mtrans);

        boxShapeBuilder.build(builder, bounds);

        mtrans = new Matrix4();
        mtrans.translate(p2.x - p1.x, p2.y - p1.y, 0);
        bounds.mul(mtrans);

        boxShapeBuilder.build(builder, bounds);
    }
}
