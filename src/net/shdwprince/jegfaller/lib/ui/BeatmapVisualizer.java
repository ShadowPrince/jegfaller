package net.shdwprince.jegfaller.lib.ui;

import net.shdwprince.jegfaller.lib.rhythm.Beatmap;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.HashMap;

/**
 * Created by sp on 5/28/16.
 */
public class BeatmapVisualizer {
    public float scale;

    public Beatmap beatmap;
    public float musicPosition;
    public Rectangle position;
    public HashMap<Integer, Color> actionColorOverride;

    protected Image[] actionImages;

    public BeatmapVisualizer(Rectangle position) throws SlickException {
        this();
        this.position = position;

        this.actionImages = new Image[9];
        Image arrow = new Image("assets/arrow.png");
        for (int i = 1; i <= 8; i++) {
            Image rotatedArrow = arrow.copy();
            rotatedArrow.setRotation((i - 1) * (float) 1 / 8);
            rotatedArrow.setRotation((i - 1) * 45.f);
            this.actionImages[i] = rotatedArrow;
        }
    }

    public BeatmapVisualizer() {
        this.scale = 120.f;
        this.actionColorOverride = new HashMap<>();
    }

    public void render(Graphics graphics) {
        float minOffset = 1f;
        if (this.beatmap != null) {
            int index = 0;
            for (float start = this.beatmap.beatOffset; start < this.beatmap.totalLength; index++, start += this.beatmap.beatSize) {
                float offset = this.musicPosition - start;
                if (Math.abs(offset) < minOffset) {
                    minOffset = Math.abs(offset);
                }

                float x = (this.position.getCenterX() - offset * this.scale);

                if (x < this.position.getX() || x > this.position.getMaxX()) {
                    continue;
                }

                graphics.setColor(Color.gray);
                float markerHeight = this.position.getHeight() / 2;
                graphics.drawLine(x, this.position.getCenterY() - markerHeight / 2, x, this.position.getCenterY() + markerHeight / 2);

                int action = this.beatmap.actions[index];
                if (action != 0) {
                    float halfsize = this.position.getHeight() / 6;
                    Color color;
                    if ((color = this.actionColorOverride.get(new Integer(index))) == null) {
                        color = Color.white;
                    }

                    this.drawActionAt(graphics, action, new Color(color.r, color.g, color.b, 0.8f), halfsize, x, this.position.getCenterY());
                }
            }
        }

        float width = 1.f;
        if (minOffset < 0.1f) {
            width = 3.f;
        }

        graphics.setColor(Color.white);
        graphics.fillRect(
                this.position.getCenterX() - width / 2,
                this.position.getY(),
                width,
                this.position.getHeight()
        );
    }

    public void drawActionAt(Graphics graphics, int action, Color c, float halfsize, float x, float y) {
        graphics.setColor(c);
        graphics.fillArc(x - halfsize, y - halfsize, halfsize * 2, halfsize * 2, 0, 360);
        graphics.setColor(Color.white);
        graphics.drawArc(x - halfsize, y - halfsize, halfsize * 2, halfsize * 2, 0, 360);
        graphics.setColor(Color.black);

        Image actionImage = this.actionImages[action];
        graphics.drawImage(actionImage, x - actionImage.getWidth() / 2, y - actionImage.getHeight() / 2);
    }

    public void overrideActionColor(int idx, Color c) {
        this.actionColorOverride.put(new Integer(idx), c);
    }

    public void reset() {
        this.actionColorOverride = new HashMap<>();
    }
}