package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class User extends JPanel{
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

	public void setUserColor(int teamId) {
		if(teamId == 1){
    		textColor = "#FF69B4";
    	}
    	else {
    		textColor = "green";
    	}
		
    	nameField.setText("<span style='color:"+textColor+"'>"+name+"</span>");
    	repaint();
	}	
}