package com.zombies.map.neighborhood;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.zombies.Zone;
import com.zombies.overlappable.PolygonOverlappable;
import com.zombies.interfaces.ThreadedModelBuilderCallback;
import com.zombies.lib.Assets;
import com.zombies.lib.math.M;
import com.zombies.lib.math.LineSegment;
import com.zombies.lib.ThreadedModelBuilder;

public class StreetSegment extends PolygonOverlappable {
    public LineSegment line;
    public Vector2 a, b, center;
    public double angle;
    private float width, height;
    public float length, surfaceArea;
    private Zone zone;
    private Street street;
    private ThreadedModelBuilder modelBuilder = new ThreadedModelBuilder();

    public static StreetSegment createStreetSegment(Street street, Vector2 p1, Vector2 p2, double angle) {
        if (p1.x == p2.x && p1.y == p2.y)
            return null;

        return new StreetSegment(street, p1, p2, angle);
    }

    private StreetSegment(Street street, Vector2 a, Vector2 b, double angle) {
        this.a      = a;
        this.b      = b;
        this.angle  = angle;
        this.street = street;
        line        = new LineSegment(a, b);
        center      = M.center(a, b);
        length      = a.dst(b);
        surfaceArea = length * street.radius;
        zone = Zone.getZone(center);
        zone.addPendingObject(this);

        setCorners(M.lineToCorners(line, width / 2));

        final float fangle = (float) angle;
        modelBuilder.setCallback(new ThreadedModelBuilderCallback() {
            @Override
            public void response(Model model) {
                ModelInstance modelInstance = new ModelInstance(model);
                modelInstance.transform.rotateRad(Vector3.Z, fangle);
                modelInstance.transform.setTranslation(center.x, center.y, 0);

                zone.addPendingObject(modelInstance);
            }
        });

        buildMesh();
    }

    @Override
    public boolean overlaps(PolygonOverlappable po) {
        if (!(po instanceof StreetSegment)) return super.overlaps(po);

        StreetSegment otherSegment = (StreetSegment) po;
        Vector2 p = otherSegment.line.intersectionPoint(line);
        return Math.min(p.dst(a), p.dst(b)) < Intersection.MIN_INTERSECTION_DISTANCE;
    }

    private void buildMesh() {
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("street",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(Assets.MATERIAL.STREET.texture.attributes));
        builder.rect(
                -width / 2, -height / 2, 0,
                width / 2,  -height / 2, 0,
                width / 2, height / 2, 0,
                -width / 2, height / 2, 0,
                1, 1, 1);
        modelBuilder.finish();
    }
}
