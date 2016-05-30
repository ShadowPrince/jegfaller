package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.lib.entities.Entity;
import org.newdawn.slick.SlickException;

/**
 * Created by sp on 5/29/16.
 */
public class BodyAlert extends Entity {
    public long creationTime;

    public BodyAlert(float x, float y) throws SlickException {
        super(new String [] {"assets/body_alert.png"}, 1);
        this.setY(y);
        this.setX(x);
        this.creationTime = System.currentTimeMillis();
    }

}
