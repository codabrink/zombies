package com.zombies.zombie;

import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombie;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by coda on 2/27/2016.
 */
public class Zone {
    private int x, y, frame, fsAdjCheck=0;
    private ArrayList<Zone> adjZones = new ArrayList<Zone>();
    public ArrayList<Zombie> zombies = new ArrayList<Zombie>();

    public Zone(int x, int y) {
        this.x = x;this.y = y;
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
                Zone z = Zone.getZone(j, i);
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

        // get x row
        ArrayList<Zone> rowX;
        try {
            rowX = GameView.zones.get(indX);
        } catch (IndexOutOfBoundsException e) {
            rowX = new ArrayList<Zone>();
            GameView.zones.add(indX, rowX);
        }

        // get zone
        try {
            return rowX.get(indY);
        } catch (IndexOutOfBoundsException e) {
            Zone z = new Zone(indX, indY);
            rowX.add(indY, z);
            return z;
        }
    }
}
