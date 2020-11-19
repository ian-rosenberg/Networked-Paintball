package server;

public enum PayloadType {
    CONNECT, 
    DISCONNECT, 
    MESSAGE, 
    CLEAR_PLAYERS, 
    SYNC_DIRECTION, 
    SYNC_POSITION, 
    SHOOT, 
    CREATE_ROOM, 
    JOIN_ROOM, 
    GET_ROOMS,
    SYNC_DIMENSIONS,
    ASSIGN_TEAM,
    ASSIGN_ID,
    SET_TEAM_INFO,
    READY
}