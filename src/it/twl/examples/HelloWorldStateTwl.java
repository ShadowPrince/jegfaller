package it.twl.examples;

import it.twl.util.TWLStateBasedGame;

import java.io.File;
import java.net.URL;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

public class HelloWorldStateTwl extends TWLStateBasedGame {

	protected HelloWorldStateTwl(String name) {
		super(name);
	}

	@Override
	protected URL getThemeURL() {
        try {
            return new File("./res/it/twl/examples/simple.xml").toURL();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new GameState());
	}

	public static void main(String[] args) throws SlickException {
		HelloWorldStateTwl game = new HelloWorldStateTwl(
				"Twl Slick Hello World");
		AppGameContainer container = new AppGameContainer(game);
		container.setDisplayMode(1024, 768, false);
		container.setShowFPS(false);
		container.setAlwaysRender(true);
		container.setTargetFrameRate(40);
		container.start();
	}

}
