package net.shdwprince.jegfaller.states;

import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Label;
import it.twl.util.BasicTWLGameState;
import it.twl.util.RootPane;
import net.shdwprince.jegfaller.JegFaller;
import net.shdwprince.jegfaller.game.Pile;
import net.shdwprince.jegfaller.game.RhythmGameSettings;
import net.shdwprince.jegfaller.lib.ui.Background;
import net.shdwprince.jegfaller.lib.util.SingleRandom;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sp on 6/10/16.
 */
public class RhythmGameResultsState extends BasicTWLGameState {
    protected StateBasedGame stateBasedGame;
    protected GameContainer gameContainer;

    public long bodies;
    public HashMap<Integer, RhythmGameState.HitStatus> hits;

    protected long bodyAnimationTimeout = 0, addedBodies, addedCoins, score;
    protected Background bg;
    protected Image doctorImage;
    protected Pile resultingPile;

    protected Label totalBodiesLabel, totalCoinsLabel, totalScoreLabel;
    protected Label[] hitsLabels = new Label[4];

    @Override
    public int getID() {
        return JegFaller.GAMERESULTS;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame stateBasedGame) throws SlickException {
        this.stateBasedGame = stateBasedGame;
        this.gameContainer = gc;

        this.hits = new HashMap<>();

        gc.getGraphics().setBackground(Color.black);
        this.bg = new Background(new Rectangle(0, 0, gc.getWidth(), gc.getHeight()));
        this.bg.update();

        this.doctorImage = new Image("assets/doctor_big.png");
        this.resultingPile = new Pile(new Rectangle(0, 0, 600, 500));
        this.resultingPile.setX(45);
        this.resultingPile.setY(gc.getHeight() - 500 - 45);
        this.resultingPile.incrementalMargin = 35.f;
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        this.bg.render(graphics);
        graphics.drawImage(this.doctorImage, gameContainer.getWidth() - this.doctorImage.getWidth(), gameContainer.getHeight() - this.doctorImage.getHeight());

        this.resultingPile.draw(graphics);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        if (this.addedBodies < this.bodies && System.currentTimeMillis() - this.bodyAnimationTimeout > 600) {
            this.bodyAnimationTimeout = System.currentTimeMillis();
            this.addedBodies++;
            this.resultingPile.addBody();
            this.addedCoins += SingleRandom.nextInt(55);

            this.score *= 1.12f;

            this.totalScoreLabel.setText(String.format("Score: %d", score));
            this.totalBodiesLabel.setText(String.format("Bodies: %d", this.addedBodies));
            this.totalCoinsLabel.setText(String.format("Coins: %d", this.addedCoins));
        }
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);

        this.addedBodies = 0;
        this.addedCoins = 0;

        this.resultingPile.reset();
        for (int i = 0; i < SingleRandom.nextInt(32) + 24; i++) {
            this.resultingPile.addBody();
        }

        long[] hits = new long[4];
        for (Map.Entry<Integer, RhythmGameState.HitStatus> e : this.hits.entrySet()) {
            hits[e.getValue().ordinal()]++;
        }

        String[] labels = new String[] {"Excellent", "Good", "Ok", "Missed"};
        for (int i = 0; i < hits.length; i++) {
            this.hitsLabels[i].setText(String.format("%s: %d", labels[i], hits[i]));
        }

        this.score = hits[0] * 300 + hits[1] * 250 + hits[2] * 150;
        this.totalScoreLabel.setText(String.format("Score: %d", score));
   }

    @Override
    public RootPane getRootPane() {
        RootPane p = super.createRootPane();

        BoxLayout box = new BoxLayout(BoxLayout.Direction.VERTICAL);
        box.setTheme("resultsPane");
        BoxLayout resultsBox = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        resultsBox.setTheme("mainMenuPane");
        BoxLayout statsBox = new BoxLayout(BoxLayout.Direction.VERTICAL);
        this.totalScoreLabel = new Label("Score: 0");
        this.totalBodiesLabel = new Label("Bodies: 0");
        this.totalCoinsLabel = new Label("Coins: 0");
        statsBox.add(this.totalScoreLabel);
        statsBox.add(this.totalBodiesLabel);
        statsBox.add(this.totalCoinsLabel);

        BoxLayout hitsBox = new BoxLayout(BoxLayout.Direction.VERTICAL);
        for (int i = 0; i < 4; i++) {
            this.hitsLabels[i] = new Label("Excellent: 0");
            hitsBox.add(this.hitsLabels[i]);
        }

        BoxLayout menuBox = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        menuBox.setTheme("mainMenuPane");
        Button replayButton = new Button("Replay");
        replayButton.addCallback(this::replay);

        Button mainMenuButton = new Button("Main menu");
        mainMenuButton.addCallback(() -> {
            this.stateBasedGame.enterState(JegFaller.MAINMENU);
        });
        Button quitButton = new Button("Quit");
        quitButton.addCallback(() -> {
            this.gameContainer.exit();
        });
        menuBox.add(replayButton);
        menuBox.add(mainMenuButton);
        menuBox.add(quitButton);

        resultsBox.add(statsBox);
        resultsBox.add(hitsBox);
        box.add(resultsBox);
        box.add(menuBox);
        p.add(box);

        box.setPosition(100, 300);
        box.setSize(300, 125);
        return p;
    }

    @Override
    protected void layoutRootPane() {
        super.layoutRootPane();
    }

    protected void replay() {
        this.stateBasedGame.enterState(JegFaller.RHYTHMGAME);
    }
}
