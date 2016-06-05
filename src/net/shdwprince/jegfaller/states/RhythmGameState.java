package net.shdwprince.jegfaller.states;

import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ValueAdjusterFloat;
import it.twl.util.BasicTWLGameState;
import it.twl.util.RootPane;
import net.shdwprince.jegfaller.JegFaller;
import net.shdwprince.jegfaller.game.*;
import net.shdwprince.jegfaller.lib.entities.Lighting;
import net.shdwprince.jegfaller.lib.rhythm.RhythmInput;
import net.shdwprince.jegfaller.lib.sound.SoundSet;
import net.shdwprince.jegfaller.lib.ui.Background;
import net.shdwprince.jegfaller.lib.util.ShaderProgram;
import net.shdwprince.jegfaller.lib.util.SingleRandom;
import net.shdwprince.jegfaller.lib.entities.Entity;
import net.shdwprince.jegfaller.lib.rhythm.Beatmap;
import net.shdwprince.jegfaller.lib.ui.BeatmapVisualizer;
import net.shdwprince.jegfaller.lib.ui.MeterVisualizer;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;
import java.util.HashMap;

/**
 * Created by sp on 5/27/16.
 */
public class RhythmGameState extends BasicTWLGameState implements BodyManager.Listener {
    private static enum HitStatus {
        Excellent,
        Good,
        Ok,
        Missed
    };

    public File beatmapFile;

    protected RhythmInput input;
    protected Beatmap beatmap;
    protected GameContainer gameContainer;
    protected StateBasedGame stateBasedGame;
    protected Music music;
    protected float heat;
    protected long bodies, feverUntil;
    protected boolean isFever, isPaused;
    protected HashMap<Integer, HitStatus> hits;

    protected Cart cart;
    protected BodyManager bodyManager;
    protected Pile pile;
    protected FollowersManager followersManager;

    protected Background background;
    protected BeatmapVisualizer beatmapVisualizer;
    protected MeterVisualizer heatVisualizer, musicPositionVisualizer;
    protected PileSpriteFactory fireFactory;

    protected ShaderProgram lightingShader;
    protected long lastThunderHit, lightingUntil;
    protected Lighting lightingDrawable;
    protected SoundSet soundSetHit, soundSetThunder, soundSetBody;

