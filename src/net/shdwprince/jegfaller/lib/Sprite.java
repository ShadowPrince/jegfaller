package net.shdwprince.jegfaller.lib;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;

import java.util.Vector;

/**
 * Created by sp on 5/22/16.
 */
public class Sprite {
    protected Animation anim;
    protected float x, y, rotation;

    public Sprite(String[] refs, int timeout) throws SlickException {
        super();

        Vector<Image> images = new Vector<Image>();
        for (String ref : refs) {
            Image x = new Image(ref);
            images.add(x);
        }

        this.anim = new Animation(images.toArray(new Image[]{}), timeout);
    }

    public Sprite(String[] refs) throws SlickException {
        this(refs, Integer.MAX_VALUE);
    }

    public void draw(Graphics g) {
        if (this.anim.getFrameCount() > 0) {
            this.anim.getCurrentFrame().rotate(this.rotation);
            this.anim.draw(this.x, this.y);
        }
    }

    public Rectangle box() {
        return new Rectangle(this.x, this.y, this.anim.getWidth(), this.anim.getHeight());
    }

    public Rectangle roundedBoxWithMargin(float margin) {
        return new RoundedRectangle(
                this.x + margin,
                this.y + margin,
                this.anim.getWidth() - margin * 2,
                this.anim.getHeight() - margin,
                margin);
    }

    public boolean collidesWith(Sprite s) {
        return this.box().intersects(s.box());
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getWidth() {
        return this.anim.getWidth();
    }

    public float getHeight() {
        return this.anim.getHeight();
    }

    public Animation getAnimation() {
        return anim;
    }
}
