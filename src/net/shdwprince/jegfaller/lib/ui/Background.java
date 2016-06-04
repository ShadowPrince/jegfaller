package net.shdwprince.jegfaller.lib.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.w3c.dom.css.Rect;

import javax.net.ssl.HostnameVerifier;
import java.util.Random;
import java.util.Vector;

/**
 * Created by sp on 5/29/16.
 */
public class Background {
    protected Image background, pavement;
    protected Image[] houseImages;
    protected float[][] housePositions;
    protected int lastHouse;
    protected Rectangle position;
    protected float backgroundX, pavementX;

    protected final float HOUSES_OFFSET = 70.f;

    protected Random random;

    public Background(Rectangle position) throws SlickException {
        super();
        this.random = new Random();

        this.position = position;
        this.background = new Image("assets/bg.png");
        this.pavement = new Image("assets/pavement.png");
        this.houseImages = new Image[] {
                new Image("assets/house1.png"),
                new Image("assets/house2.png"),
                new Image("assets/house1.png"),
                new Image("assets/house2.png"),

        };
        this.housePositions = new float[this.houseImages.length][2];
        for (int i = 0; i < this.housePositions.length; i++) {
            this.housePositions[i] = new float[] {-Float.MAX_VALUE, 0};
        }

        this.housePositions[0] = new float[] {0.f, 0.f};
        this.lastHouse = 0;
    }

    protected void drawParallaxImage(Image image, float x, float y, Graphics graphics) {
        graphics.drawImage(image, x, y);
        graphics.drawImage(image, x + image.getWidth(), y);
    }

    public void render(Graphics graphics) {
        this.drawParallaxImage(this.background, this.backgroundX, 0, graphics);
        this.drawParallaxImage(this.pavement, this.pavementX, this.position.getHeight() - 80, graphics);

        for (int i = 0; i < this.houseImages.length; i++) {
            graphics.drawImage(
                    this.houseImages[i],
                    this.housePositions[i][0],
                    this.position.getHeight() - this.houseImages[i].getHeight() - HOUSES_OFFSET - this.housePositions[i][1]);
        }
    }

    public void update() {
        this.backgroundX -= 0.3f;
        if (this.backgroundX <= -this.background.getWidth()) {
            this.backgroundX = 0;
        }

        this.pavementX -= 0.9f;
        if (this.pavementX <= -this.pavement.getWidth()) {
            this.pavementX = 0.f;
        }

        for (int i = 0; i < this.houseImages.length; i++) {
            if (this.housePositions[i][0] <= -this.houseImages[i].getWidth()) {
                float lastPosition = this.housePositions[lastHouse][0];
                float lastWidth = this.houseImages[lastHouse].getWidth();
                this.lastHouse = i;
                this.housePositions[i][0] = lastPosition + lastWidth;
                this.housePositions[i][1] = (float) this.random.nextInt(10);
            } else {
                this.housePositions[i][0]-=0.9f;
            }
        }
    }

    public Rectangle[] getHousesRectangles() {
        Vector<Rectangle> result = new Vector<>();

        for (int i = 0; i < this.houseImages.length; i++) {
            if (this.housePositions[i][0] > -this.houseImages[i].getWidth() && this.housePositions[i][0] < this.position.getWidth()) {
                float originalX = this.housePositions[i][0];

                result.add(new Rectangle(
                        originalX < 0 ? 0 : originalX,
                        this.position.getHeight() - this.houseImages[i].getHeight() - HOUSES_OFFSET - this.housePositions[i][1],
                        this.houseImages[i].getWidth() + (originalX < 0 ? originalX : 0),
                        this.houseImages[i].getHeight()
                ));
            }
        }

        return result.toArray(new Rectangle[] {});
    }
}
