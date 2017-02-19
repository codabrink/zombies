package com.zombies.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zombies.Zombies;

public class DesktopLauncher {
    public static boolean DESKTOP = true;

    public static void main (String[] arg) {
        new LwjglApplication(new Zombies(), "Zombie Surge", 1900, 1024);
    }
}
