package net.shdwprince.jegfaller.game;

import net.shdwprince.jegfaller.lib.entities.Entity;
import net.shdwprince.jegfaller.lib.entities.EntityManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by sp on 5/29/16.
 */
public class FollowersManager extends EntityManager {
    public class Follower {
        protected float x, y;
        protected boolean shouldDraw;
    }

    protected Vector<Follower> followers;
    public float followX, followY;

    public FollowersManager() {
        super();

        this.followers = new Vector<>();
    }

    public void addEntity(Entity ent, float offsetX, float offsetY, boolean draw) {
        Follower follower = new Follower();
        follower.x = offsetX;
        follower.y = offsetY;
        follower.shouldDraw = draw;
        this.followers.add(follower);
        this.addEntity(ent);
    }

    public void addEntity(Entity ent, float oX, float oY) {
        this.addEntity(ent, oX, oY, true);
    }

    @Override
    public void destroyEntity(Entity s) {
        int idx = this.entities.indexOf(s);
        this.followers.removeElementAt(idx);
        super.destroyEntity(s);
    }

    @Override
    public void update() throws SlickException {
        super.update();

        for (int i = 0; i < this.followers.size(); i++) {
            Entity e = this.entities.elementAt(i);
            Follower f = this.followers.elementAt(i);

            e.setX(this.followX + f.x);
            e.setY(this.followY + f.y);
        }
    }

    @Override
    public void draw(Graphics graphics) {
        for (int i = 0; i < this.followers.size(); i++) {
            Entity e = this.entities.elementAt(i);
            Follower f = this.followers.elementAt(i);

            if (f.shouldDraw) {
                e.draw(graphics);
            }
        }
    }
}
