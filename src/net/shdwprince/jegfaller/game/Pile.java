package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.lib.entities.Entity;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.util.Random;

/**
 * Created by sp on 5/29/16.
 */
public class Pile extends Entity {
    protected Image bodyImage;
    protected Image pileImage;
    protected Rectangle size;
    protected Random random;

    protected float bodyOffsetX = 0, bodyOffsetY = 0;

    public Pile(Rectangle size) throws SlickException {
        super(new String[] {}, 1);
        this.random = new Random();
        this.size = size;
        this.pileImage = new Image((int) size.getWidth(), (int) size.getHeight());
        this.bodyImage = new Image("assets/body.png");
    }

    public void addBody() throws SlickException {
        float minOffset = (bodyOffsetY/this.bodyImage.getHeight()+1) * 3.f;
        float maxOffset = this.size.getWidth() - minOffset;

        if (this.bodyOffsetX + this.bodyImage.getWidth() > maxOffset) {
            this.bodyOffsetX = minOffset;
            this.bodyOffsetY += this.bodyImage.getHeight()/2;
        } else {
            this.bodyOffsetX += this.bodyImage.getWidth()/1.5f;
        }

        Image bodyImageToPlace = this.bodyImage.getFlippedCopy(this.random.nextBoolean(), this.random.nextBoolean());
        this.pileImage.getGraphics().drawImage(bodyImageToPlace, this.bodyOffsetX + this.random.nextInt(10) - 5, this.pileImage.getHeight() - this.bodyOffsetY - this.bodyImage.getHeight());
        this.pileImage.getGraphics().flush();
    }

    public void reset() throws SlickException {
        this.pileImage = new Image((int) size.getWidth(), (int) size.getHeight());
        this.bodyOffsetX = 0;
        this.bodyOffsetY = 0;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(this.pileImage, this.x, this.y);
    }

    @Override
    public float getHeight() {
        return this.size.getHeight();
    }

    @Override
    public float getWidth() {
        return this.size.getWidth();
    }

    @Override
    public Rectangle box() {
        return new Rectangle(this.getX(), this.getY() + this.getHeight() - this.bodyOffsetY, this.getWidth(), this.bodyOffsetY);
    }
}
