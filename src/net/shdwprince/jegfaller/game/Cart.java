package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.lib.entities.Entity;
import org.lwjgl.Sys;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;

import java.util.Random;

/**
 * Created by sp on 5/22/16.
 */
public class Cart extends Entity {
    protected float speed;
    protected float bump;

    public static float BUMP, SLOWDOWN, MAX_SPEED, MIN_SPEED;

    public Cart(float x, float y) throws SlickException {
        super(new String[]{"assets/cart.png", "assets/cart_f2.png"}, 300);

        this.x = x;
        this.y = y;
    }

    public void update() {
        this.bump = this.bump > MAX_SPEED * 4 ? MAX_SPEED * 4 : this.bump;
        if (this.bump > 0) {
            this.bump -= 0.3f;
            this.speed += 0.3f;
        }

        this.speed -= SLOWDOWN;
        this.speed = this.speed < MIN_SPEED ? MIN_SPEED : this.speed;
        this.speed = this.speed > MAX_SPEED ? MAX_SPEED : this.speed;

        this.x += this.speed;
    }

    public void checkBounds(float minX, float maxX) {
        if (this.x < minX) {
            this.bump();
        }

        if (this.x > maxX) {
            this.speed = 0;
        }
    }

    @Override
    public Rectangle box() {
        return this.roundedBoxWithMargin(30.f);
    }

    public void bump() {
        this.bump += BUMP;
    }

    public void missedBumpBreak() {
        this.speed = MIN_SPEED;
    }

    public void missedBumpSpeedup() {
        this.speed = MAX_SPEED;
    }
}
