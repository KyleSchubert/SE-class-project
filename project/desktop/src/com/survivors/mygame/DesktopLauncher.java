package com.survivors.mygame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.survivors.mygame.MyGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1440, 900); // 1440x900 looks kind of OK
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setTitle("Survivors");
        new Lwjgl3Application(new MyGame(), config);
    }
}
