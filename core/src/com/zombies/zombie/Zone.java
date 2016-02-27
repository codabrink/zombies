package com.zombies.zombie;

import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombie;

import java.util.ArrayList;

/**
 * Created by coda on 2/27/2016.
 */
public class Zone {
    private int x, y, frame, fsAdjCheck=0;
    private GameView view;
    private ArrayList<Zone> adjZones;
    public ArrayList<Zombie> zombies;

    public Zone(GameView view, int x, int y) {
        this.view = view;this.x = x;this.y = y;
    }

    public void update(int frame, int limit) {
        if (this.frame == frame)
            return;
        this.frame = frame;

        if (adjZones.size() < 8) {
            fsAdjCheck++;
            if (fsAdjCheck > 20)
                checkNearbyZones();
        }

        for (Zombie z: zombies) {
            z.update(frame);
        }

        if (limit > 0)
            for (Zone z: adjZones) {
                z.update(frame, limit - 1);
            }
    }

    private void checkNearbyZones() {
        for (int i = y-1; i <= y+1; i++) {
            for (int j = x-1; j <= x+1; j++) {
                Zone z = view.zones.get(j).get(i);
                if (z != this && adjZones.indexOf(z) != -1) {
                    adjZones.add(z);
                }
            }
        }
        fsAdjCheck = 0;
    }

    public static Zone getZone(float x, float y) {
        int indX = (int)(x / C.ZONE_SIZE);
        int indY = (int)(y / C.ZONE_SIZE);
        return GameView.m.zones.get(indX).get(indY);
    }
}
