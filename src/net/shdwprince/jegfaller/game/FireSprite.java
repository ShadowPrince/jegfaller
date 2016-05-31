package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.lib.SingleRandom;
import net.shdwprince.jegfaller.lib.entities.Entity;
import net.shdwprince.jegfaller.lib.entities.EntityManager;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.util.Random;

/**
 * Created by sp on 6/1/16.
 */
public class FireSprite extends Entity {
    public FireSprite(Image[] images) {
        super(images, 300);
    }

    private static Image[] singleFireSprites;
    public static void loadSingleFireSprites() throws SlickException {
        singleFireSprites = new Image[] {new Image("assets/fire1.png")};
    }

    public static FireSprite fireSpriteWithSize(int w, int h) throws SlickException {
        int animSize = 1;
        Image[] frames = new Image[animSize];
        for (int i = 0; i < animSize; i++) {
            Image frame = new Image(w, h);

            float xDensity = 0.5f, yDensity = 0.5f;
            int xRand = 10, yRand = 3;
            for (float x = 0; x < w; x+=xDensity) {
                for (float y = 0; y < h; y+=yDensity) {
                    int id = (int) Math.abs(Math.cos(x + y)) * 100;
                    int singleFrameNumber = (id + i) % animSize;
                    frame.getGraphics().drawImage(
                            FireSprite.singleFireSprites[singleFrameNumber],
                            x + SingleRandom.nextInt(xRand) - xRand/2,
                            y + SingleRandom.nextInt(yRand) - yRand/2);
                }
            }

            frames[i] = frame;
        }

        return new FireSprite(frames);
    }
}
