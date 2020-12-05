package client;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.GameState;
import server.Payload;
import server.PayloadType;

public enum SocketClient {
	INSTANCE; // see https://dzone.com/articles/java-singletons-using-enum "Making Singletons
	// with Enum"

	private static Socket server;
	private static Thread fromServerThread;
	private static Thread clientThread;
	private static String clientName;
	private static ObjectOutputStream out;
	private final static Logger log = Logger.getLogger(SocketClient.class.getName());
	private static List<Event> events = new ArrayList<Event>();// change from event to list<event>

	private Payload buildMessage(String message) {
		Payload payload = new Payload();
		payload.setPayloadType(PayloadType.MESSAGE);
		payload.setClientName(clientName);
		payload.setMessage(message);
		return payload;
	}

	private Payload buildConnectionStatus(String name, boolean isConnect) {
		Payload payload = new Payload();
		if (isConnect) {
			payload.setPayloadType(PayloadType.CONNECT);
		} else {
			payload.setPayloadType(PayloadType.DISCONNECT);
		}
		payload.setClientName(name);
		return payload;
	}

	private void sendPayload(Payload p) {
		try {
			out.writeObject(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void listenForServerMessage(ObjectInputStream in) {
		if (fromServerThread != null) {
			log.log(Level.INFO, "Server Listener is likely already running");
			return;
		}
		// Thread to listen for responses from server so it doesn't block main thread
		fromServerThread = new Thread() {
			@Override
			public void run() {
				try {
					Payload fromServer;
					// while we're connected, listen for Payloads from server
					while (!server.isClosed() && (fromServer = (Payload) in.readObject()) != null) {
						processPayload(fromServer);
					}
				} catch (Exception e) {
					if (!server.isClosed()) {
						e.printStackTrace();
						log.log(Level.INFO, "Server closed connection");
					} else {
						log.log(Level.INFO, "Connection closed");
					}
				} finally {
					close();
					log.log(Level.INFO, "Stopped listening to server input");
				}
			}
		};
		fromServerThread.start();// start the thread
	}

	private void sendOnClientConnect(String name, String message, int id) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onClientConnect(name, message, id);
			}
		}
	}

