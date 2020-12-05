package core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

public abstract class GameObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9145932773417678588L;
	protected Point position = new Point(0, 0);
	protected Point previousPosition = new Point(0, 0);
	protected Point speed = new Point(2, 2);
	protected Point direction = new Point(0, 0);
	protected int team = 0;
	protected Dimension size = new Dimension(25, 25);
	protected String name = "";
	protected boolean isActive = true;
	protected int id = -1;

	/**
	 * Set the index of the corresponding team to place the player into. There is no
	 * friendly fire, so once a bullet has been fired from anyone on team teamNumber
	 * it cannot collide with the same team, only other teams.
	 * 
	 * @param teamNumber
	 */
	public void setTeam(int teamNumber) {
		if (teamNumber < 1) {
			System.out.println("Invalid team number!");
			return;
		}

		System.out.println("Assigned player " + id + " to team " + team);
		team = teamNumber;
	}

	/**
	 * Set the x,y speed of the object, values can only be positive. Set -1 to
	 * ignore speed change for that dimension. A value of 0 would stop this object
	 * from moving on that dimension Use setDirection for changes in direction
	 * 
	 * @param x
	 * @param y
	 */
	public void setSpeed(int x, int y) {
		// not using Math.max here since we want to be able to ignore a speed dimension
		// Math.max would set it to a value
		if (x > -1) {
			speed.x = x;
		}
		if (y > -1) {
			speed.y = y;
		}
	}

	/**
	 * Sets the dimensions of the object
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		size.width = Math.max(0, width);
		size.height = Math.max(0, height);
	}
	
	public Point getSize() {
		return new Point(size.width, size.height);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Enable or disable object
	 * 
	 * @param isActive
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setId(int ID) {
		if (ID < 0) {
			System.out.println("Invalid id!");
			return;
		}

		id = ID;

		System.out.println("Assigned player " + id);
	}

	public boolean isActive() {
		return this.isActive;
	}

	/**
	 * Call to apply speed/direction to position
	 */
	public void move() {
		if (!isActive) {
			return;
		}
		previousPosition.x = position.x;
		previousPosition.y = position.y;
		position.x += (speed.x * direction.x);
		position.y += (speed.y * direction.y);
	}

	/***
	 * Sets the direction of this object. Use the return value to determine if a
	 * network request should sync
	 * 
	 * @param x
	 * @param y
	 * @return returns true if changed, false if it's the same.
	 */
	public boolean setDirection(int x, int y) {
		x = Helpers.clamp(x, -1, 1);
		y = Helpers.clamp(y, -1, 1);
		boolean changed = false;
		if (direction.x != x) {
			direction.x = x;
			changed = true;
		}
		if (direction.y != y) {
			direction.y = y;
			changed = true;
		}
		return changed;
	}

	public Point getDirection() {
		return direction;
	}

	public int getTeam() {
		return team;
	}

	public int getId() {
		return id;
	}

	/**
	 * Instantly sets a position
	 * 
	 * @param position
	 */
	public void setPosition(Point position) {
		previousPosition.x = position.x;
		previousPosition.y = position.y;
		this.position.x = position.x;
		this.position.y = position.y;
	}

	public Point getPosition() {
		return position;
	}

	/**
	 * Checks if previous position differs from current position
	 * 
	 * @return
	 */
	public boolean changedPosition() {
		return (previousPosition.x != position.x || previousPosition.y != position.y);
	}

	/**
	 * use to determine if subclass should draw due to active status
	 * 
	 * @param g
	 * @return
	 */
	public boolean draw(Graphics g) {
		if (!isActive) {
			return false;
		}
		return true;
	}
}