/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board
 *  Should not be used directly (use getWidth()/getHeight() instead).
 *  * */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
	private static int life = NTURNS;

	//game speed
	private int DELAY;
	
	public static void main(String[] args) {
		new Breakout().start(args);
	}

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		
		//set up the stage
		setup();
		//add score label
		score = 0;
		GLabel score_board = new GLabel("Score: " + score);
		score_board.setLocation(30, getHeight() - PADDLE_Y_OFFSET/2);
		add(score_board);
		//display starter message
		starter();
		//track the paddle with mouse motion
		addMouseListeners();
		//ball appears
		ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
		//initialize the ball status
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5))	vx=-vx;
		vy = 3.0;
		//ball starts moving
		DELAY = 20;
		paddle_bounce_count = 0;
		while (!gameOver())
		{
			ball.move(vx, vy);
			checkBounce();
			checkCollision();
			//refresh score_board
			score_board.setLabel("Score: "+score);
			pause(DELAY);
		}

	}
	
	//setup the stage for the game
	private void setup()	{
		brickNumber = 0;
		int x = (getWidth() - BRICK_WIDTH * NBRICKS_PER_ROW - BRICK_SEP * (NBRICKS_PER_ROW - 1)) / 2;
		int y = BRICK_Y_OFFSET;
		//draw the bricks
		for (int i = 0;i < NBRICK_ROWS;i++)
		{
			//define rainbow color
			Color color;
			if (i < 2)
			{
				color = Color.RED;
			}
			else if (i < 4)
			{
				color = Color.orange;
			}
			else if (i < 6)
			{
				color = Color.yellow;
			}
			else if (i < 8)
			{
				color = Color.green;
			}
			else
			{
				color = Color.cyan;
			}
			
			for (int j = 0; j < NBRICKS_PER_ROW; j++)
			{
				GRect brick= new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setColor(color);
				add(brick);
				brickNumber++;
				//update x coordinate
				x +=  BRICK_WIDTH + BRICK_SEP;
			}
			//a new line
			x = (getWidth() - BRICK_WIDTH * NBRICKS_PER_ROW - BRICK_SEP * (NBRICKS_PER_ROW - 1)) / 2;
			y += BRICK_HEIGHT + BRICK_SEP;
		}
		//draw the paddle
		paddle = new GRect((getWidth() - PADDLE_WIDTH) / 2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		//add life label
		GLabel life_tag = new GLabel("Life: "+life);
		life_tag.setLocation(getWidth() - 60, getHeight() - PADDLE_Y_OFFSET/2);
		add(life_tag);
		
	}
	
	//display starter message
	private void starter()
	{
		GLabel start = new GLabel("Click to start!");
		start.setFont("Serif-34");
		start.setColor(Color.MAGENTA);
		double start_x = (getWidth() - start.getWidth())/2;
		double start_y = (getHeight() - start.getAscent())/2;
		start.setLocation(start_x, start_y);
		add(start);
		waitForClick();
		remove(start);
	}
	
	//mouse motion tracker, keep the paddle attached to mouse 
	public void mouseMoved(MouseEvent e)
	{
		double X = e.getX();
		//check edge
		if (X < PADDLE_WIDTH/2)	{
			X = PADDLE_WIDTH/2;
		}	else if (X > getWidth() - PADDLE_WIDTH/2)	{
			X = getWidth() - PADDLE_WIDTH/2;
		}
		paddle.setLocation(X - PADDLE_WIDTH/2, paddle.getY());
	
	}
	
	//check ball bounce around the world
	private void checkBounce()
	{
		double ball_x = ball.getX();
		double ball_y = ball.getY();
		//check left and right wall bounce
		if (ball_x < 0 || ball_x > getWidth() - 2 * BALL_RADIUS)
		{
			vx = -vx;
		}
		//check up wall bounce
		if (ball_y < 0)
		{
			vy = -vy;
		}
		
	}
	
	//check collision for paddle or bricks
	private void checkCollision()
	{
		double x = ball.getX();
		double y = ball.getY();
		//sound extension
		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
		//check four corners of the ball to see collision
		obj = getElementAt(x,y);
		if (obj == null)
		{
			obj = getElementAt(x + 2* BALL_RADIUS, y);
		}
		if (obj == null)
		{
			obj = getElementAt(x, y + 2* BALL_RADIUS);
		}
		if (obj == null)
		{
			obj = getElementAt(x + 2* BALL_RADIUS, y + 2* BALL_RADIUS);
		}
		//check collision for paddle or bricks
		if (obj != null)
		{
			if (obj == paddle)  //ball collide with paddle, bounce back
			{
				//println("vx: "+vx+" vy: "+vy+" ballx "+ball.getX()+" bally "+ball.getY());
				if (x <= obj.getX() - BALL_RADIUS || x >= obj.getX() + PADDLE_WIDTH - BALL_RADIUS) //ball hit the corner of paddle
				{
					vx = -vx;
				}
			/*
			 * This code prevent ball glued onto the paddle. The reason of ball glueing to the
			 * paddle is that ball gets down too far. When the first time collision to the paddle,
			 * the ball moves up vy, but the ball is still in collision with paddle, so the ball
			 * bounce again and moves down, then it gets stuck at paddle.
			 * This implement neglects the ball when it drops further to the surface of the paddle+vy,
			 * so next bouncing will definetly get away from paddle.	
			 */
				if (y >= getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT-2*BALL_RADIUS 
					&& y < getHeight()-PADDLE_Y_OFFSET - PADDLE_HEIGHT-2*BALL_RADIUS + vy)
				vy = -vy;
				//increase the speed when bounce 7 times on paddle
				paddle_bounce_count++;
				if (paddle_bounce_count % 7 == 0)
					DELAY = (DELAY > 1)? DELAY - 1 : DELAY;
			}
			else	//ball collide with bricks, remove bricks and reduce brick count
			{
				//check whether ball is below paddle, because of existence of additional labels below paddle
				if (y < getHeight() - PADDLE_Y_OFFSET- PADDLE_HEIGHT-2*BALL_RADIUS + vy) {
					vy = -vy;
					/*
					 * check brick color and assign relative point to score
					 * cyan == 10
					 * green == 20
					 * yellow == 30
					 * orange == 40
					 * red == 50
					 */
					Color c = obj.getColor();
					if (c == Color.cyan)
						score += 10;
					else if (c == Color.GREEN) 
						score += 20;
					else if (c == Color.YELLOW)
						score += 30;
					else if (c == Color.ORANGE)
						score += 40;
					else
						score += 50;
					
					remove(obj);
					bounceClip.play();
					brickNumber--;
				}
			}
		}
	}
	
	//check whether game over
	private boolean gameOver()
	{
		double y = ball.getY();
		//ball touch bottom wall
		if (y > getHeight() - 2 * BALL_RADIUS)	{

			lost_msg();
			return true;
		}
		//no bricks left
		if (brickNumber == 0)
		{

			win_msg();
			return true;
		}
			
		return false;
	}
	
	//display lost message
	private void lost_msg()
	{
		life--;
		removeAll();
		if (life > 0) {
		GLabel msg = new GLabel("Click to restart.");
		msg.setFont("Serif-34");
		msg.setColor(Color.MAGENTA);
		double msg_x = (getWidth() - msg.getWidth())/2;
		double msg_y = (getHeight() - msg.getAscent())/2;
		msg.setLocation(msg_x, msg_y);
		add(msg);
		waitForClick();
		remove(msg);
		run();
		}
		else {
			GLabel msg = new GLabel("Sorry, you lost.");
			msg.setFont("Serif-34");
			msg.setColor(Color.MAGENTA);
			double msg_x = (getWidth() - msg.getWidth())/2;
			double msg_y = (getHeight() - msg.getAscent())/2;
			msg.setLocation(msg_x, msg_y);
			GLabel restart =new GLabel("Click to restart.");
			restart.setFont("Serif-34");
			restart.setColor(Color.MAGENTA);
			add(restart, msg_x, msg_y+msg.getHeight());
			add(msg);
			waitForClick();
			life = NTURNS;
			remove(msg);
			remove(restart);
			this.start();
		}
	}
	
	//display win message
	private void win_msg()
	{
		removeAll();
		GLabel msg = new GLabel("Congrats. You won!"); 
		msg.setFont("Serif-34");
		msg.setColor(Color.MAGENTA);
		double msg_x = (getWidth() - msg.getWidth())/2;
		double msg_y = (getHeight() - msg.getAscent())/2;
		msg.setLocation(msg_x, msg_y);
		add(msg);
	}
	
	/* private instant variables */
	private int brickNumber;
	private GRect paddle;
	private GOval ball;
	private double vx, vy;  //velocity of the ball in x,y direction
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private GObject obj;
	private int score;
	private int paddle_bounce_count;
}
