package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.lib.entities.Entity;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;

/**
 * Created by sp on 5/22/16.
 */
public class Body extends Entity {
    protected float speed;

    public Body(float x, float y) throws SlickException {
        super(new String[] {"assets/body.png"});

        this.x = x;
        this.y = y;
    }

    @Override
    public void update() throws SlickException {
        super.update();

        this.rotation += RhythmGameSettings.currentSettings().BodyRotationSpeed;
        this.rotation = this.rotation > 1 ? this.rotation - 1 : this.rotation;

        this.speed += RhythmGameSettings.currentSettings().BodyGravity;
        this.y += this.speed;
    }
}
