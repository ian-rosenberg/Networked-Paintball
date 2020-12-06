package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import server.ClientPlayer;

public class Projectile extends GameObject {
	private int radius = 2;
	private int dirX = 0;
	
	public Projectile(int tId, int projId, int xDir, Point player){
		this.team = tId;
		this.setId(projId);
		this.dirX = xDir;
		setPosition(new Point(player));
		this.setActive(true);
		this.setTeam(tId);
		this.setSpeed(15, 0);
	}
	
	public boolean passedScreenBounds(Dimension bounds) {
		if(position.getX() < 5 || position.getX() > bounds.getWidth()-10) {		
			return true; 
		}
		
		return false;
	}

	public void getCollidingPlayers(List<ClientPlayer> clientPlayers) {

	}
	
	@Override
	public boolean draw(Graphics g) {
		// using a boolean here so we can block drawing if isActive is false via call to
		// super
		if (super.draw(g)) {
			g.setColor(color);
			g.fillOval(position.x, position.y, size.width, size.height);
		}
		return true;
	}
	
	public void setDirX(int dir) {
		dirX = dir;
	}
	
	public int getDirX() {
		return dirX;
	}
	
	@Override
	public void move() {
		if (!isActive) {
			return;
		}
		previousPosition.x = position.x;
		previousPosition.y = position.y;
		position.x += (speed.x * dirX);
	}
}
