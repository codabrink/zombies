package com.zombies.map.thread;

import com.zombies.C;
import com.zombies.Zone;
import com.zombies.data.D;
import com.zombies.map.neighborhood.StreetSystem;

public class Generator {
    private static Thread thread;

    public static void update() {
        if (thread != null && thread.isAlive())
            return;

        if (D.currentZone == null)
            D.update();

        for (Zone z: D.currentZone.getAdjZones(1)) {
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
            StreetSystem.populateBox(zone.getPosition(), C.ZONE_SIZE, C.ZONE_SIZE, StreetSystem.GRIDSIZE);
            D.removeRunningThread(Thread.currentThread());
        }
    }
}
