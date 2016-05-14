package com.zombies;

public class C {
    public static float scale = 1f;

    public static boolean ENABLE_ACCEL = false;
    public static boolean ENABLE_WALL_DESRUCTION = true;
    public static boolean ENABLE_DEBUG_LINES = true;
    public static float BOX_HEIGHT = 10 * scale;
    public static float BOX_SIZE = 15 * scale;
    public static float BOX_WIDTH = 15 * scale;
    public static int BULLET_DAMAGE_FACTOR = 2;
    public static float BULLET_RADIUS = 0.1f * scale;
    public static long CRATE_MPOS_DURATION = 500l;
    public static boolean ENABLE_SHOTGUN = true;
    public static float SHOTGUN_CHANCE = 0.25f;
    public static int SHOTGUN_AMMO = 20;
    public static boolean ENABLE_PISTOL = true;
    public static float PISTOL_CHANCE = 0.25f;
    public static boolean ENABLE_SURVIVORS = false;
    public static boolean ENABLE_CRATES = true;
    public static float HEALTH_CHANCE = 0.1f;
    public static boolean ENABLE_HEALTH = true;
    public static float CRATE_CHANCE = 0.4f;
    public static int GRID_HEIGHT = 12; //height of map boxes
    public static int GRID_WIDTH = 12; //width of map boxes
    public static float GUNBOX_WIDTH = 0.15f;
    public static float LINEAR_DAMPING = 2.5f;
    public static float PLAYER_HEALTH = 100;
    public static float PLAYER_SIZE = 1f * scale;
    public static float PLAYER_SPEED = 10f * scale;
    public static long PULSE_RATE = 800l;
    public static int SCORE_FIND_SURVIVOR = 1000;
    public static int SCORE_ZOMBIE_KILL = 100;
    public static float SURVIVOR_CHANCE = 0.07f;
    public static long SURVIVOR_FIRE_RATE = 250;
    public static float SURVIVOR_HEALTH = 60;
    public static float SURVIVOR_WAKE_DIST = 10f;
    public static float TILT_IGNORE = 5f;
    public static float TILT_SENSITIVITY = 0.8f;
    public static long SHOTGUN_SPEED = 600l;
    public static int UPDATE_LIGHTING_INTERVAL = 8; //number of frames between light updates
    public static float ZOMBIE_AGILITY = 8f;
    public static float ZOMBIE_AWARENESS = 0.001f;
    public static long ZOMBIE_ATTACK_RATE = 1000l;
    public static int   ZOMBIE_HEALTH = 6;
    public static float ZOMBIE_SIZE = 1f;
    public static float ZOMBIE_SPEED = 15f;
    public static float ZOMBIE_STRENGTH = 7f;

    public static int DRAW_LAYERS=3;
    public static float ZONE_SIZE = 100 * scale;
    public static int DRAW_DISTANCE = 1;
    public static float FOV = 60;

    //DEBUG
    public static boolean DEBUG = true;


    public static boolean POPULATE_ZOMBIES = true;
    public static boolean POPULATE_SURVIVORS = true;
}