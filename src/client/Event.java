package client;

import java.awt.Point;

public interface Event {
    void onClientConnect(String clientName, String message, int clientId);

    void onClientDisconnect(String clientName, String message);

    void onMessageReceive(String clientName, String message);

    void onChangeRoom();

    void onSyncDirection(String clientName, Point direction);

    void onSyncPosition(String clientName, Point position);
    
    void onSyncWeaponFire(int team, Point position, Point direction);
    
    void onGetRoom(String roomname);

	void onChangeTeam(int number);
	
	void onSetId(int id);
	
	void onSetPlayerColor(int teamId, String clientName);
	
	void onGameStart(Point startPos,  int playerId);//Setting up for game start

	void onSetPlayerActivity(String clientName, boolean bool);
}