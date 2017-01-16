package com.zombies.data;

import com.zombies.Player;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;

import java.util.ArrayList;

public class Data {
    public static Zone currentZone;
    public static Box currentBox;
    public static Room currentRoom;
    public static ArrayList<Player> players = new ArrayList<>();
    public static Player player() { return players.get(0); }
}
