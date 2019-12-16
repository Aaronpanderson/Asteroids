package mvc.model;

import mvc.controller.Game;

import java.awt.*;
import java.util.ArrayList;

public class Mine extends Sprite {
    // Makes a mine for the maze levels which are essential walls for the maze

    public Mine() {
        super();
        //Foe team
        setTeam(Team.FOE);
        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0, 6));
        pntCs.add(new Point(4,4));
        pntCs.add(new Point(6,0));
        pntCs.add(new Point(4,-4));
        pntCs.add(new Point(0, -6));
        pntCs.add(new Point(-4,-4));
        pntCs.add(new Point(-6,0));
        pntCs.add(new Point(-4,4));

        assignPolarPoints(pntCs);

        setColor(Color.white);

        // Default location 0,0
        setCenter(new Point(0, 0));

        // Small
        setRadius(5);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.setColor(Color.RED);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        g.setColor(Color.RED);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }
}
