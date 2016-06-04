package net.shdwprince.jegfaller.lib.entities;

import org.newdawn.slick.*;

import java.util.Vector;

/**
 * Created by sp on 5/22/16.
 */
public class Sprite extends Drawable {
    protected Animation anim;

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

    public Sprite(Image[] imgs, int timeout) {
        this.anim = new Animation(imgs, timeout);
    }

    public void draw(Graphics g) {
        if (this.anim.getFrameCount() > 0) {
            this.anim.getCurrentFrame().rotate(this.getRotation());
            this.anim.draw(this.getX(), this.getY());
        }
    }

    public boolean collidesWith(Sprite s) {
        return this.box().intersects(s.box());
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
