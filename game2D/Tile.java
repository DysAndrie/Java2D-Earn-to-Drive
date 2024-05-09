package game2D;

import java.awt.Rectangle;

// Modified by: 3140298

public class Tile {

    private char character; // The character associated with this tile
    private int xc; // The tile's x coordinate in pixels
    private int yc; // The tile's y coordinate in pixels
    private int width; // The width of the tile
    private int height; // The height of the tile

    /**
     * Create an instance of a tile
     * 
     * @param c The character associated with this tile
     * @param x The x tile coordinate in pixels
     * @param y The y tile coordinate in pixels
     * @param width The width of the tile
     * @param height The height of the tile
     */
    public Tile(char c, int x, int y, int width, int height) {
        character = c;
        xc = x;
        yc = y;
        this.width = width;
        this.height = height;
    }

    /**
     * @return The character for this tile
     */
    public char getCharacter() {
        return character;
    }

    /**
     * @param character The character to set the tile to
     */
    public void setCharacter(char character) {
        this.character = character;
    }

    /**
     * @return The x coordinate (in pixels)
     */
    public int getXC() {
        return xc;
    }

    /**
     * @return The y coordinate (in pixels)
     */
    public int getYC() {
        return yc;
    }

    /**
     * Checks if this tile intersects with a rectangle.
     * 
     * @param rect The rectangle to check for intersection with.
     * @return true if this tile intersects with the rectangle, false otherwise.
     */
    public boolean intersects(Rectangle rect) {
        Rectangle tileRect = new Rectangle(xc, yc, width, height);
        return tileRect.intersects(rect);
    }

    /**
     * @return The x coordinate (in pixels)
     */
    public float getX() {
        return xc;
    }

    /**
     * @return The width of the tile
     */
    public float getWidth() {
        return width;
    }

    /**
     * @return The y coordinate (in pixels)
     */
    public float getY() {
        return yc;
    }

    /**
     * @return The height of the tile
     */
    public float getHeight() {
        return height;
    }

    /**
     * Checks if the sprite is damaging.
     * 
     * @return true if the sprite is damaging, false otherwise.
     */
    public boolean isDamaging() {
        // TODO Auto-generated method stub
        return false;
    }
    
} // End of Java Class 

//Last Modified on: 4/18/2024
//3140298

