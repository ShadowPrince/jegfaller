package net.shdwprince.jegfaller.lib.entities;

import net.shdwprince.jegfaller.lib.Sprite;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.Random;
import java.util.Vector;

/**
 * Created by sp on 5/22/16.
 */
public class EntityManager {
    protected Vector<Entity> entities;
    protected Random random;

    public EntityManager() {
        this.entities = new Vector<>();
        this.random = new Random();
    }

    public void reset() throws SlickException {

    }

    public void update() throws SlickException {
        for (Entity e : this.entities) {
            e.update();
        }
    }

    public void draw(Graphics graphics) {
        for (Entity e : this.entities) {
            e.draw(graphics);
        }
    }

    public Entity[] entitiesCollidedWith(Sprite s) {
        Vector<Entity> result = new Vector();
        for (Entity e : this.iter()) {
            if (e.collidesWith(s)) {
                result.add(e);
            }
        }

        return result.toArray(new Entity[] {});
    }

    public void destroyEntity(Entity s) {
        this.entities.remove(s);
    }

    public Vector<Entity> iter() {
        return this.entities;
    }

    public void addEntity(Entity ent) {
        this.entities.add(ent);
    }

    public boolean isAnimating() {
        return !this.iter().firstElement().getAnimation().isStopped();
    }

    public void startAnimating() {
        for (Entity e : this.iter()) {
            e.getAnimation().start();
        }
    }

    public void stopAnimating() {
        for (Entity e : this.iter()) {
            e.getAnimation().stop();
        }
    }
}