    @Override
    public int getID() {
        return JegFaller.RHYTHMGAME;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame stateBasedGame) throws SlickException {
        RhythmGameSettings.instantiateDefaultSettings();
        try {
            String path = "assets/Colors.jfb/settings.dat";
            RhythmGameSettings.setCurrentSettings(RhythmGameSettings.loadFromFileAt(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.gameContainer = gc;
        this.stateBasedGame = stateBasedGame;
        this.input = new RhythmInput(gc.getInput());

        this.lightingShader = ShaderProgram.loadProgram("assets/shader/a.vert", "assets/shader/a.frag");
        this.lightingShader.bind();
        this.lightingShader.setUniform1i("tex0", 0); //texture 0
        this.lightingShader.unbind();

        int x = gc.getWidth() / 4;
        int width = gc.getWidth() / 2;
        this.beatmapVisualizer = new BeatmapVisualizer(new Rectangle(
                x,
                150,
                width,
                100
        ));

        float visualizersWidth = width / 2;
        this.heatVisualizer = new MeterVisualizer(new Rectangle(x, 150, visualizersWidth - 15, 10), 100.f);
        this.musicPositionVisualizer = new MeterVisualizer(new Rectangle(x + visualizersWidth + 15, 150, visualizersWidth - 15, 10), 1.f);
        this.musicPositionVisualizer.reverse = true;
        this.background = new Background(new Rectangle(0, 0, gc.getWidth(), gc.getHeight()));
        this.lastThunderHit = System.currentTimeMillis();
        this.lightingDrawable = new Lighting(new Rectangle(100, 0, 300, 500));

        this.soundSetHit = new SoundSet("assets/sound/hit%d.ogg", 3);
        this.soundSetThunder = new SoundSet("assets/sound/tsunder%d.ogg", 4);
        this.soundSetBody = new SoundSet("assets/sound/body%d.ogg", 3);

        this.cart = new Cart(gc.getWidth() / 2 - 100, gc.getHeight() - 65.f - 20.f);
        this.followersManager = new FollowersManager();

        this.pile = new Pile(new Rectangle(0, 0, this.cart.getWidth(), 400.f));
        this.fireFactory = new PileSpriteFactory(new SpriteSheet("assets/firesheet.png", 64, 64), 0.4f, 0.3f, 800);

        Entity doctor = new Entity(new String[] {"assets/doctor.png", "assets/doctor_f2.png"}, 1);
        Entity horses = new Entity(new String[] {"assets/horses.png", "assets/horses_f2.png"}, 500);
        this.followersManager.addEntity(horses, this.cart.getWidth(), -(horses.getHeight() - this.cart.getHeight()));
        this.followersManager.addEntity(doctor, this.cart.getWidth() + horses.getWidth() - 30.f, -(doctor.getHeight() - this.cart.getHeight()));
        this.followersManager.addEntity(this.pile, 0, -this.pile.getHeight() - this.cart.getHeight() + 90.f, false);
        this.bodyManager = new BodyManager(this.pile, this.background, this, gc.getHeight());
    }

    @Override
    public void render(GameContainer gc, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        this.background.render(graphics);

        this.pile.draw(graphics);
        this.cart.draw(graphics);
        this.followersManager.draw(graphics);
        this.bodyManager.draw(graphics);

        if (this.lightingUntil - System.currentTimeMillis() > 350) {
            if (this.lightingDrawable != null)
                this.lightingDrawable.draw(graphics);
        } else if (this.lightingUntil > System.currentTimeMillis()) {
            this.lightingShader.bind();
            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, gc.getWidth(), gc.getHeight());
        } else {
            this.lightingShader.unbind();
        }

        this.beatmapVisualizer.render(graphics);
        this.heatVisualizer.render(graphics);
        this.musicPositionVisualizer.render(graphics);

        graphics.setColor(Color.white);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame stateBasedGame, int i) throws SlickException {
        if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            if (this.isPaused) {
                this.resumeGame();
            } else {
                this.pauseGame();
            }
        }

        if (this.isPaused) {
            return;
        }

        this.background.update();

        this.cart.update();
        this.cart.checkBounds(-this.cart.box().getWidth()/2, gc.getWidth() - this.cart.box().getWidth()/2);
        this.bodyManager.update();

        this.followersManager.followX = this.cart.getX();
        this.followersManager.followY = this.cart.getY();
        this.followersManager.update();

        if (this.cart.getAnimation().isStopped()) {
            this.cart.getAnimation().start();
        }
        if (!this.followersManager.isAnimating()) {
            this.followersManager.startAnimating();
        }

        this.heatVisualizer.value = this.heat;
        this.musicPositionVisualizer.value = this.music.getPosition() / this.beatmap.totalLength;
        this.beatmapVisualizer.musicPosition = this.music.getPosition();

        if (System.currentTimeMillis() - this.lastThunderHit > RhythmGameSettings.currentSettings().GameThunderInterval && SingleRandom.nextInt(100) < RhythmGameSettings.currentSettings().GameThunderChance) {
            this.performThunderHit();
            this.lastThunderHit = System.currentTimeMillis();
        }

        Beatmap.Hit missedHit = this.beatmap.previousHitFrom(this.music.getPosition());
        if (missedHit.diff > RhythmGameSettings.currentSettings().GameHitOkInterval && !this.hits.containsKey(missedHit.idx)) {
            this.performHit(missedHit.idx, HitStatus.Missed);
        }

        if (this.heat >= RhythmGameSettings.currentSettings().GameFeverHeat) {
            this.performHeatHit();
            this.isFever = true;
        } else if (System.currentTimeMillis() > this.feverUntil && this.isFever) {
            this.undoHeatHit();
            this.isFever = false;
        }

        this.heat -= RhythmGameSettings.currentSettings().GameHeatDrain;
        this.heat = this.heat < 0 ? 0.f : this.heat;

        this.input.update();
        if (this.input.wasKeysPressed(Input.KEY_SPACE)) {
            Beatmap.Hit hit = this.beatmap.closestHitFrom(this.music.getPosition());

            if (this.hitStatus(hit) == HitStatus.Missed) {
                this.performBumpMissed();
            } else {
                this.performBump();
            }
        }

        if (this.input.wasKeysPressed(Input.KEY_UP, Input.KEY_LEFT)) {
            this.performAction(8);
        } else if (this.input.wasKeysPressed(Input.KEY_UP, Input.KEY_RIGHT)) {
            this.performAction(2);
        } else if (this.input.wasKeysPressed(Input.KEY_DOWN, Input.KEY_LEFT)) {
            this.performAction(6);
        } else if (this.input.wasKeysPressed(Input.KEY_DOWN, Input.KEY_RIGHT)) {
            this.performAction(4);
        } else if (this.input.wasKeysPressed(Input.KEY_UP)) {
            this.performAction(1);
        } else if (this.input.wasKeysPressed(Input.KEY_DOWN)) {
            this.performAction(5);
        } else if (this.input.wasKeysPressed(Input.KEY_LEFT)) {
            this.performAction(7);
        } else if (this.input.wasKeysPressed(Input.KEY_RIGHT)) {
            this.performAction(3);
        }

        if (gc.getInput().isKeyPressed(Input.KEY_COMMA)) {
            this.performHeatHit();
        }
        if (gc.getInput().isKeyPressed(Input.KEY_M)) {
            this.bodyCollision(null);
        }

        if (gc.getInput().isKeyPressed(Input.KEY_W)) {
            stateBasedGame.enterState(JegFaller.MAINMENU);
        }
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame stateBasedGame) throws SlickException {
        super.enter(gc, stateBasedGame);

        try {
            this.beatmap = Beatmap.beatmapBasedOn(this.beatmapFile.getAbsolutePath());
            this.beatmapVisualizer.beatmap = this.beatmap;
            this.music = new Music(this.beatmapFile.getParent() + File.separator + "music.ogg");
            RhythmGameSettings.setCurrentSettings(RhythmGameSettings.loadFromFileAt(this.beatmapFile.getParent() + File.separator + "settings.dat"));
            System.out.println(RhythmGameSettings.currentSettings().BodySpawnInterval);
        } catch (Exception e) {
            e.printStackTrace();
            stateBasedGame.enterState(JegFaller.MAINMENU);
            return;
        }

        this.heat = RhythmGameSettings.currentSettings().GameInitialHeat;
        this.bodies = 0;

        BodyManager.SPAWN_INTERVAL = RhythmGameSettings.currentSettings().BodySpawnInterval;
        BodyManager.DROP_INTERVAL = RhythmGameSettings.currentSettings().BodyDropInterval;
        this.beatmapVisualizer.reset();
        this.bodyManager.reset();
        this.followersManager.reset();
        this.pile.reset();
        this.music.play();
        this.hits = new HashMap<>();

        this.pauseGame();
        this.resumeGame();
    }

    @Override
    public void leave(GameContainer gc, StateBasedGame stateBasedGame) throws SlickException {
        super.leave(gc, stateBasedGame);

        this.music.stop();
        this.music = null;
        this.beatmap = null;
    }

    private BoxLayout boxMenu;
    private ValueAdjusterFloat musicVolumeAdjuster, effectsVolumeAdjuster, hitsoundVolumeAdjuster;
    @Override
    protected RootPane createRootPane() {
        RootPane p = super.createRootPane();
        p.setTheme("");
        p.setVisible(false);

        this.musicVolumeAdjuster = new ValueAdjusterFloat();
        this.musicVolumeAdjuster.setMinMaxValue(0, 1.f);
        this.musicVolumeAdjuster.setFormat("Music volume: %.1f");
        this.musicVolumeAdjuster.setStepSize(0.2f);
        this.musicVolumeAdjuster.setValue(0.5f);

        this.effectsVolumeAdjuster = new ValueAdjusterFloat();
        this.effectsVolumeAdjuster.setMinMaxValue(0, 1.f);
        this.effectsVolumeAdjuster.setFormat("Effects volume: %.1f");
        this.effectsVolumeAdjuster.setStepSize(0.2f);
        this.effectsVolumeAdjuster.setValue(0.8f);

        this.hitsoundVolumeAdjuster = new ValueAdjusterFloat();
        this.hitsoundVolumeAdjuster.setMinMaxValue(0, 1.f);
        this.hitsoundVolumeAdjuster.setStepSize(0.2f);
        this.hitsoundVolumeAdjuster.setFormat("Hitsound volume: %.1f");
        this.hitsoundVolumeAdjuster.setValue(0.8f);

        BoxLayout settings = new BoxLayout(BoxLayout.Direction.VERTICAL);
        settings.setTheme("pane");
        settings.add(this.musicVolumeAdjuster);
        settings.add(this.effectsVolumeAdjuster);
        settings.add(this.hitsoundVolumeAdjuster);

        BoxLayout menu = new BoxLayout(BoxLayout.Direction.VERTICAL);
        Button b;
        b = new Button("Resume");
        b.addCallback(this::resumeGame);
        menu.add(b);

        b = new Button("Main menu");
        b.addCallback(() -> {
            this.stateBasedGame.enterState(JegFaller.MAINMENU);
        });
        menu.add(b);

        b = new Button("Quit to desktop");
        b.addCallback(this.gameContainer::exit);
        menu.add(b);

        this.boxMenu = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
        this.boxMenu.add(menu);
        this.boxMenu.add(settings);
        p.add(this.boxMenu);
        return p;
    }

    @Override
    protected void layoutRootPane() {
        super.layoutRootPane();

        this.boxMenu.adjustSize();
        this.boxMenu.setPosition(
                this.gameContainer.getWidth() / 2 - this.boxMenu.getWidth() / 2,
                this.gameContainer.getHeight() / 2 - this.boxMenu.getHeight() / 2);
    }

    protected void pauseGame() {
        this.isPaused = true;

        this.cart.getAnimation().stop();
        this.followersManager.stopAnimating();
        this.music.pause();
        this.getRootPane().setVisible(true);
    }

    protected void resumeGame() {
        this.isPaused = false;

        this.cart.getAnimation().start();
        this.followersManager.startAnimating();
        this.music.resume();
        this.getRootPane().setVisible(false);

        this.music.setVolume(this.musicVolumeAdjuster.getValue());
        float effectsVolume = this.effectsVolumeAdjuster.getValue();
        this.soundSetBody.setVolume(effectsVolume);
        this.soundSetThunder.setVolume(effectsVolume);
        this.soundSetHit.setVolume(this.hitsoundVolumeAdjuster.getValue());
    }

    @Override
    public void bodyCollision(Body b) throws SlickException {
        this.pile.addBody();
        this.bodies++;
        this.soundSetBody.playRandom();
    }

    protected void performAction(int action) {
        this.soundSetHit.playRandom();

        Beatmap.Hit hit = this.beatmap.closestHitFrom(this.music.getPosition());
        if (!this.hits.containsKey(hit.idx)) {
            HitStatus status = HitStatus.Missed;
            if (hit.action == action) {
                status = this.hitStatus(hit);
            }

            this.performHit(hit.idx, status);
        }
    }

    protected void performHit(int index, HitStatus status) {
        Color color = null;
        float heatAdd = 0;
        switch (status) {
            case Excellent:
                heatAdd = RhythmGameSettings.currentSettings().GameHitExcellentHeat;
                color = new Color(0x84F5D9);
            break;
            case Good:
                heatAdd = RhythmGameSettings.currentSettings().GameHitGoodHeat;
                color = new Color(0xA0F584);
                break;
            case Ok:
                heatAdd = RhythmGameSettings.currentSettings().GameHitOkHeat;
                color = new Color(0xE4F584);
                break;
            case Missed:
                heatAdd = RhythmGameSettings.currentSettings().GameHitMissHeat;
                color = new Color(0xF59384);
                break;
        }

        this.heat += heatAdd;
        this.beatmapVisualizer.overrideActionColor(index, color);
        this.hits.put(index, status);
    }

    protected void performBumpMissed() {
        switch (SingleRandom.nextInt(1)) {
            case 0:
                this.cart.missedBumpBreak();
                break;
            case 1:
                this.cart.missedBumpSpeedup();
                break;
        }
    }

    protected void performBump() {
        this.cart.bump();
    }

    protected void performThunderHit() {
        this.lightingUntil = System.currentTimeMillis() + RhythmGameSettings.currentSettings().GameThunderDuration;

        float x = SingleRandom.nextInt(500) + 100;
        float y = SingleRandom.nextInt(500);
        this.lightingDrawable = new Lighting(new Rectangle(x, 0, this.cart.getWidth(), y));

        this.soundSetThunder.playRandom();
    }

    protected void performHeatHit() throws SlickException {
        this.heat = 0.f;
        this.feverUntil = System.currentTimeMillis() + RhythmGameSettings.currentSettings().GameFeverDuration;
        this.isFever = true;
        this.lastThunderHit = System.currentTimeMillis();
        this.lightingUntil = System.currentTimeMillis() + RhythmGameSettings.currentSettings().GameThunderDuration;
        this.lightingDrawable = new Lighting(new Rectangle(this.cart.getX(), 0, this.cart.getWidth(), this.pile.box().getY()));

        BodyManager.SPAWN_INTERVAL = RhythmGameSettings.currentSettings().BodyFeverSpawnInterval;
        this.heatVisualizer.fill = true;

        float height = Math.max(this.pile.box().getHeight() + 35.f, this.fireFactory.maxSpriteHeight());

        Entity fires = this.fireFactory.pileSpriteWithSize(this.pile.getWidth(), height);
        this.followersManager.addEntity(
                fires,
                0,
                -fires.getHeight() + 35.f
        );

        this.soundSetThunder.playLast();
    }

    protected void undoHeatHit() {
        BodyManager.SPAWN_INTERVAL = RhythmGameSettings.currentSettings().BodySpawnInterval;
        this.heatVisualizer.fill = false;
        this.followersManager.destroyEntity(this.followersManager.iter().lastElement());
    }

    protected HitStatus hitStatus(Beatmap.Hit hit) {
        HitStatus status;
        if (hit.diff < RhythmGameSettings.currentSettings().GameHitExcellentInterval) {
            status = HitStatus.Excellent;
        } else if (hit.diff < RhythmGameSettings.currentSettings().GameHitGoodInterval) {
            status = HitStatus.Good;
        } else if (hit.diff < RhythmGameSettings.currentSettings().GameHitOkInterval) {
            status = HitStatus.Ok;
        } else {
            status = HitStatus.Missed;
        }

        return status;
    }
}
