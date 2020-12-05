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
	private static final int LINE_FACTOR = 20;
	private Color color = Color.WHITE;
	private Point nameOffset = new Point(0, -5);
	private boolean isReady = false;
	private Point lineEnd = new Point(position.x + (size.width/2), position.y + (size.height/2));
	private int HP = 3;

	public void setDirectionLine(Point dir) {
		lineEnd.x = dir.x;
		lineEnd.y = dir.y;
	}
	
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
			if(lineEnd.x != position.x || lineEnd.y != position.y) {
				g.drawLine(position.x + (size.width/2), position.y + (size.height/2), position.x + (size.width/2) + (lineEnd.x * LINE_FACTOR), position.y + (size.height/2) + (lineEnd.y * LINE_FACTOR));
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("Player ID: %d, Name: %s, p: (%d,%d), s: (%d, %d), d: (%d, %d), isActive: %s", id, name,
				position.x, position.y, speed.x, speed.y, direction.x, direction.y, isActive);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color teamColor) {
		color = teamColor;
	}
}