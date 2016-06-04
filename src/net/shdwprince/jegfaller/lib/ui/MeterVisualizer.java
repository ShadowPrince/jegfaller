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
    public boolean fill, reverse;


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
        float width = fillWidth < position.getWidth() ? fillWidth : position.getWidth();

        Color color = new Color(fillPercent + 0.5f, fillPercent + 0.5f, fillPercent+0.5f);
        if (fillPercent == 1) {
            color = Color.red;
        }
        g.setColor(color);
        if (this.reverse) {
            g.fillRect(position.getX() + position.getWidth() - width, position.getY(), width, position.getHeight());
        } else {
            g.fillRect(position.getX(), position.getY(), width, position.getHeight());
        }
        //g.setColor(Color.white);
        //g.drawRect(position.getX(), position.getY(), position.getWidth(), position.getHeight());
    }
}
