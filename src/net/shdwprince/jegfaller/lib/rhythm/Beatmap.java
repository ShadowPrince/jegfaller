package net.shdwprince.jegfaller.lib.rhythm;

import org.newdawn.slick.Music;
import org.newdawn.slick.openal.Audio;

import java.io.*;
import java.lang.reflect.Field;

/**
 * Created by sp on 5/27/16.
 */
public class Beatmap implements Serializable {
    public class Hit {
        public int idx, action;
        public float diff;

        public Hit(int idx, float diff, int action) {
            this.idx = idx;
            this.diff = diff;
            this.action = action;
        }
    }

    public float beatSize, beatOffset, totalLength;
    public int[] actions;

    public static Beatmap beatmapBasedOn(String path) throws Exception {
        Beatmap result = new Beatmap();

        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(path));
        result.actions = (int[]) stream.readObject();
        result.beatOffset = stream.readFloat();
        result.beatSize = stream.readFloat();
        result.totalLength = stream.readFloat();

        stream.close();
        return result;
    }

    public void saveIn(String path) throws Exception {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(path));
        stream.writeObject(this.actions);
        stream.writeFloat(this.beatOffset);
        stream.writeFloat(this.beatSize);
        stream.writeFloat(this.totalLength);
        stream.close();
    }

    protected int nextIndexFrom(float offset) {
        int index = 0;
        for (float start = this.beatOffset; start < offset; start += this.beatSize, index++);
        return index;
    }

    protected int prevIndexFrom(float offset) {
        int index = 0;
        for (float start = this.beatOffset; start + this.beatSize < offset; start += this.beatSize, index++);
        return index;
    }

    public float nextOffsetFrom(float offset) {
        float start = this.beatOffset;
        for (; start < offset; start += this.beatSize);
        return start;
    }

    public float prevOffsetFrom(float offset) {
        float start = this.beatOffset;
        for (; start + this.beatSize < offset; start += this.beatSize);
        return start;
    }

    public Hit closestHitFrom(float offset) {
        float nextDelta = this.nextOffsetFrom(offset) - offset;
        float prevDelta = offset - this.prevOffsetFrom(offset);

        int idx;
        float delta;
        if (nextDelta < prevDelta) {
            idx = this.nextIndexFrom(offset);
            delta = nextDelta;
        } else {
            idx = this.prevIndexFrom(offset);
            delta = prevDelta;
        }

        int action = this.actions[idx];
        return new Hit(idx, delta, action);
    }

    public Hit previousHitFrom(float offset) {
        float delta = offset - this.prevOffsetFrom(offset);
        int idx = this.prevIndexFrom(offset);

        int action = this.actions[idx];
        return new Hit(idx, delta, action);
    }

    public Hit nextHitFrom(float offset) {
        float delta = this.nextOffsetFrom(offset) - offset;
        int idx = this.nextIndexFrom(offset);

        int action = this.actions[idx];
        return new Hit(idx, delta, action);
    }

    public String stringNameOfAction(int action) {
        switch (action) {
            case 1: return "/\\";
            case 2: return "/\\>";
            case 3: return ">";
            case 4: return "\\/>";

            case 5: return "\\/";
            case 6: return "\\/<";

            case 7: return "<";
            case 8: return "/\\<";
            default: return "";
        }
    }

    public void totalLengthFrom(Music music) {
        try {
            Field field = music.getClass().getDeclaredField("sound");
            field.setAccessible(true);
            Audio audio = (Audio) field.get(music);
            field = audio.getClass().getDeclaredField("length");
            field.setAccessible(true);

            this.totalLength = (Float) field.get(audio);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createActionsArray() {
        int maxIndex = (int) Math.floor((this.totalLength - this.beatOffset) / this.beatSize);
        if (this.actions == null) {
            this.actions = new int[maxIndex + 1];
        } else {
            int[] oldActions = this.actions;
            this.actions = new int[maxIndex + 1];

            System.arraycopy(oldActions, 0, this.actions, 0, Integer.min(oldActions.length, this.actions.length));
        }
    }
}
