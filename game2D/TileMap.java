package game2D;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

// Modified by: 3140298
public class TileMap 
{

	private Tile [][] tmap;		// The tile map grid, initially null
	private int mapWidth=0;		// The maps width in tiles
	private int mapHeight=0;	// The maps height in tiles
	private int tileWidth=0;	// The width of a tile in pixels
	private int tileHeight=0;	// The height of a tile in pixels
	
	 // Define the characters for harmful tiles
    private static final char[] harmfulTiles = {'s', 'k', 't'};
	
	// imagemap contains a set of character to image mappings for
	// quick loop up of the image associated with a given character.
	private Map<String,Image> imagemap = new HashMap<String,Image>();
	
	/**
	 * @return The map height in tiles
	 */
	public int getMapHeight() {
		return mapHeight;
	}

	/**
	 * @return The map width in tiles
	 */
	public int getMapWidth() {
		return mapWidth;
	}
	
	/**
	 * @return The height of a tile in pixels.
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * @return The width of a tile in pixels.
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * @return The map height in pixels
	 */
	public int getPixelHeight() {
		return mapHeight * tileHeight;
	}

	/**
	 * @return The map width in pixels
	 */
	public int getPixelWidth() {
		return mapWidth * tileWidth;
	}
	
	/**
	 * Loads a 'mapfile' that is contained in the given 'folder'. It is expected that
	 * the images associated with the map will also be in 'folder'.
	 *  
	 * @param folder The folder the tile map and images are located in
	 * @param mapfile The name of the map file in the map folder
	 * @return true if the map loaded successfully, false otherwise
	 */
	public boolean loadMap(String folder, String mapfile)
	{
		// Create a full path to the tile map by sticking the folder and mapfile together
		String path = folder + "/" + mapfile;
		int row=0;
		
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line="";
			String trimmed="";
			String [] vals;
			
			// First we need to clear out the old image map
			imagemap.clear();
			
			// Read the first line of the tile map to find out
			// the relevant dimensions of the map plus the tiles
			line = in.readLine();
			vals = line.split(" ");
			// Check that we read 4 values
			if (vals.length != 4)
			{
				System.err.println("Incorrect number of parameters in the TileMap header:" + vals.length);
				in.close();
				return false;
			}
			
			// Read in the map dimensions
			mapWidth = Integer.parseInt(vals[0]);
			mapHeight = Integer.parseInt(vals[1]);
			tileWidth = Integer.parseInt(vals[2]);
			tileHeight = Integer.parseInt(vals[3]);
			
			// Now look for the character assignments
			while ((line = in.readLine()) != null)
			{
				trimmed = line.trim();
				// Skip the current line if it's a comment
				if (trimmed.startsWith("//")) continue;
				// Break out of the loop if we find the map
				if (trimmed.startsWith("#map")) break;
				
				if (trimmed.charAt(0) == '#') // Look for a character to image map
				{
					// Extract the character
					
					String ch = "" + trimmed.charAt(1);
					// and it's file name
					String fileName = trimmed.substring(3,trimmed.length());
					
					Image img  = new ImageIcon(folder + "/" + fileName).getImage();
					// Now add this character->image mapping to the map
					if (img != null)
						imagemap.put(ch,img);
					else
						System.err.println("Failed to load image '" + folder + "/" + fileName + "'");
				}
			}
			
			// Check the map dimensione are at least > 0
			if ((mapWidth > 0) && (mapHeight > 0))
			{
				tmap = new Tile[mapWidth][mapHeight]; 
			}
			else
			{
				System.err.println("Incorrect image map dimensions.");
				trimmed = "";
			}
			
			// Now read in the tile map structure
			if (trimmed.startsWith("#map"))
			{
				row=0;
				while ((line = in.readLine()) != null)
				{
					if (line.trim().startsWith("//")) continue;
				
					if (line.length() != mapWidth)
					{
						System.err.println("Incorrect line length in map");
						System.err.println(row + " : " + line);
						continue;
					}
					
					for (int col=0; col<mapWidth && col<line.length(); col++)
						tmap[col][row] = new Tile(line.charAt(col),col*tileWidth,row*tileHeight, col, col);
					row++;
					
					if (row >= mapHeight) break;
				}
			}
			
			in.close();
			
		}
		catch (Exception e)
		{
			System.err.println("Failed to read in tile map '" + path + "':" + e);
			return false;
		}
		
