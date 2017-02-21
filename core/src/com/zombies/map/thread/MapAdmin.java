package com.zombies.map.thread;

import com.zombies.Player;
import com.zombies.data.Data;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;
import com.zombies.util.U;

import java.util.HashSet;

public class MapAdmin {
    public static HashSet<Thread> threads = new HashSet<>();

    public static void update(Player p) {
        if (false)
            return;

        Room room = Data.currentRoom();

        if (room != null) {
            if (room.getBuilding().getRooms().size() > 7 || room.genState != Room.GenState.FINALIZED)
                return;

            Box box = (Box)U.random(room.getOuterBoxes());

            if (box == null)
                return;

            Thread thread = new Thread(new RunnableAdjRoom(box));
            threads.add(thread);
            thread.run();
        } else {
            Runnable r = new GenRoomOnPlayer(p);
            Thread thread = new Thread(r);
            threads.add(thread);
            thread.run();
        }
    }
}

class GenRoomOnPlayer implements Runnable {
    private Player p;
    public GenRoomOnPlayer(Player p) {
        this.p = p;
    }
    public void run() {
        Generator.genRoom(new Building(p.getPosition().cpy()), new int[]{0, 0});
    }
}