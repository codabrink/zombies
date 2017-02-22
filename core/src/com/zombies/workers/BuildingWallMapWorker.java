package com.zombies.workers;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.Zone;
import com.zombies.data.Data;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Wall;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class BuildingWallMapWorker implements Runnable {
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            callback();
        }
    };
    private boolean running = false;
    @Override
    public void run() { timer.scheduleAtFixedRate(task, C.THREAD_INTERVAL, C.THREAD_DELAY); }

    private void callback() {
        if (running)
            return;
        running = true;

        if (Data.currentBox != null)
            if (Data.currentBox.getBuilding().wallMapState == Building.DataState.BAD)
                buildWallMap(Data.currentBox.getBuilding());
        for (Zone z : Data.currentZone.getAdjZones(1))
            for (Building b : z.getBuildings())
                if (b.wallMapState == Building.DataState.BAD)
                    buildWallMap(b);

        running = false;
    }

    private void buildWallMap(Building building) {
        building.wallMapState = Building.DataState.PROCESSING;
        building.wallMap = new HashMap<>();
        for (Box b : building.boxMap.values()) {
            if (building.wallMapState == Building.DataState.BAD) { // Data has been invalidated. Run again.
                run();
                return;
            }

            Vector2 queryPoint = new Vector2(b.getPosition()).add(0, C.BOX_RADIUS);
            HashSet<Wall> walls = Zone.getWallsOverlappingCircle(queryPoint, 1);
            if (walls.size() == 1)
                building.wallMap.put(b.getSKey() + ",u", walls.iterator().next());
            else
                System.out.println("Building: Found more than 1 wall..");

            queryPoint = new Vector2(b.getPosition()).add(C.BOX_RADIUS, 0);
            walls      = Zone.getWallsOverlappingCircle(queryPoint, 1);
            if (walls.size() == 1)
                building.wallMap.put(b.getSKey() + ",r", walls.iterator().next());
            else
                System.out.println("Building: Found more than 1 wall..");
        }
        building.wallMapState = Building.DataState.GOOD;
    }
}
