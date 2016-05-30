package net.shdwprince.jegfaller.lib.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.w3c.dom.css.Rect;

/**
 * Created by sp on 5/28/16.
 */
public class MusicProgressVisualizer {
    public float totalLength, musicPosition;
    public Rectangle position;

    public MusicProgressVisualizer(Rectangle position) {
        this.position = position;
    }

    public void render(Graphics graphics) {
        graphics.setColor(Color.white);
        float y = this.position.getY();
        float height = this.position.getHeight();
        float width = this.position.getWidth();
        graphics.drawRect(this.position.getX(), y, width, height);
        graphics.fillRect(this.position.getX(), y, width * (this.musicPosition / this.totalLength), height);
    }
}
