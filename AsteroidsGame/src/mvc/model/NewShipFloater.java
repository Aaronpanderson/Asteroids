package mvc.model;

import mvc.controller.Game;
import mvc.view.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class NewShipFloater extends Sprite {


	private int nSpin;

	public NewShipFloater() {

		super();
		setTeam(Team.FLOATER);
		ArrayList<Point> pntCs = new ArrayList<Point>();

		pntCs.add(new Point(5, 5));
		pntCs.add(new Point(4,0));
		pntCs.add(new Point(5, -5));
		pntCs.add(new Point(0,-4));
		pntCs.add(new Point(-5, -5));
		pntCs.add(new Point(-4,0));
		pntCs.add(new Point(-5, 5));
		pntCs.add(new Point(0,4));

		assignPolarPoints(pntCs);

		setExpire(250);
		setRadius(50);
		setColor(Color.BLUE);
		
		//set random DeltaX
		int nSpin = Game.R.nextInt(10);
		if(nSpin %2 ==0)
			nSpin = -nSpin;
		setSpin(nSpin);

		//random delta-x
		int nDX = Game.R.nextInt(10);
		if(nDX %2 ==0)
			nDX = -nDX;
		setDeltaX(nDX);

		//random delta-y
		int nDY = Game.R.nextInt(10);
		if(nDY %2 ==0)
			nDY = -nDY;
		setDeltaY(nDY);

		//random point on the screen
		setCenter(new Point(Game.R.nextInt(Game.DIM.width),
				Game.R.nextInt(Game.DIM.height)));

		//random orientation 
		 setOrientation(Game.R.nextInt(360));

	}

	@Override
	public void move() {
		super.move();
		setOrientation(getOrientation() + getSpin());

		//adding expire functionality
		if (getExpire() == 0)
			CommandCenter.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
		else
			setExpire(getExpire() - 1);


	}

	public int getSpin() {
		return this.nSpin;
	}

	public void setSpin(int nSpin) {
		this.nSpin = nSpin;
	}




	@Override
	public void draw(Graphics g) {
		super.draw(g);
		//fill this polygon (with whatever color it has)
		g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
		//now draw a white border
		g.setColor(Color.BLUE);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}

}
