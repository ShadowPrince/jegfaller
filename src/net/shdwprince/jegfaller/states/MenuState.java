package net.shdwprince.jegfaller.states;

import de.matthiasmann.twl.*;
import it.twl.util.BasicTWLGameState;
import it.twl.util.RootPane;
import net.shdwprince.jegfaller.JegFaller;
import net.shdwprince.jegfaller.lib.util.ShaderProgram;
import net.shdwprince.jegfaller.lib.ui.UIHelper;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;

/**
 * Created by sp on 5/27/16.
 */
public class MenuState extends BasicTWLGameState {
    protected StateBasedGame game;
    protected GameContainer container;

    protected ShaderProgram shader;
    protected Image backgroundImage, doctorImage, logoImage;
    protected long lastLighting, lightingDuration;

    @Override
    public int getID() {
        return JegFaller.MAINMENU;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        gameContainer.getGraphics().setBackground(Color.black);
        this.backgroundImage = new Image("assets/bg.png");
        this.doctorImage = new Image("assets/doctor_big.png");
        this.logoImage = new Image("assets/logo.png");

        this.shader = ShaderProgram.loadProgram("assets/shader/a.vert", "assets/shader/a.frag");
        this.shader.bind();
        this.shader.setUniform1i("tex0", 0);
        this.shader.unbind();

        this.game = stateBasedGame;
        this.container = gameContainer;
        this.lastLighting = System.currentTimeMillis();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        if (System.currentTimeMillis() - this.lastLighting > 10000) {
            this.lightingDuration = System.currentTimeMillis() + 300;
            this.lastLighting = System.currentTimeMillis();
        }

        if (this.lightingDuration > System.currentTimeMillis()) {
            this.shader.bind();
        } else {
            this.shader.unbind();
        }

        graphics.drawImage(this.backgroundImage, 0, 0);
        graphics.drawImage(this.doctorImage, gameContainer.getWidth() - this.doctorImage.getWidth(), gameContainer.getHeight() - this.doctorImage.getHeight());
        graphics.drawImage(this.logoImage, 100, 200);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {}

    protected void openBeatmap() {
        File beatmapFile;
        if ((beatmapFile = UIHelper.instance().selectFile()) != null) {
            RhythmGameState game = (RhythmGameState) this.game.getState(JegFaller.RHYTHMGAME);
            game.beatmapFile = beatmapFile;
            this.game.enterState(JegFaller.RHYTHMGAME);
        }
    }

    protected void openEditor() {
        this.game.enterState(JegFaller.EDITOR);
    }

    protected void quit() {
        this.container.exit();
    }

    protected BoxLayout boxMenu;
    @Override
    protected RootPane createRootPane() {
        RootPane p = super.createRootPane();
        p.setTheme("");
        this.boxMenu = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        this.boxMenu.setTheme("mainMenuPane");

        Button openBeatmapButton = new Button("Open");
        openBeatmapButton.addCallback(this::openBeatmap);

        Button editorButton = new Button("Editor");
        editorButton.addCallback(this::openEditor);

        Button quitButton = new Button("Quit");
        quitButton.addCallback(this::quit);

        this.boxMenu.add(openBeatmapButton);
        this.boxMenu.add(editorButton);
        this.boxMenu.add(quitButton);
        p.add(this.boxMenu);
        return p;
    }

    @Override
    protected void layoutRootPane() {
        this.boxMenu.adjustSize();
        this.boxMenu.setPosition(100 + this.logoImage.getWidth() / 2 - this.boxMenu.getWidth()/2, 200 + this.logoImage.getHeight() + 15);
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
    }
}
