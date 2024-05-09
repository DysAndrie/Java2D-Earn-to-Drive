import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Timer;
import game2D.*;

// Game overrides the GameCore class
// CSCU9N6 [202324_Spring] - 2D Game Development 

// Student ID: 3140298

@SuppressWarnings("serial")
public class Game extends GameCore {
	
    // Useful pre-existing game constants
    static int screenWidth = 512;
    static int screenHeight = 384;

    float gravity = 0.0002f; // Strength of gravity affecting game objects
    float moveSpeed = 0.2f; // Speed the player moves

    // Game state flags
    boolean flap = false;
    boolean moveLeft = false;
    boolean moveRight = false;
    boolean debug = true; // Flag indicating if debug mode is enabled
    boolean escapePressed = false; // Variable to track if the Escape key is pressed
    
    // Game resources
    Animation landing;
    Animation jump; 
    Animation left;
    Animation idle; 

    Sprite player = null; // Player sprite
    ArrayList<Sprite> planes = new ArrayList<Sprite>(); // List of plane sprites
    ArrayList<Sprite> worm = new ArrayList<Sprite>(); // List of worm sprites
    ArrayList<Sprite> buzzsaws = new ArrayList<Sprite>(); // List of buzzsaw sprites
    ArrayList<Tile> collidedTiles = new ArrayList<Tile>(); // List of tiles collided with

    TileMap tmap = new TileMap(); // Tile map for the game world
    
    long total; // Total game time counter 

    // Background images for parallax scrolling
    Image backImage;
    Image midImage;
    Image foreImage;
    
    int backImageX =0; // X-coordinate for background image
    int midImageX =0; // X-coordinate for middle layer image
    int foreImageX =0; // X-coordinate for foreground image
    
    Sound collisionSound; // Sound effect for collisions
    boolean collisionSoundPlaying = false; // Flag indicating if collision sound is currently playing
    
    // Countdown timer
    private int countdown = 100; // Initial countdown value
    private Timer timer; // Timer for countdown
    
    private boolean youLost = false; // Flag indicating if the player has lost the game
    private boolean youWon= false; // Flag indicating if the player has won the game
    
    private int lifeCount = 3; // Number of lives remaining  
    int collectedParts = 0; // Number of parts collected by the player
    
    // Animations and collectibles
    Animation coinAnim;
    Collect coin;
    Animation coinAnim2;
    Collect coin2;
    Animation coinAnim3;
    Collect coin3;
    Animation coinAnim4;
    Collect coin4;
    Animation coinAnim5;
    Collect coin5;
    Animation coinAnim6;
    Collect coin6;
    Animation coinAnim7;
    Collect coin7;
    Animation coinAnim8;
    Collect coin8;

    /**
   	 * The obligatory main method that creates
        * An instance of our class and starts it running
        * 
        * @param args	The list of parameters this program might use (ignored)
        */
    public static void main(String[] args) {
        Game gct = new Game();
        gct.init();
        gct.run(false, screenWidth, screenHeight);
	
    }
    
