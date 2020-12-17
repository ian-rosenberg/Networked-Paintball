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
		private int hp = 0;

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
	
	public class IdNamePair implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -471077415562688660L;
		private int id;
		private String name;
		
		public IdNamePair(int i, String s) {
			id = i;
			name = s;
		}
		
		public void setId(int ID) {
			id = ID;
		}
		
		public int getId() {
			return id;
		}
		
		public void SetName(String n) {
			name = n;
		}
		
		public String getName() {
			return name;
		}
	}
	
	public void setClientIdNamePair(int ID, String name) {
		clientIdName.setId(ID);
		clientIdName.SetName(name);
	}
	
	public void setDisablePayload(int id, String clientName) {
		disableClient.setId(id);
		disableClient.SetName(clientName);
	}
	
	public IdNamePair getDisableClient() {
		return disableClient;
	}
	
	public IdNamePair getClientIdName() {
		return clientIdName;
	}
	
	private IdNamePair disableClient = new IdNamePair(-1, "");
	private IdNamePair clientIdName = new IdNamePair(-1, "");

	public class TeamScore implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7385670673857354560L;
		private int scoreA;
		private int scoreB;
		
		public TeamScore(int a, int b) {
			setScoreA(a);
			setScoreB(b);
		}
		
		private void setScoreA(int scoreA) {
			this.scoreA = scoreA;		
		}

		public void setScoreB(int scoreB) {
			this.scoreB = scoreB;
		}

		public int getScoreA() {
			return scoreA;
		}
		public int getScoreB() {
			return scoreB;
		}
	}
	
	private TeamScore teamScore = new TeamScore(0,0);
	
	public TeamScore getScorePayload() {
		return teamScore;
	}
	
	public void setScorePayload(int a, int b) {
		teamScore.scoreA = a;
		teamScore.scoreB = b;
	}
	
	private int clientId = -1;

	/**
	 * @return the clientId
	 */
	public int getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	private PlayerInfo playerInfo = new PlayerInfo();

	public PlayerInfo getPlayerInfo() {
		return playerInfo;
	}

	public void setPlayerInfo(int teamID, int playerID) {
		playerInfo.teamId = teamID;
		playerInfo.playerId = playerID;
	}

	public class ProjectileInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private int teamId = 0;
		private int playerId = -1;
		private int dirX = 0;
		private Point position = new Point(0, 0);

		public void setTeamId(int id) {
			teamId = id;
		}

		public int getTeamId() {
			return teamId;
		}

		public int getDirX() {
			return dirX;
		}

		public void setDirX(int dirX) {
			this.dirX = dirX;
		}

		public int getPlayerId() {
			return playerId;
		}

		public void setPlayerId(int playerId) {
			this.playerId = playerId;
		}

		public Point getPosition() {
			return position;
		}

		public void setPosition(Point position) {
			this.position = position;
		}
	}

	public ProjectileInfo getProjectileInfo() {
		return projectilePayload;
	}

	public void setProjectileInfo(int team, int ownerId, int dX, Point position) {
		projectilePayload.setTeamId(team);
		projectilePayload.setDirX(dX);
		projectilePayload.setPlayerId(ownerId);
		projectilePayload.setPosition(position);
	}

	private ProjectileInfo projectilePayload = new ProjectileInfo();

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