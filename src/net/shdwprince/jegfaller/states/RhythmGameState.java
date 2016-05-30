package net.shdwprince.jegfaller.states;

import it.twl.util.BasicTWLGameState;
import net.shdwprince.jegfaller.JegFaller;
import net.shdwprince.jegfaller.game.*;
import net.shdwprince.jegfaller.lib.Background;
import net.shdwprince.jegfaller.lib.ShaderProgram;
import net.shdwprince.jegfaller.lib.Sprite;
import net.shdwprince.jegfaller.lib.entities.Entity;
import net.shdwprince.jegfaller.lib.rhythm.Beatmap;
import net.shdwprince.jegfaller.lib.ui.BeatmapVisualizer;
import net.shdwprince.jegfaller.lib.ui.MeterVisualizer;
import net.shdwprince.jegfaller.lib.ui.MusicProgressVisualizer;
import org.lwjgl.Sys;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;
import org.w3c.dom.css.Rect;
import sun.nio.cs.HistoricallyNamedCharset;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by sp on 5/27/16.
 */
public class RhythmGameState extends BasicTWLGameState implements BodyManager.Listener {
    private static float DIFF_EXC = 0.03f, DIFF_GOOD = 0.1f, DIFF_OK = 0.3f;
    private static enum HitStatus {
        Excellent,
        Good,
        Ok,
        Missed
    };

    public File beatmapFile;

    protected ShaderProgram lightingShader;
    protected Beatmap beatmap;
    protected Music music;
    protected float heat;
    protected long bodies;
    protected HashMap<Integer, HitStatus> hits;

    protected Cart cart;
    protected BodyManager bodyManager;
    protected Pile pile;
    protected FollowersManager followersManager;

    protected Background background;
    protected BeatmapVisualizer beatmapVisualizer;
    protected MeterVisualizer heatVisualizer;
    protected Random random;
    protected long lastThunderHit, lightingUntil, feverUntil;

    @Override
    public int getID() {
        return JegFaller.RHYTHMGAME;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame stateBasedGame) throws SlickException {
        Cart.SLOWDOWN = 0.1f;
        Cart.BUMP = 8.f;
        Cart.MIN_SPEED = -2f;
        Cart.MAX_SPEED = 2.5f;

        Body.GRAVITY = 0.05f;
        Body.ROTATION_SPEED = 0.1f;

        BodyManager.SPAWN_INTERVAL = 8000;
        BodyManager.DROP_INTERVAL = 3500;

        this.random = new Random();
        this.lightingShader =  ShaderProgram.loadProgram("assets/shader/a.vert", "assets/shader/a.frag");
        this.beatmapVisualizer = new BeatmapVisualizer(new Rectangle(
                150,
                150,
                gc.getWidth() - 300,
                100
        ));
        this.heatVisualizer = new MeterVisualizer(new Rectangle(150, 150, 100, 10), 100.f);
        this.background = new Background(new Rectangle(0, 0, gc.getWidth(), gc.getHeight()));
        this.lastThunderHit = System.currentTimeMillis();

        this.cart = new Cart(gc.getWidth() / 2 - 100, gc.getHeight() - 65.f - 20.f);
        this.followersManager = new FollowersManager();

        this.pile = new Pile(new Rectangle(0, 0, this.cart.getWidth(), 400.f));
        Entity doctor = new Entity(new String[] {"assets/doctor.png", "assets/doctor_f2.png"}, 1);
        Entity horses = new Entity(new String[] {"assets/horses.png", "assets/horses_f2.png"}, 500);
        this.followersManager.addEntity(horses, this.cart.getWidth(), -(horses.getHeight() - this.cart.getHeight()));
        this.followersManager.addEntity(doctor, this.cart.getWidth() + horses.getWidth() - 30.f, -(doctor.getHeight() - this.cart.getHeight()));
        this.followersManager.addEntity(this.pile, 0, -this.pile.getHeight() - this.cart.getHeight() + 90.f, false);
        this.bodyManager = new BodyManager(this.pile, this.background, this, gc.getHeight());
    }

    @Override
    public void render(GameContainer gc, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        if (this.lightingUntil > System.currentTimeMillis()) {
            this.lightingShader.bind();
            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, gc.getWidth(), gc.getHeight());
        } else {
            this.lightingShader.unbind();
        }

        this.background.render(graphics);

        this.pile.draw(graphics);
        this.cart.draw(graphics);
        this.followersManager.draw(graphics);
        this.bodyManager.draw(graphics);

        this.beatmapVisualizer.render(graphics);
        this.heatVisualizer.render(graphics);

