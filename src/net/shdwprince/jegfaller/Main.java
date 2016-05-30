package net.shdwprince.jegfaller;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Created by sp on 5/22/16.
 */
public class Main {
    public static void main(String[] args) {
        try {
            StateBasedGame main = new JegFaller("Jeg Faller");
            AppGameContainer container = new AppGameContainer(main);
            container.setDisplayMode(1024, 768, false);
            container.setTargetFrameRate(60);
            container.setVSync(true);
            container.setAlwaysRender(true);
            container.setShowFPS(false);
            container.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
