package mvc.model;

import java.awt.*;
import java.util.ArrayList;

public class MazeNuisance extends Sprite {
    public static final double PULL = 0.3;
    private int spin;
    private Falcon falcon;

    public MazeNuisance(Falcon falcon) {
        super();
        this.falcon = falcon;
        ArrayList<Point> pntCs = new ArrayList<Point>();

        //Design
        pntCs.add(new Point(0, 6));
        pntCs.add(new Point(4,4));
        pntCs.add(new Point(6,0));
        pntCs.add(new Point(4,-4));
        pntCs.add(new Point(0, 6));
        pntCs.add(new Point(-4,-4));
        pntCs.add(new Point(-6,0));
        pntCs.add(new Point(-4,4));
        assignPolarPoints(pntCs);

        // Bad guy
        setTeam(Team.FOE);
        setColor(Color.ORANGE);
        // don't want it to expire
        setExpire(2100000000);
        // Sorta big
        setRadius(20);
        // Spinning is pretty
        setSpin(20);

    }

    public int getSpin() {
        return this.spin;
    }

    public void setSpin(int nSpin) {
        this.spin = nSpin;
    }

    @Override
    public void move() {
        super.move();
        setOrientation(getOrientation() + getSpin());

        Point myCenter = getCenter();
        Point falCenter = falcon.getCenter();

        if (myCenter.x > falCenter.x){
            setDeltaX(getDeltaX() - PULL);
        } else {
            setDeltaX(getDeltaX() +PULL);
        }

        if (myCenter.y > falCenter.y){
            setDeltaY(getDeltaY() -PULL);
        } else {
            setDeltaY(getDeltaY() +PULL);
        }

        if (getDeltaX()>10) {
            setDeltaX(10);
        }
        if (getDeltaY()>10) {
            setDeltaY(10);
        }
        if (getDeltaY()<-10) {
            setDeltaY(-10);
        }
        if (getDeltaX()<-10){
            setDeltaX(-10);
        }

    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(Color.ORANGE);
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }
}
