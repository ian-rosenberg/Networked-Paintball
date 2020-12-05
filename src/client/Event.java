package client;

import java.awt.Point;

import server.GameState;

public interface Event {
	void onClientConnect(String clientName, String message, int clientId);

	void onClientDisconnect(String clientName, String message);

	void onMessageReceive(String clientName, String message);

	void onChangeRoom();

	void onSyncDirection(String clientName, Point direction);

	void onSyncPosition(String clientName, Point position);

	void onGetRoom(String roomname);

	void onChangeTeam(int number);

	void onSetId(int id);

	void onSetPlayerColor(int teamId, String clientName);

	void onGameStart(Point startPos, int playerId);// Setting up for game start

	void onSetPlayerActivity(boolean bool);

	void onSetGameState(GameState state);

	void onSetTimeLeft(long time);

	void onSetGameBoundary(int x, int y);

	void onSetBulletPosition(int teamId, int bulletId, int xDir, Point newPos);

	void onRemoveBullet(int id);
}