		if (row != mapHeight)
		{
			System.err.println("Map failed to load. Incorrect rows in map");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Generate the tile map as a String so we can inspect its current state
	 */
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		for (int r=0; r<mapHeight; r++)
		{
			for (int c=0; c<mapWidth; c++)
				s.append(tmap[c][r].getCharacter());
	
			s.append('\n');
		}
		return s.toString();
	}
	
	/**
	 * Get the Image object associated with the tile at position 'x','y'
	 * 
	 * @param x	The x tile coordinate (in tiles, not pixels)
	 * @param y The y tile coordinate (in tiles, not pixels)
	 * @return The Image object associated with the tile at position 'x,y', null if blank or not found
	 */
	public Image getTileImage(int x, int y)
	{
		if (!valid(x,y)) return null;
		Tile t = tmap[x][y];
		if (t == null) return null;
		char ch = t.getCharacter();
		if (ch == '.') return null; // Blank space
		return imagemap.get(ch + "");
	}
	
	/**
	 * Get the top left pixel x coordinate of a tile at position 'x,y' in the tile map
	 *  
	 * @param x The x tile coordinate (in tiles, not pixels)
	 * @param y The y tile coordinate (in tiles, not pixels)
	 * @return The top left pixel x coordinate of a tile at position 'x,y' in the tile map
	 */
	public int getTileXC(int x, int y)
	{
		if (!valid(x,y)) return 0;
		return tmap[x][y].getXC();
	}
	
	/**
	 * Get the top left pixel y coordinate of a tile at position 'x,y' in the tile map
	 *  
	 * @param x The x tile coordinate (in tiles, not pixels)
	 * @param y The y tile coordinate (in tiles, not pixels)
	 * @return The top left pixel y coordinate of a tile at position 'x,y' in the tile map
	 */	
	public int getTileYC(int x, int y)
	{
		if (!valid(x,y)) return 0;
		return tmap[x][y].getYC();
	}
	
	/**
	 * 
	 * @param x The x tile coordinate (in tiles, not pixels)
	 * @param y The y tile coordinate (in tiles, not pixels)
	 * @return true if tile coordinate 'x,y' is a valid position in the tile map
	 */
	public boolean valid(int x, int y)
	{
		return (x >= 0 && y >= 0 && x<mapWidth && y<mapHeight);
	}
	
	/**
	 * Sets the tile character at position 'x,y' to the value of 'ch'.
	 * 
	 * @param ch The character to set the tile to.
	 * @param x The x tile coordinate (in tiles, not pixels)
	 * @param y The y tile coordinate (in tiles, not pixels)
	 * @return true if the character was correctly set
	 */
	public boolean setTileChar(char ch, int x, int y)
	{
		if (!valid(x,y)) return false;
		tmap[x][y].setCharacter(ch);
		return true;
	}
	
	/**
	 * Gets the tile character at position 'x,y'
	 * 
	 * @param x The x tile coordinate (in tiles, not pixels)
	 * @param y The y tile coordinate (in tiles, not pixels)
	 * @return The character the tile is currently set to.
	 */
	public char getTileChar(int x, int y)
	{
		if (!valid(x,y)) return '?';
		return tmap[x][y].getCharacter();
	}

	/**
	 * Gets the tile object at position 'x,y'
	 * 
	 * @param x The x tile coordinate (in tiles, not pixels)
	 * @param y The y tile coordinate (in tiles, not pixels)
	 * @return The tile object at position 'x,y'.
	 */
	public Tile getTile(int x, int y)
	{
		if (!valid(x,y)) return null;
		return tmap[x][y];
	}
	
	
	
	/**
	 * Draws the tile map to the graphics device pointed to by 'g'.
	 * 
	 * @param g The graphics device to draw to
	 * @param xoff The xoffset to shift the tile map by
	 * @param yoff The yoffset to shift the tile map by
	 */
	public void draw(Graphics2D g, int xoff, int yoff)
	{
		if (g == null) return;
	
		Image img=null;
		Rectangle rect = (Rectangle)g.getClip();
		int xc,yc;
		
		for (int r=0; r<mapHeight; r++)
		{
			for (int c=0; c<mapWidth; c++)
			{
				img = getTileImage(c, r);
				if (img == null) continue;
				xc = xoff + c*tileWidth;
				yc = yoff + r*tileHeight;
				
				// Only draw the tile if it is on screen, otherwise go back round the loop
				if (xc+tileWidth < 0 || xc >= rect.x + rect.width) continue;
				if (yc+tileHeight < 0 || yc >= rect.y + rect.height) continue;
				g.drawImage(img,xc,yc,null);
			}
		}		
	}
	
