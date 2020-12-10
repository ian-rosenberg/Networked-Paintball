package server;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.Player;
import core.BaseGamePanel;
import core.Projectile;

public class Room extends BaseGamePanel implements AutoCloseable {
    private static SocketServer server;// used to refer to accessible server functions
    private String name;
    private int roomId = -1;
    private final static long MINUTE_NANO = TimeUnit.MINUTES.toNanos(1);
    private final static long ROUND_TIME = TimeUnit.MINUTES.toNanos(5);// Round time is 5 min in nanoseconds
    private final static Logger log = Logger.getLogger(Room.class.getName());
    private GameState state = GameState.LOBBY;

    private final static int TEAM_A = 1;
    private final static int TEAM_B = 2;
    private final static int BULLET_RADIUS = 15;
    private final int MAX_HP = 3;

    // Commands
    private final static String COMMAND_TRIGGER = "/";
    private final static String CREATE_ROOM = "createroom";
    private final static String JOIN_ROOM = "joinroom";
    private final static String READY = "ready";
    private List<ClientPlayer> clients = new ArrayList<ClientPlayer>();
    private List<Projectile> projectiles = new ArrayList<Projectile>();
    private static Dimension gameAreaSize = new Dimension(1280, 720);

    private long timeLeft = ROUND_TIME;
    private int minutesLeft = 5;
    private long currentNS = 0;
    private long prevNS = currentNS;

    public Room(String name, boolean delayStart, int id) {
	super(delayStart);
	this.name = name;
	isServer = true;
	roomId = id;
    }

    public Room(String name, int id) {
	this.name = name;
	// set this for BaseGamePanel to NOT draw since it's server-side
	isServer = true;
	roomId = id;
    }

    public static void setServer(SocketServer server) {
	Room.server = server;
    }

    @Override
    public String getName() {
	return name;
    }

    public int getRoomId() {
	return roomId;
    }

