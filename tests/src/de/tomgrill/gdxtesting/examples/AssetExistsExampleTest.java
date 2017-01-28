package de.tomgrill.gdxtesting.examples;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.map.MapGen;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;
import com.zombies.map.thread.Generator;

import java.util.HashMap;
import java.util.HashSet;

import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class AssetExistsExampleTest {

	@Test
	public void getAdjZone() {
		Zombies instance = new Zombies();
		instance.setScreen(new GameView());

        Zone zone = Zone.getZone(0, 0);
        Room room = Generator.genRoom(new Vector2(300, 300));
        Generator.zoneRoom(room);

        // assert a room is generating
		assertTrue(zone.getRooms().size() > 0);

        // assert getAdjZones works
        HashSet<Zone> adjZones = zone.getAdjZones(1);
        HashMap<String, Zone> adjZonesMap = new HashMap<String, Zone>();
        assertTrue(adjZones.size() == 9);

        for (Zone z : adjZones)
            adjZonesMap.put(z.getKey(), z);

        assertTrue(adjZonesMap.get("-1,-1") != null);
        assertTrue(adjZonesMap.get("0,-1")  != null);
        assertTrue(adjZonesMap.get("1,-1")  != null);
        assertTrue(adjZonesMap.get("-1,0")  != null);
        assertTrue(adjZonesMap.get("0,0")   != null);
        assertTrue(adjZonesMap.get("1,0")   != null);
        assertTrue(adjZonesMap.get("-1,1")  != null);
        assertTrue(adjZonesMap.get("0,1")   != null);
        assertTrue(adjZonesMap.get("1,1")   != null);

        adjZones = zone.getAdjZones(2);
        assertTrue(adjZones.size() == 25);
    }

    @Test
    public void testRoomZoning() {
        HashMap<String, Box> boxMap = new HashMap<String, Box>();
        Box originBox = new Box(-C.BOX_RADIUS, -C.BOX_RADIUS);
        boxMap.put("0,0", originBox);
        boxMap.put("1,0", new Box(originBox.getPosition().cpy().add(C.BOX_DIAMETER, 0)));
        boxMap.put("-1,0", new Box(originBox.getPosition().cpy().add(-C.BOX_DIAMETER, 0)));

        boxMap.put("0,1", new Box(originBox.getPosition().cpy().add(0, C.BOX_DIAMETER)));
        boxMap.put("0,-1", new Box(originBox.getPosition().cpy().add(0, -C.BOX_DIAMETER)));

        Room r = new Room(boxMap);
        Generator.zoneRoom(r);

        Zone z1 = Zone.getZone(0, 0);
        Zone z2 = Zone.getZone(-1, 0);
        Zone z3 = Zone.getZone(-1, -1);
        Zone z4 = Zone.getZone(0, -1);

        System.out.println();
    }
}
