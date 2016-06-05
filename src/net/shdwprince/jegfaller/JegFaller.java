package net.shdwprince.jegfaller;

import it.twl.util.TWLStateBasedGame;
import net.shdwprince.jegfaller.states.EditorGameState;
import net.shdwprince.jegfaller.states.RhythmGameState;
import net.shdwprince.jegfaller.states.MenuState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;
import java.net.URL;

/**
 * Created by sp on 5/27/16.
 */
public class JegFaller extends TWLStateBasedGame {
    public static final int MAINMENU = 0;
    public static final int RHYTHMGAME = 1;
    public static final int EDITOR = 99;

    public JegFaller(String name) {
        super(name);
    }

    @Override
    protected URL getThemeURL() {
        try {
            return new File("res/RadicalFish.xml").toURL();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        this.addState(new MenuState());
        this.addState(new RhythmGameState());
        this.addState(new EditorGameState());

        //this.enterState(EDITOR);
        /*
        ((RhythmGameState) this.getState(RHYTHMGAME)).beatmapFile = new File("assets/Colors.jfb/beatmap.dat");
        this.enterState(RHYTHMGAME);
        */
    }
}
