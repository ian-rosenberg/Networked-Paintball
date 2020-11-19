package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

import core.GameObject;

public class Player extends GameObject implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6088251166673414031L;
    Color color = Color.WHITE;
    Point nameOffset = new Point(0, -5);
    boolean isReady = false;

    public void setReady(boolean r) {
    	isReady = r;
    }

    public boolean isReady() {
    	return isReady;
    }


    /**
     * Gets called by the game engine to draw the current location/size
     */
    @Override
    public boolean draw(Graphics g) {
	// using a boolean here so we can block drawing if isActive is false via call to
	// super
	if (super.draw(g)) {
	    g.setColor(color);
	    g.fillOval(position.x, position.y, size.width, size.height);
	    g.setColor(Color.WHITE);
	    g.setFont(new Font("Monospaced", Font.PLAIN, 12));
	    g.drawString("Name: " + name, position.x + nameOffset.x, position.y + nameOffset.y);
	}
	return true;
    }

    @Override
    public String toString() {
	return String.format("Player ID: %d, Name: %s, p: (%d,%d), s: (%d, %d), d: (%d, %d), isAcitve: %s", id, name, position.x,
		position.y, speed.x, speed.y, direction.x, direction.y, isActive);
    }
    
    public void setTeam(int teamNumber) {
    	//Only two teams. Splatoon colors, heh
    	switch(teamNumber) {
    		case 1: 
    			color = Color.pink;
    			break;
    		case 2: 
    			color = Color.green;
    			break;
    		default:
    			break;
    	}  	
    	
    	team = teamNumber;
    }
    
    public int getId() {
    	return id;
    }
    
    public void setId(int playerId) {
    	id = playerId;
    }

	public Color getColor() {
		return color;
	}
	
    public void setColor(Color teamColor) {
    	color = teamColor;
    }
}