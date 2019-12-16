package mvc.model;

import mvc.controller.Game;

import java.awt.*;

public class BonusLevelFloater extends NewShipFloater{
    // Green floater for the mazes, this floater gives a huge score and an extra life

    public BonusLevelFloater() {
        super();
        setColor(Color.GREEN);
        // Don't want it to move
        setDeltaX(0);
        setDeltaY(0);
        // Spin makes it look cool
        setSpin(8);
        // default at 0,0
        setCenter(new Point(0,0));
        // don't want it to expire
        setExpire(2100000000);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.setColor(Color.GREEN);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }
}
