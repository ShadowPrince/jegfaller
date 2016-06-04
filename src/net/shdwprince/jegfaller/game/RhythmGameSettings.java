package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.states.RhythmGameState;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Vector;

/**
 * Created by sp on 6/1/16.
 */
public class RhythmGameSettings {
    public float BodyGravity = 0.25f, BodyRotationSpeed = 0.1f;
    public int BodySpawnInterval = 8000, BodyFeverSpawnInterval = 2000, BodyDropInterval = 5000;

    public float CartMaxSpeed = 2.5f, CartMinSpeed = -2.f, CartSlowdown = 0.1f, CartBumpAmount = 8.f;

    public int GameThunderInterval = 15000, GameThunderChance = 5, GameThunderDuration = 350 + 250;
    public float GameHitOkInterval = 0.15f, GameHitGoodInterval = 0.08f, GameHitExcellentInterval = 0.03f;
    public float GameHitOkHeat = 1.f, GameHitGoodHeat = 1.5f, GameHitExcellentHeat = 3.f,GameHitMissHeat = -1.f;
    public float GameHeatDrain = 0.01f, GameInitialHeat = 50f, GameFeverHeat = 100.f;
    public int GameFeverDuration = 10000;

    private static RhythmGameSettings currentSettings;

    public static void instantiateDefaultSettings() {
        currentSettings = new RhythmGameSettings();
    }

    public static void setCurrentSettings(RhythmGameSettings settings) {
        currentSettings = settings;
    }

    public static RhythmGameSettings currentSettings() {
        if (currentSettings == null)
            throw new NullPointerException("currentSettings is not yet initialized!");

        return currentSettings;
    }

    public static RhythmGameSettings loadFromFileAt(String pathRef) throws Exception {
        RhythmGameSettings result = new RhythmGameSettings();
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(pathRef));

        String key;
        try {
            while ((key = (String) stream.readObject()) != null) {
                result.getClass().getDeclaredField(key).set(result, stream.readObject());
            }
        } catch (EOFException e) {}

        stream.close();
        return result;
    }

    public void saveToFileAt(String pathRef) throws Exception {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(pathRef));

        for (Field f : RhythmGameSettings.serializationFields()) {
            stream.writeObject(f.getName());
            stream.writeObject(f.get(this));
        }

        stream.close();
    }

    public static Vector<Field> serializationFields() {
        Vector<Field> result = new Vector<>();

        for (Field f : RhythmGameSettings.class.getDeclaredFields()) {
            if (!Objects.equals(f.getName(), "currentSettings")) {
                result.add(f);
            }
        }

        return result;
    }
}