    private ClientPlayer getClientPlayer(ServerThread client) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer cp = iter.next();
	    if (cp.client == client) {
		return cp;
	    }
	}
	return null;
    }

    private static Point getRandomStartPosition() {
	Point startPos = new Point();
	startPos.x = (int) (Math.random() * gameAreaSize.width);
	startPos.y = (int) (Math.random() * gameAreaSize.height);
	return startPos;
    }

    protected void createRoom(String room, ServerThread client) {
	if (server.createNewRoom(room)) {
	    sendMessage(client, "Created a new room");
	    joinRoom(room, client);
	}
    }

    protected synchronized void addClient(ServerThread client) {
	client.setCurrentRoom(this);
	boolean exists = false;
	// since we updated to a different List type, we'll need to loop through to find
	// the client to check against
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    if (c.client == client) {
		exists = true;
		if (c.player == null) {
		    log.log(Level.WARNING, "Client " + client.getClientName() + " player was null, creating");
		    Player p = new Player();
		    p.setName(client.getClientName());

		    c.player = p;

		    syncClient(c);
		}
		break;
	    }
	}

	if (exists) {
	    log.log(Level.INFO, "Attempting to add a client that already exists");
	}
	else {
	    // create a player reference for this client
	    // so server can determine position
	    Player p = new Player();
	    p.setName(client.getClientName());
	    p.setId(clients.size());
	    // happens in syncClient
	    // client.sendTeamInfo(p.getId() % 2, p.getName());

	    // add Player and Client reference to ClientPlayer object reference
	    ClientPlayer cp = new ClientPlayer(client, p);
	    clients.add(cp);// this is a "merged" list of Clients (ServerThread) and Players (Player)
	    // objects

	    // that's so we don't have to keep track of the same client in two different
	    // list locations
	    syncClient(cp);

	}
    }

    private void setPlayerInfo(ClientPlayer c) {
	// already set on join
	// c.player.setId(clients.indexOf(c));
	c.client.sendId(c.player.getId());
	teamAssign(c);
	c.client.sendBoundary(gameAreaSize);
    }

    private void teamAssign(ClientPlayer clientPlayer) {
	int playerId = clientPlayer.player.getId();
	String name = clientPlayer.client.getClientName();
	int team = TEAM_B;
	if (playerId % 2 == 0) {
	    team = TEAM_A;
	}
	clientPlayer.player.setTeam(team);
	clientPlayer.client.sendTeamInfo(team, name);
    }

    private void syncClient(ClientPlayer cp) {
	if (cp.client.getClientName() != null) {
	    cp.client.sendClearList();
	    sendConnectionStatus(cp.client, true, "joined the room " + getName(), cp.player.getId());

	    setPlayerInfo(cp);

	    // calculate random start position
	    Point startPos = Room.getRandomStartPosition();
	    cp.player.setPosition(startPos);
	    // tell our client of our server determined position
	    cp.client.sendPosition(cp.client.getClientName(), startPos);
	    // tell everyone else about our server determiend position
	    sendPositionSync(cp.client, startPos);
	    // get the list of connected clients (for ui panel)
	    updateClientList(cp.client);
	    // get dir/pos of existing players
	    updatePlayers(cp.client);
	    // Disable all player gameobjects when we add a client, for now
	    // At some point I should filter out extra players after a game begins
	    // into spectators
	    broadcastSetPlayersInactive();
	}
    }

    /***
     * Syncs the existing players in the room with our newly connected player
     * 
     * @param client
     */
    private synchronized void updatePlayers(ServerThread client) {
	// when we connect, send all existing clients current position and direction so
	// we can locally show this on our client
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();

	    if (c.client != client) {
		client.sendOtherPlayerId(c.player.getId(), c.client.getClientName());
		boolean messageSent = client.sendDirection(c.client.getClientName(), c.player.getDirection());
		if (messageSent) {
		    if (client.sendPosition(c.client.getClientName(), c.player.getPosition())) {
			if (client.sendTeamInfo(c.player.getTeam(), c.client.getClientName())) {
			    // syncTeams(c);
			}
		    }
		}
	    }
	}
    }

    private void syncTeams(ClientPlayer current) {
	Iterator<ClientPlayer> clientIter = clients.iterator();
	while (clientIter.hasNext()) {
	    ClientPlayer cp = clientIter.next();
	    if (cp != current) {
		current.client.sendTeamInfo(cp.player.getTeam(), cp.client.getClientName());
	    }
	}
    }

    /**
     * Syncs the existing clients in the room with our newly connected client
     * 
     * @param client
     */
    private synchronized void updateClientList(ServerThread client) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    if (c.client != client) {
		boolean messageSent = client.sendConnectionStatus(c.client.getClientName(), true, null,
			c.player.getId());
	    }
	}
    }

    protected synchronized void removeClient(ServerThread client) {
	ClientPlayer clientPlayer = null;
	Iterator<ClientPlayer> iter = clients.iterator();

	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    if (c.client == client) {
		clientPlayer = c;
		iter.remove();
		log.log(Level.INFO, "Removed client " + c.client.getClientName() + " from " + getName());
	    }
	}
	if (clients.size() > 0) {
	    sendConnectionStatus(client, false, "left the room " + getName(), clientPlayer.player.getId());
	}
	else {
	    cleanupEmptyRoom();
	}
    }

    private void cleanupEmptyRoom() {
	// If name is null it's already been closed. And don't close the Lobby
	if (name == null || name.equalsIgnoreCase(SocketServer.LOBBY)) {
	    return;
	}
	try {
	    log.log(Level.INFO, "Closing empty room: " + name);
	    close();
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    protected void joinRoom(String room, ServerThread client) {
	server.joinRoom(room, client);
	state = GameState.LOBBY;
	log.log(Level.INFO, "Game is in Lobby state");
    }

    protected void joinLobby(ServerThread client) {
	server.joinLobby(client);
	state = GameState.LOBBY;
    }

    /***
     * Helper function to process messages to trigger different functionality.
     * 
     * @param message The original message being sent
     * @param client  The sender of the message (since they'll be the ones
     *                triggering the actions)
     */
    private String processCommands(String message, ServerThread client) {
	String response = null;
	try {
	    if (message.indexOf(COMMAND_TRIGGER) > -1) {
		String[] comm = message.split(COMMAND_TRIGGER);
		log.log(Level.INFO, message);
		String part1 = comm[1];
		String[] comm2 = part1.split(" ");
		String command = comm2[0];
		ClientPlayer clientPlayer = null;
		if (command != null) {
		    command = command.toLowerCase();
		}
		String roomName;
		switch (command) {
		case CREATE_ROOM:
		    roomName = comm2[1];
		    clientPlayer = getClientPlayer(client);
		    if (clientPlayer != null) {
			createRoom(roomName, client);
		    }
		    break;
		case JOIN_ROOM:
		    roomName = comm2[1];
		    joinRoom(roomName, client);
		    break;
		case READY:
		    /*
		     * if (name.equals("Lobby")) { response =
		     * "ready is not valid for Lobby! Join a new room!"; break; }
		     */

		    clientPlayer = getClientPlayer(client);
		    if (clientPlayer != null) {
			clientPlayer.player.setReady(true);
			readyCheck();
		    }
		    response = "Ready to go!";
		    break;
		default:
		    // not a command, let's fix this function from eating messages
		    response = message;
		    break;
		}
	    }
	    else {
		response = message;
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	return response;
    }

    protected void sendConnectionStatus(ServerThread client, boolean isConnect, String message, int userId) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    boolean messageSent = c.client.sendConnectionStatus(client.getClientName(), isConnect, message, userId);
	    if (!messageSent) {
		iter.remove();
		log.log(Level.INFO, "Removed client " + c.client.getId());
	    }
	}
    }

    private void readyCheck() {
	Iterator<ClientPlayer> iter = clients.iterator();
	int total = clients.size();
	int ready = 0;
	while (iter.hasNext()) {
	    ClientPlayer cp = iter.next();
	    if (cp != null && cp.player.isReady()) {
		ready++;
	    }
	}
	if (ready >= total) {
	    // start
	    System.out.println("Everyone's ready, let's do this!");
	    state = GameState.GAME;
	    iter = clients.iterator();
	    while (iter.hasNext()) {
		ClientPlayer cp = iter.next();
		if (cp != null && cp.player.isReady()) {
		    cp.player.setHP(MAX_HP);
		}
	    }
	    broadcastHP(-1, MAX_HP);
	    broadcastSetPlayersActive();
	    broadcastGameState();
	    currentNS = System.nanoTime();
	    prevNS = currentNS;
	    log.log(Level.INFO, "Game has begun in room " + name);
	}
    }

    /***
     * Takes a sender and a message and broadcasts the message to all clients in
     * this room. Client is mostly passed for command purposes but we can also use
     * it to extract other client info.
     * 
     * @param sender  The client sending the message
     * @param message The message to broadcast inside the room
     */
    protected void sendMessage(ServerThread sender, String message) {
	log.log(Level.INFO, getName() + ": Sending message to " + clients.size() + " clients");
	message = processCommands(message, sender);
	if (message == null) {
	    // it was a command, don't broadcast
	    return;
	}
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer client = iter.next();
	    boolean messageSent = client.client.send(sender.getClientName(), message);
	    if (!messageSent) {
		iter.remove();
		log.log(Level.INFO, "Removed client " + client.client.getId());
	    }
	}
    }

    /**
     * Broadcasts this client/player direction to all connected clients/players
     * 
     * @param sender
     * @param dir
     */
    protected void sendDirectionSync(ServerThread sender, Point dir) {
	if (state != GameState.GAME)
	    return;

	boolean changed = false;
	// first we'll find the clientPlayer that sent their direction
	// and update the server-side instance of their direction
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer client = iter.next();
	    // update only our server reference for this client
	    // if we don't have this "if" it'll update all clients (meaning everyone will
	    // move in sync)
	    if (client.client == sender) {
		changed = client.player.setDirection(dir.x, dir.y);
		break;
	    }
	}
	// if the direction is "changed" (it should be, but check anyway)
	// then we'll broadcast the change in direction to all clients
	// so their local movement reflects correctly
	if (changed) {
	    iter = clients.iterator();
	    while (iter.hasNext()) {
		ClientPlayer client = iter.next();
		boolean messageSent = client.client.sendDirection(sender.getClientName(), dir);

		if (!messageSent) {
		    iter.remove();
		    log.log(Level.INFO, "Removed client " + client.client.getId());
		}
	    }

	}
    }

    /**
     * Broadcasts this client/player position to all connected clients/players
     * 
     * @param sender
     * @param pos
     */
    protected void sendPositionSync(ServerThread sender, Point pos) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer client = iter.next();
	    boolean messageSent = client.client.sendPosition(sender.getClientName(), pos);
	    if (!messageSent) {
		iter.remove();
		log.log(Level.INFO, "Removed client " + client.client.getId());
	    }
	}
    }

    protected void sendSyncProjectile(Projectile proj) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer client = iter.next();
	    client.client.sendSyncProjectile(proj);
	}
    }

    protected void sendRemoveProjectile(int id) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer client = iter.next();

	    client.client.syncRemoveProjectile(id);
	}
    }

    public List<String> getRooms(String search) {
	return server.getRooms(search);
    }

    /***
     * Will attempt to migrate any remaining clients to the Lobby room. Will then
     * set references to null and should be eligible for garbage collection
     */
    @Override
    public void close() throws Exception {
	int clientCount = clients.size();
	if (clientCount > 0) {
	    log.log(Level.INFO, "Migrating " + clients.size() + " to Lobby");
	    Iterator<ClientPlayer> iter = clients.iterator();
	    Room lobby = server.getLobby();
	    while (iter.hasNext()) {
		ClientPlayer client = iter.next();
		lobby.addClient(client.client);
		iter.remove();
	    }
	    log.log(Level.INFO, "Done Migrating " + clients.size() + " to Lobby");
	}
	server.cleanupRoom(this);
	name = null;
	isRunning = false;
	// should be eligible for garbage collection now
    }

    @Override
    public void awake() {
	// TODO Auto-generated method stub

    }

    @Override
    public void start() {
	// TODO Auto-generated method stub
	log.log(Level.INFO, getName() + " start called");
    }

    long frame = 0;

    void checkPositionSync(ClientPlayer cp) {
	// determine the maximum syncing needed
	// you do NOT need it every frame, if you do it could cause network congestion
	// and
	// lots of bandwidth that doesn't need to be utilized
	if (frame % 120 == 0) {// sync every 120 frames (i.e., if 60 fps that's every 2 seconds)
	    // check if it's worth sycning the position
	    // again this is to save unnecessary data transfer
	    if (cp.player.changedPosition()) {
		sendPositionSync(cp.client, cp.player.getPosition());
	    }
	}

    }

    // TODO fix update
    @Override
    public void update() {
	if (state != GameState.GAME)
	    timeLeft = ROUND_TIME;

	prevNS = currentNS;
	currentNS = System.nanoTime();
	timeLeft -= (currentNS - prevNS);

	if ((timeLeft / MINUTE_NANO) < minutesLeft && state == GameState.GAME) {
	    minutesLeft--;
	    broadcastTimeLeft();
	}

	if (timeLeft <= 0 && state != GameState.END) {
	    state = GameState.END;
	    broadcastGameState();
	    broadcastSetPlayersInactive();
	    return;
	}

	Iterator<Projectile> pIter = projectiles.iterator();
	while (pIter.hasNext()) {
	    Projectile p = pIter.next();

	    if (p != null && p.isActive()) {
		int projId = p.getId();

		p.move();

		List<Integer> targetIds = p.getCollidingPlayers(clients);
		if (p.passedScreenBounds(gameAreaSize)) {
		    ClientPlayer cp = getClientPlayerById(projId);
		    cp.setHasFired(false);
		    sendRemoveProjectile(projId);
		    pIter.remove();
		}
		else if (targetIds.size() > 0) {
		    for (int id : targetIds) {
			ClientPlayer cp = getClientPlayerById(id);
			cp.player.setHP(cp.player.getHP() - 1);
			broadcastHP(cp.player.getId(), cp.player.getHP());
			log.log(Level.INFO, cp.client.getClientName() + " was hit!");
			sendMessage(cp.client, cp.client.getClientName() + " was hit!");
		    }

		    ClientPlayer cp = getClientPlayerById(projId);
		    cp.setHasFired(false);
		    sendRemoveProjectile(projId);
		    pIter.remove();
		}
	    }
	}

	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer p = iter.next();
	    if (p != null && p.player.isActive()) {
		// have the server-side player calc their potential new position
		p.player.move();
		int passedBounds = p.player.passedScreenBounds(gameAreaSize);

		switch (passedBounds) {
		case 1:
		    p.player.setPosition(new Point(p.player.getPosition().x, p.player.getSize().y));// North
		    break;

		case 2:
		    p.player.setPosition(
			    new Point(gameAreaSize.width - p.player.getSize().x, p.player.getPosition().y));// East
		    break;

		case 3:
		    p.player.setPosition(
			    new Point(p.player.getPosition().x, gameAreaSize.height - p.player.getSize().y));// South
		    break;

		case 4:
		    p.player.setPosition(new Point(p.player.getSize().x, p.player.getPosition().y));// West
		    break;
		}

		if (passedBounds > 0) {
		    sendPositionSync(p.client, p.player.getPosition());
		}

		// determine if we should sync this player's position to all other players
		checkPositionSync(p);
	    }
	}
    }

    private ClientPlayer getClientPlayerById(int playerId) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer p = iter.next();
	    if (p != null) {
		if (p.player.getId() == playerId) {
		    return p;
		}
	    }
	}

	return null;
    }

    private void broadcastHP(int id, int hp) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    c.client.sendHP(id, hp);
	}
    }

    private void broadcastTimeLeft() {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    c.client.sendTimeLeft(timeLeft);
	    log.log(Level.INFO, timeLeft / MINUTE_NANO + " minutes left");
	}
    }

    private void broadcastGameState() {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    c.client.sendGameState(state);
	    log.log(Level.INFO, "Sending client " + c.player.getId() + " game status " + state.toString());
	}
    }

    private void broadcastSetPlayersInactive() {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    c.player.setActive(false);
	    c.client.sendActiveStatus(false);
	    log.log(Level.INFO, "Set client " + c.player.getId() + " inactive!");
	}
    }

    private void broadcastSetPlayersActive() {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer c = iter.next();
	    c.player.setActive(true);
	    c.client.sendActiveStatus(true);
	    log.log(Level.INFO, "Set client " + c.player.getId() + " active!");
	}
    }

    // don't call this more than once per frame
    private void nextFrame() {
	// we'll do basic frame tracking so we can trigger events
	// less frequently than each frame
	// update frame counter and prevent overflow
	if (Long.MAX_VALUE - 5 <= frame) {
	    frame = Long.MIN_VALUE;
	}
	frame++;
    }

    @Override
    public void lateUpdate() {
	nextFrame();
    }

    @Override
    public void quit() {
	// don't call close here
	log.log(Level.WARNING, getName() + " quit() ");
    }

    @Override
    public void attachListeners() {
	// no listeners either since server side receives no input
    }

    public static Dimension getDimensions() {
	return gameAreaSize;
    }

    @Override
    public void draw(Graphics g) {
	// TODO Auto-generated method stub

    }

    public static long getMinute() {
	return MINUTE_NANO;
    }

    public void getSyncBullet(ServerThread client) {
	Iterator<ClientPlayer> iter = clients.iterator();
	while (iter.hasNext()) {
	    ClientPlayer cp = iter.next();

	    if (cp.client == client && !cp.hasFired()) {
		cp.setHasFired(true);
		int pt = cp.player.getTeam();
		int xdir = pt == 1 ? -1 : 1;

		Projectile newProj = new Projectile(pt, cp.player.getId(), xdir, new Point(
			cp.player.getPosition().x + BULLET_RADIUS, cp.player.getPosition().y + BULLET_RADIUS));

		projectiles.add(newProj);

		sendSyncProjectile(newProj);
	    }
	}
    }

}