package de.tomgrill.gdxtesting.examples;

import com.badlogic.gdx.math.Vector2;
import com.zombies.abstract_classes.Overlappable;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BenchmarkTest {

    @Test
    public void OverlappableTest() {
        Overlappable o1 = new Overlappable(new Vector2(0, 0), 100, 100);
        Overlappable o2 = new Overlappable(new Vector2(50, 50), 100, 100);
        Overlappable o3 = new Overlappable(new Vector2(100, 0), 100, 100);

        assertTrue(!o1.overlaps(o3));
        assertTrue(o1.overlaps(o2));


        long start = System.currentTimeMillis(), end;
        for (int i = 0; i < 1000000; i++) {
            o1.overlaps(o2);
        }
        end = System.currentTimeMillis();
        System.out.println("A million square overlap tests took " + (end - start) + " milliseconds.");
    }
}
