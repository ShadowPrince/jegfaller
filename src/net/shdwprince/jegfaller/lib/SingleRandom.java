package net.shdwprince.jegfaller.lib;

import java.util.Random;

/**
 * Created by sp on 6/1/16.
 */
public class SingleRandom {
    private static Random rand;

    private static void init() {
        if (rand == null) {
            rand = new Random();
        }
    }

    public static int nextInt(int max) {
        SingleRandom.init();
        return rand.nextInt(max);
    }
}