    /**
     * Initialisation of the class
     * 
     * This shows you the general principles but you should create specific
     * methods for setting up your game that can be called again when you wish to 
     * restart the game
     */
    public void init() {
        // Load the tile map
        tmap.loadMap("maps", "map.txt");
        
        //Game title to the window 
        setTitle("Earn to Drive | Java 2D Game");

        // Set fullscreen view
        setFullScreen();

        // Load the animation for the player's right movement
        landing = new Animation();
        landing.loadAnimationFromSheet("images/character/right.png", 4, 1, 100);

        // animation for the player's left movement
        left = new Animation();
        left.loadAnimationFromSheet("images/character/left.png", 4, 1, 100);

        // animation for the player's jumping action
        jump = new Animation();
        jump.loadAnimationFromSheet("images/character/jump.png", 6, 1, 150);

        //  for the player's idle state
        idle = new Animation();
        idle.loadAnimationFromSheet("images/character/idle.png", 4, 1, 600);

        player = new Sprite(landing);
        player.setPosition(200, screenHeight - 100); // Adjusted Y position to be near the bottom of the screen

        // Load planes animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/sprites/plane.png"), 1000);
        
        // Load enemy worm animation
        Animation ew = new Animation();
        ew.addFrame(loadImage("images/sprites/enemyworm.png"), 1000);

        // Create worm
        Sprite wormSprite = new Sprite(ew);
        wormSprite.setPosition(450, 570); // Set initial position on the ground
        wormSprite.setVelocityX(-0.02f); // Set initial horizontal velocity
        worm.add(wormSprite); // Add worm sprite to the list
        
        //buzzsaw animation 
        Animation bs = new Animation();
        bs.addFrame(loadImage("images/sprites/buzzsaw.png"), 1000);
        
        // Create buzzsaw sprite
        Sprite buzzsawSprite = new Sprite(bs);
        buzzsawSprite.setPosition(1500, 570); // Set initial position
        buzzsawSprite.setVelocityX(-0.02f); // Set initial horizontal velocity
        buzzsaws.add(buzzsawSprite); // Add buzzsaw sprite to the list

        // Load collision sound effect
        collisionSound = new Sound("sounds/collision.wav");
        
        // Load collectible animations and images
        coinAnim = new Animation();
        coinAnim.addFrame(loadImage("images/sprites/coin.png"), 100); 
        coinAnim2 = new Animation();
        coinAnim2.addFrame(loadImage("images/sprites/key.png"), 100); 
        coinAnim3 = new Animation();
        coinAnim3.addFrame(loadImage("images/sprites/silvergear.png"), 100); 
        coinAnim4 = new Animation();
        coinAnim4.addFrame(loadImage("images/sprites/toolbox.png"), 100);
        coinAnim5 = new Animation();
        coinAnim5.addFrame(loadImage("images/sprites/tools.png"), 100); 
        coinAnim6 = new Animation();
        coinAnim6.addFrame(loadImage("images/sprites/battery.png"), 100); 
        coinAnim7 = new Animation();
        coinAnim7.addFrame(loadImage("images/sprites/fuel.png"), 100); 
        coinAnim8 = new Animation();
        coinAnim8.addFrame(loadImage("images/sprites/safe.png"), 100); 

        // Create coin
        coin = new Collect(coinAnim);
        coin.setPosition(800, 370); //initial positions
        coin.show();
        
        coin2 = new Collect(coinAnim2);
        coin2.setPosition(100, 300); 
        coin2.show();

        coin3 = new Collect(coinAnim3);
        coin3.setPosition(1630, 190); 
        coin3.show(); 
        
        coin4 = new Collect(coinAnim4);
        coin4.setPosition(2060, 130); 
        coin4.show();
        
        coin5 = new Collect(coinAnim5);
        coin5.setPosition(1460, 430); 
        coin5.show();
        
        coin6 = new Collect(coinAnim6);
        coin6.setPosition(280, 170); 
        coin6.show();
        
        coin7 = new Collect(coinAnim7);
        coin7.setPosition(2260, 160); 
        coin7.show();
        
        coin8 = new Collect(coinAnim8);
        coin8.setPosition(2290, 400); 
        coin8.show();
        
        
        initialiseGame(); // Initialize the game
        
        // Create plane sprites
        Sprite p;
        for (int c = 0; c < 3; c++) {
            p = new Sprite(ca);
            p.setX(screenWidth + (int) (Math.random() * 400.0f));
            p.setY(30 + (int) (Math.random() * 430.0f));
            p.setVelocityX(-0.04f);
            p.show();
            planes.add(p);
        }
        
        // Create multiple worm sprites
        Sprite w; 
        for (int i = 0; i < 5; i++) {
            w = new Sprite(ew);
            w.setPosition(450 , 570); // Set initial position for each worm
            w.setVelocityX(-0.001f); // Set initial horizontal velocity
            worm.add(w); // Add worm sprite to the list
        }
        
        // Create multiple saw sprites
        Sprite b; 
        for (int i = 0; i < 5; i++) {
            b = new Sprite(bs);
            b.setPosition(1550 , 570); // Set initial position for each saw
            b.setVelocityX(-0.007f); // Set initial horizontal velocity
            buzzsaws.add(b); // Add saw sprite to the list
        }

        // Load background images
        backImage = loadImage("images/background/back.png");
        midImage = loadImage("images/background/mid.png");
        foreImage = loadImage("images/background/fore.png");
        
        lifeCount = 3; // Initialize life count to 3


     // Initialize the count down timer
        timer = new Timer(1000, e -> {
            countdown--;
            if (countdown == 0) {
                // Set a indicator to indicate that the game is lost
                youLost = true;
                
                playGameOverSound(); //plays Game over Sound 
            }
        });
        timer.start();
        
    }
    
