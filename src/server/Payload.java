package server;

import java.awt.Point;
import java.io.Serializable;

public class Payload implements Serializable {

	public class PlayerInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -485567639439554589L;
		private int teamId = 0;
		private int playerId;

		public void setTeamId(int id) {
			teamId = id;
		}

		public void setPlayerId(int id) {
			playerId = id;
		}

		public int getTeamId() {
			return teamId;
		}

		public int getPlayerId() {
			return playerId;
		}
	}

	private PlayerInfo playerInfo = new PlayerInfo();

	public PlayerInfo getPlayerInfo() {
		return playerInfo;
	}

	public void setPlayerInfo(int teamID, int playerID) {
		playerInfo.teamId = teamID;
		playerInfo.playerId = playerID;
	}

	/**
	 * baeldung.com/java-serial-version-uid
	 */
	private static final long serialVersionUID = -6687715510484845706L;

	private String clientName;

	public void setClientName(String s) {
		this.clientName = s;
	}

	public String getClientName() {
		return clientName;
	}

	private String message;

	public void setMessage(String s) {
		this.message = s;
	}

	public String getMessage() {
		return this.message;
	}

	private PayloadType payloadType;

	public void setPayloadType(PayloadType pt) {
		this.payloadType = pt;
	}

	public PayloadType getPayloadType() {
		return this.payloadType;
	}

	private int number;

	public void setNumber(int n) {
		this.number = n;
	}

	public int getNumber() {
		return this.number;
	}

	int x = 0;
	int y = 0;

	public void setPoint(Point p) {
		x = p.x;
		y = p.y;
	}

	public Point getPoint() {
		return new Point(x, y);
	}

	@Override
	public String toString() {
		return String.format("Type[%s], Number[%s], Message[%s]", getPayloadType().toString(), getNumber(),
				getMessage());
	}

	private boolean boolVal;

	public void setBool(boolean b) {
		boolVal = b;
	}

	public boolean getBool() {
		return boolVal;
	}

	private GameState gameState;

	public void setState(GameState state) {
		gameState = state;
	}

	public GameState getState() {
		return gameState;
	}

	private long timer;

	public void setTime(long t) {
		timer = t;
	}

	public long getTime() {
		return timer;
	}
}