Hello and thanks for playing my game!

I mostly just made a base asteroids game and extended it with some modifications.

See below for a list of modifications to the game:

MAZE LEVELS
Every other level in the game is a maze level instead of an asteroids level. There is a green floating goal and red mines
that act as walls. Reaching the goal results in an extra life and a score boost. Running into any wall subtracts a life
and ends the maze level. There is also a nuisance enemy that follows the player during the maze and will also end the
maze level if it catches the falcon. The maze has a time limit as well. There are four maze layouts which are randomly
chosen from for each maze level.

SHIELDS
During asteroids levels you are allowed one shield per level that makes you invincible for a short period of time. There
is also a new floater that when collected gives you another shield. The number of shields available is shown at the top
of the screen. Shield is activated with the a button

HYPERSPACE
Pressing d during asteroids level allows for a hyperspace jump which puts you in another random location on the screen.

NEWSHIPFLOATER
The NewShipFloater is a floater that will randomly spawn in asteroids levels which when collected will give the player
an extra life. The number of lives is now shown at the top of the screen to be easier to see.

DOUBLEFALCONFLOATER
The DoubleFalconFloater is a floater that will randomly spawn in asteroids levels which allows for multiple falcons on
the screen at a time. The game class holds a value numFalcons which tracks the number of falcons on the screen at a time
and will only spawn new ones when there is only one on the screen. Falcons that die when multiple are on the screen do
not subtract lives.

DIFFICULTASTEROID
Some asteroids have a chance at being difficult asteroids which take three hits to destroy and change colors instead of
the normal gray asteroids that take one hit to destroy.

MINES
Mines are the sprites used for the walls in the maze sections. There is also a build mine line function of the maze class
which allows for building lines of mines of a certain length and at a certain location.

DEBRIS
Debris has been implemented for when asteroids explode.

SCORING
Scoring has been added for defeating asteroids and for completing maze levels and the score is shown at the top of the
screen.

FALCON
I slightly tweaked the design and speed/rotation of the falcon to make it a bit easier to control.

Thanks again for playing! -Aaron

