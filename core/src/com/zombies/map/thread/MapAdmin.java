package com.zombies.map.thread;

import com.zombies.Player;
import com.zombies.data.D;
import com.zombies.map.Hallway;
import com.zombies.map.HallwaySegment;
import com.zombies.map.room.Room;
import com.zombies.util.U;

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

        Room room = D.currentRoom();

        if (room != null) {
            Hallway h = (Hallway)U.random(room.getBuilding().getHallways());
            HallwaySegment s = h.segments.get(h.segments.size() - 1);
            (new Thread(new RunnableAdjRoom(s))).start();
        } else {
            Runnable r = new GenRoomOnPlayer(D.player());
            (new Thread(r)).start();
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

class GenRoomOnPlayer implements Runnable {
    private Player p;
    public GenRoomOnPlayer(Player p) {
        this.p = p;
    }
    public void run() {
        Generator.genFullBuilding(p.getPosition().cpy());
    }
}