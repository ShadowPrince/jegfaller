package net.shdwprince.jegfaller.lib.ui;

import net.shdwprince.jegfaller.lib.rhythm.Beatmap;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
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

    public BeatmapVisualizer(Rectangle position) {
        this();
        this.position = position;
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

                    color = new Color(color.r, color.g, color.b, 0.8f);
                    graphics.setColor(color);
                    graphics.fillArc(x - halfsize, this.position.getCenterY() - halfsize, halfsize * 2, halfsize * 2, 0, 360);
                    graphics.setColor(Color.white);
                    graphics.drawArc(x - halfsize, this.position.getCenterY() - halfsize, halfsize * 2, halfsize * 2, 0, 360);
                    graphics.setColor(Color.black);

                    String actionStr = this.beatmap.stringNameOfAction(action);

                    graphics.drawString(actionStr, x - graphics.getFont().getWidth(actionStr) / 2, this.position.getCenterY() - graphics.getFont().getLineHeight() / 2);
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

    public void overrideActionColor(int idx, Color c) {
        this.actionColorOverride.put(new Integer(idx), c);
    }
}