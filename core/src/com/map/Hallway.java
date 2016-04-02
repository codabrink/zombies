package com.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.interfaces.Drawable;
import com.zombies.Box;
import com.zombies.GameView;
import com.zombies.Wall;
import com.zombies.Zone;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by coda on 3/31/2016.
 */
public class Hallway implements Drawable {
    public static int MAX_HALLWAY_SEGMENTS = 20;
    public static float WIDTH = 2;

    ArrayList<Vector2> axises = new ArrayList<Vector2>();
    private Random r;
    private ArrayList<Zone> zones = new ArrayList<Zone>();
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private char lastDirection;
    private Box originBox;

    public Hallway(Box b, char direction, float width) {
        r = GameView.gv.random;
        originBox = b;
        // assuming the box direction is null (empty)
        switch(direction) {
            case 'n':
                axises.add(new Vector2(horizBoxRange(b, width), b.getPosition().y + b.height));
                break;
            case 'e':
                axises.add(new Vector2(b.getPosition().x + b.width, vertBoxRange(b, width)));
                break;
            case 's':
                axises.add(new Vector2(horizBoxRange(b, width), b.getPosition().y));
                break;
            case 'w':
                axises.add(new Vector2(b.getPosition().x, vertBoxRange(b, width)));
                break;
        }
        move(direction);
    }

    private float hallwayLength() {
        return r.nextFloat() * 10 + 5;
    }

    // set lastDirection first, please
    private void move(char direction) {
        lastDirection = direction;
        switch(direction) {
            case 'n':
                move(new float[] {0, hallwayLength()});
                break;
            case 'e':
                move(new float[] {hallwayLength(), 0});
                break;
            case 's':
                move(new float[] {0, -hallwayLength()});
                break;
            case 'w':
                move(new float[] {-hallwayLength(), 0});
                break;
        }
    }

    private void move(float[] modifiers) {
        updateZones();
        Vector2 newPosition = axises.get(axises.size()-1).cpy().add(modifiers[0], modifiers[1]);
        Box b = collides(newPosition);
        if (b != null) {
            // reign back hallway
            float edge = b.oppositeEdge(lastDirection);
            if (modifiers[0] != 0) newPosition.x = edge;
            else if (modifiers[1] != 0) newPosition.y = edge;
            axises.add(newPosition);
        } else if (axises.size() < MAX_HALLWAY_SEGMENTS) {
            axises.add(newPosition);
            char newDirection;
            do {
                newDirection = MapGen.DIRECTIONS[r.nextInt(4)];
            } while(newDirection == lastDirection);
            move(newDirection);
        } else {
            axises.add(newPosition);
            rasterize();
        }
    }

    private void rasterize() {
        for (int i = 0; i < axises.size(); i++) {
            if (i < axises.size() - 1)
                createParallelWalls(axises.get(i), axises.get(i + 1));
            Zone.getZone(axises.get(i)).addDrawable(this);
        }

    }

    private void createParallelWalls(Vector2 v1, Vector2 v2) {
        float a = Math.abs(v1.y - v2.y);
        float b = Math.abs(v1.x - v2.x);
        float c = (float)Math.sqrt(a*a+b*b);

        double angle = Math.asin(a/c);
        double angleRight = angle + Math.toRadians(90);
        double angleLeft  = angle - Math.toRadians(90);

        float radius = WIDTH / 2;
        Vector2 w1 = new Vector2(v1.cpy().add((float)(radius*Math.cos(angleRight)), (float)(radius*Math.sin(angleRight))));
        Vector2 w2 = new Vector2(v1.cpy().add((float)(radius*Math.cos(angleLeft)), (float)(radius*Math.sin(angleLeft))));

        walls.add(new Wall(w1, v1.dst(v2), (float)Math.toDegrees(angle)));
        walls.add(new Wall(w2, v1.dst(v2), (float)Math.toDegrees(angle)));
    }

    private void updateZones() {
        Zone z;
        for (Vector2 v: axises) {
            z = Zone.getZone(v.x, v.y);
            if (zones.indexOf(z) == -1)
                zones.add(z);
        }
    }

    private Box collides(Vector2 v) {
        Box b;
        for (Zone z: zones) {
            b = MapGen.collides(z, v, WIDTH, WIDTH);
            if (b != null && b != originBox)
                return b;
        }
        //TODO: this definitely checks zones twice
        for (Zone z: zones) {
            for (Zone zz: z.getAdjZones()) {
                b = MapGen.collides(zz, v, 1, 1);
                if (b != null && b != originBox)
                    return b;
            }
        }
        return null;
    }

    private float horizBoxRange(Box b, float width) {
        return b.getPosition().x + r.nextFloat() * (b.width - width) + width / 2;
    }
    private float vertBoxRange(Box b, float width) {
        return b.getPosition().y + r.nextFloat() * (b.height - width) + width / 2;
    }

    @Override
    public String className() {
        return "Hallway";
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer) {
        for (Wall w: walls) {
            w.draw(spriteBatch, shapeRenderer);
        }
    }
}
