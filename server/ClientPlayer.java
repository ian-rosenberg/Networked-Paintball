package server;

import client.Player;

public class ClientPlayer {
	public ClientPlayer(ServerThread client, Player player) {
		this.client = client;
		this.player = player;
	}

	public boolean hasFired() {
		return hasFired;
	}

	public void setHasFired(boolean hasFired) {
		this.hasFired = hasFired;
	}

	public ServerThread client;
	public Player player;
	
	private boolean hasFired = false;
}