package com.zombies.data;

import com.zombies.Player;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;

import java.util.ArrayList;
import java.util.HashMap;

public class Data {
    public static Zone currentZone;
    public static Box currentBox;
    public static Room currentRoom() {
        if (currentBox != null)
            return currentBox.getRoom();
        return null;
    }
    public static ArrayList<Player> players = new ArrayList<>();
    public static Player player() { return players.get(0); }

    public static HashMap<String, Thread> workers = new HashMap<>();

    public static void update() {
        currentZone = Zone.getZone(player().getPosition());
        currentBox  = currentZone.getBox(player().getPosition());
        if (currentBox != null)
            currentBox.getBuilding().update();
     }
}
