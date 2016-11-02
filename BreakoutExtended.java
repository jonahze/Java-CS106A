/*
 * File: Breakout.java
 * -------------------
 * Name:Jonah Zamora Epstein
 * Section Leader: Elana Leone
 * 
 * This file runs the game of breakout by first creating the bricks and then 
 * making a paddle which can be controlled by the mouse. Then it runs a while 
 * loop which takes into account the number of turns and in that loop it has 
 * the ball. The game is over when the player runs out of turns or runs out of
 * bricks.
 * 
 * This is file is extended. It provides a different playing experience by having a
 * 50/50 chance of making the game have a strobe like effect whenever it hits a 
 * brick. It also gives the player more control of the game by changing the x 
 * direction of the ball if the player hits it with the edge of his paddle and 
 * changing the velocity of the x direction whenever it hits a corner. It adds
 * sounds to the games when different actions occur. It speeds the game up gradually
 * as the player gets rid of more bricks. It has a function where to pause the game
 * all one must do is press a key then just click to un-pause it. There is now a score
 * board on the bottom right hand corner of the screen which keeps track of the last
 * brick you hit and its score value, as well as the total score. Also, on the bottom
 * left hand corner of the screen it shows the user the number of lives they have left
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutExtended extends GraphicsProgram {

	/* Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/* Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/* Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/* Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/* Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/* Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/* Separation between bricks */
	private static final int BRICK_SEP = 1;

	/* Width of a brick */
	private static final int BRICK_WIDTH =
			(WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/* Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/* Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/* Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/* Number of turns */
	private static final int NTURNS = 3;

	/* Number of turns left*/
	private int turnsLeft = NTURNS;

	/*Paddle instance variable*/
	private GRoundRect paddle;

	/*Ball instance variable*/
	private GOval ball;

	/*random generator instance variable*/
	private RandomGenerator rgen = RandomGenerator.getInstance();

	/*instance variables for x and y components of velocity*/
	private double vx = 0;
	private double vy = 9.0;

	/*minimum components of x velocity*/
	private static final double MIN_X_VELOCITY = 1.0;
	private static final double MAX_X_VELOCITY = 3.0;

	/*gives the pause time at the beginning of the game*/
	private static int START_PAUSET = 30;

	/*gives the amount of time the program pauses between each movement of
	 * the ball */
	private double pauseTime = START_PAUSET;

	/*instance variable for the total number of bricks*/
	private static final double TOTAL_BRICKS = NBRICKS_PER_ROW * NBRICK_ROWS;

	/*gives the number of bricks left to tell if you win the game*/
	private double bricksLeft = TOTAL_BRICKS;

	/*instance variable for the background*/
	private GRect background;

	/*instance boolean for pausing the game*/
	private boolean isPaused = false;

	/*instance GLabel for the number of lives left*/
	private GLabel livesLeft;

	/*static instance variable for the score*/
	private static final int SCORE_START = 0;

	/*changeable instance variable for the score*/
	private int score = SCORE_START;

	/*instance variable for the GLabel which displays the score*/
	private GLabel scoreBoard;

	/*static instance variable for the score*/
	private static final String NO_BRICK = " ";

	/*instance variable for the last brick hit*/
	private String lastBrick = NO_BRICK;

	/*time elapsed initial value*/
	private int TIME_ELAP_START = 0;
	
	/*time elapsed instance variable*/
	private int timeElapsed = TIME_ELAP_START;
	
	/* Runs the Breakout program. */
	public void run() {
		initialize();
		while (turnsLeft > 0 && bricksLeft > 0) {
			addLivesLabel(turnsLeft);
			addScoreBoard(lastBrick, score);
			createBall();
			waitForClick();
			gamePlay();
		}
		if (bricksLeft != 0) {
			removeAll();
			centerLabel("LOSER");
			AudioClip loserClip = MediaTools.loadAudioClip("crowd boo 1-soundbible.com-183064743.au");
			loserClip.play();
			GLabel click = centerLabel("click to play again");
			click.move(0, click.getAscent());
			addScore();

		} else { 
			pause(2000);
			removeAll();
			AudioClip winnerClip = MediaTools.loadAudioClip("applause light-soundbible.com-176488688.au");
			winnerClip.play();
			addWinGIF();
			endGame();
		}
		waitForClick();
		removeAll();
		run();
	}

	/*Method: initialize*/
	/*
	 * this method initializes the game and its starting conditions
	 */
	private void initialize() {
		warningLabel();
		bricksLeft = TOTAL_BRICKS;
		turnsLeft = NTURNS;
		score = SCORE_START;
		lastBrick = NO_BRICK;
		pauseTime = START_PAUSET;
		timeElapsed = TIME_ELAP_START;
		background = new GRect(0, 0, getWidth(), getHeight());
		background.setFilled(true);
		add(background);
		createBricks();
		createPaddle();
		addMouseListeners();
		addKeyListeners();
	}

	/*Method: createBricks*/
	/*
	 * creates the starting breaks by using two for loops and changing the
	 * each iteration
	 */
	private void createBricks() {
		for(int rows = 0; rows < NBRICK_ROWS; rows++) {
			double xStart = (getWidth() - (((NBRICKS_PER_ROW-1) * BRICK_SEP) + (NBRICKS_PER_ROW * BRICK_WIDTH))) / 2;
			double yStart = BRICK_Y_OFFSET;
			for(int brickInRow = 0; brickInRow < NBRICKS_PER_ROW; brickInRow++) {
				double x = xStart + brickInRow * (BRICK_WIDTH + BRICK_SEP);
				double y = yStart + rows * (BRICK_HEIGHT + BRICK_SEP);
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(getColor(rows));	
				add(brick);
			}
		}
	}

	/*Method: getColor()*/
	/*
	 * this method takes any number less than 100 and then divides it by 10 to 
	 * the remainder and then uses a switch to return a color based on the 
	 * remainder. In other words, it gives the pattern of the brick colors. 
	 */
	private Color getColor(int rowNumber) {
		switch (rowNumber % 10) {
		case 0:
		case 1:
			return Color.RED;	
		case 2:
		case 3:
			return Color.ORANGE;	
		case 4:
		case 5:
			return Color.YELLOW;	
		case 6:
		case 7:
			return Color.GREEN;	
		case 8:
		case 9:
			return Color.CYAN;	
		default:
			return null;
		}
	}

	/*Method: createPaddle*/
	/*Creates a paddle with a filled GRect and then tracks it 
	 * using mouse listeners
	 */
	private void createPaddle() {
		double x = (getWidth() - PADDLE_WIDTH)/2;
		double y = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
		paddle = new GRoundRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.WHITE);
		paddle.setFilled(true);
		add(paddle);
	}

	/*Method: mouseMoved(MouseEvent e)*/
	/*
	 *this method tells the program what it should do every time the mouse moves.
	 *For this game of Breakout it uses the mouse's x coordinate to move the 
	 *paddle on the bottom of the screen. It also doesn't allow to paddle to 
	 *move off screen if the mouse's position would normally cause it to. Also
	 *pauses the mouse movement if the game is paused
	 */
	public void mouseMoved(MouseEvent e) {
		if (!isPaused) {
			double y = getHeight() - PADDLE_HEIGHT - PADDLE_Y_OFFSET;
			paddle.setLocation(e.getX() - PADDLE_WIDTH/2, y);
			if (e.getX() - PADDLE_WIDTH/2 < 0)
				paddle.setLocation(0, y);
			if (e.getX() + PADDLE_WIDTH/2 > getWidth())
				paddle.setLocation(getWidth() - PADDLE_WIDTH, y);
		}
	}

	/*Method: createBall()*/
	/*
	 * this method creates the ball that will be used in the game
	 */
	private void createBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval(x, y, BALL_RADIUS*2, BALL_RADIUS*2);
		if (background.getColor() == Color.BLACK) ball.setColor(Color.WHITE);
		else ball.setColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
	}

	/*Method: gamePlay()*/
	/*
	 * This method is the overall gameplay.It runs with a while loop which breaks 
	 * whenever the game runs out of bricks or the ball hits the bottom wall 
	 */
	private void gamePlay() {
		vx = rgen.nextDouble(MIN_X_VELOCITY, MAX_X_VELOCITY);
		if (rgen.nextBoolean()) vx = -vx;
		boolean colorSwitchOn = false;
		AudioClip paddleWallClip = MediaTools.loadAudioClip("blop-mark_diangelo-79054334.au");
		while(true) {
			ball.move(vx, vy);
			double xBall = ball.getX();
			double yBall = ball.getY();
			checkForWalls(xBall, yBall, paddleWallClip);
			if (yBall + 2 * BALL_RADIUS >= getHeight()) {
				remove(ball);
				AudioClip gameLost = MediaTools.loadAudioClip("power failure-soundbible.com-1821346166.au");
				gameLost.play();
				pause(2000); //waits to start new game for audio file to finish
				score -= 100;
				remove(scoreBoard);
				break;
			}
			GObject collider = getColliderAndAct(xBall, yBall, paddleWallClip);
			if (collider != paddle && collider != null && collider != background) {
				AudioClip brickClip = MediaTools.loadAudioClip("realistic_punch-mark_diangelo-1609462330.au");
				brickClip.play();
				remove(collider);
				bricksLeft--;
				pauseTime -= .2; //speeds up the game for every brick hit
				colorSwitchOn = rgen.nextBoolean();
				AudioClip raveClip = MediaTools.loadAudioClip("41722__m-red__happy-freaq.wav");
				if(colorSwitchOn && timeElapsed == 0) raveClip.play(); //prevents the song from playing on itself most of the time
				lastBrick = getBrickColor(collider);
				int brickValue = getBrickValue(collider);
				score += brickValue;
				remove(scoreBoard);
				addScoreBoard(lastBrick, score);
			}
			if (bricksLeft == 0) break;
			if (colorSwitchOn) switchColor();
			pause(pauseTime);
			if (colorSwitchOn) timeElapsed += pauseTime;
			else timeElapsed = 0;
			checkIfPause();
		}
		turnsLeft--;
		remove(livesLeft);
	}

	/*Method: checkForWalls*/
	/*
	 * this method checks to see if the balls hits the walls of the of the 
	 * screen and then changes the x or y coordinate as necessary. It also slows the
	 * x down in order to compensate for the increase in x velocity from hitting the
	 * corner of the paddle
	 */
	private void checkForWalls(double x, double y, AudioClip wallClip) {
		if (x <= 0 && vx < 0 ) {
			vx = -(vx + .4);
			wallClip.play();
		}
		if (x + 2 * BALL_RADIUS >= getWidth()) {
			vx = -Math.abs(vx -.4);
			wallClip.play();
		}
		if (y <= 0) {
			vy = - vy;
			wallClip.play();
		}
	}

	/*Method: getCollider*/
	/*
	 * this method checks to see if the ball is colliding with an object by 
	 * using get element at specific points around the ball. It then changes 
	 * the velocity based one where the ball is hit and returns the object so 
	 * it can be removed if it is a brick. It also checks to see if the ball hits
	 * the middle of the paddle or if it hits a corner. If it hits the corner of the
	 * paddle on the side the ball is coming from, it then reverses the x direction 
	 * as well and increases the x velocity. If it hits the corner of the paddle 
	 * opposite the side it is coming from it just increases the x velocity.
	 * This method also checks to see if the ball is hitting the brick on its side
	 * instead of its top, and if this is true it reverses the x direction instead 
	 * of the y. 
	 */
	private GObject getColliderAndAct(double x, double y, AudioClip paddleClip) {
		if (checkIfPaddle(x, y + 2 * BALL_RADIUS) && checkIfPaddle(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS)){
			vy = - Math.abs(vy);
			paddleClip.play();
			return paddle;
		} else if (checkIfPaddle(x, y + 2 * BALL_RADIUS) && checkIfPaddle(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS) == false){
			vy = - Math.abs(vy);
			if (vx < 0) vx = -(vx - 3);
			else vx += 3;
			paddleClip.play();
			return paddle;
		} else if (checkIfPaddle(x, y + 2 * BALL_RADIUS)  == false && checkIfPaddle(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS)){
			vy = - Math.abs(vy);
			if (vx > 0) vx = -(vx + 3);
			else vx -= 3;
			paddleClip.play();
			return paddle;
		}
		if (checkIfBrick(x, y) && checkIfBrick(x, y + 2 * BALL_RADIUS)) {
			vx = - vx;
			return getElementAt(x, y);
		} else if (checkIfBrick(x + 2 * BALL_RADIUS, y) && checkIfBrick(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS)) {
			vx = - Math.abs(vx);
			return getElementAt(x + 2 * BALL_RADIUS, y);
		} else if (checkIfBrick(x, y)){
			vy = - (vy);
			return getElementAt(x, y);
		} else if (checkIfBrick(x + 2 * BALL_RADIUS, y)){
			vy = - (vy);
			return getElementAt(x + 2 * BALL_RADIUS, y);
		} else if (checkIfBrick(x, y + 2 * BALL_RADIUS)) {
			vy = - Math.abs(vy);
			return getElementAt(x, y + 2 * BALL_RADIUS);
		} else if (checkIfBrick(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS)) {
			vy = - Math.abs(vy);
			return getElementAt(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS);
		}
		else return null;
	}

	/*Method: checkIfPaddle*/
	/*
	 * checks if the object the ball is colliding with is a paddle and if it is
	 * returns a true boolean
	 */
	private boolean checkIfPaddle(double x, double y) {
		return getElementAt(x, y) == paddle;
	}

	/*Method: checkIfBrick*/
	/*
	 * checks if the object the ball is colliding with is a brick and if it is
	 * returns a true boolean
	 */
	private boolean checkIfBrick(double x, double y) {
		return getElementAt(x, y) != null && getElementAt(x, y) !=paddle && getElementAt(x, y) != background && getElementAt(x, y) != livesLeft && getElementAt(x, y) != scoreBoard;
	}

	/*Method: centerLabel*/
	/*
	 * this method displays a message to the user and centers the message in the middle 
	 * of the screen. 
	 */ 
	private GLabel centerLabel(String message) {
		GLabel label = new GLabel (message, 0, 0);
		double dy = (getHeight() - label.getAscent())/2;
		double dx = (getWidth() - label.getWidth())/2;
		label.move(dx, dy);
		add(label);
		return label;
	}

	/*Method: switchColor*/
	/*
	 * this method switches the background, ball, and paddle color so that they can be 
	 * opposite of each other. 
	 */
	private void switchColor(){
		Color backColor = Color.WHITE;
		Color ballPaddleColor = Color.BLACK;
		if (background.getColor() == backColor){
			backColor = Color.BLACK;
			ballPaddleColor = Color.WHITE;
		}		
		background.setColor(backColor);
		ball.setColor(ballPaddleColor);
		paddle.setColor(ballPaddleColor);
		livesLeft.setColor(ballPaddleColor);
		scoreBoard.setColor(ballPaddleColor);
	}

	/*Method: warningLabel*/
	/*
	 * this method adds a label before the game starts warning people who may be
	 * susceptible to seizures.
	 */
	private void warningLabel() {
		GLabel topWarning = centerLabel("Only play if not susceptible to seizures");
		add(topWarning);
		GLabel pauseInstruction = centerLabel("Hit any key to pause at anytime");
		pauseInstruction.move(0, topWarning.getAscent());
		add(pauseInstruction);
		GLabel clickInstruction = centerLabel("click to continue now and when paused");
		clickInstruction.move(0, topWarning.getAscent() + pauseInstruction.getAscent());
		add(clickInstruction);
		waitForClick();
		remove(topWarning);
		remove(pauseInstruction);
		remove(clickInstruction);
	}

	/*Method: keyTyped*/
	/*
	 * this method changes the pause boolean to true whenever a key is pressed
	 */
	public void keyTyped(KeyEvent e) {
		isPaused = true;
	}

	/*Method: checkIfPause*/
	/*
	 * This method checks is the isPaused boolean is set to true and if it is it 
	 * pauses the game until the user clicks the mouse and then it resets the 
	 * isPaused variable
	 */
	private void checkIfPause(){
		if (isPaused) {
			waitForClick();
			isPaused = false;
		}
	}

	/*Method: addLivesLabel*/
	/*
	 * this method adds a GLabel to the bottom left hand corner of the screen which
	 * shows the user how many lives they have left
	 */
	private void addLivesLabel(int lives) {
		lives -= 1;
		livesLeft = new GLabel("Lives left: " + lives);
		livesLeft.setFont("SansSerif-bold-15");
		livesLeft.move(10, getHeight() - 3);
		if (background.getColor() == Color.BLACK) livesLeft.setColor(Color.WHITE);
		else livesLeft.setColor(Color.BLACK);
		add(livesLeft);
	}

	/*Method: addScoreBoard*/
	/*
	 * this method adds a GLabel to the bottom right hand corner of the screen which
	 * shows the user their current score
	 */
	private void addScoreBoard(String lastbrick, int score) {
		scoreBoard = new GLabel(lastbrick + score);
		scoreBoard.setFont("SansSerif-bold-18");
		scoreBoard.move(getWidth() -scoreBoard.getWidth() - 10, getHeight() - 3);
		if (background.getColor() == Color.BLACK) scoreBoard.setColor(Color.WHITE);
		else scoreBoard.setColor(Color.BLACK);
		add(scoreBoard);
	}

	/*Method: getBrickColor*/
	/* this method gets the color of the brick the ball is colliding with and 
	 * returns it as a string
	 */
	private String getBrickColor(GObject brick) {
		Color color = brick.getColor();
		if (color == Color.CYAN) return "CYAN (+1) ";
		if (color == Color.GREEN) return "GREEN (+5) ";
		if (color == Color.YELLOW) return "YELLOW (+10) ";
		if (color == Color.ORANGE) return "ORANGE (+15) ";
		if (color == Color.RED) return "RED (+20) ";
		else return null;
	}

	/*Method: getBrickValue*/
	/*
	 * this method gets the color of the brick and returns the value of the brick
	 */
	private int getBrickValue(GObject brick) {
		Color color = brick.getColor();
		if (color == Color.CYAN) return 1;
		if (color == Color.GREEN) return 5;
		if (color == Color.YELLOW) return 10;
		if (color == Color.ORANGE) return 15;
		if (color == Color.RED) return 20;
		else return 0;
	}

	/*Method: addScore*/
	/*
	 * this method adds the score at the end of the game
	 */
	private void addScore(){
		GLabel scoreCard = new GLabel("FINAL SCORE: "+score);
		scoreCard.setFont("SansSerif-bold-15");
		scoreCard.move((getWidth() - scoreCard.getWidth()) / 2, getHeight() - 2 * scoreCard.getAscent());
		add(scoreCard);
	}

	/*Method: endGame*/
	/*
	 * this method adds an end of game message
	 */
	private void endGame() {
		addScore();
		GLabel winner = centerLabel("WINNER");
		winner.move(0, winner.getAscent() * 4);
		GLabel clickToPlay = centerLabel("Click to play again");
		clickToPlay.move(0, winner.getAscent() * 5);		
	}
	
	/*Method: addWinGIF*/
	/*
	 * this method adds a gif to the win screen
	 */
	private void addWinGIF() {
		GImage winGIF = new GImage("WINNER.gif");
		add(winGIF, (getWidth() - winGIF.getWidth()) / 2, (getHeight() - winGIF.getWidth()) / 2);
	}
}