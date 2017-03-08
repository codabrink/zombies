package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.interfaces.Gridable;
import com.zombies.map.Hallway;
import com.zombies.map.HallwaySegment;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;
import com.zombies.util.U;
import com.zombies.workers.RoomDoorWorker;

import java.util.Random;

public class Generator {
    public static Building genFullBuilding(Gridable g, int direction) {
        int[] key = Building.directionToBMKey(g.getKey(), direction);
        Vector2 center = g.getBuilding().positionOf(key).add(C.GRID_HALF_SIZE, C.GRID_HALF_SIZE);
        Building newBuilding = genBuilding(center);
        if (newBuilding == null)
            return null;

        if (g instanceof HallwaySegment)
            ((HallwaySegment)g).connect(newBuilding.gridMapGet(new int[]{0,0}), direction);

        modelBuilding(newBuilding);
        return newBuilding;
    }

    public static Building genFullBuilding(Vector2 center) {
        Building newBuilding = genBuilding(center);
        if (newBuilding == null)
            return null;
        modelBuilding(newBuilding);
        return newBuilding;
    }

    private static Building genBuilding(Vector2 center) {
        Building building = new Building(center);
        Zone z            = Zone.getZone(center);

        int failures = 0;

        // initial room
        Room initialRoom = genRoom(building, new int[]{0, 0});
        if (initialRoom == null)
            return null;
        initialRoom.connected = true;

        while (building.getRooms().size() < 7 && failures < 20) {
            Box b = (Box)U.random(building.getOuterBoxes());
            int[] key = (int[])U.random(b.getOpenAdjKeys());

            if (genRoom(building, key) == null)
                failures++;
        }

        building.calculateBorders();

        Box b;
        // place hallways
        // right hallway (xHigh)
        b = (Box)U.random(building.boxesOnCol(building.xHigh));
        int[] newKey = Building.directionToBMKey(b.getKey(), 0);
        new Hallway(b, newKey);

        // top hallway (yHigh)
        b = (Box)U.random(building.boxesOnRow(building.yHigh));
        newKey = Building.directionToBMKey(b.getKey(), 1);
        new Hallway(b, newKey);

        // left hallway (xLow)
        b = (Box)U.random(building.boxesOnCol(building.xLow));
        newKey = Building.directionToBMKey(b.getKey(), 2);
        new Hallway(b, newKey);

        // bottom hallway (yLow)
        b = (Box)U.random(building.boxesOnRow(building.yLow));
        newKey = Building.directionToBMKey(b.getKey(), 3);
        new Hallway(b, newKey);

        building.compile();

        for (Room room : building.getRooms())
            RoomDoorWorker.processDoorsOnRoom(room);

        return building;
    }

    private static void modelBuilding(Building building) {
        //GameView.gv.addReadyToModel(building);
        building.rebuildModel();
    }

    public static Room genRoom(Building building, int[] bmKey) {
        Vector2 startPos = building.positionOf(bmKey);
        Zone z = Zone.getZone(startPos);
        Random r = new Random();

        if (building.checkOverlap(bmKey) != null)
            return null;

        Room room = new Room(building);

        Box b;
        new Box(room, bmKey);

        int roomSize = r.nextInt(3) + 10, loops = 0;
        while (room.boxes.size() <= roomSize) {
            b = (Box) U.random(room.getOuterBoxes());

            if (b == null)
                break;

            bmKey = (int[]) U.random(b.getOpenAdjKeys());
            if (bmKey == null)
                break;

            z = Zone.getZone(building.positionOf(bmKey));

            if (building.checkOverlap(bmKey) == null)
                new Box(room, bmKey);

            loops++;
            if (loops > roomSize * 4) // catch infinite loops
                break;
        }

        D.update();
        return room;
    }
}
