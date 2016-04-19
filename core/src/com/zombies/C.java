package com.zombies;

public class C {
    public static float scale = 0.8f;

    public static boolean ENABLE_ACCEL = false;
    public static float BOX_HEIGHT = 10 * scale;
    public static int BOX_MAX_ZOMBIES = 8;
    public static float BOX_SIZE = 15 * scale;
    public static float BOX_WIDTH = 15 * scale;
    public static int BULLET_DAMAGE_FACTOR = 2;
    public static long BULLET_LIFE = 300;
    public static float BULLET_SPEED = 100f;
    public static float BULLET_RADIUS = 0.1f * scale;
    public static float CANVAS_SCALE = 3;
    public static long CRATE_MPOS_DURATION = 500l;
    public static boolean DEV_MODE = true;
    public static float DOOR_SIZE = 2f; //DOOR_SIZE is relative to PLAYER_SIZE
    public static boolean DISABLE_LIGHTING = true;
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
    public static boolean FREE_MODE = false;
    public static int GRID_HEIGHT = 12; //height of map boxes
    public static int GRID_WIDTH = 12; //width of map boxes
    public static float GUNBOX_WIDTH = 0.15f;
    public static float HEALTH_RESTORE_RATE = 2f;
    public static float JOY_SIZE = .15f; //size of the joy stick
    public static float LIGHT_DIST = 25f;
    public static float LINEAR_DAMPING = 2.5f;
    public static boolean LOG_ORIENTATION = false;
    public static int MAX_BULLETS = 4;
    public static int MAX_ROOM_SIZE = 7;
    public static int MIN_ROOM_SIZE = 2;
    public static int PATH_LENGTH = 7;
    public static float PHYSICS_FACTOR = 2f;
    public static float PLAYER_AGILITY = 5f;
    public static long PLAYER_FIRE_RATE =  125;
    public static float PLAYER_HEALTH = 100;
    public static float PLAYER_SIZE = 1f * scale;
    public static float PLAYER_SPEED = 10f * scale;
    public static long PULSE_RATE = 800l;
    public static long RESTORE_HEALTH_TIME = 2000;
    public static float ROOM_ALPHA_RATE = 2f;
    public static int SCORE_DEATH = -1000;
    public static int SCORE_FIND_SURVIVOR = 1000;
    public static int SCORE_ZOMBIE_KILL = 100;
    public static float SHOOT_BUTTON_SIZE = 0.07f;
    public static float SURVIVOR_CHANCE = 0.07f;
    public static long SURVIVOR_FIRE_RATE = 250;
    public static float SURVIVOR_HEALTH = 60;
    public static float SURVIVOR_WAKE_DIST = 10f;
    public static long TIME_LIMIT = 120000;
    public static float TILT_IGNORE = 5f;
    public static float TILT_SENSITIVITY = 0.8f;
    public static long SHOTGUN_SPEED = 600l;
    public static boolean UPDATE_LIGHTING = false;
    public static int UPDATE_LIGHTING_INTERVAL = 8; //number of frames between light updates
    public static float ZOMBIE_AGILITY = 8f;
    public static float ZOMBIE_AWARENESS = 0.001f;
    public static long ZOMBIE_ATTACK_RATE = 1000l;
    public static float ZOMBIE_CHANGE_CHANCE = 0.03f;
    public static float ZOMBIE_GUT_SPREAD = 20f;
    public static int   ZOMBIE_HEALTH = 6;
    public static float ZOMBIE_SIZE = 1f;
    public static float ZOMBIE_SLEEP_SPEED = 3f;
    public static float ZOMBIE_SPEED = 15f;
    public static float ZOMBIE_STRENGTH = 7f;
    public static float ZOOM_LEVEL = 0f;

    public static int DRAW_LAYERS=3;

    public static float ZONE_SIZE = 100 * scale;

    public static float FOV = 60;

    //DEBUG
    public static boolean DEBUG = true;


    public static boolean POPULATE_ZOMBIES = true;
    public static boolean POPULATE_SURVIVORS = true;
}