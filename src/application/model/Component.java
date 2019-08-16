package application.model;

import java.util.ArrayList;

import application.DragIconType;
import application.DraggableNode;
import javafx.scene.Node;

public class Component {
	String id;
	DragIconType type;
	private boolean isOn;
	double voltage = 0;
	double current = 0;
	double opacity;
	public DraggableNode node;
	
	
	public double getVoltage() {
		return voltage;
	}
	
	public void setCurrent(double current) {
		this.current = current;
	}
	
	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}
	
	public boolean isOn() {
		return isOn;
	}
	
	ArrayList<Component> edges = new ArrayList<Component>();
	
	public Component(String id, DragIconType type, boolean isOn, DraggableNode n) {
		super();
		this.id = id;
		this.type = type;
		this.isOn = isOn;
		this.node = n;
	}
	
	
	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}
	
	public void addComponent(Component c) {
		if (c != null && !edges.contains(c) && !this.id.equals(c.id)) {
			edges.add(c);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(id + ": " + type +  " is " + (isOn ?  " On" : " Off") + String.format(", %.2f V %.2f A ", voltage,current) + " \n Connected with: ");
		
		edges.stream().forEach(e -> {
			str.append("\t" + e.id + ": " + e.type + " \n"); 
		});
		
		return str.toString();
	}

	public double getCurrent() {
		// TODO Auto-generated method stub
		return this.current;
	}
	
	
}
