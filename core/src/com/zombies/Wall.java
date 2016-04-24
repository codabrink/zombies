package com.zombies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.interfaces.Collideable;
import com.interfaces.Drawable;
import com.interfaces.Loadable;
import com.util.MyVector2;

public class Wall implements Collideable, Loadable, Drawable {
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

        Zone.getZone(position).addDrawableNoCheck(this, 1);
    }

    public void makeDoor() {

    }

    public void setColor(Color c) {
        for (DrawLine dl : lines) {
            dl.setColor(c);
        }
    }

    @Override
    public String className() {
        return null;
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        for (DrawLine l: lines) {
            l.draw(spriteBatch, shapeRenderer, modelBatch);
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

        HashMap<Float, Float> consolidatedHoles;
        ArrayList<Float> holePositions;

        // if there are more than 2 holes, check if any overlap and can be joined.
        while (holes.size() >= 2) {

            consolidatedHoles = new HashMap<Float, Float>();
            holePositions = new ArrayList<Float>(holes.keySet());
            Collections.sort(holePositions);
            float nextHoleEndPoint;
            float nextHoleStartPoint;
            float thisHoleEndPoint;
            float thisHoleStartPoint;
            boolean lastHoleConsolidated = false;

            for (int i = 0; i <= holePositions.size() - 2; i++) {

                thisHoleEndPoint = holePositions.get(i) + holes.get(holePositions.get(i)) / 2;
                nextHoleStartPoint = holePositions.get(i + 1) - holes.get(holePositions.get(i + 1)) / 2;

                if (thisHoleEndPoint >= nextHoleStartPoint) {
                    thisHoleStartPoint = holePositions.get(i) - holes.get(holePositions.get(i)) / 2;
                    nextHoleEndPoint = holePositions.get(i + 1) + holes.get(holePositions.get(i + 1)) / 2;
                    consolidatedHoles.put(thisHoleStartPoint + (nextHoleEndPoint - thisHoleStartPoint) / 2, nextHoleEndPoint - thisHoleStartPoint);

                    if (i == holePositions.size() - 2) {
                        lastHoleConsolidated = true;
                    }

                    // skip the next iteration, because the next hole has been absorbed into this one.
                    i = i + 1;
                } else {
                    consolidatedHoles.put(holePositions.get(i), holes.get(holePositions.get(i)));
                }
            }

            // the last hole is not iterated over on its own, so make sure it gets included.
            if (!lastHoleConsolidated) {
                consolidatedHoles.put(holePositions.get(holePositions.size() - 1), holes.get(holePositions.get(holePositions.size() - 1)));
            }

            // System.out.println("Consolidated: " + consolidatedHoles);
            // System.out.println("Original:     " + holes);

            if (consolidatedHoles.equals(holes)) {
                break;
            } else {
                holes = consolidatedHoles;
            }
        }

        holePositions = new ArrayList<Float>(holes.keySet());
        Collections.sort(holePositions);
        MyVector2 vo, v1, v2;

        for (int i=0;i<holePositions.size();i++) {

            // distance to current hole starting point and wall angle.
            vo = new MyVector2(0, 0, Math.max(holePositions.get(i) - holes.get(holePositions.get(i)) / 2, 0), p1.angle());
            // v1 describes the wall segment before this hole.
            // the position of the end of the last hole (or the start of the wall if there isn't one),
            // the distance between the start of the last hole and the beginning of this one, the wall angle.
            v1 = (i == 0 ? vo : new MyVector2((float)((holePositions.get(i-1) + holes.get(holePositions.get(i-1)) / 2) * Math.cos(p1.angle() * Math.PI / 180)),
                    (float)((holePositions.get(i-1) + holes.get(holePositions.get(i-1)) / 2) * Math.sin(p1.angle() * Math.PI / 180)),
                    Math.max(holePositions.get(i) - holes.get(holePositions.get(i)) / 2 - (holePositions.get(i-1) + holes.get(holePositions.get(i-1)) / 2), 0),
                    p1.angle()));

            if (v1.len() > 0) {
                EdgeShape shape = new EdgeShape();
                shape.set(v1, v1.end());
                lines.add(new DrawLine(v1.cpy().add(body.getPosition()), v1.end().cpy().add(body.getPosition())));
                lines.get(lines.size()-1).setColor(Color.PURPLE);
                body.createFixture(shape, 0);
            }

            // if this is the last hole in the wall, draw the wall segment after it too.
            if (i == holePositions.size() - 1) {

                // v2 describes the wall segment after this wall.
                v2 = new MyVector2((float)((holePositions.get(i) + holes.get(holePositions.get(i)) / 2) * Math.cos(p1.angle() * Math.PI / 180)),
                        (float)((holePositions.get(i) + holes.get(holePositions.get(i)) / 2) * Math.sin(p1.angle() * Math.PI / 180)),
                        Math.max(p1.len() - (holePositions.get(i) + holes.get(holePositions.get(i)) / 2), 0),
                        p1.angle());

                if (v2.len() > 0) {
                    EdgeShape shape2 = new EdgeShape();
                    shape2.set(v2, v2.end());
                    body.createFixture(shape2, 0);
                    lines.add(new DrawLine(v2.cpy().add(body.getPosition()), v2.end().cpy().add(body.getPosition())));
                    lines.get(lines.size() - 1).setColor(Color.ORANGE);
                }
            }
        }
    }

    @Override
    public void handleCollision(Fixture f) {
        if (C.ENABLE_WALL_DESRUCTION) {
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
}
