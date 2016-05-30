package net.shdwprince.jegfaller.game;

import javafx.scene.control.Alert;
import net.shdwprince.jegfaller.lib.Background;
import net.shdwprince.jegfaller.lib.entities.Entity;
import net.shdwprince.jegfaller.lib.entities.EntityManager;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.w3c.dom.css.Rect;

import java.util.Random;
import java.util.Vector;

/**
 * Created by sp on 5/22/16.
 */

public class BodyManager extends EntityManager {
    public interface Listener {
        public void bodyCollision(Body b) throws SlickException;
    }

    public static long SPAWN_INTERVAL, DROP_INTERVAL;

    protected Listener listener;

    protected Entity pile;
    protected Background background;
    protected float maxY;

    private long lastSpawn;

    public BodyManager(Entity pile, Background background, Listener listener, float maxY) {
        super();

        this.pile = pile;
        this.background = background;
        this.listener = listener;
        this.maxY = maxY;
    }

    public void spawnAlert(float x, float y) throws SlickException {
        this.entities.add(new BodyAlert(x, y));
    }

    public void spawnBody(Entity alert) throws SlickException {
        Body body = new Body(alert.getX(), alert.getY());
        this.entities.add(body);
    }

    @Override
    public void reset() throws SlickException {
        super.reset();
        this.lastSpawn = System.currentTimeMillis();
    }

    @Override
    public void update() throws SlickException {
        super.update();

        if (System.currentTimeMillis() - this.lastSpawn > BodyManager.SPAWN_INTERVAL) {
            Vector<Rectangle> rectangles = new Vector<>();
            for (Rectangle r : this.background.getHousesRectangles()) {
                if (r.getWidth() > 400.f) {
                    rectangles.add(r);
                }
            }

            Rectangle targetRect = rectangles.elementAt(random.nextInt(rectangles.size()));
            int margin = 45;
            this.spawnAlert(
                    random.nextInt((int) targetRect.getWidth() - margin) + targetRect.getX() + margin,
                    random.nextInt((int) targetRect.getHeight() / 2) + targetRect.getY() + margin
            );

            this.lastSpawn = System.currentTimeMillis();
        }

        Vector<Entity> entitiesToRemove = new Vector<>();
        for (Entity e : this.iter()) {
            if (e instanceof  BodyAlert) {
                BodyAlert alert = (BodyAlert) e;
                if (System.currentTimeMillis() - ((BodyAlert) e).creationTime > DROP_INTERVAL) {
                    entitiesToRemove.add(alert);
                }
            } else if (e instanceof Body) {
                if (e.getY() > this.maxY) {
                    entitiesToRemove.add(e);
                }
            }

            e.update();
            e.setX(e.getX()-0.9f);
        }

        for (Entity e : entitiesToRemove) {
            if (e instanceof BodyAlert) {
                this.spawnBody(e);
            }

            this.destroyEntity(e);
        }

        for (Entity collidedEntity : this.entitiesCollidedWith(this.pile)) {
            this.listener.bodyCollision((Body) collidedEntity);
            this.destroyEntity(collidedEntity);
        }
    }
}
