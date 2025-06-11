I've created a complete Ping Pong game in Java! Here's what the game includes:
Game Features
Core Gameplay:

Two paddles (Player 1 on left, Player 2 on right)
Ball that bounces around with realistic physics
Score tracking (first to 5 points wins)
Ball angle changes based on where it hits the paddle

Controls:

Player 1: W (up) and S (down) keys
Player 2: UP and DOWN arrow keys
Restart: SPACE bar (when game is over)

Visual Elements:

Clean black background with white paddles and ball
Dashed center line
Score display at the top
Game over screen with winner announcement
On-screen control instructions

How to Run

Save the code as PingPongGame.java
Compile: javac PingPongGame.java
Run: java PingPongGame

Game Mechanics

The ball starts in a random direction each round
Ball speed and paddle speed are balanced for good gameplay
Ball bounces off top/bottom walls
When ball hits a paddle, it bounces back with angle based on impact point
Game ends when either player reaches 5 points
Press SPACE to restart after game over

The game runs at approximately 60 FPS for smooth gameplay. Both players can move simultaneously, making it perfect for local multiplayer fun!
