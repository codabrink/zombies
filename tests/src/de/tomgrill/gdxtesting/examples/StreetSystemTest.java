package de.tomgrill.gdxtesting.examples;


import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.map.neighborhood.Intersection;
import com.zombies.map.neighborhood.StreetSystem;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class StreetSystemTest {

    @Test
    public void PopulateBoxTest() {
        Zombies instance = new Zombies();
        instance.setScreen(new GameView());
        GameView.gv.reset();

        Zone zone = Zone.getZone(0, 0);
        StreetSystem ss = new StreetSystem(new Vector2(0, 0));
        StreetSystem.populateBox(new Vector2(0, 0), C.ZONE_SIZE * 10, C.ZONE_SIZE * 10, StreetSystem.GRIDSIZE);

        zone.update();


        System.out.println(ss.getNodes().size());
        System.out.println("Zone node count: " + zone.getStreetNodes().size());
        System.out.println("Zone street count: " + zone.getStreets().size());
        System.out.println("Zone street segment count: " + zone.getStreetSegments().size());
        System.out.println(Zone.zones.size());

        assertTrue(zone.getStreetNodes().size() > 0);
    }

    @Test
    public void StreetTest() {
        GameView.gv.reset();

        StreetSystem ss = new StreetSystem(new Vector2(0, 0));
        StreetNode node1 = Intersection.createIntersection(ss, new Vector2(0, 0));
        StreetNode node2 = Intersection.createIntersection(ss, new Vector2(C.ZONE_SIZE * 3, 0), node1);

        Zone z0 = Zone.getZone(0, 0);
        z0.update();

        assertTrue(ss.getNodes().size() == 2);
        assertTrue(ss.getConnections().size() == 1);

        assertTrue(z0.getStreetSegments().size() == 1);
        assertTrue(z0.getStreets().size() == 1);

        Zone z1 = Zone.getZone(C.ZONE_SIZE, 0);
        z1.update();
        assertTrue(z1.getStreetSegments().size() == 1);
        assertTrue(z1.getStreets().size() == 1);

        Zone z2 = Zone.getZone(C.ZONE_SIZE * 2, 0);
        z2.update();
        assertTrue(z2.getStreetSegments().size() == 1);
        assertTrue(z2.getStreets().size() == 1);

        Zone z3 = Zone.getZone(C.ZONE_SIZE * 3, 0);
        z3.update();
        assertTrue(z3.getStreetSegments().size() == 0);
        assertTrue(z3.getStreets().size() == 0);
    }
}
