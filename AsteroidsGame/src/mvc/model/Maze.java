package mvc.model;

import mvc.controller.Game;

import java.awt.*;

public class Maze {
    // This class handles making mazes for every other level

    // no arg constructor
    public Maze() {
    }

    // Makes the maze
    public void makeMaze() {
        // Randomly choose one of the four mazes
        int type = Game.R.nextInt(4);
        // Set the falcon to unprotected and stop it's momentum
        if (CommandCenter.getInstance().getFalcon() != null) {
            CommandCenter.getInstance().getFalcon().setProtected(false);
            CommandCenter.getInstance().getFalcon().setFadeValue(255);
            CommandCenter.getInstance().getFalcon().setDeltaY(0);
            CommandCenter.getInstance().getFalcon().setDeltaX(0);
        }
        // First maze
        if (type %4 == 0) {
            if (CommandCenter.getInstance().getFalcon() != null) {
                CommandCenter.getInstance().getFalcon().setCenter(new Point(130,200));
            }
            makeMineLine(true, 110,0,40);
            makeMineLine(true,110,0,800);
            makeMineLine(false,50,300,40);
            makeMineLine(false,50,700,300);
            makeFloater(900,700);
            makeNuisance(900,700);
        }
        // Second maze
        else if (type %4 == 2) {
            if (CommandCenter.getInstance().getFalcon() != null) {
                CommandCenter.getInstance().getFalcon().setCenter(new Point(130,150));
            }
            makeMineLine(true, 110,0,40);
            makeMineLine(true,110,0,800);
            makeMineLine(true,40,0,220);
            makeMineLine(true,70,500,220);
            makeMineLine(true, 70, 0,500 );
            makeMineLine(true,40,800,500);
            makeFloater(900,700);
            makeNuisance(900,700);
        }
        // Third maze
        else if (type %4 == 3) {
            if (CommandCenter.getInstance().getFalcon() != null) {
                CommandCenter.getInstance().getFalcon().setCenter(new Point(900,700));
            }
            makeMineLine(true, 110,0,40);
            makeMineLine(true,110,0,800);
            makeMineLine(true,30,0,500);
            makeMineLine(true,30,400,500);
            makeMineLine(true,30,800,500);
            makeMineLine(false, 20, 600,0 );
            makeMineLine(false, 70, 600,300 );
            makeFloater(200,700);
            makeNuisance(200,700);
        }
        // Fourth maze
        else {
            if (CommandCenter.getInstance().getFalcon() != null) {
                CommandCenter.getInstance().getFalcon().setCenter(new Point(900,700));
            }
            makeMineLine(true, 110,0,40);
            makeMineLine(true,110,0,800);
            makeMineLine(true, 90, 200,600);
            makeMineLine(true, 90, 0,400);
            makeMineLine(true, 90, 200,200);
            makeMineLine(false,80,10,40);
            makeMineLine(false,80,1080,40);
            makeFloater(900,100);
            makeNuisance(900,100);
        }
    }

    // Makes a line of mines
    public void makeMineLine(boolean horizontal, int length, int xCoord, int yCoord) {
        int x = xCoord;
        int y = yCoord;
        // Make horizontal line
        if (horizontal == true) {
            // Make mines depending on length
            for (int i = 0; i < length; i++) {
                Mine mine = new Mine();
                mine.setCenter(new Point(x, y));
                CommandCenter.getInstance().getOpsList().enqueue(mine, CollisionOp.Operation.ADD);
                x += 10;
            }
        }
        // Make vertical line
        else{
            for (int i = 0; i < length; i++) {
                Mine mine = new Mine();
                mine.setCenter(new Point(x, y));
                CommandCenter.getInstance().getOpsList().enqueue(mine, CollisionOp.Operation.ADD);
                y += 10;
            }
        }
    }

    // Makes the bonuslevelfloater at a certain location
    public void makeFloater(int xCoord, int yCoord) {
        BonusLevelFloater end = new BonusLevelFloater();
        Point pnt = new Point(xCoord,yCoord);
        end.setCenter(pnt);
        CommandCenter.getInstance().getOpsList().enqueue(end, CollisionOp.Operation.ADD);
    }

    public  void makeNuisance(int xCoord, int yCoord) {
        Falcon fal = CommandCenter.getInstance().getFalcon();
        MazeNuisance nuisance = new MazeNuisance(fal);
        Point pnt = new Point(xCoord,yCoord);
        nuisance.setCenter(pnt);
        CommandCenter.getInstance().getOpsList().enqueue(nuisance, CollisionOp.Operation.ADD);

    }
}
