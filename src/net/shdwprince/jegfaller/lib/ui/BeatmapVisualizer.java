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
        graphics.setColor(Color.white);
        graphics.drawLine(
                this.position.getCenterX(),
                this.position.getY(),
                this.position.getCenterX(),
                this.position.getY() + this.position.getHeight()
        );

        if (this.beatmap != null) {
            int index = 0;
            for (float start = this.beatmap.beatOffset; start < this.beatmap.totalLength; index++, start += this.beatmap.beatSize) {
                float offset = this.musicPosition - start;
                float x = (this.position.getCenterX() - offset * this.scale);

                if (x < this.position.getX() || x > this.position.getMaxX()) {
                    continue;
                }

                graphics.setColor(Color.white);
                float markerHeight = this.position.getHeight() / 2;
                graphics.drawLine(x, this.position.getCenterY() - markerHeight / 2, x, this.position.getCenterY() + markerHeight / 2);

                int action = this.beatmap.actions[index];
                if (action != 0) {
                    float halfsize = this.position.getHeight() / 6;
                    Color color;
                    if ((color = this.actionColorOverride.get(new Integer(index))) == null) {
                        color = Color.white;
                    }

                    graphics.setColor(color);
                    graphics.fillArc(x - halfsize, this.position.getCenterY() - halfsize, halfsize * 2, halfsize * 2, 0, 360);
                    graphics.setColor(Color.black);
                    graphics.drawString(this.beatmap.stringNameOfAction(action), x - halfsize / 2, this.position.getCenterY() - graphics.getFont().getLineHeight() / 2);
                }
            }
        }
    }

    public void overrideActionColor(int idx, Color c) {
        this.actionColorOverride.put(new Integer(idx), c);
    }
}