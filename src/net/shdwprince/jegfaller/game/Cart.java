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

    public Cart(float x, float y) throws SlickException {
        super(new String[]{"assets/cart.png", "assets/cart_f2.png"}, 300);

        this.x = x;
        this.y = y;
    }

    public void update() {
        this.bump = this.bump > RhythmGameSettings.currentSettings().CartMaxSpeed * 4 ? RhythmGameSettings.currentSettings().CartMaxSpeed * 4 : this.bump;
        if (this.bump > 0) {
            this.bump -= 0.3f;
            this.speed += 0.3f;
        }

        this.speed -= RhythmGameSettings.currentSettings().CartSlowdown;
        this.speed = this.speed < RhythmGameSettings.currentSettings().CartMinSpeed ? RhythmGameSettings.currentSettings().CartMinSpeed : this.speed;
        this.speed = this.speed > RhythmGameSettings.currentSettings().CartMaxSpeed ? RhythmGameSettings.currentSettings().CartMaxSpeed : this.speed;

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
        this.bump += RhythmGameSettings.currentSettings().CartBumpAmount;
    }

    public void missedBumpBreak() {
        this.speed = RhythmGameSettings.currentSettings().CartMinSpeed;
    }

    public void missedBumpSpeedup() {
        this.speed = RhythmGameSettings.currentSettings().CartMaxSpeed;
    }
}