        graphics.setColor(Color.white);
        graphics.drawString(String.format("Count: %d", this.bodies), 254, 146);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame stateBasedGame, int i) throws SlickException {
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
        } else if (this.followersManager.isAnimating()) {
            this.followersManager.stopAnimating();
        }

        this.heatVisualizer.value = this.heat;
        this.beatmapVisualizer.musicPosition = this.music.getPosition();

        if (System.currentTimeMillis() - this.lastThunderHit > 5000 && this.random.nextInt(100) < 5) {
            this.performThunderHit();
            this.lastThunderHit = System.currentTimeMillis();
        }

        Beatmap.Hit missedHit = this.beatmap.previousHitFrom(this.music.getPosition());
        if (missedHit.diff > DIFF_OK && !this.hits.containsKey(missedHit.idx)) {
            this.performHit(missedHit.idx, HitStatus.Missed);
        }

        if (this.heat >= 100.f) {
            this.heat = 0.f;
            this.feverUntil = System.currentTimeMillis() + 10000;
        }

        if (System.currentTimeMillis() < this.feverUntil) {
            BodyManager.SPAWN_INTERVAL = 1000;
            this.heatVisualizer.fill = true;
        } else {
            BodyManager.SPAWN_INTERVAL = 8000;
            this.heatVisualizer.fill = false;
        }

        this.heat -= 0.01f;
        this.heat = this.heat < 0 ? 0.f : this.heat;

        if (gc.getInput().isKeyPressed(Input.KEY_SPACE)) {
            Beatmap.Hit hit = this.beatmap.closestHitFrom(this.music.getPosition());

            if (this.hitStatus(hit) == HitStatus.Missed) {
                this.performBumpMissed();
            } else {
                this.performBump();
            }
        }

        if (gc.getInput().isKeyPressed(Input.KEY_K)) {
            this.performAction(2);
        } else if (gc.getInput().isKeyPressed(Input.KEY_J)) {
            this.performAction(3);
        } else if (gc.getInput().isKeyPressed(Input.KEY_I)) {
            this.performAction(1);
        } else if (gc.getInput().isKeyPressed(Input.KEY_L)) {
            this.performAction(4);
        }

        if (gc.getInput().isKeyPressed(Input.KEY_W)) {
            stateBasedGame.enterState(JegFaller.MAINMENU);
        }
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame stateBasedGame) throws SlickException {
        try {
            this.beatmap = Beatmap.beatmapBasedOn(this.beatmapFile.getAbsolutePath());
            this.beatmapVisualizer.beatmap = this.beatmap;
            this.music = new Music(this.beatmapFile.getParent() + File.separator + "music.ogg");
        } catch (Exception e) {
            e.printStackTrace();
            stateBasedGame.enterState(JegFaller.MAINMENU);
            return;
        }

        this.heat = 50.f;
        this.bodies = 0;
        this.bodyManager.reset();
        this.followersManager.reset();
        this.pile.reset();
        this.music.play();
        this.hits = new HashMap<>();
        this.music.setVolume(0.3f);
    }

    @Override
    public void leave(GameContainer gc, StateBasedGame stateBasedGame) throws SlickException {
        this.music.stop();
        this.music = null;
        this.beatmap = null;
    }

    @Override
    public void bodyCollision(Body b) throws SlickException {
        this.pile.addBody();
        this.bodies++;
    }

    protected void performAction(int action) {
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
                heatAdd = 3.f;
                color = new Color(0x84F5D9);
            break;
            case Good:
                heatAdd = 1.5f;
                color = new Color(0xA0F584);
                break;
            case Ok:
                heatAdd = 1f;
                color = new Color(0xE4F584);
                break;
            case Missed:
                heatAdd = -1.f;
                color = new Color(0xF59384);
                break;
        }

        this.heat += heatAdd;
        this.beatmapVisualizer.overrideActionColor(index, color);
        this.hits.put(index, status);
    }

    protected void performBumpMissed() {
        switch (this.random.nextInt(1)) {
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
        this.lightingUntil = System.currentTimeMillis() + 250;
    }

    protected HitStatus hitStatus(Beatmap.Hit hit) {
        HitStatus status;
        if (hit.diff < DIFF_EXC) {
            status = HitStatus.Excellent;
        } else if (hit.diff < DIFF_GOOD) {
            status = HitStatus.Good;
        } else if (hit.diff < DIFF_OK) {
            status = HitStatus.Ok;
        } else {
            status = HitStatus.Missed;
        }

        return status;
    }
}
