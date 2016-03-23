package com.zombies;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.util.MyVector2;

public class Wall implements com.interfaces.Collideable {
    private MyVector2 p1;
    private ArrayList<Float> holes = new ArrayList<Float>();

    private Box box;
    private Body body;
    private ArrayList<EdgeShape> shapes;
    private ArrayList<DrawLine> lines;
    private GameView view;
    public boolean door = false;
    int index;

    public Wall(Box box, float x, float y, float length, float angle) {
        view = GameView.gv;
        this.box = box;

        p1 = new MyVector2(x, y, length, angle);
        shapes = new ArrayList<EdgeShape>();
        lines = new ArrayList<DrawLine>();

        lines.add(new DrawLine(p1.cpy().add(box.getPosition()), p1.end().add(box.getPosition())));

        //set up physics
        body = view.getWorld().createBody(new BodyDef());
        EdgeShape shape = new EdgeShape();
        shape.set(p1, p1.end());
        shapes.add(shape);
        body.createFixture(shape, 0);
        body.setTransform(new Vector2(box.x(), box.y()), body.getAngle());
        body.setUserData(new BodData("wall", this));
    }

    public void makeDoor() {

    }

    public void setColor(Color c) {
        for (DrawLine dl : lines) {
            dl.setColor(c);
        }
    }

    public boolean samePositionAs(Wall w) {
        return (absoluteP1().dst(w.absoluteP1()) == 0.0 && p1.angle() == w.getP1().angle());
    }

    public void draw() {
        for (DrawLine l: lines) {
            l.draw();
        }
    }

    public boolean isDoor() {
        return door;
    }

    public Body getBody() {return body;}


    public Vector2 doorPosition() {
        if (isDoor()) {
            //return new Vector2((p1.x+p2.x)/2, (p1.y+p2.y)/2);
        } else {
            return null;
        }
        return null;
    }

    public void removeWall() {
        view.getWorld().destroyBody(body);
        body = view.getWorld().createBody(new BodyDef());
        body.setTransform(new Vector2(box.x(), box.y()), body.getAngle());
        lines = new ArrayList<DrawLine>();
    }

    public void unload() {
        // unload the wall
    }

    public void update() {
        for (DrawLine d: lines) {
            d.update();
        }
    }

    private void createHole(Vector2 holePosition, float holeSize) {

    }

    public Vector2 getP1() { return p1; }
    public Vector2 getP2() { return p1.end(); }
    public Vector2 absoluteP1() { return box.getPosition().cpy().add(p1); }
    public Vector2 absoluteP2() { return box.getPosition().cpy().add(p1.end()); }

    @Override
    public void handleCollision(Fixture f) {
        createHole(f.getBody().getPosition(), 3f);
    }

}
