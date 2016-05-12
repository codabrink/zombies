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
import java.util.Random;

/**
 * Created by coda on 3/31/2016.
 */
public class Hallway {
    public static int MAX_HALLWAY_SEGMENTS = 2;

    ArrayList<Vector2> axes = new ArrayList<Vector2>();
    private Random r;
    private ArrayList<Overlappable> hallwaySegments = new ArrayList<Overlappable>();
    private Box originBox;
    private Wall originWall;
    private float diameter;
    private double totalAngle = 0;

    public Hallway(Box b, char direction, float width) {
        long startTime = System.currentTimeMillis();
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
        tryToMove(angle, angle);
    }

    private float hallwayLength() { return r.nextFloat() * 10 + 15; }

    private Vector2 calculateNewAxis(double angle) {
        Vector2 a = axes.get(axes.size() - 1).cpy();
        float length = hallwayLength();
        return a.add((float)(length * Math.cos(angle)), (float)(length * Math.sin(angle)));
    }

    private void tryToMove(double angle, double previousSegmentAngle) {
        Vector2 newAxis = calculateNewAxis(angle);
        HallwaySegment hs = new HallwaySegment(axes.get(axes.size()-1), newAxis, diameter, previousSegmentAngle);
        Overlappable o = originBoxZone().checkOverlap(hs.position, hs.width, hs.height, 1, new ArrayList<Overlappable>(Arrays.asList(originBox)));
        if (o == null)
            o = Geometry.checkOverlap(hs.position.x, hs.position.y, hs.width, hs.height, hallwaySegments);

        // I would love to use lambdas here to reduce redundancy, but that's not introduced until Java 8, so... yeah
        if (o != null) {
            if (axes.size() > 1) { // is fist segment
                if (o instanceof Box && o != originBox && !originBox.isAdjacent((Box)o)) { // is not an origin box or adjacent box
                    Vector2 ip = o.intersectPointOfLine(hs.p1, hs.p2); // set to a variable for debugging..
                    hs.p2.set(ip);
                    addHallwaySegment(hs);
                    materialize();
                } else { // collision is an origin box or adjacent box
                    addHallwaySegment(hs);
                }
            } else { // is a second or more segment
                Vector2 ip = o.intersectPointOfLine(hs.p1, hs.p2); // set to a variable for debugging...
                hs.p2.set(ip);
                addHallwaySegment(hs);
                materialize();
            }
         } else { // in the clear, just add the segment
            addHallwaySegment(hs);
        }

        if (hallwaySegments.size() - 1 < MAX_HALLWAY_SEGMENTS) {
            double nextDeltaAngle = 0;
            switch (r.nextInt(2)) {
                case 0: nextDeltaAngle = -Math.PI / 2; break;
                case 1: nextDeltaAngle = 0; break;
                case 2: nextDeltaAngle = Math.PI / 2; break;
            }
            ((HallwaySegment)hallwaySegments.get(hallwaySegments.size() - 1)).setNextSegmentAngle(angle + nextDeltaAngle);
            tryToMove(angle + nextDeltaAngle, angle);
        } else {
            materialize();
        }
    }

    private void addHallwaySegment(HallwaySegment hs) {
        hallwaySegments.add(hs);
        axes.add(hs.getP2());
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
