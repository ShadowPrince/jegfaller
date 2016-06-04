package net.shdwprince.jegfaller.lib.sound;

import net.shdwprince.jegfaller.lib.util.SingleRandom;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import java.util.Vector;

/**
 * Created by sp on 6/3/16.
 */
public class SoundSet {
    protected float volume, pitch;
    public Vector<Sound> sounds;

    public SoundSet(String format, int length) throws SlickException {
        this.sounds = new Vector<>();
        for (int i = 1; i < length+1; i++) {
            this.sounds.add(new Sound(String.format(format, i)));
        }

        this.pitch = 1.f;
        this.volume = 0.1f;
    }

    public void playRandom() {
        this.sounds.elementAt(SingleRandom.nextInt(this.size())).play(this.pitch, this.volume);
    }

    public void playLast() {
        this.sounds.lastElement().play(this.pitch, this.volume);
    }

    public int size() {
        return this.sounds.size();
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
