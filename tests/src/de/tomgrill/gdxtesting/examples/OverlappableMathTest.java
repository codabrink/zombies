package de.tomgrill.gdxtesting.examples;

import com.badlogic.gdx.math.Vector2;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.util.G;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OverlappableMathTest {

    @Test
    public void OverlappableTest() {
        Overlappable o1 = new Overlappable(new Vector2(0, 0), 100, 100);
        Overlappable o2 = new Overlappable(new Vector2(50, 50), 100, 100);
        Overlappable o3 = new Overlappable(new Vector2(100, 0), 100, 100);

        assertTrue(o1.overlaps(o2));
        assertTrue(!o1.overlaps(o3));
        assertTrue(o1.contains(50, 50));
        assertTrue(!o1.contains(150, 150));

        long start = System.currentTimeMillis(), end;
        for (int i = 0; i < 1000000; i++)
            o1.overlaps(o2);
        end = System.currentTimeMillis();
        System.out.println("A million square overlap tests took " + (end - start) + " milliseconds.");

        float[] line = G.line(new Vector2(2, 3), new Vector2(3, 3));
        float[] line2 = G.line(new Vector2(0, 3), new Vector2(2, 3));
    }
}
