package net.shdwprince.jegfaller.states;

import de.matthiasmann.twl.*;
import de.matthiasmann.twl.model.SimpleAutoCompletionResult;
import de.matthiasmann.twl.textarea.TextAreaModel;
import it.twl.util.BasicTWLGameState;
import it.twl.util.RootPane;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import de.matthiasmann.twl.BoxLayout.Direction;
import de.matthiasmann.twl.DialogLayout.Group;

public class TWLState extends BasicTWLGameState {

    private BoxLayout box;

    @Override
    protected RootPane createRootPane() {
        RootPane rp = super.createRootPane();
        rp.setTheme("");

        this.box = new BoxLayout(Direction.VERTICAL);

        Label label = new Label();
        label.setText("1");

        Label label2 = new Label();
        label2.setText("2");

        Label label3 = new Label();
        label3.setText("3");

        this.box.add(label);
        this.box.add(label2);
        this.box.add(label3);

        rp.add(this.box);
        return rp;
    }

    @Override
    protected void layoutRootPane() {
        this.box.setPosition(600, 600);
        this.box.adjustSize();
    }

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1)
            throws SlickException {
        // Your slick logic here
    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
            throws SlickException {
        // Your slick logic here
    }

    @Override
    public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
            throws SlickException {
        // Your slick logic here
    }

    @Override
    public int getID() {
        return 100;
    }
}