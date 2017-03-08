package com.zombies.map.room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.zombies.BodData;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.interfaces.Collideable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Modelable;
import com.zombies.util.Assets;

public class Wall implements Collideable, Loadable, HasZone {
    private Vector2 p1, p2, center;
    private double angle;
    private Body body;
    private HashMap<Float, Float> holes = new HashMap<Float, Float>();

    private static Texture texture;
    private static TextureAttribute textureAttribute;
    private static Material material;
    static {
        texture = Assets.a.get("data/room/wall/wall.jpg", Texture.class);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        textureAttribute = new TextureAttribute(Attribute.getAttributeType("diffuseTexture"),
                new TextureDescriptor<>(texture),
                0, 0, 1, 1);
        material = new Material(textureAttribute);
    }

    protected ArrayList<WallPoint>   points   = new ArrayList<>();
    protected ArrayList<WallSegment> segments = new ArrayList<>();

    private GameView view;

    public Building building;
    public boolean vertical;
    private int[] key;
    private String sKey;


    public Wall(Vector2 p1, Vector2 p2, Building b) {
        view = GameView.gv;

        this.p1 = p1;
        this.p2 = p2;
        angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        center = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        building = b;

        for (Zone z : Zone.zonesOnLine(p1, p2))
            z.addObject(this);
    }

    public void genSegmentsFromPoints() {
        if (body != null)
            D.world.destroyBody(body);

        body = D.world.createBody(new BodyDef());
        body.setTransform(p1, (float)angle);
        body.setUserData(new BodData("wall", this));

        segments = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (i == points.size() - 1)
                break;

            segments.add(new WallSegment(
                    body,
                    points.get(i).getPoint(),
                    points.get(i + 1).getPoint(),
                    points.get(i).getHeight()));
        }
    }

    // Check if two lines are very close
    public boolean similar(Vector2 p1, Vector2 p2) {
        final float dstTolerance = 0.1f;
        if (this.p1.dst(p1) < dstTolerance && this.p2.dst(p2) < dstTolerance)
            return true;
        return false;
    }

    public Double getAngle() { return angle; }
    public Vector2 getStart() { return p1; }
    public Vector2 getEnd() { return p2; }

    public Body getBody() {return body;}

    public void consolidateHoles() {
        ArrayList<Float> holePositions = new ArrayList<Float>(holes.keySet());
        Collections.sort(holePositions);

        for (int i = 0; i < holePositions.size() - 1; i++) {
            Float holePosition, nextHolePosition, holeRadius, nextHoleRadius;
            holePosition = holePositions.get(i);
            holeRadius = holes.get(holePosition) / 2;
            nextHolePosition = holePositions.get(i + 1);
            nextHoleRadius = holes.get(nextHolePosition) / 2;

            if (holePosition + holeRadius > nextHolePosition - nextHoleRadius) {
                float newHolePosition, newHoleSize;
                newHolePosition = ((nextHolePosition + nextHoleRadius) + (holePosition - holeRadius)) / 2;
                newHoleSize = ((nextHolePosition + nextHoleRadius) - (holePosition - holeRadius));

                // consolidate the two holes
                holes.remove(holePosition);
                holes.remove(nextHolePosition);
                holes.put(newHolePosition, newHoleSize);

                consolidateHoles(); // rinse and repeat
                return;
            }
        }
    }

    public void createHole(Vector2 holePoint, float holeSize) {
        float dst = p1.dst(holePoint);
        if (dst > p1.dst(p2))
            return; // this is beyond the scope of the wall

        D.world.destroyBody(body);
        body = D.world.createBody(new BodyDef());
        body.setTransform(p1, body.getAngle());
        body.setUserData(new BodData("wall", this));
        segments = new ArrayList<WallSegment>();

        // if holePosition is not on line, this function will
        // swing the vector2 onto the line using p1 as the axis
        holes.put(dst, holeSize);
        consolidateHoles();

        ArrayList<Float> holePositions = new ArrayList<Float>(holes.keySet());

        Collections.sort(holePositions);

        // unit vector in the same direction as the wall.
        Vector2 vo = p2.cpy().sub(p1).scl(1 / p2.cpy().sub(p1).len());
        Vector2 v1, v2;

        // System.out.println("vo: " + vo);

        for (int i = 0; i <= holePositions.size(); i++) {

            // the start and end positions of this wall segment, relative to the wall position.
            v1 = (i == 0 ? new Vector2(0, 0) : vo.cpy().scl(holePositions.get(i - 1) + holes.get(holePositions.get(i - 1)) / 2));
            v2 = (i == holePositions.size() ? p2.cpy().sub(p1) : vo.cpy().scl(holePositions.get(i) - holes.get(holePositions.get(i)) / 2));

            // System.out.println("v1: " + v1);
            // System.out.println("v2: " + v2);

            // create the segment only if it has nonzero length, and is in the same direction as
            // the wall unit vector (second requirement is false if the last/first hole extends past
            // the wall, in which case this seg is not needed).
            if (v2.cpy().sub(v1).len() > 0 && v2.cpy().sub(v1).dot(vo) > 0.0) {
                segments.add(new WallSegment(body, p1.cpy().add(v1), p1.cpy().add(v2), 1));
            }
        }
        building.rebuildModel();
    }

    public void buildWallMesh(Vector2 modelCenter) {
        MeshPartBuilder wallBuilder = Assets.modelBuilder.part("Walls",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                material);
        for (WallSegment ws: segments)
            ws.buildMesh(wallBuilder, modelCenter);
    }

    public void destroy() {
        if (body != null)
            D.world.destroyBody(body);
    }

    @Override
    public void handleCollision(Fixture f) {
        if (C.ENABLE_WALL_DESTRUCTION) {
            //createHole(f.getBody().getPosition(), 5f);
        }
    }

    @Override
    public void load() {
        body.setActive(true);
    }

    @Override
    public void unload() {
        body.setActive(false);
    }

    @Override
    public Zone getZone() {
        return null;
    }

    @Override
    public void setZone(Zone z) {

    }
}
