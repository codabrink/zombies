package de.tomgrill.gdxtesting.examples;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.interfaces.Gridable;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;
import com.zombies.util.U;

import java.util.HashMap;
import java.util.HashSet;

import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class ZoneAndNeighborhoodTest {

	@Test
	public void getAdjZone() {
		Zombies instance = new Zombies();
		instance.setScreen(new GameView());
        GameView.gv.reset();

        Zone zone         = Zone.getZone(0, 0);
        Building building = Building.createBuilding(new Vector2(0, 0), 3);

        building.compile();

        // process pending objects
        zone.update();

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
        GameView.gv.reset();

        Zone z1 = Zone.getZone(0, 0);
        Zone z2 = Zone.getZone(-1, 0);
        Zone z3 = Zone.getZone(-1, -1);
        Zone z4 = Zone.getZone(0, -1);

        assertTrue(z1.getRooms().size() == 0);
        assertTrue(z2.getRooms().size() == 0);
        assertTrue(z3.getRooms().size() == 0);
        assertTrue(z4.getRooms().size() == 0);

        Building building = Building.createBuilding(new Vector2(0, 0), 0);
        Room room = Room.createRoom(building, new int[]{0, 0}, 0);

        // gen cross room
        Box b00  = Box.createBox(room, new int[]{0,0});
        Box b10  = Box.createBox(room, new int[]{1,0});
        Box bn10 = Box.createBox(room, new int[]{-1,0});
        Box b01  = Box.createBox(room, new int[]{0,1});
        Box b0n1 = Box.createBox(room, new int[]{0,-1});

        building.compile();

        // process pendingObjects lists
        z1.update();
        z2.update();
        z3.update();
        z4.update();

        // assert corners
        assertTrue(b00.getCorners()[0].x == C.GRID_HALF_SIZE);
        assertTrue(b00.getCorners()[0].y == C.GRID_HALF_SIZE);
        assertTrue(b00.getCorners()[1].x == -C.GRID_HALF_SIZE);
        assertTrue(b00.getCorners()[1].y == C.GRID_HALF_SIZE);

        assertTrue(b10.getCorners()[0].x == C.GRID_HALF_SIZE + C.GRIDSIZE);
        assertTrue(b10.getCorners()[0].y == C.GRID_HALF_SIZE);
        assertTrue(b10.getCorners()[1].x == C.GRID_HALF_SIZE);
        assertTrue(b10.getCorners()[1].y == C.GRID_HALF_SIZE);
        assertTrue(b10.getCorners()[2].x == C.GRID_HALF_SIZE);
        assertTrue(b10.getCorners()[2].y == -C.GRID_HALF_SIZE);
        assertTrue(b10.getCorners()[3].x == C.GRID_HALF_SIZE + C.GRIDSIZE);
        assertTrue(b10.getCorners()[3].y == -C.GRID_HALF_SIZE);

        // assert centers
        assertTrue(b00.getCenter().x == 0);
        assertTrue(b00.getCenter().y == 0);
        assertTrue(b10.getCenter().x == C.GRIDSIZE);
        assertTrue(b10.getCenter().y == 0);
        assertTrue(bn10.getCenter().x == -C.GRIDSIZE);
        assertTrue(bn10.getCenter().y == 0);
        assertTrue(b01.getCenter().x == 0);
        assertTrue(b01.getCenter().y == C.GRIDSIZE);
        assertTrue(b0n1.getCenter().x == 0);
        assertTrue(b0n1.getCenter().y == -C.GRIDSIZE);

        // zone 1
        assertTrue(room.getZone() == z1);
        assertTrue(z1.getRooms().size() == 1);
        assertTrue(z1.getBoxes().size() == 3);
        assertTrue(z1.getBoxes().contains(building.gridMap.get("0,0")));
        assertTrue(z1.getBoxes().contains(building.gridMap.get("1,0")));
        assertTrue(z1.getBoxes().contains(building.gridMap.get("0,1")));

        // zone 2
        assertTrue(z2.getRooms().size() == 1);
        assertTrue(z2.getBoxes().size() == 1);
        assertTrue(z2.getBoxes().contains(building.gridMap.get("-1,0")));

        // zone 3
        assertTrue(z3.getRooms().size() == 0);
        assertTrue(z3.getBoxes().size() == 0);

        // zone 4
        assertTrue(z4.getRooms().size() == 1);
        assertTrue(z4.getBoxes().size() == 1);
        assertTrue(z4.getBoxes().contains(building.gridMap.get("0,-1")));

        assertTrue(building.wallMap.get("0,-1,v") != null);
        assertTrue(building.wallMap.get("0,-1,h") != null);
        assertTrue(building.wallMap.get("1,1,h") != null);

        // Test building features
        Gridable g1 = building.gridMap.get("0,0");
        Gridable g2 = building.gridMap.get("1,0");
        assertTrue(Building.wallKeyBetweenKeys(g1, g2).equals("1,0,v"));
        g2     = building.gridMap.get("0,1");
        assertTrue(Building.wallKeyBetweenKeys(g1, g2).equals("0,1,h"));

        // test vertical wall positions
        Vector2[] positions = building.wallPositionOf("1,0,v");
        Vector2 expectedPosition = building.getCenter().cpy().sub(C.GRID_HALF_SIZE, C.GRID_HALF_SIZE).add(C.GRIDSIZE, 0);
        assertTrue(positions[0].x == expectedPosition.x);
        assertTrue(positions[0].y == expectedPosition.y);
        expectedPosition.add(0, C.GRIDSIZE);
        assertTrue(positions[1].x == expectedPosition.x);
        assertTrue(positions[1].y == expectedPosition.y);

        // test horizontal wall positions
        positions = building.wallPositionOf("0,1,h");
        expectedPosition = building.getCenter().cpy().sub(C.GRID_HALF_SIZE, C.GRID_HALF_SIZE).add(0, C.GRIDSIZE);
        assertTrue(positions[0].x == expectedPosition.x);
        assertTrue(positions[0].y == expectedPosition.y);
        expectedPosition.add(C.GRIDSIZE, 0);
        assertTrue(positions[1].x == expectedPosition.x);
        assertTrue(positions[1].y == expectedPosition.y);

        assertTrue(building.xLow == -1);
        assertTrue(building.xHigh == 1);
        assertTrue(building.yLow == -1);
        assertTrue(building.yHigh == 1);

        assertTrue(bn10 == (Box)U.random(building.boxesOnCol(building.xLow))); // left
        assertTrue(b01 == (Box)U.random(building.boxesOnRow(building.yHigh))); // top
        assertTrue(b10 == (Box)U.random(building.boxesOnCol(building.xHigh))); // right
        assertTrue(b0n1 == (Box)U.random(building.boxesOnRow(building.yLow))); // bottom
    }

    @Test
    public void testDisplacedRoomGen() {
        GameView.gv.reset();

        Building building = Building.createBuilding(new Vector2(500, 500), 0);
        Room room = Room.createRoom(building, new int[]{0, 0}, 0);

        // gen cross room
        Box b00  = Box.createBox(room, new int[]{0,0});
        Box b10  = Box.createBox(room, new int[]{1,0});
        Box bn10 = Box.createBox(room, new int[]{-1,0});
        Box b01  = Box.createBox(room, new int[]{0,1});
        Box b0n1 = Box.createBox(room, new int[]{0,-1});

        room = Room.createRoom(building, new int[]{-1, 1}, 0);
        Box newBox = Box.createBox(room, new int[]{-1, 1});

        building.compile();

        Vector2 expectedPosition = building.getCenter().cpy();
        expectedPosition.sub(C.GRID_HALF_SIZE, C.GRID_HALF_SIZE);
        expectedPosition.add(-C.GRIDSIZE, C.GRIDSIZE);

        assertTrue(newBox.getPosition().x == expectedPosition.x);
        assertTrue(newBox.getPosition().y == expectedPosition.y);
    }
}
