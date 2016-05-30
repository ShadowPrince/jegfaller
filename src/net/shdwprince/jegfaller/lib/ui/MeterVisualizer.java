package net.shdwprince.jegfaller.lib.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

/**
 * Created by sp on 5/28/16.
 */
public class MeterVisualizer {
    public Rectangle position;
    public float value, maxValue;
    public boolean fill;


    public MeterVisualizer(Rectangle position, float maxValue) {
        this.position = position;
        this.maxValue = maxValue;
    }

    public void render(Graphics g) {
        float fillPercent = (this.value / this.maxValue);
        if (this.fill) {
            fillPercent = 1;
        }

        float fillWidth = position.getWidth() * fillPercent;
        Color color = new Color(fillPercent + 0.3f, fillPercent + 0.3f, fillPercent+0.3f);
        if (fillPercent == 1) {
            color = Color.red;
        }
        g.setColor(color);
        g.fillRect(position.getX(), position.getY(), fillWidth < position.getWidth() ? fillWidth : position.getWidth(), position.getHeight());
        g.setColor(Color.white);
        g.drawRect(position.getX(), position.getY(), position.getWidth(), position.getHeight());
    }
}
