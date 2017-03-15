package com.zombies.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.zombies.Player;
import com.zombies.Zone;
import com.zombies.map.room.Box;
import com.zombies.map.room.Room;

import java.util.ArrayList;
import java.util.HashMap;

public class D {
    public enum Worker { MAP_ADMIN, ROOM_DOOR }

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

    public static HashMap<Worker, Thread> workers;

    public static void reset() {
        tick = 0l;

        world = new World(new Vector2(), true);

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBody = world.createBody(groundBodyDef);
        groundBody.setTransform(new Vector2(0, 0), 0);
    }

    public static void update() {
        currentZone = Zone.getZone(player().getPosition());
        currentBox  = currentZone.getBox(player().getPosition());
     }
}
