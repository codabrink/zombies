package com.zombies.map.thread;

import com.zombies.Player;
import com.zombies.data.Data;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;
import com.zombies.util.U;

import java.util.HashSet;

public class MapAdmin {
    public static HashSet<Thread> threads = new HashSet<>();

    public static void update(Player p) {
        if (p.getZone().getBoxes().size() < 3) {
            Room room = (Room)U.random(Data.currentZone.getRooms());

            if (room != null) {
                Box box = (Box)U.random(room.getOuterBoxes());
                Thread thread = new Thread(new RunnableAdjRoom(box));
                threads.add(thread);
                thread.run();
            } else {
                System.out.println("MapAdmin: No room found");
            }
        }
    }
}
