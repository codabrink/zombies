package com.zombies;

public class C {

	public boolean DESKTOP_MODE = false;
	public boolean ENABLE_ACCEL = false;
	public int BOX_HEIGHT = 15;
	public int BOX_MAX_ZOMBIES = 8;
	public int BOX_SIZE = 15; //x-y size of the small grid for each box
	public int BOX_WIDTH = 15;
	public int BULLET_DAMAGE_FACTOR = 2;
	public long BULLET_LIFE = 300;
	public float BULLET_SPEED = 100f;
	public float BULLET_RADIUS = 0.1f;
	public float CANVAS_SCALE = 3;
	public long CRATE_MPOS_DURATION = 500l;
	public boolean DEV_MODE = true;
	public float DOOR_SIZE = 2f; //DOOR_SIZE is relative to PLAYER_SIZE
	public boolean DISABLE_LIGHTING = true;
	public boolean ENABLE_SHOTGUN = true;
	public float SHOTGUN_CHANCE = 0.25f;
	public int SHOTGUN_AMMO = 20;
	public boolean ENABLE_PISTOL = true;
	public float PISTOL_CHANCE = 0.25f;
	public boolean ENABLE_SURVIVORS = false;
	public boolean ENABLE_CRATES = true;
	public float HEALTH_CHANCE = 0.1f;
	public boolean ENABLE_HEALTH = true;
	public float CRATE_CHANCE = 0.4f;
	public boolean FREE_MODE = false;
	public int GRID_HEIGHT = 12; //height of map boxes
	public int GRID_WIDTH = 12; //width of map boxes
	public float GUNBOX_WIDTH = 0.15f;
	public float HEALTH_RESTORE_RATE = 2f;
	public float JOY_SIZE = .15f; //size of the joy stick
	public float LIGHT_DIST = 25f;
	public float LINEAR_DAMPING = 0.9f;
	public boolean LOG_ORIENTATION = false;
	public int MAX_BULLETS = 4;
	public int MAX_ROOM_SIZE = 7;
	public int MIN_ROOM_SIZE = 2;
	public int PATH_LENGTH = 7;
	public float PHYSICS_FACTOR = 2f;
	public float PLAYER_AGILITY = 5f;
	public long PLAYER_FIRE_RATE =  125;
	public float PLAYER_HEALTH = 100;
	public float PLAYER_SIZE = 1f; //player draw size
	public float PLAYER_SPEED = 10f; //how fast the player will move
	public long PULSE_RATE = 800l;
	public long RESTORE_HEALTH_TIME = 2000;
	public float ROOM_ALPHA_RATE = 2f;
	public int SCORE_DEATH = -1000;
	public int SCORE_FIND_SURVIVOR = 1000;
	public int SCORE_ZOMBIE_KILL = 100;
	public float SHOOT_BUTTON_SIZE = 0.07f;
	public float SURVIVOR_CHANCE = 0.07f;
	public long SURVIVOR_FIRE_RATE = 250;
	public float SURVIVOR_HEALTH = 20;
	public float SURVIVOR_WAKE_DIST = 10f;
	public long TIME_LIMIT = 120000;
	public float TILT_IGNORE = 5f;
	public float TILT_SENSITIVITY = 0.8f;
	public long SHOTGUN_SPEED = 600l;
	public boolean UPDATE_LIGHTING = false;
	public int UPDATE_LIGHTING_INTERVAL = 8; //number of frames between light updates
	public float ZOMBIE_AGILITY = 8f;
	public float ZOMBIE_AWARENESS = 0.001f;
	public long ZOMBIE_ATTACK_RATE = 1000l;
	public float ZOMBIE_CHANGE_CHANCE = 0.03f;
	public float ZOMBIE_GUT_SPREAD = 20f;
	public int   ZOMBIE_HEALTH = 6;
	public float ZOMBIE_SIZE = 1f;
	public float ZOMBIE_SLEEP_SPEED = 3f;
	public float ZOMBIE_SPEED = 15f;
	public float ZOMBIE_STRENGTH = 7f;
	public float ZOOM_LEVEL = 0f;

    //DEBUG
    public boolean DEBUG_BULLETS = true;


    public boolean POPULATE_ZOMBIES = true;
    public boolean POPULATE_SURVIVORS = false;
}