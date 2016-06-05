package net.shdwprince.jegfaller.lib.rhythm;

import org.lwjgl.Sys;
import org.newdawn.slick.Input;

/**
 * Created by sp on 6/4/16.
 */
public class RhythmInput {
    protected Input input;
    protected long[] keyPresses;
    protected long pressLiveInterval;
    protected long lastUpdate;

    public RhythmInput(Input input) {
        this.input = input;

        this.keyPresses = new long[255];
        this.pressLiveInterval = 250;
    }

    public void update() {
        if (System.currentTimeMillis() - this.lastUpdate < 77)
            return;

        this.lastUpdate = System.currentTimeMillis();

        int trackedKeys[] = new int[] {Input.KEY_UP, Input.KEY_DOWN, Input.KEY_LEFT, Input.KEY_RIGHT, Input.KEY_SPACE};

        for (int c : trackedKeys) {
            if (this.input.isKeyPressed(c)) {
                this.keyPresses[c] = this.pressLiveInterval + System.currentTimeMillis();
            }
        }
    }

    public String debug() {
        int trackedKeys[] = new int[] {Input.KEY_UP, Input.KEY_DOWN, Input.KEY_LEFT, Input.KEY_RIGHT, Input.KEY_SPACE};

        StringBuffer buf = new StringBuffer();
        for (int c : trackedKeys) {
            if (System.currentTimeMillis() < this.keyPresses[c]) {
                buf.append(String.format("%d: %d\n", c, this.keyPresses[c] - System.currentTimeMillis()));
            }
        }

        return buf.toString();
    }

    public boolean wasKeysPressed(int... keys) {
        for (int k : keys) {
            if (System.currentTimeMillis() > this.keyPresses[k]) {
                return false;
            }
        }

        for (int k : keys) {
            this.keyPresses[k] = 0;
        }

        return true;
    }
}
