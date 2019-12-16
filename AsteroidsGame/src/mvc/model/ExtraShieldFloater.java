package mvc.model;

import java.awt.*;

public class ExtraShieldFloater extends NewShipFloater{
    public ExtraShieldFloater() {
        super();
        setColor(Color.cyan);
    }
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.setColor(Color.cyan);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }
}
