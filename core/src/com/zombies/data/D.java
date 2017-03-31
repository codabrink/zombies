package com.zombies.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.zombies.Player;
import com.zombies.Zone;
import com.zombies.map.neighborhood.StreetSystem;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class D {
    public enum Worker { MAP_ADMIN, ROOM_DOOR }

    public static D d;

    public static long tick;
    public static Zone currentZone;
    public static Box currentBox;
    public static Room currentRoom() {
        if (currentBox != null)
            return currentBox.getRoom();
        return null;
    }
    public static ArrayList<Player> players = new ArrayList<>();
    public static Player player() { return players.get(0); }
    public static World world;
    public static Body groundBody;
    public static long mainThreadId;
    private static Set runningThreads = Collections.synchronizedSet(new HashSet<>());

    public static HashMap<Worker, Thread> workers;

    public static void reset() {
        tick = 0l;

        synchronized (runningThreads) {
            for (Object thread : runningThreads)
                ((Thread)thread).interrupt();
            runningThreads = Collections.synchronizedSet(new HashSet<>());
        }

        mainThreadId         = Thread.currentThread().getId();

        Zone.zones           = new HashMap<>();
        Zone.loadedZones     = new HashSet<>();
        StreetSystem.systems = new LinkedHashSet<>();

        world                = new World(new Vector2(), true);

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type    = BodyDef.BodyType.StaticBody;
        groundBody            = world.createBody(groundBodyDef);
        groundBody.setTransform(new Vector2(0, 0), 0);
    }

    public static void addRunningThread(Thread thread) {
        synchronized (runningThreads) {
            runningThreads.add(thread);
        }
    }
    public static void removeRunningThread(Thread thread) {
        synchronized (runningThreads) {
            runningThreads.remove(thread);
        }
    }

    public static void update() {
        currentZone = Zone.getZone(player().getPosition());
        currentBox  = currentZone.getBox(player().getPosition());
     }
}