	/**
	 * Draw a border around the tile map using the given colour and offsets.
	 * This may be useful for debugging purposes.
	 * 
	 * @param g The graphics device to draw to
	 * @param xoff The xoffset to shift the tile map by
	 * @param yoff The yoffset to shift the tile map by
	 * @param colour The colour to draw the border with
	 */
	public void drawBorder(Graphics2D g, int xoff, int yoff, Color colour)
	{
		if (g == null) return;
			
		g.setColor(colour);
		g.drawRect(xoff, yoff, getPixelWidth(), getPixelHeight());		
	}

	public ArrayList<Tile> getTiles(Sprite player) {
	    ArrayList<Tile> collidedTiles = new ArrayList<>();

	    // Calculate player's grid position
	    int playerX = (int) (player.getX() / tileWidth);
	    int playerY = (int) (player.getY() / tileHeight);

	    // Check adjacent tiles around the player
	    for (int row = playerY - 1; row <= playerY + 1; row++) {
	        for (int col = playerX - 1; col <= playerX + 1; col++) {
	            if (valid(col, row)) {
	                Tile tile = getTile(col, row);
	                Rectangle playerBounds = player.getBounds();
	                Rectangle tileBounds = new Rectangle(tile.getXC(), tile.getYC(), tileWidth, tileHeight);

	                // Check for collision
	                if (playerBounds.intersects(tileBounds)) {
	                    // Check if the tile is harmful
	                    if (isHarmfulTile(col, row)) {
	                        // Apply damage to the player
	                        applyDamageToPlayer(player);
	                    }
	                    collidedTiles.add(tile);
	                }
	            }
	        }
	    }

	    return collidedTiles;
	}

    /**
     * Applies damage to the player sprite if it collides with harmful tiles.
     * 
     * @param player The player sprite.
     */
    private void applyDamageToPlayer(Sprite player) {
        // Get the tile at the player's position
        Tile tile = getTileAtSprite(player);
        
        // Check if the tile is not null and if it is harmful
        if (tile != null && isHarmfulTile(tile.getCharacter(), mapHeight)) {
            // Reduce player's health based on the type of tile collided
            switch (tile.getCharacter()) {
                case 's':
                case 'k':
                case 't':
                    player.reduceHealth(1); // Reduce player's health by 1 when colliding with harmful tiles
                    break;
                // Add cases for other harmful tiles if needed
                default:
                    break;
            }
        }
    }

    /**
     * Retrieves the character representation of the tile at the specified pixel position.
     * 
     * @param sx The x-coordinate of the pixel position.
     * @param sy The y-coordinate of the pixel position.
     * @return The character representation of the tile.
     */
    public char getTileChar(float sx, float sy) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Retrieves the tile at the position of the given sprite.
     * 
     * @param player The sprite whose position is to be checked.
     * @return The tile at the sprite's position.
     */
    public Tile getTileAtSprite(Sprite player) {
        // Calculate player's grid position
        int playerX = (int) (player.getX() / tileWidth);
        int playerY = (int) (player.getY() / tileHeight);

        // Check if the player position is valid
        if (valid(playerX, playerY)) {
            // Return the tile at the player's position
            return tmap[playerX][playerY];
        } else {
            // If player's position is invalid, return null
            return null;
        }
    }

    /**
     * Checks if a tile at position 'x,y' is harmful to the player.
     *
     * @param x The x tile coordinate (in tiles, not pixels)
     * @param y The y tile coordinate (in tiles, not pixels)
     * @return true if the tile is harmful, false otherwise
     */
    public boolean isHarmfulTile(int x, int y) {
        char tileChar = getTileChar(x, y);
        for (char harmfulTile : harmfulTiles) {
            if (tileChar == harmfulTile) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the tile at the specified pixel position.
     * 
     * @param playerBottomX The x-coordinate of the pixel position.
     * @param playerBottomY The y-coordinate of the pixel position.
     * @return The tile at the specified pixel position.
     */
    public Tile getTileAtPixel(float playerBottomX, float playerBottomY) {
        // TODO Auto-generated method stub
        return null;
    }

} //End of Java Class 

//Last Modified on: 4/18/2024
//3140298

