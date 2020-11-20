package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class User extends JPanel implements Event{
	private int id = -1;
    private String name;
    private JEditorPane nameField;
    private String textColor;

    public User(String name, int ID) {
	this.name = name;
	id = ID;
	nameField = new JEditorPane();
	nameField.setText(name);
	nameField.setContentType("text/html");
	nameField.setEditable(false);
	this.setLayout(new BorderLayout());
	this.add(nameField);
    }

    public String getName() {
	return name;
    }

	public int getId() {
		return id;
	}
	
	public void setId(int ID) {
		id = ID;
	}

	@Override
	public void onClientConnect(String clientName, String message, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientDisconnect(String clientName, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageReceive(String clientName, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangeRoom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSyncDirection(String clientName, Point direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSyncPosition(String clientName, Point position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSyncWeaponFire(int team, Point position, Point direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetRoom(String roomname) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSetId(int newId) {
		id = newId;	
	}

	@Override
	public void onSetPlayerColor(int teamId, int playerId) {
		if(teamId == 1){
    		textColor = "pink";
    	}
    	else {
    		textColor = "green";
    	}

    	nameField.setText("<span style='color:"+textColor+"'>"+name+"</span>");
    	repaint();
	}

	@Override
	public void onChangeTeam(int number) {
		// TODO Auto-generated method stub
		
	}
}