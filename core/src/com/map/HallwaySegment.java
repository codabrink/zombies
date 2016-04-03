package com.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.interfaces.Collideable;
import com.interfaces.Drawable;
import com.interfaces.Overlappable;
import com.util.Geometry;
import com.zombies.GameView;
import com.zombies.Wall;
import com.zombies.Zone;

import java.util.LinkedList;

/**
 * Created by coda on 4/2/16.
 */
public class HallwaySegment implements Overlappable, Drawable {
    private static int DRAWABLE_LAYER = 1;
    public Vector2 a1, a2, position;
    public float diameter, radius, width, height;
    private char direction;
    private LinkedList<Wall> walls = new LinkedList<Wall>();

    // only handles modulus 90 degree angles
    public HallwaySegment(Vector2 a1, Vector2 a2, float width) {
        this.a1  = a1;
        this.a2  = a2;
        diameter = width;
        radius   = width / 2;

        calculateInfo();
    }

    public void materialize() {
        calculateInfo(); // do this a second time
        createWalls();
        registerDrawable();
    }

    private void createWalls() {
        GameView.gv.addDebugDots(a1, Color.GREEN);
        GameView.gv.addDebugDots(a2, Color.RED);
        float dy = a2.y - a1.y;
        float dx = a2.x - a1.x;
        double angle = Math.atan(dy / dx);

        double angleRight = angle + Math.toRadians(90);
        double angleLeft  = angle - Math.toRadians(90);

        float radius = diameter / 2;
        Vector2 w1 = new Vector2(a1.cpy().add((float)(radius*Math.cos(angleRight)), (float)(radius*Math.sin(angleRight))));
        Vector2 w2 = new Vector2(a1.cpy().add((float)(radius*Math.cos(angleLeft)), (float)(radius*Math.sin(angleLeft))));

        System.out.println("rad: "+angle+", deg: "+Math.toDegrees(angle) + ", dx: "+dx+", dy: "+dy);

        walls.add(new Wall(w1, a1.dst(a2), (float) Math.toDegrees(angle)));
        walls.add(new Wall(w2, a1.dst(a2), (float) Math.toDegrees(angle)));
    }

    private void calculateInfo() {
        // calculate position
        if (a1.x < a2.x || a1.y < a2.y) {
            position = new Vector2(a1.x - radius, a1.y - radius);
        } else {
            position = new Vector2(a2.x - radius, a2.y - radius);
        }

        // calculate width and height
        width = Math.abs(a1.x - a2.x) + diameter;
        height = Math.abs(a1.y - a2.y) + diameter;

        // calculate direction
        if (a1.x < a2.x)
            direction = 'e';
        else if (a1.x > a2.x)
            direction = 'w';
        else if (a1.y < a2.y)
            direction = 'n';
        else if (a1.y > a2.y)
            direction = 's';
    }

    private void registerDrawable() {
        Zone.getZone(position).addDrawable(this, DRAWABLE_LAYER);
        Zone.getZone(position.cpy().add(0, height)).addDrawable(this, DRAWABLE_LAYER);
        Zone.getZone(position.cpy().add(width, height)).addDrawable(this, DRAWABLE_LAYER);
        Zone.getZone(position.cpy().add(width, 0)).addDrawable(this, DRAWABLE_LAYER);
    }

    @Override
    public String className() {
        return "HallwaySegment";
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        for (Wall w: walls) {
            w.draw(spriteBatch, shapeRenderer);
        }
    }

    @Override
    public boolean overlaps(float x, float y, float w, float h) {
        return Geometry.rectOverlap(position.x, position.y, width, height, x, y, w, h);
    }

    @Override
    public float edge(char direction) {
        switch(direction) {
            case 'n':
                return position.y + height;
            case 'e':
                return position.x + width;
            case 's':
                return position.y;
            case 'w':
                return position.x;
        }
        return 0;
    }

    @Override
    public float oppositeEdge(char direction) {
        switch(direction) {
            case 'n':
                return edge('s');
            case 'e':
                return edge('w');
            case 's':
                return edge('n');
            case 'w':
                return edge('e');
        }
        return 0;
    }
}
