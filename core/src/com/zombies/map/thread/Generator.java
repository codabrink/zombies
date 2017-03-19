package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.interfaces.Gridable;
import com.zombies.map.Hallway;
import com.zombies.map.HallwaySegment;
import com.zombies.map.neighborhood.Street;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;
import com.zombies.util.U;
import com.zombies.workers.RoomDoorWorker;

import java.util.Random;

public class Generator {
    public static void generateZone(Zone zone) {
        // I know this is probably already set to GENERATING.
        zone.genState = Zone.GENERATOR_STATE.GENERATING;

        // Generate roads
        // Check if zone already has a road
        if (zone.getStreetSegments().size() > 0) {

        }

        // Search for nearby roads
        final float streetSearchRadius = C.GRID_SIZE * 30;
        for (Zone z : zone.getAdjZones(1)) {
            for (Street s : z.getStreets())
        }
    }

    public static Building genFullBuilding(Gridable g, int direction) {
        int[] key = Building.directionToBMKey(g.getKey(), direction);
        Vector2 center = g.getBuilding().positionOf(key).add(C.GRID_HALF_SIZE, C.GRID_HALF_SIZE);
        Building newBuilding = Building.createBuilding(center, 3);
        if (newBuilding == null)
            return null;

        if (g instanceof HallwaySegment)
            ((HallwaySegment)g).connect(newBuilding.gridMapGet(new int[]{0, 0}), direction);

        return newBuilding;
    }
}
