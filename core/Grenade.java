package core;

import java.awt.Point;
import java.lang.Math;

public class Grenade extends Projectile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6333188098629282277L;
	
	private int maxRadius = 100;
	private int maxTravel;
	private int currentTravel = 0;
	
	public Grenade(int tId, int projId, int xDir, Point player, int maxDistance) {
		super(tId, projId, xDir, player);
		
		this.maxTravel = maxDistance;
	}

	public int getMaxRadius() {
		return maxRadius;
	}

	public void setMaxRadius(int endRadius) {
		this.maxRadius = endRadius;
	}

	public int getMaxTravel() {
		return maxTravel;
	}

	public void setMaxTravel(int maxTravel) {
		this.maxTravel = maxTravel;
	}
	
	@Override
	public void move() {
		if (!isActive) {
			return;
		}
		
		if(currentTravel >= maxTravel)
		{
			if(super.getRadius() >= maxRadius) {
				return;
			}
			expandRadius();
			return;
		}
		
		previousPosition.x = position.x;
		previousPosition.y = position.y;
		position.x += (speed.x * super.getDirX());
		currentTravel += Math.abs(position.x - previousPosition.x);
	}

	private void expandRadius() {
		int r = getRadius();
		setRadius(r + 1);
		setSize(r, r);
	}
}
