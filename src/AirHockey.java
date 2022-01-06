import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class AirHockey extends PApplet {

    float dT;
    ArrayList<Ball> balls;

    Ball paddle1; // left paddle
    Ball paddle2; // right paddle
    Ball ball; // ball

    PVector vel1; // paddle 1 velocity
    PVector vel2; // paddle 2 velocity
    PVector acc; // paddle accelerations

    int leftScore;
    int rightScore;

    boolean gameActive; // whether game is being played
    boolean gamePaused; // whether game is paused or not

    public static void main(String[] args) {
        PApplet.main(new String[]{AirHockey.class.getName()});
    }

    public void settings() {
        size(3000, 1875);
    }

    public void setup() {

        dT = 0.1F;
        vel1 = new PVector(100, 100); // velocity of left paddle
        vel2 = new PVector(100, 100); // velocity of right paddle
        acc = new PVector(10, 10); // both paddles have the same acceleration

        balls = new ArrayList();

        paddle1 = new Ball(width / 4, height / 2, 75, 30, vel1);
        paddle2 = new Ball(2500, height / 2, 75, 30, vel2);
        ball = new Ball(random(50, 2895), random(515, 1495), 30, 12, new PVector(random(-dT * 500, dT * 500), random(-dT * 500, dT * 500))); // makes a Ball with random position on the board and velocity

        // add paddles and balls to list of balls
        balls.add(paddle1);
        balls.add(paddle2);
        balls.add(ball);

        gameActive = false; // game is not currently being played
        gamePaused = false; // game is not paused
    }

    public void draw() {
        background(0);

        // if game is not being played / hasn't started, gives instructions
        if (gameActive == false) {
            textSize(100);
            fill(255);
            text("Player 1:", 5, 150);
            text("W (up)", 5, 300);
            text("S (down)", 5, 450);
            text("A (left)", 5, 600);
            text("D (right)", 5, 750);
            text("Player 2:", 2200, 150);
            text("UP (up)", 2200, 300);
            text("DOWN (down)", 2200, 450);
            text("LEFT (left)", 2200, 600);
            text("RIGHT (right)", 2200, 750);
            text("Press spacebar to reset the game once it has started", 300, 1000);
            text("Press tab to pause at any time", 650, 1200);
            text("Click on the board to add a ball there", 500, 1400);
            text("Press enter to begin", 1000, 1600);
        }
        if (gameActive == true) {
            for (int i = 515; i < height - 360; i += 30) {
                fill(255);
                ellipse(width / 2, i, 5, 5); // draws center partition
            }

            // draws lines that make up the board
            stroke(255);
            noFill();
            strokeWeight(5);
            line(50, 512, 2925, 512);
            line(50, 1512, 2925, 1512);
            line(50, 512, 50, 900);
            line(50, 1160, 50, 1512);
            line(2925, 512, 2925, 900);
            line(2925, 1160, 2925, 1512);

            for (Ball i : balls) {
                fill(255);
                ellipse(i.position.x, i.position.y, i.radius, i.radius); // draws each Ball
                if (i.radius != 75) { // if ball is not a paddle (which have radii of 75), change velocity and position
                    i.position.add(PVector.mult(i.velocity, dT)); // adds velocity * time to position
                    i.velocity.add(PVector.mult(i.acceleration, dT)); // adds acceleration * time to velocity

                    if (i.position.x <= 60 || i.position.x >= 2925) { // if ball hits walls on left and right, bounce back
                        if (i.position.y < 900 || i.position.y > 1160) {
                            i.velocity.x *= -.85;
                            if (i.position.x < 60) { // puts ball back on the board
                                i.position.x = 60;
                            }
                            if (i.position.x > 2925) { // puts ball back on the board
                                i.position.x = 2925;
                            }
                        }
                    }

                    if (i.position.y < 515 || i.position.y > 1495) { // if ball hits walls on top and bottom, bounce back
                        i.velocity.y *= -.85;
                    }

                    if (i.position.y < 515) { // puts ball back on the board
                        i.position.y = 515;
                    }

                    if (i.position.y > 1495) { // puts ball back on the board
                        i.position.y = 1495;
                    }

                    if (i.position.x <= 50) { // if ball is scored on left side, increase right side's score, set ball scored to middle of board with random velocity toward left side
                        if (i.position.y >= 900 && i.position.y <= 1160) {
                            rightScore += 1;
                            i.position.set(width / 2, height / 2);
                            i.velocity.set(random(-5, 0), random(-5, 5));
                        }
                    }

                    if (i.position.x >= 2925) { // if ball is scored on right side, increase left side's score, set ball scored to middle of board with random velocity toward right side
                        if (i.position.y >= 900 && i.position.y <= 1160) {
                            leftScore += 1;
                            i.position.set(width / 2, height / 2);
                            i.velocity.set(random(0, 5), random(-5, 5));
                        }
                    }
                }

                for (Ball j : balls) { // check for collisions between balls
                    if (j.position.dist(i.position) < j.radius + i.radius) {
                        i.collision(j);
                    }
                }
            }

            // if balls accidentally tunnels through the board, it isn't visible above or below the board
            fill(0);
            noStroke();
            rect(0, 1514, width, height);
            rect(0, 0, width, 510);
            rect(0, 0, 48, height);
            rect(2930, 0, width, height);

            // displays scores
            fill(255);
            textSize(100);
            text(leftScore, width / 4, 200);
            text(rightScore, width - width / 4, 200);

            // keeps left paddle on left side
            if (paddle1.position.x >= 1402) {
                paddle1.position.x = 1402;
            }

            if (paddle1.position.y <= 550) {
                paddle1.position.y = 550;
            }

            if (paddle1.position.x <= 90) {
                paddle1.position.x = 90;
            }

            if (paddle1.position.y >= 1475) {
                paddle1.position.y = 1475;
            }

            // keeps right paddle on right side
            if (paddle2.position.x <= 1575) {
                paddle1.position.x = 1575;
            }

            if (paddle2.position.y <= 550) {
                paddle2.position.y = 550;
            }

            if (paddle2.position.x >= 2881) {
                paddle2.position.x = 2881;
            }

            if (paddle2.position.y >= 1475) {
                paddle2.position.y = 1475;
            }

            // ends game when one side hits 10
            if (leftScore == 10 || rightScore == 10) {
                fill(0);
                rect(0, 0, width, height);
                fill(255);
                textSize(100);
                text("GAME OVER", width / 2 - 200, height / 2 - 50);
                if (leftScore == 10) {
                    text("Left Wins", width / 2 - 200, height / 2 + 200);
                    text("Press space to play again", width / 2 - 200, height / 2 + 500);
                    noLoop();
                }
                if (rightScore == 10) {
                    text("Right Wins", width / 2 - 200, height / 2 + 200);
                    text("Press space to play again", width / 2 - 200, height / 2 + 500);
                    noLoop();
                }
            }
        }

    }

    public void keyPressed() { // can only move paddles when game isn't paused, otherwise pressing the keys would change the paddles when unpausing
        switch (key) {
            case 'w': // moves left paddle up
                if (gamePaused == false) {
                    paddleUp1();
                    break;
                }
                break;
            case 's': // moves left paddle down
                if (gamePaused == false) {
                    paddleDown1();
                    break;
                }
                break;
            case 'a': // moves left paddle left
                if (gamePaused == false) {
                    paddleLeft1();
                    break;
                }
                break;
            case 'd': // moves left paddle right
                if (gamePaused == false) {
                    paddleRight1();
                    break;
                }
                break;
            case ' ': // resets game
                reset();
                break;
            case ENTER: // starts game
                gameActive = true;
                break;
            case TAB: // pauses/unpauses game
                if (gameActive == true) {
                    if (gamePaused == false) {
                        gamePaused = true; // pauses game if game is active and not paused
                        fill(255);
                        textSize(100);
                        text("PAUSED", 1350, 1750);
                        noLoop();
                        break;
                    } else {
                        gamePaused = false; // unpauses game if game is active and paused
                        loop();
                        break;
                    }
                } else {
                    break;
                }

            default:
                break;
        }
        if (key == CODED) {
            switch (keyCode) {
                case UP: // moves right paddle up
                    if (gamePaused == false) {
                        paddleUp2();
                        break;
                    }
                    break;
                case DOWN: // moves right paddle down
                    if (gamePaused == false) {
                        paddleDown2();
                        break;
                    }
                    break;
                case RIGHT: // moves right paddle right
                    if (gamePaused == false) {
                        paddleRight2();
                        break;
                    }
                    break;
                case LEFT: // moves right paddle left
                    if (gamePaused == false) {
                        paddleLeft2();
                        break;
                    }
                    break;
            }
        }
    }

    public void keyReleased() { // resets velocities of the paddles
        vel1.set(100, 100);
        vel2.set(100, 100);
    }

    void paddleUp1() { // if paddle1 isn't at the top of the board, add acceleration to velocity, add velocity to position
        if (paddle1.position.y >= 550) {
            paddle1.velocity.y += acc.y * dT;
            paddle1.position.y -= paddle1.velocity.y * dT;
            if (paddle1.position.y < 550) { // puts paddle back on board
                paddle1.position.y = 550;
            }
        }
    }

    void paddleRight1() { // if paddle1 isn't at the right of its side, add acceleration to velocity, add velocity to position
        if (paddle1.position.x <= 1402) {
            paddle1.velocity.x += acc.x * dT;
            paddle1.position.x += paddle1.velocity.x * dT;
            if (paddle1.position.x > 1402) { // puts paddle back on board
                paddle1.position.x = 1402;
            }
        }
    }

    void paddleLeft1() { // if paddle1 isn't at the left of its side, add acceleration to velocity, add velocity to position
        if (paddle1.position.x >= 90) {
            paddle1.velocity.x += acc.x * dT;
            paddle1.position.x -= paddle1.velocity.x * dT;
            if (paddle1.position.x < 90) { // puts paddle back on board
                paddle1.position.x = 90;
            }
        }
    }

    void paddleDown1() { // if paddle1 isn't at the bottom of the board, add acceleration to velocity, add velocity to position
        if (paddle1.position.y <= 1475) {
            paddle1.velocity.y += acc.y * dT;
            paddle1.position.y += paddle1.velocity.y * dT;
            if (paddle1.position.y > 1475) { // puts paddle back on board
                paddle1.position.y = 1475;
            }
        }
    }

    void paddleUp2() { // if paddle2 isn't at the top of the board, add acceleration to velocity, add velocity to position
        if (paddle2.position.y >= 550) {
            paddle2.velocity.y += acc.y * dT;
            paddle2.position.y -= paddle2.velocity.y * dT;
            if (paddle2.position.y < 550) { // puts paddle back on board
                paddle2.position.y = 550;
            }
        }
    }

    void paddleRight2() { // if paddle2 isn't at the right of its side, add acceleration to velocity, add velocity to position
        if (paddle2.position.x <= 2881) {
            paddle2.velocity.x += acc.x * dT;
            paddle2.position.x += paddle2.velocity.x * dT;
            if (paddle2.position.x > 2881) { // puts paddle back on board
                paddle2.position.x = 2881;
            }
        }
    }

    void paddleLeft2() { // if paddle1 isn't at the left of its side, add acceleration to velocity, add velocity to position
        if (paddle2.position.x >= 1600) {
            paddle2.velocity.x += acc.x * dT;
            paddle2.position.x -= paddle2.velocity.x * dT;
            if (paddle2.position.x < 1600) { // puts paddle back on board
                paddle2.position.x = 1600;
            }
        }
    }

    void paddleDown2() { // if paddle2 isn't at the bottom of the board, add acceleration to velocity, add velocity to position
        if (paddle2.position.y <= 1475) {
            paddle2.velocity.y += acc.y * dT;
            paddle2.position.y += paddle2.velocity.y * dT;
            if (paddle2.position.y > 1475) { // puts paddle back on board
                paddle2.position.y = 1475;
            }
        }
    }

    public void mouseClicked() { // adds balls when mouse is clicked where the mouse is if the mouse is on the board
        if (gameActive == true && gamePaused == false) {
            if (mouseY >= 512 && mouseY <= 1512) {
                balls.add(new Ball(mouseX, mouseY, 30, 12, new PVector(random(-dT * 500, dT * 500), random(-dT * 500, dT * 500))));
            }
        }
    }

    public void reset() { // resets game to its starting configuration
        balls.clear();
        gameActive = false;
        paddle1.position.set(width / 2, height / 2);
        paddle2.position.set(2500, height / 2);
        ball.position.set(random(50, 2895), random(515, 1495));
        ball.velocity.set(random(-dT * 500, dT * 500), random(-dT * 500, dT * 500));
        balls.add(paddle1);
        balls.add(paddle2);
        balls.add(ball);
        leftScore = 0;
        rightScore = 0;
        loop();
    }
}