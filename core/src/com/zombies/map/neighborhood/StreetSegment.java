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
import com.zombies.interfaces.ThreadedModelBuilderCallback;
import com.zombies.util.Assets;
import com.zombies.util.G;
import com.zombies.util.ThreadedModelBuilder;

public class StreetSegment {
    public Vector2 p1, p2, center;
    public double angle;
    private float width, height;
    private Vector2[] corners = new Vector2[4];
    private Zone zone;
    private ThreadedModelBuilder modelBuilder = new ThreadedModelBuilder(new ThreadedModelBuilderCallback() {
        @Override
        public void response(Model model) {
            ModelInstance modelInstance = new ModelInstance(model);
            modelInstance.transform.setTranslation(center.x, center.y, 0);
            modelInstance.transform.rotateRad(Vector3.Z, (float) angle);

            zone.addPendingObject(modelInstance);
        }
    });

    public StreetSegment(Vector2 p1, Vector2 p2, double angle) {
        this.p1    = p1;
        this.p2    = p2;
        this.angle = angle;

        center = G.center(p1, p2);
        width      = p1.dst(p2);
        height     = Street.RADIUS * 2;

        zone = Zone.getZone(p1);
        compile();

        buildMesh();
    }

    private void compile() {
        // corners are counter clockwise from p1
        corners[0] = new Vector2(G.projectVector(p1, angle + G.THRPIHALF, Street.RADIUS));
        corners[1] = new Vector2(G.projectVector(p2, angle + G.THRPIHALF, Street.RADIUS));
        corners[2] = new Vector2(G.projectVector(p2, angle + G.PIHALF, Street.RADIUS));
        corners[3] = new Vector2(G.projectVector(p1, angle + G.PIHALF, Street.RADIUS));
    }

    private void buildMesh() {
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("street",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(Assets.MATERIAL.STREET.texture.textureAttribute));
        builder.rect(
                -width / 2, -height / 2, 0,
                width / 2,  -height / 2, 0,
                width / 2, height / 2, 0,
                -width / 2, height / 2, 0,
                1, 1, 1);
        modelBuilder.finish();
    }
}
