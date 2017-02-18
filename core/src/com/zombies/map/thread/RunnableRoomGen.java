package com.zombies.map.thread;

import com.badlogic.gdx.math.Vector2;
import com.zombies.map.room.Room;

public class RunnableRoomGen implements Runnable {
    private Vector2 startPosition;
    private volatile Room room;

    public RunnableRoomGen() {
    }

    @Override
    public void run() {
    }

    public void setStartPosition(Vector2 startPosition) {
        this.startPosition = startPosition;
    }
    public Room getRoom() {
        return room;
    }
}
