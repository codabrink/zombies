package com.zombies;

public class C {
    public static final float SCALE = 1f;

    //DEBUG
    public static final boolean DEBUG = true;
    public static final int[] DIRECTIONS = {0, 90, 180, 270};
    public static final float HALLWAY_WIDTH = 4f * SCALE;

    public static final long THREAD_DELAY = 200l;
    public static final long THREAD_INTERVAL = 200l;

    public static final boolean ENABLE_ACCEL = false;
    public static final boolean ENABLE_WALL_DESTRUCTION = true;
    public static final boolean ENABLE_DEBUG_LINES = true;
    public static final float BOX_DEPTH = 7 * SCALE;
    public static final float GRIDSIZE = 15 * SCALE;
    public static final float GRID_HALF_SIZE = GRIDSIZE / 2;
    public static final int BULLET_DAMAGE_FACTOR = 2;
    public static final float BULLET_RADIUS = 0.1f * SCALE;
    public static final long CRATE_MPOS_DURATION = 500l;
    public static final boolean ENABLE_SHOTGUN = true;
    public static final float SHOTGUN_CHANCE = 0.25f;
    public static final int SHOTGUN_AMMO = 20;
    public static final boolean ENABLE_PISTOL = true;
    public static final float PISTOL_CHANCE = 0.25f;
    public static final boolean ENABLE_SURVIVORS = false;
    public static final boolean ENABLE_CRATES = true;
    public static final float HEALTH_CHANCE = 0.1f;
    public static final boolean ENABLE_HEALTH = true;
    public static final float CRATE_CHANCE = 0.4f;
    public static final float GUNBOX_WIDTH = 0.15f;
    public static final float LINEAR_DAMPING = 2.5f;
    public static final float PLAYER_HEALTH = 100;
    public static final float PLAYER_SIZE = 1f * SCALE;
    public static final float PLAYER_SPEED = 10f * SCALE;
    public static final long PULSE_RATE = 800l;
    public static final int SCORE_FIND_SURVIVOR = 1000;
    public static final int SCORE_ZOMBIE_KILL = 100;
    public static final float SURVIVOR_CHANCE = 0.07f;
    public static final long SURVIVOR_FIRE_RATE = 250;
    public static final float SURVIVOR_HEALTH = 60;
    public static final float SURVIVOR_WAKE_DIST = 10f;
    public static final float TILT_IGNORE = 5f;
    public static final float TILT_SENSITIVITY = 0.8f;
    public static final long SHOTGUN_SPEED = 600l;
    public static final int UPDATE_LIGHTING_INTERVAL = 8; //number of frames between light updates
    public static final float ZOMBIE_AGILITY = 8f;
    public static final float ZOMBIE_AWARENESS = 0.001f;
    public static final long ZOMBIE_ATTACK_RATE = 1000l;
    public static final int   ZOMBIE_HEALTH = 6;
    public static final float ZOMBIE_SIZE = 1f;
    public static final float ZOMBIE_SPEED = 15f;
    public static final float ZOMBIE_STRENGTH = 7f;

    public static final float ZONE_SIZE = 100 * SCALE;
    public static final float ZONE_HALF_SIZE = ZONE_SIZE / 2;
    public static int DRAW_DISTANCE = 0;
    public static final float FOV = 90;
    public static final int ERROR_TOLERANCE = 4;

    public static boolean DEBUG_SHOW_BOXMAP = false;
    public static boolean DEBUG_SHOW_ADJBOXCOUNT = false;

    public static final boolean POPULATE_ZOMBIES = true;
    public static final boolean POPULATE_SURVIVORS = true;
}