    /** 
     * Sets the game to full screen mode.
     * Method checks if full screen mode is supported, then sets the window to full screen.
     * 
     * If full screen mode is not supported, it prints a message and exits the program.
     */
    private void setFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); // Get local graphics environment
        GraphicsDevice gd = ge.getDefaultScreenDevice(); // Get default screen device
        if (gd.isFullScreenSupported()) { // Check if full screen mode is supported
            gd.setFullScreenWindow(this); // Set full screen window to this JFrame
            validate(); // Validate the window
        } else {
            // Print message if full screen mode is not supported
            System.out.println("Fullscreen mode not supported");
            System.exit(0); // Exit the program
        }
    }

    /** 
     * Initializes the game.
     * Method initializes the total game time counter and sets the initial position and velocity of the player sprite.
     */
    public void initialiseGame() {
        total = 0; // Initialize total game time counter

        player.setPosition(210, 550); // Set initial position of the player sprite
        player.setVelocity(0, 0); // Set initial velocity of the player sprite
        player.show(); // Show the player sprite
    }

    /** 
     * Draws the game components on the screen.
     * Calculates the background offsets for parallax scrolling and draws the background layers with parallax effect.
     * Draws planes, worms, buzzsaws, tile map, health bar, player, and collectibles.
     * Displays score, countdown timer, and debug information.
     * 
     * @param g The Graphics2D object to draw the components
     */
    public void draw(Graphics2D g) {
        // Calculates the background offsets for parallax scrolling
        int xo = -(int) player.getX() + 200;
        int yo = 0;
        
        // Draw the background layers with parallax effect
        g.drawImage(backImage, 0 + xo / 4, 0, null); // Back layer moves slower than the foreground
        g.drawImage(midImage, 0 + xo / 2, 0, null); // Mid layer moves faster than the background
        g.drawImage(foreImage, 0 + xo, 0, null); // Foreground moves at the same speed as the player

        // Draw planes
        for (Sprite s : planes) {
            s.setOffsets(xo, yo);
            s.draw(g);
        }
        
        // Draw worms
        for (Sprite w : worm) {
            w.setOffsets(xo, yo);
            w.draw(g);
        }
              
        // Draw saws
        for (Sprite b : buzzsaws) {
            b.setOffsets(xo, yo);
            b.draw(g);
        }
        
        // Draw tile map
        tmap.draw(g, xo, yo);
        
        //Draw 3 Red circle healthbars 
        drawHealthBar(g); 

        // Draw player
        player.setOffsets(xo, yo);
        player.draw(g);
        
        //COLLECTIBLES
        coin.setOffsets(xo, yo);
        coin.draw(g);
        coin2.setOffsets(xo, yo);
        coin2.draw(g);
        coin3.setOffsets(xo, yo);
        coin3.draw(g);
        coin4.setOffsets(xo, yo);
        coin4.draw(g);
        coin5.setOffsets(xo, yo);
        coin5.draw(g);
        coin6.setOffsets(xo, yo);
        coin6.draw(g);
        coin7.setOffsets(xo, yo);
        coin7.draw(g);
        coin8.setOffsets(xo, yo);
        coin8.draw(g);
        
        Font scoreFont = new Font("Arial", Font.BOLD, 20);
        g.setFont(scoreFont);

        // Show score
        String msg = String.format("PARTS collected: %d / 7", collectedParts);
        g.setColor(Color.yellow);
        g.drawString(msg, screenWidth - 500, 100);
        
        // Draw the count down timer
        String countdownMsg = String.format("TIMER: %d", countdown);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(countdownMsg, screenWidth - 500,70);
        

        // Draw debug info
        if (debug) {
            tmap.drawBorder(g, xo, yo, Color.black);

            // Draw red bounding box for the player
            g.setColor(Color.red);
            player.drawBoundingCircle(g);

            drawCollidedTiles(g, tmap, xo, yo);
            
            
            // Draw the "YOU LOST" message if the game is lost
            if (youLost) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD,190));
                String lostMsg = "GAME OVER";
                g.fillRect(0, 0, 2000, 2000);
                g.setColor(Color.WHITE);
                g.drawString(lostMsg.toUpperCase(), 200, 400);
             
            }
            
            if (youWon) {
           
           // Display the VICTORY message when final collectible is collected 
           youLost= false; 
           g.setColor(Color.BLACK);
           g.setFont(new Font("Arial", Font.BOLD, 190));
           String victoryMsg = "YOU WON";
           g.fillRect(0, 0, 4000, 4000);
           g.setColor(Color.GREEN);
           g.drawString(victoryMsg.toUpperCase(), 300, 400);
           
            }            

        }
       
    }

    /** 
     * Draws rectangles around collided tiles on the screen.
     * 
     * @param g The Graphics2D object to draw the rectangles
     * @param map The TileMap containing the tiles
     * @param xOffset The horizontal offset for drawing
     * @param yOffset The vertical offset for drawing
     */
    public void drawCollidedTiles(Graphics2D g, TileMap map, int xOffset, int yOffset) {
        if (collidedTiles.size() > 0) {
            int tileWidth = map.getTileWidth();
            int tileHeight = map.getTileHeight();

            g.setColor(Color.blue);
            for (Tile t : collidedTiles) {
                g.drawRect(t.getXC() + xOffset, t.getYC() + yOffset, tileWidth, tileHeight);
            }
        }
    }
    
    /** 
     * Updates the position and movement of Worm and Buzzsaw sprites on the ground.
     * 
     * @param elapsed The elapsed time since the last update
     */
    public void updateWorm(long elapsed) {
        // Update worm position and movement
        for (Sprite w : worm) {
            w.update(elapsed);
            // Define boundaries for worm movement
            int minX = 80; // Minimum X position
            int maxX = 800; // Maximum X position
            
            // Make the worm move back and forth within the defined boundaries
            if (w.getX() <= minX || w.getX() + w.getWidth() >= maxX) {
            	w.setVelocityX(-0.0005f); // Adjust the velocity as needed
            }
        }
        
        // Update buzzsaw position and movement
        for (Sprite buzzsawSprite : buzzsaws) {
            buzzsawSprite.update(elapsed);         
            //Makes the buzzsaw move back and forth
            if (buzzsawSprite.getX() <= 1400 || buzzsawSprite.getX() + buzzsawSprite.getWidth() >= 1600) {
                buzzsawSprite.setVelocityX(-buzzsawSprite.getVelocityX()); // Reverse direction when reaching designated boundaries
            }
        }
    }

    /** 
     * Updates the game state based on user input and elapsed time.
     * 
     * @param elapsed The elapsed time since the last update
     */
    public void update(long elapsed) {
        // Check if the player is trying to move left or right
        if (moveLeft) {
            player.setVelocityX(-moveSpeed);
            player.setAnimation(left); // Set animation to left when moving left
        } else if (moveRight) {
            player.setVelocityX(moveSpeed);
            player.setAnimation(landing); // Set animation to right when moving right
        } else {
            // Set animation to idle when neither left nor right movement keys are pressed
            player.setAnimation(idle);
            player.setVelocityX(0); // Set velocity to zero when no movement keys are pressed
         // Check if the player is not moving left, right, or jumping
            if (!moveLeft && !moveRight && !flap) {
                player.setAnimation(idle); // Set the idle animation
            }
            // Check if the player is jumping
            else if (flap) {
                player.setAnimation(jump); // Set the jumping animation
            }
            // Check if the player is moving left
            else if (moveLeft) {
                player.setVelocityX(-moveSpeed);
                player.setAnimation(left); // Set animation to left when moving left
            }
            // Check if the player is moving right
            else if (moveRight) {
                player.setVelocityX(moveSpeed);
                player.setAnimation(landing); // Set animation to right when moving right
            }
            
            
         // Define a flag to track if a collision with a coin has already been detected
            boolean coinCollisionDetected = false;

            // In the update method:
            if (!coinCollisionDetected && boundingBoxCollision(player, coin) && coin.isVisible()) {
                // Collision detected with the coin
                // Perform actions for collecting the coin
            	collectedParts++; // Increase score by 1
                coin.hide(); // Hide the coin
                playCollectSound(); // Play the collect sound only if the coin is visible
                coinCollisionDetected = true; // Set the flag to true to indicate that a collision occurred
            }
            
            //Repeat its functionality for every sprite collectible
            if (!coinCollisionDetected && boundingBoxCollision(player, coin2) && coin2.isVisible()) {            
            	collectedParts++;
                coin2.hide(); 
                playCollectSound(); 
                coinCollisionDetected = true; 
            }
            
        
            if (!coinCollisionDetected && boundingBoxCollision(player, coin3) && coin3.isVisible()) {            
            	collectedParts++;
                coin3.hide();
                playCollectSound(); 
                coinCollisionDetected = true;
            }
            
        
            if (!coinCollisionDetected && boundingBoxCollision(player, coin4) && coin4.isVisible()) {           
            	collectedParts++; 
                coin4.hide(); 
                playCollectSound(); 
                coinCollisionDetected = true; 
            }
            
            // In the update method:
            if (!coinCollisionDetected && boundingBoxCollision(player, coin5) && coin5.isVisible()) {            
            	collectedParts++; 
                coin5.hide(); 
                playCollectSound(); 
                coinCollisionDetected = true; 
            }
            
         
            if (!coinCollisionDetected && boundingBoxCollision(player, coin6) && coin6.isVisible()) {            
            	collectedParts++; 
                coin6.hide(); 
                playCollectSound(); 
                coinCollisionDetected = true; 
            }
            
            // Same general function
            if (!coinCollisionDetected && boundingBoxCollision(player, coin7) && coin7.isVisible()) {           
            	collectedParts++; 
                coin7.hide();
                playCollectSound(); 
                coinCollisionDetected = true;
            }
                    
            // Final sprite collectible
            if (!coinCollisionDetected && boundingBoxCollision(player, coin8) && coin8.isVisible()) {
                // Perform actions for collecting the coin
            	collectedParts++; // Increase score by 1
           
            	 // Sets a flag to indicate that the game has been Won 
                youWon = true;
                playGameWonSound();
                coin8.hide(); // Hide the coin
                playCollectSound();              
                coinCollisionDetected = true; // Set the flag to true to indicate that a collision occurred
            }

            // Reset the coin collision flag once the collision is resolved
            coinCollisionDetected = false;
            
            updateWorm(elapsed);
            
         // Check for collision with the worm sprite
            for (Sprite w : worm) {
                if (boundingBoxCollision(player, w)) {
                    // Collision detected with the worm
                    // Perform actions for a worm collision
                    handleDamagingCollision(false);
                    break;
                }
            }
            
            // Check for collision with the buzzsaw sprite
            for (Sprite b : buzzsaws) {
                if (boundingBoxCollision(player, b)) {
                    // Collision detected with the buzzsaw
                    // Perform actions for a buzzsaw collision
                    handleDamagingCollision(false);
                    break; 
                }
            }
            

        }
        
        // For Movement            
        // Checks if the player is on the ground
        boolean onGround = false;
        float playerBottomY = player.getY() + player.getHeight();
        float groundLevel = tmap.getPixelHeight() - player.getHeight();

        if (playerBottomY >= groundLevel) {
            onGround = true;
        }

        // If the player is below the ground level, set its Y position to the ground level
        if (playerBottomY > groundLevel) {
            player.setY(groundLevel - player.getHeight());
            player.setVelocityY(0);
        }
        
        // Check if the player is trying to jump and is on the ground
        if (flap && onGround) {
            // Adjust jump height and landing speed
            player.setVelocityY(-0.15f); // Lower jump height
            player.setAnimation(jump); // Set animation to jump when jumping
        }

        // Check if the player is trying to jump and is NOT on the ground, or on a tile
        if ((flap && !onGround)) {
            // Adjust jump height and landing speed for when not on the ground
            player.setVelocityY(-0.1f);
            player.setAnimation(jump); 
        }

        // Apply gravity if the player is not on the ground
        if (!onGround) {
            player.setVelocityY(player.getVelocityY() + gravity * elapsed);
        }

        // Update the player's position
        player.update(elapsed);

        // Move planes
        for (Sprite s : planes) {
            // Update plane position
            s.update(elapsed);
            
            // Check for collision between player and worm sprites
            if (boundingBoxCollision(player, s)) {
                // Collision detected with a plane sprite
                // Perform actions for a plane collision
                handleDamagingCollision(false);
                break; 
            }

            // Reset planes position if it goes off the left edge of the screen
            if (s.getX() + s.getWidth() < 0) {
                s.setX(screenWidth);
                s.setY(30 + (int) (Math.random() * 150.0f));
            }
        }

        // Update worm position and movement
        for (Sprite w : worm) {
            w.update(elapsed);                     
            // Check for collision between player and worm sprites
            if (boundingBoxCollision(player, w)) {
                // Collision detected with a worm sprite
                // Perform actions for a worm collision
                handleDamagingCollision(false);
                break; 
            }
            // Make the worm move back and forth
            if (w.getX() <= 0 || w.getX() + w.getWidth() >= screenWidth) {
                w.setVelocityX(-w.getVelocityX()); // Reverse direction when reaching screen edges
            }
            
        }
        
        // Handle screen edge collision
        handleScreenEdge(player, tmap, elapsed);
        
        // Check for tile collision
        checkTileCollision(player, tmap);
        
    }
  

    /** 
     *A bounding box collision between sprites s1 and s2.
     * 
     * @return	true if a collision may have occurred, false if it has not.
     */
    public boolean boundingBoxCollision(Sprite s1, Sprite s2) {
        // Get the current position and size of the first sprite (s1)
        float x1 = s1.getX();
        float y1 = s1.getY();
        float width1 = s1.getWidth();
        float height1 = s1.getHeight();
        
        // Get the current position and size of the second sprite (s2)
        float x2 = s2.getX();
        float y2 = s2.getY();
        float width2 = s2.getWidth();
        float height2 = s2.getHeight();
        
        // Check for bounding box collision using the provided BoundBoxCollision method
        boolean collision = ((x1 + width1) >= x2) && (x1 <= (x2 + width2)) &&
                            ((y1 + height1) >= y2) && (y1 <= (y2 + height2));
        
        return collision;
    }
        
    
    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     */
    public void checkTileCollision(Sprite s, TileMap tmap) {
        // Empty out our current set of collided tiles
        collidedTiles.clear();

        // Take note of the sprite's current position
        float sx = s.getX();
        float sy = s.getY();

        // Find out how wide and how tall a tile is
        float tileWidth = tmap.getTileWidth();
        float tileHeight = tmap.getTileHeight();

        // Calculate the position of the sprite's bottom-left and bottom-right corners in tile coordinates
        int xtileBottomLeft = (int) (sx / tileWidth);
        int xtileBottomRight = (int) ((sx + s.getWidth()) / tileWidth);
        int ytileBottom = (int) ((sy + s.getHeight()) / tileHeight);

        // Calculate the position of the sprite's top-left and top-right corners in tile coordinates
        int xtileTopLeft = (int) (sx / tileWidth);
        int xtileTopRight = (int) ((sx + s.getWidth()) / tileWidth);
        int ytileTop = (int) (sy / tileHeight);

        boolean onGround = false; // Flag to track if the sprite is on the ground

        boolean lifeDeducted = false; // Flag to track if life count deduction has occurred


        // Check collision with tiles at the bottom-left and bottom-right corners of the sprite
        for (int x = xtileBottomLeft; x <= xtileBottomRight; x++) {
            Tile tile = tmap.getTile(x, ytileBottom);
            if (tile != null && tile.getCharacter() != '.') {
                if (tile.getCharacter() == 'p') {
                    // Change the map to "maplvl2" when stepping on tile 'p'
                    tmap.loadMap("maps", "maplvl2.txt");
                    // Play teleport sound
                    playTeleportSound();
                    // Reset the player's position to the initial position
                    initialiseGame();
                    // Break out of the loop since map is changed
                    break;
                } else if (tile.isDamaging() || tile.getCharacter() == 's' || tile.getCharacter() == 'k' || tile.getCharacter() == 't') {
                    if (!lifeDeducted) { // Check if life count deduction has already occurred
                        // Perform actions for a damaging tile collision
                        playDamageSound();
                        handleDamagingCollision(true); // Decrease life count
                        lifeDeducted = true; // Set flag to indicate that life count deduction has occurred
                    }
                    // Other actions for damaging tile collision
                } else {
                    // Handle collision with non-damaging tile

                    // Collision detected, add the collided tile to the list
                    collidedTiles.add(tile);
                    // Adjust the sprite's position to be just above the collided tile
                    float tileY = ytileBottom * tileHeight;
                    // Move the sprite just above the collided tile
                    s.setY(tileY - s.getHeight());
                    // Stop the sprite's vertical velocity
                    s.setVelocityY(0);
                    // Set the flag indicating the sprite is on the ground
                    onGround = true;
                    // Break out of the loop since we only need to handle one collision per frame
                    break;
                }
            }
        }

        // Check collision with tiles at the top-left and top-right corners of the sprite if jumping
        if (s.getVelocityY() < 0) {
            for (int x = xtileTopLeft; x <= xtileTopRight; x++) {
                Tile tile = tmap.getTile(x, ytileTop);
                if (tile != null && tile.getCharacter() != '.') {
                    if (tile.isDamaging() || tile.getCharacter() == 's' || tile.getCharacter() == 'k' || tile.getCharacter() == 't') {
                        if (!lifeDeducted) { // Check if life count deduction has already occurred
                            playDamageSound();
                            handleDamagingCollision(true); // Decrease life count
                            lifeDeducted = true; // Set flag to indicate that life count deduction has occurred
                        }
                        // Other actions for damaging tile collision
                    } else {
                        // Collision detected, add the collided tile to the list
                        collidedTiles.add(tile);
                        // Adjust the sprite's position to be just above the collided tile
                        float tileY = ytileBottom * tileHeight;
                        // Move the sprite just above the collided tile
                        s.setY(tileY - s.getHeight());
                        // Stop the sprite's vertical velocity
                        s.setVelocityY(0);
                        // Set the flag indicating the sprite is on the ground
                        onGround = true;
                        // Break out of the loop since we only need to handle one collision per frame
                        break;
                    }
                }
            }
        }

        // Update the onGround flag based on whether the sprite is on a tile or not
        onGround = onGround || (s.getVelocityY() == 0 && s.getY() + s.getHeight() >= tmap.getPixelHeight());
    }


    /** 
     * Handles actions when the player collides with a damaging tile.
     * 
     * Method handles actions such as reducing player health, playing sound effects, 
     * resetting player position, and checking for game over conditions.
     * 
     * @param isTile Indicates if the collision is with a damaging tile (true) or with other sprites (false)
     */
    public void handleDamagingCollision(boolean isTile) {
        if (isTile) {
            // Collision with a bad tile
            playDamageSound(); // Play damage sound effect
        } else {
            // Collision with a worm or plane or buzzsaw, or bad tile 
            playCollisionSound(); // Play collision sound effect
        }
        
        // Decrease life count accodingly 
        lifeCount--;
        
        // Reset the player's position to the starting position
        initialiseGame();
        
        // Check if all lives are lost after deducting one life
        if (lifeCount == 0) {
            // Set the flag to indicate that the game is lost
            youLost = true;
            // Play the Game Over sound
            playGameOverSound();
        }
    }

    /** 
     * Draws the health bar on the screen.
     * Draws red circles representing player health on the screen.
     * 
     * @param g The Graphics2D object to draw the health bar
     */
    public void drawHealthBar(Graphics2D g) {
        int lifeCircleX = 150; // X-coordinate for the first life circle
        int lifeCircleY = 50;  // Y-coordinate for the life circles
        int lifeCircleSize = 20; // Diameter of the life circles
        int lifeCircleSpacing = 10; // Spacing between life circles
        
        // Draw the health bar
        g.setColor(Color.RED);
        for (int i = 0; i < lifeCount; i++) {
            int x = lifeCircleX + i * (lifeCircleSize + lifeCircleSpacing);
            g.fillOval(x, lifeCircleY, lifeCircleSize, lifeCircleSize);
        }
    }
    
    /** 
     * Handles the player's interaction with the edges of the screen. 
     * Method ensures the player stays within the boundaries of the game world.
     * 
     * @param player The player sprite
     * @param tmap The TileMap representing the game world
     * @param elapsed The elapsed time since the last update
     */
    public void handleScreenEdge(Sprite player, TileMap tmap, long elapsed) {
        // Check if the player is beyond the left edge of the map
        if (player.getX() < 0) {
            player.setX(0); // Keep the player within the left edge of the map
        }
        
        // Calculate the maximum x-coordinate allowed for the player
        float maxX = tmap.getPixelWidth() - player.getWidth();
        
        // Check if the player is beyond the right edge of the map
        if (player.getX() > maxX) {
            player.setX(maxX); // Keep the player within the right edge of the map
        }

        // Calculate the difference between the player's bottom position and the map's height
        float difference = player.getY() + player.getHeight() - tmap.getPixelHeight();
        
        // Check if the player is beyond the bottom edge of the map
        if (difference > 0) {
            // Adjust the player's Y position to stay within the bottom edge of the map
            player.setY(player.getY() - difference);
        }
    }

    /** 
     * Handles key presses from the user.
     * 
     * Method responds to specific key presses and updates game state accordingly.
     * 
     * @param e The KeyEvent object representing the key press event
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_UP: // Allow jumping with the Arrow Up key
            case KeyEvent.VK_SPACE: // Allow jumping with the Space key
            case KeyEvent.VK_W: // Allow jumping with the W key
                if (!flap) {
                    flap = true;
                    player.setAnimation(jump);
                    Sound up = new Sound("sounds/jump.wav"); // Jump Sound 
                    up.start();
                }
                break;
            case KeyEvent.VK_LEFT: // Movement to the left with Left Arrow key
            case KeyEvent.VK_A: // Movement to the left with A key
                moveLeft = true;
                player.setAnimation(left);
                break;
            case KeyEvent.VK_RIGHT: // Movement to the right with Right Arrow key
            case KeyEvent.VK_D: // Movement to the right with D key
                moveRight = true;
                player.setAnimation(landing);
                break;
            case KeyEvent.VK_Q: // Control over background theme with a MIDI Track.
                Sound q = new Sound("sounds/bgmusic.mid");
                q.start();
                break;
            case KeyEvent.VK_ESCAPE: //ESCAPE key means exiting the game
                if (!escapePressed) {
                    escapePressed = true;
                    stop(); //closes
                }
                break;
            case KeyEvent.VK_B:  // Toggle debug mode when B key is pressed
                debug = !debug;
                break;
            default:
                break;
        }
    }

    /** 
     * Handles key releases from the user.
     * 
     * Method responds to specific key releases and updates game state accordingly.
     * 
     * @param e The KeyEvent object representing the key release event
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_ESCAPE:
                escapePressed = false;
                break;
            case KeyEvent.VK_UP:// Release Arrow Up key for jumping
            case KeyEvent.VK_SPACE: // Release Space key for jumping
            case KeyEvent.VK_W: // Release W key for jumping
                flap = false;
                break;
            case KeyEvent.VK_LEFT: // Release Left Arrow key for left movement
            case KeyEvent.VK_A: // Release A key for left movement
                moveLeft = false;
                player.setAnimation(landing);
                break;
            case KeyEvent.VK_RIGHT: // Release Right arrow key for right movement
            case KeyEvent.VK_D: // Release D key for right movement
                moveRight = false;
                player.setAnimation(landing);
                break;
            default:
                break;
        }
    }
    
    // Sound effect Methods 

    /** 
     * Plays the teleport sound effect.
     * Attempts to play the teleport sound effect stored in the "teleport.wav" file.
     * 
     * If an error occurs during playback, the exception is printed.
     */
    private void playTeleportSound() {
        try {
            File soundFile = new File("sounds/teleport.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** 
     * Plays the collision sound effect.
     * Attempts to play the collision sound effect stored in the "collision.wav" file.
     * 
     * If an error occurs during playback, the exception is printed.
     */
    private void playCollisionSound() {
        try {
            File soundFile = new File("sounds/collision.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** 
     * Plays the damage sound effect.
     * Attempts to play the damage sound effect stored in the "damage.wav" file.
     * 
     * If an error occurs during playback, the exception is printed.
     */
    private void playDamageSound() {
        try {
            File soundFile = new File("sounds/damage.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** 
     * Plays the game over sound effect.
     * Attempts to play the game over sound effect stored in the "lose.wav" file.
     * 
     * If an error occurs during playback, the exception is printed.
     */
    private void playGameOverSound() {
        try {
            File soundFile = new File("sounds/lose.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** 
     * Plays the collect sound effect.
     * Attempts to play the collect sound effect stored in the "coin.wav" file.
     * 
     * If an error occurs during playback, the exception is printed.
     */
    private void playCollectSound() {
        try {
            File soundFile = new File("sounds/coin.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** 
     * Plays the game won sound effect.
     * Attempts to play the game won sound effect stored in the "victory.wav" file.
     * 
     * If an error occurs during playback, the exception is printed.
     */
    private void playGameWonSound() {
        try {
            File soundFile = new File("sounds/victory.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

  
} // End of Java Class 

// Last Modified on: 4/18/2024
// 3140298


