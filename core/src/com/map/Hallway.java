package com.map;

import com.badlogic.gdx.math.Vector2;
import com.interfaces.Overlappable;
import com.util.Geometry;
import com.zombies.Box;
import com.zombies.GameView;
import com.zombies.Wall;
import com.zombies.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

/**
 * Created by coda on 3/31/2016.
 */
public class Hallway {
    public static int MAX_HALLWAY_SEGMENTS = 5;

    ArrayList<Vector2> axes = new ArrayList<Vector2>();
    private Random r;
    private ArrayList<Overlappable> hallwaySegments = new ArrayList<Overlappable>();
    private char lastDirection;
    private Box originBox;
    private Wall originWall;
    private float diameter;

    public Hallway(Box b, char direction, float width) {
        r = GameView.gv.random;
        originBox = b;
        diameter = width;
        double angle = 0;
        switch(direction) {
            case 'n':
                angle = Math.PI / 2;
                axes.add(new Vector2(horizBoxRange(b, width), b.getPosition().y + b.height));
                break;
            case 'e':
                angle = 0;
                axes.add(new Vector2(b.getPosition().x + b.width, vertBoxRange(b, width)));
                break;
            case 's':
                angle = Math.PI * 1.5;
                axes.add(new Vector2(horizBoxRange(b, width), b.getPosition().y));
                break;
            case 'w':
                angle = Math.PI;
                axes.add(new Vector2(b.getPosition().x, vertBoxRange(b, width)));
                break;
        }
        originWall = b.getWallsByDirection().get(direction);
        tryToMove(angle);
    }

    private float hallwayLength() { return r.nextFloat() * 10 + 5; }

    private Vector2 calculateNewAxis(double angle) {
        Vector2 a = axes.get(axes.size() - 1).cpy();
        float length = hallwayLength();
        return a.add((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }

    private void tryToMove(double angle) {
        Vector2 newAxis = calculateNewAxis(angle);
        HallwaySegment hs = new HallwaySegment(axes.get(axes.size()-1), newAxis, diameter, originWall);
        Overlappable o = originBoxZone().checkOverlap(hs.position, hs.width, hs.height, 1, new ArrayList<Overlappable>(Arrays.asList(originBox)));
        if (o == null)
            o = Geometry.checkOverlap(hs.position.x, hs.position.y, hs.width, hs.height, hallwaySegments);

        // if it's not the origin box or intersecting on the first axis
        if (o != null && o != originBox && (axes.size() <= 1 || (o instanceof Box && !originBox.isAdjacent((Box)o)))) {
            hs.a2.set(o.intersectPointOfLine(hs.a1, hs.a2));
            hallwaySegments.add(hs);
            materialize();
        } else {
            hallwaySegments.add(hs);
            axes.add(hs.getA2());
        }

        // max turn is +\- 90 degrees
        // TODO: allow straight movement
        if (axes.size() - 1 < MAX_HALLWAY_SEGMENTS)
            tryToMove(angle + (r.nextBoolean() ? 90 : -90));
        else
            materialize();
    }

    private void materialize() {
        for (Overlappable hs: hallwaySegments) {
            ((HallwaySegment)hs).materialize();
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
