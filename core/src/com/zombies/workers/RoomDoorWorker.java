package com.zombies.workers;

import com.zombies.C;
import com.zombies.map.room.Box;
import com.zombies.map.room.Building;
import com.zombies.map.room.Room;

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
            r.getZone().readyToModel.add(r);
            itr.remove();
        }
    }

    private void processDoorsOnRoom(Room room) {
        HashSet<Room> adjRooms = new HashSet<>();
        HashMap<String, HashMap<String, Box[]>> potentialConnections = new HashMap<>();
        Random rand = new Random();

        HashMap<String, Box[]> doorMap;
        for (Box b1 : room.getBoxes()) {
            for (Box b2 : b1.getAdjBoxes()) {
                if (b2.getRoom() != room)
                    adjRooms.add(b2.getRoom());

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
                    connectRooms(connection[0], connection[1], roomsKey, (String)pair.getKey());
                else if (rand.nextFloat() < 0.3f)
                    connectRooms(connection[0], connection[1], roomsKey, (String)pair.getKey());
            }
        }
    }

    private void connectRooms(Box b1, Box b2, String roomKey, String boxKey) {
        initRoomConnectionList(b1, b2, roomKey);

        // do not generate door twice
        if (checkDoorExistence(b1, roomKey, boxKey))
            return;

        // u stands for "un-generated"
        b1.getRoom().doors.get(roomKey).put("u" + boxKey, new Box[]{b1, b2});
        b2.getRoom().doors.get(roomKey).put("u" + boxKey, new Box[]{b2, b1});

        if (b1.getRoom().connected == true)
            b2.getRoom().connected = true;
        if (b2.getRoom().connected == true)
            b1.getRoom().connected = true;
    }

    // true - exists, false - doesn't exist
    private boolean checkDoorExistence(Box b, String roomKey, String boxKey) {
        if (b.getRoom().doors.get(roomKey).get("u" + boxKey) != null)
            return true;
        if (b.getRoom().doors.get(roomKey).get(boxKey) != null)
            return true;
        return false;
    }

    private void initRoomConnectionList(Box b1, Box b2, String roomKey) {
        if (b1.getRoom().doors.get(roomKey) == null)
            b1.getRoom().doors.put(roomKey, new HashMap<String, Box[]>());
        if (b2.getRoom().doors.get(roomKey) == null)
            b2.getRoom().doors.put(roomKey, new HashMap<String, Box[]>());
    }
}
