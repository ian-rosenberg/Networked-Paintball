package client;

import java.awt.Point;

import server.GameState;

public interface Event {
    void onClientConnect(String clientName, String message, int clientId);

    void onClientDisconnect(String clientName, String message);

    void onMessageReceive(String clientName, String message);

    void onChangeRoom();

    void onSyncDirection(int clientId, Point direction);

    void onSyncPosition(int clientId, Point position);

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

    void onSetHP(Point idHP);

	void onDisablePlayer(int id, String clientName);

	void onSetScores(int scoreA, int scoreB);

	void onSetGrenadePosition(int teamId, int playerId, int dirX, Point position, int radius);

	void onRemoveGrenade(int id);
}