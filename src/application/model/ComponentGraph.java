package application.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;

import application.DragIconType;
import application.DraggableNode;
import application.util.Message;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class ComponentGraph {
	public static TreeMap<String, Component> components = new TreeMap<String, Component>();

	
	private static final double VOLTAGE_INTENSITY = 10;
	
	public static void printComponents() {

		System.out.println("--------------------------------------------");
		components.forEach((s, c) -> {
			System.out.println("-> " + c.toString());
		});
	}

	/*
	 * Clears all Circuit Nodes reachable from closed switches
	 * 
	 */
//	public static void filterUntravelledComponents() {
//		components.values().stream().filter(c -> c.type.equals(DragIconType.SWITCH) && !c.isOn())
//				.forEach(c -> {
//					c.edges.clear();
//				});
//	}

	public static void removeComponent(String id) {
		if (!components.containsKey(id))
			return;

		components.remove(id);
		components.forEach((i, c) -> {
			c.edges.removeIf(e -> e.id.equals(id));
		});
	}

	public static void executeGraph(double volts, double current) {
		components.values().stream().filter( e -> e.type.equals(DragIconType.BULB)).forEach( e -> {
			e.node.setEffect(null);
		});
		components.values().stream().filter( e -> e.type.equals(DragIconType.AMMETER)).forEach( e -> {
			e.node.getTitleBar().setText("Ammeter");
		});
		
		
		// check if battery exists
		Component battery = components.values().stream().filter(e -> e.type.equals(DragIconType.BATTERY)).findFirst()
				.orElse(null);
		if (battery == null) {
			Message.display("ERROR: NO BATTERY FOUND!");
			return;
		}

//		ComponentGraph.filterUntravelledComponents();

		// Check if there is a closed loop with the battery
		// Find any components (excluding the battery component) and see if any of their
		// edges have a battery
//		
//		if (components.values().stream().filter(e -> !e.id.equals(battery.id) && e.edges.contains(battery))
//				.count() == 0) {}		
		
		if (!isReachable(battery, battery)) {
			Message.display("ERROR: CIRCUIT NOT CLOSED");
			return;
		}
		
		components.values().forEach( e -> {
			e.current = 0;
			e.voltage = 0;
		});
		

		Component tmp = battery;
		tmp.current = current;
		tmp.voltage = volts;

		Stack<Component> stack = new Stack<Component>();

		HashMap<Component, Boolean> isVisited = new HashMap<Component, Boolean>();
		components.values().forEach(c -> isVisited.put(c, false));
		
	
		final double noOfBulbs = components.values().stream().filter(e -> e.type.equals(DragIconType.BULB)).count();
		
		
		stack.add(tmp);
		while (!stack.empty()) {
			Component temp = stack.pop();
			isVisited.put(temp, true);
			double newCurrent = temp.current / temp.edges.size();


			temp.edges.forEach(e -> {
				
				if (!isVisited.get(e)) {
					stack.add(e);
				}
				if (e != battery) {
					e.setCurrent(e.getCurrent() + newCurrent);
					e.setVoltage(e.type.equals(DragIconType.BULB) ? temp.voltage - (1 / (noOfBulbs) )
							: temp.voltage);
				}
			});
			
		}

		normalizeVoltages();
		addLabels();

		return;
	}

	public static void normalizeVoltages() {
		double maxVoltage = components.values().stream().mapToDouble(Component::getVoltage).max().orElse(0);
		if (maxVoltage == 0)
			return;

		components.values().stream().filter(e -> e.type.equals(DragIconType.BULB) && e.getVoltage() != 0).forEach(e -> {
			e.opacity = e.getVoltage() / maxVoltage;
			DropShadow d = new DropShadow(e.opacity * VOLTAGE_INTENSITY, Color.YELLOW);
			e.node.setEffect(d);
		});

	}

	public static void resetComponents() {
		components = new TreeMap<String, Component>();
	}

	public static void addLabels() {

		components.values().stream().filter(c -> c.type.equals(DragIconType.AMMETER)).forEach(c -> {
			DraggableNode n = c.node;
			double reading = c.current;
			n.getTitleBar().setText(String.format("Ammeter\n%.2f A", reading));
			
		});

	}
	
	
    //prints BFS traversal from a given source s 
    public static boolean isReachable(Component s, Component d) 
    { 
        LinkedList<Component>temp; 
  
        // Mark all the vertices as not visited(By default set 
        // as false) 
        HashMap<Component, Boolean> visited = new HashMap<Component, Boolean>();
        components.values().forEach( e -> visited.put(e, false));
        
        // Create a queue for BFS 
        LinkedList<Component> queue = new LinkedList<Component>(); 
  
        // Mark the current node as visited and enqueue it 
        visited.put(s, true);
        queue.add(s); 
  
        // 'i' will be used to get all adjacent vertices of a vertex 
        Iterator<Component> i; 
        while (queue.size()!=0) 
        { 
            // Dequeue a vertex from queue and print it 
            s = queue.poll(); 
  
            Component n; 
            i = s.edges.stream().filter( e -> e.isOn()).iterator(); 
  
            // Get all adjacent vertices of the dequeued vertex s 
            // If a adjacent has not been visited, then mark it 
            // visited and enqueue it 
            while (i.hasNext()) 
            { 
                n = i.next(); 
  
                // If this adjacent node is the destination node, 
                // then return true 
                if (n==d) 
                    return true; 
  
                // Else, continue to do BFS 
                if (!visited.get(n)) 
                { 
                    visited.put(n,true); 
                    queue.add(n); 
                } 
            } 
        } 
  
        // If BFS is complete without visited d 
        return false; 
    } 

}
