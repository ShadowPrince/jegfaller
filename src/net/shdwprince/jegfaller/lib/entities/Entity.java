package net.shdwprince.jegfaller.lib.entities;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * Created by sp on 5/22/16.
 */
public class Entity extends Sprite {
    public Entity(String[] refs, int timeout) throws SlickException {
        super(refs, timeout);
    }

    public Entity(Image[] imgs, int timeout) {
        super(imgs, timeout);
    }

    public Entity(String[] refs) throws SlickException {
        super(refs);
    }

    public void update() throws SlickException {

    }
}
