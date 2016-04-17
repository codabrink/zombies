package com.zombies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.interfaces.Collideable;
import com.interfaces.Loadable;
import com.map.MapGen;
import com.util.MyVector2;

public class Wall implements Collideable, Loadable {
    private MyVector2 p1;
    private Body body;
    private HashMap<Float, Float> holes = new HashMap<Float, Float>();
    private ArrayList<DrawLine> lines;
    private GameView view;
    public boolean door = false;
    int index;
    private boolean exploded = false;

    public Wall(Vector2 position, float length, float angle) {
        view = GameView.gv;

        p1 = new MyVector2(position.x, position.y, length, angle);
        lines = new ArrayList<DrawLine>();

        //set up physics
        EdgeShape shape = new EdgeShape();
        MyVector2 shapeVector = new MyVector2(0, 0, length, angle);
        shape.set(shapeVector, shapeVector.end());
        body = view.getWorld().createBody(new BodyDef());
        body.createFixture(shape, 0);
        body.setTransform(new Vector2(p1.x, p1.y), body.getAngle());
        body.setUserData(new BodData("wall", this));
        lines.add(new DrawLine(p1, p1.end()));
    }

    public void makeDoor() {

    }

    public void setColor(Color c) {
        for (DrawLine dl : lines) {
            dl.setColor(c);
        }
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        for (DrawLine l: lines) {
            l.draw(spriteBatch, shapeRenderer);
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

    public void update() {
        for (DrawLine d: lines) {
            d.update();
        }
    }

    public void createHole(Vector2 holePosition, float holeSize) {
        view.getWorld().destroyBody(body);
        body = view.getWorld().createBody(new BodyDef());
        body.setTransform(new Vector2(p1.x, p1.y), body.getAngle());
        body.setUserData(new BodData("wall", this));
        lines = new ArrayList<DrawLine>();

        // if holePosition is not on line, this function will
        // swing the vector2 onto the line using p1 as the axis
        float dst = body.getPosition().dst(holePosition);
        holes.put(dst, holeSize);
        ArrayList<Float> holePositions = new ArrayList<Float>(holes.keySet());
        Collections.sort(holePositions);

        MyVector2 vo, v1, v2;

        for (int i=0;i<holePositions.size();i++) {
            vo = new MyVector2(0, 0, Math.max(holePositions.get(i) - holeSize / 2, 0), p1.angle());
            v1 = i == 0 ? vo : new MyVector2(vo.project(holePositions.get(i-1) + holeSize / 2), Math.max(holePositions.get(i) - holeSize / 2 - (holePositions.get(i-1) + holeSize / 2), 0), p1.angle());
            float wallSegmentLength = (i + 1 == holePositions.size() ? p1.len() : holePositions.get(i + 1)) - holePositions.get(i) - holeSize / 2;
            v2 = new MyVector2(vo.project(holePositions.get(i) + holeSize / 2), Math.max(wallSegmentLength, 0), p1.angle());

            //MyVector2 v1 = new MyVector2(0, 0, Math.max(dst - holeSize / 2, 0), p1.angle());
            //MyVector2 v2 = new MyVector2(v1.project(dst + holeSize / 2), Math.max(p1.len() - dst - holeSize / 2, 0), p1.angle());

            if (v1.len() > 0) {
                EdgeShape shape = new EdgeShape();
                shape.set(v1, v1.end());
                lines.add(new DrawLine(v1.cpy().add(body.getPosition()), v1.end().cpy().add(body.getPosition())));
                lines.get(lines.size()-1).setColor(Color.PURPLE);
                body.createFixture(shape, 0);
            }

            if (v2.len() > 0) {
                EdgeShape shape2 = new EdgeShape();
                shape2.set(v2, v2.end());
                body.createFixture(shape2, 0);
                lines.add(new DrawLine(v2.cpy().add(body.getPosition()), v2.end().cpy().add(body.getPosition())));
                lines.get(lines.size()-1).setColor(Color.ORANGE);
            }
        }
    }

    public Vector2 getP1() { return p1; }
    public Vector2 getP2() { return p1.end(); }

    @Override
    public void handleCollision(Fixture f) {
        createHole(f.getBody().getPosition(), 5f);
    }

    @Override
    public void load() {
        body.setActive(true);
    }

    @Override
    public void unload() {
        body.setActive(false);
    }
}
