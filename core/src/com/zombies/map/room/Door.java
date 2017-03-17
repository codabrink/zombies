package com.zombies.map.room;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.zombies.BodData;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.interfaces.Collideable;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Modelable;
import com.zombies.util.Assets;
import com.zombies.util.Geometry;

public class Door implements Drawable, Modelable, HasZone, Collideable {
    private static BoxShapeBuilder boxShapeBuilder = new BoxShapeBuilder();
    private Vector2 p1, p2, center;
    private Building building;
    private double angle;
    private Zone z;

    private Body body;

    private Model model;
    private ModelInstance modelInstance;

    private static Texture texture;
    private static TextureAttribute textureAttribute;
    private static Material material;
    private static ModelBuilder modelBuilder = new ModelBuilder();
    static {
        texture = Assets.a.get("data/room/floor/dining_room.jpg", Texture.class);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        textureAttribute = new TextureAttribute(Attribute.getAttributeType("diffuseTexture"),
                new TextureDescriptor<>(texture),
                0, 0, 1, 1);
        material = new Material(textureAttribute);
    }

    public Door(Vector2 p1, Vector2 p2, Building b) {
        this.p1 = p1;
        this.p2 = p2;
        building = b;
        angle = Geometry.getAngle(p1, p2);
        center = new Vector2((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
        Zone.getZone(center).addPendingObject(this);

        body = D.world.createBody(new BodyDef());
        body.setType(BodyDef.BodyType.DynamicBody);
        body.setTransform(p1, (float)angle);
        body.setUserData(new BodData("door", this));

        EdgeShape shape = new EdgeShape();
        shape.set(new Vector2(0, 0), new Vector2(p1.dst(p2), 0));
        body.createFixture(shape, 0);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.initialize(body, D.groundBody, p1);
    }

    @Override
    public Zone getZone() {
        return z;
    }

    @Override
    public void setZone(Zone z) {
        this.z = z;
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        if (modelInstance == null)
            return;

        //modelInstance.transform.setTranslation(body.getPosition().x, body.getPosition().y, 1);
        //modelInstance.transform.setToRotation(Vector3.Z, body.getAngle());
        Matrix4 mTrans = new Matrix4();
        mTrans.translate(body.getPosition().x, body.getPosition().y, 1);
        mTrans.rotate(Vector3.Z, body.getAngle());

        modelInstance.transform.set(mTrans);

        modelBatch.begin(GameView.gv.getCamera());
        modelBatch.render(modelInstance);
        modelBatch.end();
    }

    @Override
    public void rebuildModel() {
        BoundingBox bounds;
        Vector3 min, max;

        float height = C.BOX_DEPTH * 0.6f;

        min = new Vector3(0, -0.5f, 0);
        max = new Vector3(p1.dst(p2), 0.5f, height);
        bounds = new BoundingBox(min, max);

        Matrix4 mtrans = new Matrix4();
        mtrans.translate(p1.x - center.x, p1.y - center.y, 0);
        mtrans.rotate(Vector3.Z, (float)Math.toDegrees(angle));
        bounds.mul(mtrans);

        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("DoorFrames",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                material);
        boxShapeBuilder.build(builder, bounds);
        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
        modelInstance.transform.setTranslation(center.x, center.y, 1);
    }

    @Override
    public void handleCollision(Fixture f) {

    }
}
