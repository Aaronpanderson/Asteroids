package mvc.controller;

import mvc.model.*;
import mvc.view.GamePanel;
import sounds.Sound;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

	// ===============================================
	// FIELDS
	// ===============================================

	public static final Dimension DIM = new Dimension(1100, 900); //the dimension of the game.
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
	private int nTick = 0;
	// Sets when each maze level is started
	private int mazeTime=0;

	private boolean bMuted = false;
	private boolean inMaze = true;
	public static int shieldsAvailable = 1;
	

	private final int PAUSE = 80, // p key
			QUIT = 81, // q key
			LEFT = 37, // rotate left; left arrow
			RIGHT = 39, // rotate right; right arrow
			UP = 38, // thrust; up arrow
			START = 83, // s key
			FIRE = 32, // space key
			MUTE = 77, // m-key mute

	// for possible future use
	 HYPER = 68, 					// d key
	 SHIELD = 65, 				// a key
	 SPECIAL = 70; 					// fire special weapon;  F key

	private Clip clpThrust;
	private Clip clpMusicBackground;

	private static final int SPAWN_NEW_SHIP_FLOATER = 800;
	private static final int SPAWN_DOUBLE_FALCON_FLOATER = 1500;
	private static final int SPAWN_EXTRA_SHIELD_FLOATER = 400;

	//Tracks number of falcons on the screen
	private int numFalcons=1;



	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {

		gmpPanel = new GamePanel(DIM);
		gmpPanel.addKeyListener(this);
		clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
		clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");
	

	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
			thrAnim.start();
		}
	}

	// implements runnable - must have run method
	public void run() {

		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim) {
			tick();
			gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must 
														// surround the sleep() in a try/catch block
														// this simply controls delay time between 
														// the frames of the animation

			//this might be a good place to check for collisions
			checkCollisions();

			// Non maze levels
			if (!inMaze) {
				spawnRandom();
			}
			else {
				if (nTick > mazeTime+1000) {
					clearMaze();
				}
			}


			//this might be a god place to check if the level is clear (no more foes)
			//if the level is clear then spawn some big asteroids -- the number of asteroids 
			//should increase with the level. 
			checkNewLevel();

			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run

	private void checkCollisions() {

		

		Point pntFriendCenter, pntFoeCenter;
		int nFriendRadiux, nFoeRadiux;

		for (Movable movFriend : CommandCenter.getInstance().getMovFriends()) {
			for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {

				pntFriendCenter = movFriend.getCenter();
				pntFoeCenter = movFoe.getCenter();
				nFriendRadiux = movFriend.getRadius();
				nFoeRadiux = movFoe.getRadius();

				//detect collision
				if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {

					//falcon
					if ((movFriend instanceof Falcon) ){
						if (!CommandCenter.getInstance().getFalcon().getProtected()){
							// Clear the mines if one is ran into
							if (movFoe instanceof Mine || movFoe instanceof MazeNuisance) {
								clearMaze();
							}
							CommandCenter.getInstance().getOpsList().enqueue(movFriend, CollisionOp.Operation.REMOVE);
							// If only one falcon on screen spawn a new one and decrement lives
							if (numFalcons == 1) {
								CommandCenter.getInstance().spawnFalcon(false);
							}
							// Other wise just decrease the number of falcons on the screen
							else {
								numFalcons-=1;
							}

						}
					}
					//not the falcon
					else {
						CommandCenter.getInstance().getOpsList().enqueue(movFriend, CollisionOp.Operation.REMOVE);
					}//end else
					//kill the foe and if asteroid, then spawn new asteroids
					if (movFoe instanceof DifficultAsteroid) {
						int hits = ((DifficultAsteroid) movFoe).getHits();
						((DifficultAsteroid) movFoe).setHits(hits-1);
						if (((DifficultAsteroid) movFoe).getHits() == 0) {
							killFoe(movFoe);
						}
						else if (((DifficultAsteroid) movFoe).getHits() == 2){
							((DifficultAsteroid) movFoe).setColor(Color.yellow);
						}
						else if  (((DifficultAsteroid) movFoe).getHits() == 1){
							((DifficultAsteroid) movFoe).setColor(Color.darkGray);
						}
					}
					else if (movFoe instanceof Asteroid) {
						killFoe(movFoe);
						// 50 points for killing an asteroid
						CommandCenter.getInstance().increaseScore(50);
					}
					Sound.playSound("kapow.wav");

				}//end if 
			}//end inner for
		}//end outer for


		//check for collisions between falcon and floaters
		if (CommandCenter.getInstance().getFalcon() != null){
			Point pntFalCenter = CommandCenter.getInstance().getFalcon().getCenter();
			int nFalRadiux = CommandCenter.getInstance().getFalcon().getRadius();
			Point pntFloaterCenter;
			int nFloaterRadiux;
			
			for (Movable movFloater : CommandCenter.getInstance().getMovFloaters()) {
				pntFloaterCenter = movFloater.getCenter();
				nFloaterRadiux = movFloater.getRadius();
	
				//detect collision
				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
					if (movFloater instanceof DoubleFalconFloater) {
						extraFalcon();
					}
					else if (movFloater instanceof ExtraShieldFloater) {
						shieldsAvailable+=1;
					}
					else {
						int nFalcons = CommandCenter.getInstance().getNumFalcons();
						CommandCenter.getInstance().setNumFalcons(nFalcons+1);
					}
					if (movFloater instanceof BonusLevelFloater) {
						CommandCenter.getInstance().increaseScore(1000 * CommandCenter.getInstance().getLevel());
						clearMaze();
					}
					CommandCenter.getInstance().getOpsList().enqueue(movFloater, CollisionOp.Operation.REMOVE);
					Sound.playSound("pacman_eatghost.wav");
	
				}//end if 
			}//end inner for
		}//end if not null



		//we are dequeuing the opsList and performing operations in serial to avoid mutating the movable arraylists while iterating them above
		while(!CommandCenter.getInstance().getOpsList().isEmpty()){
			CollisionOp cop =  CommandCenter.getInstance().getOpsList().dequeue();
			Movable mov = cop.getMovable();
			CollisionOp.Operation operation = cop.getOperation();

			switch (mov.getTeam()){
				case FOE:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovFoes().add(mov);
					} else {
						CommandCenter.getInstance().getMovFoes().remove(mov);
					}

					break;
				case FRIEND:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovFriends().add(mov);
					} else {
						CommandCenter.getInstance().getMovFriends().remove(mov);
					}
					break;

				case FLOATER:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovFloaters().add(mov);
					} else {
						CommandCenter.getInstance().getMovFloaters().remove(mov);
					}
					break;

				case DEBRIS:
					if (operation == CollisionOp.Operation.ADD){
						CommandCenter.getInstance().getMovDebris().add(mov);
					} else {
						CommandCenter.getInstance().getMovDebris().remove(mov);
					}
					break;


			}

		}
		//a request to the JVM is made every frame to garbage collect, however, the JVM will choose when and how to do this
		System.gc();
		
	}

	private void killFoe(Movable movFoe) {
		
		if (movFoe instanceof Asteroid){

			//we know this is an Asteroid, so we can cast without threat of ClassCastException
			Asteroid astExploded = (Asteroid)movFoe;
			//big asteroid 
			if(astExploded.getSize() == 0){
				//spawn two medium Asteroids
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);

			} 
			//medium size aseroid exploded
			else if(astExploded.getSize() == 1){
				//spawn three small Asteroids
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);

			}
			// Spawn debris
			spawnDebris(astExploded);

		} 

		//remove the original Foe
		CommandCenter.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);

	}

	private void extraFalcon() {
		// Spawn a new falcon and increment the number of falcons on the screen
		CommandCenter.getInstance().spawnFalcon(true);
		numFalcons+=1;
	}

	private void clearMaze() {
		for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {
			// Kill things that aren't asteroids
			if (!(movFoe instanceof Asteroid)){
				CommandCenter.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);
			}
		}
		// Also kill the floaters
		for (Movable movFloater : CommandCenter.getInstance().getMovFloaters()) {
			CommandCenter.getInstance().getOpsList().enqueue(movFloater, CollisionOp.Operation.REMOVE);
		}
	}

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}

	public int getTick() {
		return nTick;
	}

	// Spawn random things
	private void spawnRandom() {
		if (nTick % (SPAWN_NEW_SHIP_FLOATER - CommandCenter.getInstance().getLevel() * 7) == 0) {
			CommandCenter.getInstance().getOpsList().enqueue(new NewShipFloater(), CollisionOp.Operation.ADD);
		}
		if (nTick % (SPAWN_DOUBLE_FALCON_FLOATER - CommandCenter.getInstance().getLevel() * 7) == 0) {
			CommandCenter.getInstance().getOpsList().enqueue(new DoubleFalconFloater(), CollisionOp.Operation.ADD);
		}
		if (nTick % (SPAWN_EXTRA_SHIELD_FLOATER - CommandCenter.getInstance().getLevel() * 7) == 0) {
			CommandCenter.getInstance().getOpsList().enqueue(new ExtraShieldFloater(), CollisionOp.Operation.ADD);
		}
	}

	private void spawnDebris(Movable mov) {
		for (int i = 0; i < 5; i++) {
			Debris debris = new Debris();
			debris.setCenter(mov.getCenter());
			CommandCenter.getInstance().getOpsList().enqueue(debris, CollisionOp.Operation.ADD);
		}
	}

	// Called when user presses 's'
	private void startGame() {
		CommandCenter.getInstance().clearAll();
		CommandCenter.getInstance().initGame();
		CommandCenter.getInstance().setLevel(0);
		CommandCenter.getInstance().setPlaying(true);
		CommandCenter.getInstance().setPaused(false);
		if (!bMuted)
		   clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
	}

	//this method spawns new asteroids
	private void spawnAsteroids(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			int ran = Game.R.nextInt(10);
			// Sometimes make difficult asteroids
			if (ran+nNum > 10) {
				CommandCenter.getInstance().getOpsList().enqueue(new DifficultAsteroid(), CollisionOp.Operation.ADD);
			}
			// Otherwise normal
			else {
				CommandCenter.getInstance().getOpsList().enqueue(new Asteroid(0), CollisionOp.Operation.ADD);
			}

		}
	}

	
	private boolean isLevelClear(){
		//if there are no more Asteroids on the screen
		boolean bEnemyFree = true;
			for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {
				bEnemyFree = false;
				break;
			}
		return bEnemyFree;

	}
	
	private void checkNewLevel(){
		if (isLevelClear() ){
			if (CommandCenter.getInstance().getLevel() % 2 == 1) {
				inMaze = false;
				if (CommandCenter.getInstance().getFalcon() != null)
					CommandCenter.getInstance().getFalcon().setProtected(true);
				shieldsAvailable = 1;
				spawnAsteroids(CommandCenter.getInstance().getLevel() + 2);

			}
			else {
				clearMaze();
				inMaze=true;
				shieldsAvailable = 0;
				mazeTime = nTick;
				Maze maze = new Maze();
				maze.makeMaze();
			}
			CommandCenter.getInstance().setLevel(CommandCenter.getInstance().getLevel() + 1);
		}
	}

	

	// Varargs for stopping looping-music-clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
		int nKey = e.getKeyCode();

		if (nKey == START && !CommandCenter.getInstance().isPlaying())
			startGame();


		for (Movable movFriend : CommandCenter.getInstance().getMovFriends()) {
			if (movFriend instanceof Falcon) {
				Falcon fal = (Falcon) movFriend;
				if (fal != null) {

					switch (nKey) {
						case PAUSE:
							CommandCenter.getInstance().setPaused(!CommandCenter.getInstance().isPaused());
							if (CommandCenter.getInstance().isPaused())
								stopLoopingSounds(clpMusicBackground, clpThrust);
							else
								clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
							break;
						case QUIT:
							System.exit(0);
							break;
						case UP:
							fal.thrustOn();
							if (!CommandCenter.getInstance().isPaused())
								clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
							break;
						case LEFT:
							fal.rotateLeft();
							break;
						case RIGHT:
							fal.rotateRight();
							break;

						// possible future use
						// case KILL:
						case SHIELD:
							if (shieldsAvailable > 0) {
								fal.setProtected(true);
								shieldsAvailable=shieldsAvailable-1;
							}
							break;

						case HYPER:
							if (!inMaze) {
								int x = Game.R.nextInt(1100);
								int y = Game.R.nextInt(900);
								fal.setCenter(new Point(x, y));
							}

						default:
							break;
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (Movable movFriend : CommandCenter.getInstance().getMovFriends()) {
			if (movFriend instanceof Falcon) {
				Falcon fal = (Falcon) movFriend;

		int nKey = e.getKeyCode();
		 System.out.println(nKey);

		if (fal != null) {
			switch (nKey) {
				case FIRE:
					CommandCenter.getInstance().getOpsList().enqueue(new Bullet(fal), CollisionOp.Operation.ADD);
					Sound.playSound("laser.wav");
					break;

				//special is a special weapon fires the cruise missile.
				case SPECIAL:
					CommandCenter.getInstance().getOpsList().enqueue(new Cruise(fal), CollisionOp.Operation.ADD);
					Sound.playSound("laser.wav");
					break;

				case LEFT:
					fal.stopRotating();
					break;
				case RIGHT:
					fal.stopRotating();
					break;
				case UP:
					fal.thrustOff();
					clpThrust.stop();
					break;

				case MUTE:
					if (!bMuted) {
						stopLoopingSounds(clpMusicBackground);
						bMuted = !bMuted;
					} else {
						clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
						bMuted = !bMuted;
					}
					break;


				default:
					break;
			}
		}
		}
		}
	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {
	}

}


