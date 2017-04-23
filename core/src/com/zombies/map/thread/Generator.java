package com.zombies.map.thread;

import com.zombies.C;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.map.building.Building;

public class Generator {
    private static Thread thread;

    public static void update() {
        if (thread != null && thread.isAlive())
            return;

        for (Zone z: D.currentZone.getAdjZones(C.DRAW_DISTANCE)) {
            if (z.genState != Zone.GENERATOR_STATE.UNINITIATED)
                continue;

            z.genState = Zone.GENERATOR_STATE.GENERATING;
            thread = new Thread(new NeighborhoodGenerator(z));
            thread.start();
            break;
        }
    }

    static class NeighborhoodGenerator implements Runnable {
        private Zone zone;
        public NeighborhoodGenerator(Zone zone) {
            this.zone = zone;
        }

        @Override
        public void run() {
            D.addRunningThread(Thread.currentThread());
            //StreetSystem.populateBox(zone.randomPosition(), C.ZONE_SIZE * 3, C.ZONE_SIZE * 3, GameView.r.nextInt((int) C.GRIDSIZE * 3));
            Building.createBuilding(zone.getCenter(), 3);
            zone.rebuildModel();
            D.removeRunningThread(Thread.currentThread());
        }
    }
}
