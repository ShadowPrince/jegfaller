package net.shdwprince.jegfaller.lib.entities;

import net.shdwprince.jegfaller.lib.util.SingleRandom;
import org.lwjgl.Sys;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Created by sp on 6/1/16.
 */
public class Lighting extends Drawable {
    private Vector<Line> path;
    private long duration, lastFrameChange;
    private int frame;

    public Lighting(Rectangle rect) {
        super();
        this.x = rect.getX();
        this.y = rect.getY();
        this.width = rect.getWidth();
        this.height = rect.getHeight();

        this.frame = 0;
        this.lastFrameChange = System.currentTimeMillis();
        this.duration = 1;

        this.path = new Vector<>();
        int ySpread = 60, xSpread = 40;
        float yAppend = SingleRandom.nextInt(ySpread) + 30.f;

        for (float y = this.getY() + yAppend; y < this.getHeight(); y += yAppend) {
            float x = (SingleRandom.nextInt(xSpread * 2) - xSpread) + this.getX() + this.getWidth() / 2;
            yAppend = SingleRandom.nextInt(ySpread) + 30.f;

            Line line;
            try {
                Line previous = this.path.lastElement();
                if (y + yAppend <= this.getHeight()) {
                    line = new Line(previous.getX2(), previous.getY2(), x, y);
                } else {
                    // last line
                    line = new Line(previous.getX2(), previous.getY2(), this.getWidth() / 2 + this.getX(), this.getY() + this.getHeight());
                }
            } catch (NoSuchElementException e) {
                line = new Line(this.getX() + this.getWidth() / 2, this.getY(), x, y);
            }

            this.path.add(line);
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.white);

        for (int i = 0; i < Math.min(this.frame, this.path.size()); i++) {
            g.draw(this.path.elementAt(i));
        }

        if (System.currentTimeMillis() - this.lastFrameChange > this.duration) {
            this.lastFrameChange = System.currentTimeMillis();
            this.frame++;
        }
    }
}