	private void sendOnClientDisconnect(String name, String message) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onClientDisconnect(name, message);
			}
		}
	}

	private void sendOnMessage(String name, String message) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onMessageReceive(name, message);
			}
		}
	}

	private void sendOnChangeRoom() {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onChangeRoom();
			}
		}
	}

	private void sendSyncDirection(String clientName, Point direction) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onSyncDirection(clientName, direction);
			}
		}
	}

	private void sendSyncPosition(String clientName, Point position) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onSyncPosition(clientName, position);
			}
		}
	}

	private void sendRoom(String roomName) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onGetRoom(roomName);
			}
		}
	}

	private void changeTeam(int number) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onChangeTeam(number);
			}
		}
	}

	private void setPlayerId(int n) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onSetId(n);
			}
		}
	}

	private void setPlayerColor(int teamId, String clientName) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onSetPlayerColor(teamId, clientName);
			}
		}
	}

	private void setPlayerActivity(boolean bool) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onSetPlayerActivity(bool);
			}
		}
	}

	private void setGameState(GameState state) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onSetGameState(state);
			}
		}
	}
	

	private void setTimeLeft(long time) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onSetTimeLeft(time);
			}
		}
	}
	
	private void setGameBoundary(Point point) {
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event e = iter.next();
			if (e != null) {
				e.onSetGameBoundary(point.x, point.y);
			}
		}
	}	

	/***
	 * Determine any special logic for different PayloadTypes
	 * 
	 * @param p
	 */
	private void processPayload(Payload p) {

		switch (p.getPayloadType()) {
		case CONNECT:
			sendOnClientConnect(p.getClientName(), p.getMessage(), p.getNumber());
			break;
		case DISCONNECT:
			sendOnClientDisconnect(p.getClientName(), p.getMessage());
			break;
		case MESSAGE:
			sendOnMessage(p.getClientName(), p.getMessage());
			break;
		case CLEAR_PLAYERS:
			sendOnChangeRoom();
			break;
		case SYNC_DIRECTION:
			sendSyncDirection(p.getClientName(), p.getPoint());
			break;
		case SYNC_POSITION:
			sendSyncPosition(p.getClientName(), p.getPoint());
			break;
		case GET_ROOMS:
			// reply from ServerThread
			sendRoom(p.getMessage());
			break;
		case ASSIGN_ID:
			setPlayerId(p.getNumber());
			break;
		case SET_TEAM_INFO:
			setPlayerColor(p.getNumber(), p.getClientName());
			break;
		case SET_ACTIVITY:
			setPlayerActivity(p.getBool());
			break;
		case GAME_STATE:
			setGameState(p.getState());
			break;
		case TIMER:
			setTimeLeft(p.getTime());
			break;
		case SYNC_DIMENSIONS:
			setGameBoundary(p.getPoint());
			break;
		default:
			log.log(Level.WARNING, "unhandled payload on client" + p);
			break;

		}
	}

	// TODO Start public methods here

	public void registerCallbackListener(Event e) {
		events.add(e);
		log.log(Level.INFO, "Attached listener");
	}

	public void removeCallbackListener(Event e) {
		events.remove(e);
	}

	public void requestGameDimensions() {
		Payload p = new Payload();
		p.setPayloadType(PayloadType.SYNC_DIMENSIONS);

		sendPayload(p);
	}

	public boolean connectAndStart(String address, String port) throws IOException {
		if (connect(address, port)) {
			return start();
		}
		return false;
	}

	public boolean connect(String address, String port) {
		try {
			server = new Socket(address, Integer.parseInt(port));
			log.log(Level.INFO, "Client connected");
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setUsername(String username) {
		clientName = username;
		sendPayload(buildConnectionStatus(clientName, true));
	}

	public void sendMessage(String message) {
		sendPayload(buildMessage(message));
	}

	public void sendCreateRoom(String room) {
		Payload p = new Payload();
		p.setPayloadType(PayloadType.CREATE_ROOM);
		p.setMessage(room);
		sendPayload(p);
	}

	public void sendJoinRoom(String room) {
		Payload p = new Payload();
		p.setPayloadType(PayloadType.JOIN_ROOM);
		p.setMessage(room);
		sendPayload(p);
	}

	public void sendGetRooms(String query) {
		Payload p = new Payload();
		p.setPayloadType(PayloadType.GET_ROOMS);
		p.setMessage(query);
		sendPayload(p);
	}
	


	protected void sendShootBullet(int team, int playerId, Point clickPos, Point playerPos) {
		Payload p = new Payload();
		p.setPayloadType(PayloadType.SHOOT);
		Point direction = new Point(clickPos.x - playerPos.x, clickPos.y - playerPos.y);
		double length = Math.hypot(direction.x, direction.y);
		if (length == 0.0) {
			return;
		}
		
		double dirX = direction.x/length;
		double dirY = direction.y/length;
		
		p.setProjectileInfo(team, playerId, dirX, dirY);
				
		sendPayload(p);
	}

	/**
	 * Sends desired to change direction to server
	 * 
	 * @param dir
	 */
	public void syncDirection(Point dir) {
		Payload p = new Payload();
		// no need to add clientName here since ServerThread has the info
		// so let's save a few bytes
		p.setPayloadType(PayloadType.SYNC_DIRECTION);
		p.setPoint(dir);
		sendPayload(p);
	}

	/**
	 * we won't be syncing position from the client since our server is the one
	 * that'll do it so creating this unused method as a reminder not to use/make it
	 */
	@Deprecated
	public void syncPosition() {
		log.log(Level.SEVERE, "My sample doesn't use this");
	}

	public boolean start() throws IOException {
		if (server == null) {
			log.log(Level.WARNING, "Server is null");
			return false;
		}
		if (clientThread != null && clientThread.isAlive()) {
			log.log(Level.SEVERE, "Client thread is already active");
			return false;
		}
		if (clientThread != null) {
			clientThread.interrupt();
			clientThread = null;
		}
		log.log(Level.INFO, "Client Started");
		clientThread = new Thread() {
			@Override
			public void run() {

				// listen to console, server in, and write to server out
				try (ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(server.getInputStream());) {
					SocketClient.out = out;

					// starts new thread
					listenForServerMessage(in);

					// Keep main thread alive until the socket is closed
					// initialize/do everything before this line
					// (Without this line the program would stop after the first message
					while (!server.isClosed()) {
						Thread.sleep(50);
					}
					log.log(Level.INFO, "Client Thread stopping");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					close();
				}
			}
		};
		clientThread.start();
		return true;
	}

	public void close() {
		if (server != null && !server.isClosed()) {
			try {
				server.close();
				log.log(Level.INFO, "Closed Socket");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}