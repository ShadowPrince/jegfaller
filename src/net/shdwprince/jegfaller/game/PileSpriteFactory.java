package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.lib.util.SingleRandom;
import net.shdwprince.jegfaller.lib.entities.Entity;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * Created by sp on 6/1/16.
 */
public class PileSpriteFactory {
    protected int duration, size;
    protected float xDensity, yDensity;
    protected Image[][] singleFireSprites;

    public PileSpriteFactory(String[][] singleSpriteRefs, float xDensity, float yDensity, int duration) throws SlickException {
        int minSize = -1;
        this.singleFireSprites = new Image[singleSpriteRefs.length][];
        for (int i = 0; i < singleSpriteRefs.length; i++) {
            this.singleFireSprites[i] = new Image[singleSpriteRefs[i].length];
            int n = 0;
            for (n = 0; n < singleSpriteRefs[i].length; n++) {
                this.singleFireSprites[i][n] = new Image(singleSpriteRefs[i][n]);
            }

            if (minSize == -1 || minSize > n) {
                minSize = n;
            }
        }

        this.size = minSize + 1;
        this.xDensity = xDensity;
        this.yDensity = yDensity;
        this.duration = duration;
    }

    public PileSpriteFactory(SpriteSheet sheet, float xDensity, float yDensity, int duration) throws SlickException {
        this.singleFireSprites = new Image[sheet.getVerticalCount()][sheet.getHorizontalCount()];
        for (int i = 0; i < sheet.getVerticalCount(); i++) {
            for (int n = 0; n < sheet.getHorizontalCount(); n++) {
                this.singleFireSprites[i][n] = sheet.getSprite(i, n);
            }
        }

        this.size = sheet.getHorizontalCount();
        this.xDensity = xDensity;
        this.yDensity = yDensity;
        this.duration = duration;
    }

    public Entity pileSpriteWithSize(float w, float h) throws SlickException {
        Image[] frames = new Image[this.size];
        for (int i = 0; i < this.size; i++) {
            Image frame = new Image((int) w, (int) h);

            int xRand = 20, yRand = 10;
            for (float x = 0; x < w;) {
                Image resultingSprite = null;
                for (float y = 0; y < h;) {
                    int id = (int) Math.abs(Math.cos(x + y)) * 100;
                    int singleFrameIndex = id % this.singleFireSprites.length;
                    int singleFrameNumber = (id + i) % this.size;

                    float resultingX = x + SingleRandom.nextInt(xRand) - xRand/2;
                    float resultingY = y + SingleRandom.nextInt(yRand) - yRand/2;
                    resultingSprite = this.singleFireSprites[singleFrameIndex][singleFrameNumber];
                    System.out.println(String.format("%d;%d. id %d, anim size %d", singleFrameIndex, singleFrameNumber, id, this.size));

                    if (resultingX + resultingSprite.getWidth() > w)
                        resultingX = w - resultingSprite.getWidth();
                    if (resultingY + resultingSprite.getHeight() > h)
                        resultingY = h - resultingSprite.getHeight();

                    y += resultingSprite.getHeight() * this.yDensity;
                    frame.getGraphics().drawImage(resultingSprite, resultingX, resultingY);
                }

                x += resultingSprite.getWidth() * this.xDensity;
            }

            frame.getGraphics().flush();
            frames[i] = frame;
        }

        Entity e = new Entity(frames, this.duration);
        e.getAnimation().start();
        return e;
    }

    public int maxSpriteHeight() {
        int max = 0;
        for (Image[] frames : this.singleFireSprites) {
            if (frames[0].getHeight() > max)
                max = frames[0].getHeight();
        }

        return max;
    }
}
