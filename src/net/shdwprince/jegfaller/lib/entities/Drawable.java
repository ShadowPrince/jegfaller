package net.shdwprince.jegfaller.lib.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;

/**
 * Created by sp on 6/1/16.
 */
public abstract  class Drawable {
    protected float x, y, rotation, width, height;

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

    public Rectangle box() {
        return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public Rectangle roundedBoxWithMargin(float margin) {
        return new RoundedRectangle(
                this.getX() + margin,
                this.getY() + margin,
                this.getWidth() - margin * 2,
                this.getHeight() - margin,
                margin);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public abstract void draw(Graphics g);
}
