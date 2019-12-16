package mvc.model;

import java.awt.*;

public class DoubleFalconFloater extends NewShipFloater {
    public DoubleFalconFloater() {
        super();
        setColor(Color.YELLOW);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.setColor(Color.YELLOW);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }
}
