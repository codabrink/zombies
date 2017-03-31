package com.zombies.workers;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;
import com.zombies.map.room.DoorWall;
import com.zombies.util.Assets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RoomDoorWorker implements Runnable {
    public static LinkedList<Room> roomList = new LinkedList<>();
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            callback();
        }
    };
    @Override
    public void run() { timer.scheduleAtFixedRate(task, C.THREAD_INTERVAL, C.THREAD_DELAY); }

    private void callback() {
        Room r;
        Iterator itr = roomList.iterator();
        while (itr.hasNext()) {
            r = (Room)itr.next();
            processDoorsOnRoom(r);
            itr.remove();
        }
    }

    public static void processDoorsOnRoom(Room room) {
        HashSet<Room> adjRooms = new HashSet<>();
        HashMap<String, HashMap<String, Box[]>> potentialConnections = new HashMap<>();
        Random rand = new Random();

        HashMap<String, Box[]> doorMap;
        for (Box b1 : room.getBoxes()) {
            for (Box b2 : b1.getAdjBoxes()) {
                if (b2.getRoom() == room)
                    continue;

                String roomKey = room.giveKey(b2.getRoom());
                if (potentialConnections.get(roomKey) == null)
                    potentialConnections.put(roomKey, new HashMap<String, Box[]>());

                doorMap = potentialConnections.get(roomKey);
                String boxKey = b1.giveKey(b2);
                if (doorMap.get(boxKey) == null)
                    doorMap.put(boxKey, new Box[]{b1, b2});
            }
        }

        HashMap<String, Box[]> connectionList;
        Box[] connection;
        for (String roomsKey : potentialConnections.keySet()) {
            connectionList = potentialConnections.get(roomsKey);

            Iterator itr = connectionList.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry pair = (Map.Entry)itr.next();
                connection = (Box[])pair.getValue();
                Room otherRoom = connection[0].getRoom() == room ? connection[1].getRoom() : connection[0].getRoom();
                if ((!room.connected || !otherRoom.connected) && !itr.hasNext())
                    connectRooms(connection[0], connection[1], roomsKey);
                else if (rand.nextFloat() < 0.3f)
                    connectRooms(connection[0], connection[1], roomsKey);
            }
        }
    }

    private static void connectRooms(Box b1, Box b2, String roomKey) {
        Building building = b1.getBuilding();
        String wallMapKey = building.wallKeyBetweenGridables(b1, b2);
        Vector2[] positions = building.wallPositionOf(wallMapKey);

        // do not generate door twice
        if (checkDoorExistence(b1, roomKey, wallMapKey))
            return;

        new DoorWall(positions[0], positions[1], building, Assets.MATERIAL.WALL_WHITE_WALLPAPER);

        if (b1.getRoom().connected == true)
            b2.getRoom().connected = true;
        if (b2.getRoom().connected == true)
            b1.getRoom().connected = true;
    }

    // true - exists, false - doesn't exist
    private static boolean checkDoorExistence(Box b, String roomKey, String wallKey) {
        return false;
    }
}
