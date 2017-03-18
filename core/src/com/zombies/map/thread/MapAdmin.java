package com.zombies.map.thread;

import com.zombies.Zone;
import com.zombies.Zone.GENERATOR_STATE;
import com.zombies.data.D;

import java.util.Timer;
import java.util.TimerTask;

public class MapAdmin implements Runnable {
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            callback();
        }
    };
    public static boolean reset = false;

    private void callback() {
        if (reset) {
            reset();
            return;
        }

        Zone zone = D.currentZone;
        if (zone == null)
            return;

        for (Zone z : zone.getAdjZones(1)) {
            if (z.genState == GENERATOR_STATE.UNINITIATED) {
                z.genState = GENERATOR_STATE.GENERATING;
                new Thread(new GenZone(z)).start();
            }
        }
    }

    @Override
    public void run() {
        timer.scheduleAtFixedRate(task, 500, 500);
    }
    public void reset() {
        reset = false;
    }
}

class GenZone implements Runnable {
    private Zone zone;
    public GenZone(Zone z) {
        zone = z;
    }
    public void run() {
        D.addRunningThread(Thread.currentThread());
        Generator.generateZone(zone);
        D.removeRunningThread(Thread.currentThread());
    }
}