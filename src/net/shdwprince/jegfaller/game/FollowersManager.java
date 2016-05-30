package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.lib.entities.Entity;
import net.shdwprince.jegfaller.lib.entities.EntityManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.Vector;

/**
 * Created by sp on 5/29/16.
 */
public class FollowersManager extends EntityManager {
    public class Follower extends Entity {
        protected Entity entity;
        protected boolean shouldDraw;

        public Follower() throws SlickException {
            super(new String [] {}, 1);
        }
    }

    public float followX, followY;

    public FollowersManager() {
    }

    public void addEntity(Entity ent, float offsetX, float offsetY, boolean draw) {
        try {
            Follower follower = new Follower();
            follower.entity = ent;
            follower.setX(offsetX);
            follower.setY(offsetY);
            follower.shouldDraw = draw;
            this.addEntity(follower);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void addEntity(Entity ent, float oX, float oY) {
        this.addEntity(ent, oX, oY, true);
    }

    @Override
    public void update() throws SlickException {
        super.update();

        for (Entity f : this.iter()) {
            ((Follower) f).entity.setX(this.followX + f.getX());
            ((Follower) f).entity.setY(this.followY + f.getY());
        }
    }

    @Override
    public void draw(Graphics graphics) {
        for (Entity e : this.iter()) {
            Follower f = (Follower) e;

            if (f.shouldDraw) {
                f.entity.draw(graphics);
            }
        }
    }
}
