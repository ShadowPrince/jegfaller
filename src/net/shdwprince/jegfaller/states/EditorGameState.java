package net.shdwprince.jegfaller.states;

import de.matthiasmann.twl.*;
import it.twl.util.BasicTWLGameState;
import it.twl.util.RootPane;
import it.twl.util.TWLStateBasedGame;
import net.shdwprince.jegfaller.JegFaller;
import net.shdwprince.jegfaller.lib.rhythm.Beatmap;
import net.shdwprince.jegfaller.lib.ui.BeatmapVisualizer;
import net.shdwprince.jegfaller.lib.ui.MusicProgressVisualizer;
import net.shdwprince.jegfaller.lib.ui.UIHelper;
import org.newdawn.slick.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;
import java.util.Vector;

/**
 * Created by sp on 5/27/16.
 */
public class EditorGameState extends BasicTWLGameState {
    protected BeatmapVisualizer beatmapVisualizer;
    protected MusicProgressVisualizer musicProgressVisualizer;

    protected File beatmapFile;
    protected Beatmap beatmap;
    protected Music music;
    protected float musicPosition;

    protected long bmpMusicStart;
    protected long bmpLastKeyPress;
    protected Vector<Long> bpmMeterBeats;
    protected int state;

    @Override
    public int getID() {
        return JegFaller.EDITOR;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        this.beatmapVisualizer = new BeatmapVisualizer(new Rectangle(0, 300, gameContainer.getWidth(), 300));
        this.musicProgressVisualizer = new MusicProgressVisualizer(new Rectangle(300, gameContainer.getHeight() - 100, gameContainer.getWidth() - 600, 50));
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        graphics.setColor(Color.white);
        graphics.drawString("n - new beatmap; o - open beatmap; j - save beatmap", 100, 0);
        graphics.drawString("Current beatmap: " + this.beatmap, 100, 0 + graphics.getFont().getLineHeight());
        graphics.drawString("Editor mode: " + this.state, 100, 0 + graphics.getFont().getLineHeight() * 2);

        if (this.music != null) {
            String musicState = String.format("[%s] %f", this.music.playing() ? "playing" : "paused", this.musicPosition);
            graphics.drawString(musicState, 100, 0 + graphics.getFont().getLineHeight() * 3);
        }

        if (this.state == 1 && this.bmpLastKeyPress > 0) {
            long delta = (System.currentTimeMillis() - this.bmpLastKeyPress) / 3;
            float c = ((float) 255 - delta) / 255;
            graphics.setColor(new Color(c, c, c));
            graphics.fillRect(300, 300, 300, 300);
        } else if (this.state == 10) {
            this.beatmapVisualizer.render(graphics);
            this.musicProgressVisualizer.render(graphics);
        }
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int skip) throws SlickException {
        Input i = gameContainer.getInput();
        if (i.isKeyPressed(Input.KEY_O)) {
            try {
                this.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (i.isKeyPressed(Input.KEY_N)) {
            this.reset();
        }

        switch (this.state) {
            case 0:
                if (i.isKeyPressed(Input.KEY_SPACE)) {
                    this.state = 1;
                    this.music.play();
                    this.bmpMusicStart = System.currentTimeMillis();
                }
                break;
            case 1:
                if (i.isKeyPressed(Input.KEY_SPACE)) {
                    this.bmpLastKeyPress = System.currentTimeMillis();
                    this.bpmMeterBeats.add(new Long(System.currentTimeMillis()));
                }

                if (i.isKeyPressed(Input.KEY_ENTER)) {
                    this.calculateIntervals();
                    this.state = 10;
                }
                break;
            case 10:
                if (this.music.playing()) {
                    this.musicPosition = this.music.getPosition();
                    this.musicProgressVisualizer.musicPosition = this.musicPosition;
                    this.beatmapVisualizer.musicPosition = this.musicPosition;
                }

                if (i.isKeyPressed(Input.KEY_SPACE)) {
                    if (this.music.playing()) {
                        this.music.pause();
                    } else {
                        this.music.resume();
                        this.music.setPosition(this.musicPosition);

                    }
                } else if (i.isKeyPressed(Input.KEY_A)) {
                    this.seekBackward();
                } else if (i.isKeyPressed(Input.KEY_D)) {
                    this.seekForward();


                } else if (i.isKeyPressed(Input.KEY_UP)) {
                    this.insertAction(1);
                } else if (i.isKeyPressed(Input.KEY_DOWN)) {
                    this.insertAction(2);
                } else if (i.isKeyPressed(Input.KEY_LEFT)) {
                    this.insertAction(3);
                } else if (i.isKeyPressed(Input.KEY_RIGHT)) {
                    this.insertAction(4);
                } if (i.isKeyPressed(Input.KEY_BACK)) {
                    this.removeAction();

                } else if (i.isKeyPressed(Input.KEY_J)) {
                    try {
                        this.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (i.isKeyPressed(Input.KEY_X)) {
                    this.beatmapVisualizer.scale += 33.f;
                } else if (i.isKeyPressed(Input.KEY_Z)) {
                    this.beatmapVisualizer.scale -= 33.f;
                }


                break;
        }
    }

    @Override
    public void enter(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        super.enter(gameContainer, stateBasedGame);
        gameContainer.getInput().enableKeyRepeat();
        this.state = -1;

        if (false) {
            try {
                this.music = new Music("assets/Colors.jfb/music.ogg");
                this.music.play();
                this.beatmap = Beatmap.beatmapBasedOn("assets/Colors.jfb/beatmap.dat");
                this.beatmapVisualizer.beatmap = this.beatmap;
                this.musicProgressVisualizer.totalLength = this.beatmap.totalLength;
                this.state = 10;
            } catch (Exception e) {
                e.printStackTrace();
                throw new SlickException("failed to load beatmap");
            }
        }
    }

    @Override
    public void leave(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        super.leave(gameContainer, stateBasedGame);
    }

    @Override
    protected RootPane createRootPane() {
        RootPane p = super.createRootPane();

        return p;
    }

    protected BoxLayout createStatusbar() {
        BoxLayout statusbar = new BoxLayout(BoxLayout.Direction.HORIZONTAL);
    }

    protected void reset() throws SlickException{
        File path;
        if ((path = UIHelper.instance().selectFile()) != null) {
            this.bpmMeterBeats = new Vector<>();
            this.bmpLastKeyPress = 0;
            this.bmpMusicStart = 0;

            this.music = new Music(path.getParent() + File.pathSeparator + "music.ogg");
            this.beatmap = new Beatmap();
            this.beatmap.totalLengthFrom(this.music);
            this.beatmapVisualizer.beatmap = this.beatmap;
            this.musicProgressVisualizer.totalLength = this.beatmap.totalLength;

            this.state = 0;
            this.music.stop();
        }
    }

    protected void load() throws Exception {
        File path;
        if ((path = UIHelper.instance().selectFile()) != null) {
            this.beatmap = Beatmap.beatmapBasedOn(path.getAbsolutePath());
            this.beatmapVisualizer.beatmap = this.beatmap;
            this.musicProgressVisualizer.totalLength = this.beatmap.totalLength;

            this.state = 10;
            this.music = new Music(path.getParent() + File.separator + "music.ogg");
            this.music.play();
        }
    }

    protected void save() throws Exception {
        if (this.beatmapFile != null) {
            this.beatmap.saveIn(this.beatmapFile.getAbsolutePath());
        } else {
            File path;
            if ((path = UIHelper.instance().selectFile()) != null) {
                this.beatmapFile = new File(path + File.separator + "beatmap.dat");
                this.save();
            }
        }
    }

    protected void calculateIntervals() {
        long count = 0;
        long intervalTotal = 0;
        long previous = 0;
        long offset = 0;
        for (Long beatTime : this.bpmMeterBeats) {
            if (previous == 0) {
                offset = beatTime - this.bmpMusicStart;
            } else {
                count++;
                intervalTotal += beatTime - previous;
            }

            previous = beatTime;
        }

        this.beatmap.beatSize = (float) (intervalTotal/count) / 1000;
        this.beatmap.beatOffset = (float) offset / 1000;
        this.beatmap.createActionsArray();
    }

    protected void insertAction(int action) {
        int idx = this.beatmap.closestHitFrom(this.musicPosition).idx;
        this.beatmap.actions[idx] = action;
    }

    protected void removeAction() {
        int idx = this.beatmap.closestHitFrom(this.musicPosition).idx;
        this.beatmap.actions[idx] = 0;
    }

    protected void seekBackward() {
        float prev = this.beatmap.prevOffsetFrom(this.musicPosition-0.001f);
        this.musicPosition = prev;
        this.beatmapVisualizer.musicPosition = this.musicPosition;
        this.musicProgressVisualizer.musicPosition = this.musicPosition;
        this.music.setPosition(prev);
    }

    protected void seekForward() {
        float next = this.beatmap.nextOffsetFrom(this.musicPosition+0.001f);
        this.musicPosition = next;
        this.beatmapVisualizer.musicPosition = this.musicPosition;
        this.musicProgressVisualizer.musicPosition = this.musicPosition;
        this.music.setPosition(next);
    }
}
