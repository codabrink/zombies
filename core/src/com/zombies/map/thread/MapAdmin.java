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
        if (true) { // p.getZone().getBoxes().size() < 3) {
            Room room = (Room)U.random(Data.currentZone.getRooms());

            if (room != null) {
                Box box = (Box)U.random(room.getOuterBoxes());
                Thread thread = new Thread(new RunnableAdjRoom(box));
                threads.add(thread);
                thread.run();
            } else {
                System.out.println("MapAdmin: Cannot find room. Starting fresh.");
                Runnable r = new GenRoomOnPlayer(p);
                Thread thread = new Thread(r);
                threads.add(thread);
                thread.start();
            }
        }
    }
}

class GenRoomOnPlayer implements Runnable {
    private Player p;
    public GenRoomOnPlayer(Player p) {
        this.p = p;
    }
    public void run() {
        Generator.genRoom(p.getPosition());
    }
}