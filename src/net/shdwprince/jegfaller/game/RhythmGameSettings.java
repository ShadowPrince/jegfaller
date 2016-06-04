package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.states.RhythmGameState;

/**
 * Created by sp on 6/1/16.
 */
public class RhythmGameSettings {
    public float BodyGravity = 0.05f, BodyRotationSpeed = 0.1f;
    public int BodySpawnInterval = 8000, BodyFeverSpawnInterval = 2000, BodyDropInterval = 5000;

    public float CartMaxSpeed = 2.5f, CartMinSpeed = -2.f, CartSlowdown = 0.1f, CartBumpAmount = 8.f;

    public int GameThunderInterval = 15000, GameThunderChance = 5, GameThunderDuration = 350 + 250;
    public float GameHitOkInterval = 0.3f, GameHitGoodInterval = 0.1f, GameHitExcellentInterval = 0.03f;
    public float GameHitOkHeat = 1.f, GameHitGoodHeat = 1.5f, GameHitExcellentHeat = 3.f,GameHitMissHeat = -1.f;
    public float GameHeatDrain = 0.01f, GameInitialHeat = 0.5f, GameFeverHeat = 100.f;
    public int GameFeverDuration = 10000, GameFeverThunderDuration = 400;

    private static RhythmGameSettings currentSettings;

    public static void instantiateDefaultSettings() {
        currentSettings = new RhythmGameSettings();
    }

    public static void setDefaultSettings(RhythmGameSettings settings) {
        currentSettings = settings;
    }

    public static RhythmGameSettings currentSettings() {
        if (currentSettings == null)
            throw new NullPointerException("currentSettings is not yet initialized!");

        return currentSettings;
    }
}
