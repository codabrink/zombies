package de.tomgrill.gdxtesting.examples;


import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombies;
import com.zombies.Zone;
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
        System.out.println(Zone.zones.size());

        assertTrue(zone.getStreetNodes().size() > 0);
    }
}
