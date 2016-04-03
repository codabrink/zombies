package com.map;

import com.badlogic.gdx.math.Vector2;
import com.interfaces.Overlappable;
import com.zombies.Box;
import com.zombies.GameView;
import com.zombies.Zone;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by coda on 3/31/2016.
 */
public class Hallway {
    public static int MAX_HALLWAY_SEGMENTS = 4;

    ArrayList<Vector2> axises = new ArrayList<Vector2>();
    private Random r;
    private ArrayList<HallwaySegment> hallwaySegments = new ArrayList<HallwaySegment>();
    private char lastDirection;
    private Box originBox;
    private float diameter;

    public Hallway(Box b, char direction, float width) {
        r = GameView.gv.random;
        originBox = b;
        diameter = width;
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
        HallwaySegment hs = new HallwaySegment(axises.get(axises.size()-1), axises.get(axises.size()-1).cpy().add(modifiers[0], modifiers[1]), diameter);
        Overlappable o = originBoxZone().checkOverlap(hs.position, hs.width, hs.height);

        if (o != null) {
            // reign back hallway
            float edge = o.oppositeEdge(lastDirection);
            if (modifiers[0] != 0) hs.a2.x = edge;
            else if (modifiers[1] != 0) hs.a2.y = edge;
            hallwaySegments.add(hs);
            materialize();
        } else if (axises.size() < MAX_HALLWAY_SEGMENTS) {
            hallwaySegments.add(hs);
            char newDirection;
            do {
                newDirection = MapGen.DIRECTIONS[r.nextInt(4)];
            } while(newDirection == lastDirection);
            move(newDirection);
        } else {
            materialize();
        }

    }

    private void materialize() {
        for (HallwaySegment hs: hallwaySegments) {
            hs.materialize();
        }
    }

    private Zone originBoxZone() {
        return Zone.getZone(originBox.getPosition());
    }

    private float horizBoxRange(Box b, float width) {
        return b.getPosition().x + r.nextFloat() * (b.width - width) + width / 2;
    }
    private float vertBoxRange(Box b, float width) {
        return b.getPosition().y + r.nextFloat() * (b.height - width) + width / 2;
    }
}
