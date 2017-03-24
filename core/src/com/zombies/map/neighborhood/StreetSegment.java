package com.zombies.map.neighborhood;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector2;
import com.zombies.util.G;

public class StreetSegment {
    public Vector2 p1, p2;
    public double angle;
    private Vector2[] corners = new Vector2[4];

    public StreetSegment(Vector2 p1, Vector2 p2, double angle) {
        this.p1 = p1;
        this.p2 = p2;
        this.angle = angle;

        compile();
    }

    private void compile() {
        // corners are counter clockwise from p1
        corners[0] = new Vector2(G.projectVector(p1, angle + G.THRPIHALF, Street.RADIUS));
        corners[1] = new Vector2(G.projectVector(p2, angle + G.THRPIHALF, Street.RADIUS));
        corners[2] = new Vector2(G.projectVector(p2, angle + G.PIHALF, Street.RADIUS));
        corners[3] = new Vector2(G.projectVector(p1, angle + G.PIHALF, Street.RADIUS));
    }

    private void buildMesh(MeshPartBuilder builder, Vector2 modelCenter) {
        Vector2 relp = G.center(p1, p2).sub(modelCenter);
        builder.rect(
                corners[2].x + relp.x, corners[2].y + relp.y, 0,
                corners[3].x + relp.x, corners[3].y + relp.y, 0,
                corners[0].x + relp.x, corners[0].y + relp.y, 0,
                corners[1].x + relp.x, corners[1].y + relp.y, 0,
                1, 1, 1);
    }
}
