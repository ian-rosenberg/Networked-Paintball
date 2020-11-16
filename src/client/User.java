package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class User extends JPanel implements Event{
    private String name;
    private JEditorPane nameField;
    private Color textColor;

    public User(String name) {
	this.name = name;
	nameField = new JEditorPane();
	nameField.setText(name);
	nameField.setEditable(false);
	this.setLayout(new BorderLayout());
	this.add(nameField);
    }

    public String getName() {
	return name;
    }
    
    public void setColor(Color teamColor) {
    	textColor = teamColor;
    	nameField.setText("<span style=\\\"color:"+textColor+"\\\">"+name+"</span>");
    }

	@Override
	public void onClientConnect(String clientName, String message) {
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
	public void onChangeTeam(int number) {
		switch(number) {
			case 1: 
				setColor(Color.pink);
				break;
			case 2: 
				setColor(Color.green);
				break;
			default:
				break;
		}
		
	}
}