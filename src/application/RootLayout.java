package application;

import java.io.IOException;

import application.model.Component;
import application.model.ComponentGraph;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class RootLayout extends AnchorPane{

	@FXML SplitPane base_pane;
	@FXML AnchorPane right_pane;
	@FXML VBox left_pane;
	@FXML VBox menu;

	
	private DragIcon mDragOverIcon = null;
	
	private EventHandler<DragEvent> mIconDragOverRoot = null;
	private EventHandler<DragEvent> mIconDragDropped = null;
	private EventHandler<DragEvent> mIconDragOverRightPane = null;
	
	
	private static final double MIN_VOLT = 1;
	private static final double MAX_VOLT = 5;
	private static final double MIN_CURR = 1;
	private static final double MAX_CURR = 5;
	
	static Slider voltSlider = new Slider(MIN_VOLT, MAX_VOLT, 0.5*(MAX_VOLT+MIN_VOLT));
	static Slider currentSlider = new Slider(MIN_CURR, MAX_CURR, 0.5*(MAX_CURR+MIN_CURR));
	
	private Label vLabel = new Label(String.format("Voltage: %.2f",voltSlider.getValue()));
	private Label cLabel = new Label(String.format("Current: %.2f",currentSlider.getValue()));
	
	
	
	public RootLayout() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/RootLayout.fxml")
				);
		
		fxmlLoader.setRoot(this); 
		fxmlLoader.setController(this);
		
		try { 
			fxmlLoader.load();
        
		} catch (IOException exception) {
		    throw new RuntimeException(exception);
		}
	}
	
	@FXML
	private void initialize() {
		
		//Add one icon that will be used for the drag-drop process
		//This is added as a child to the root anchorpane so it can be visible
		//on both sides of the split pane.
		mDragOverIcon = new DragIcon();
		
		mDragOverIcon.setVisible(false);
		mDragOverIcon.setOpacity(0.65);
		getChildren().add(mDragOverIcon);
		
		Label heading = new Label("Components");
		left_pane.getChildren().add(heading);
		
		//populate left pane with multiple colored icons for testing
		for (int i = 0; i < DragIconType.values().length; i++) {
			
			DragIcon icn = new DragIcon();
			
			addDragDetection(icn);
			
			icn.setType(DragIconType.values()[i]);
			left_pane.getChildren().add(icn);
		}
		
		buildDragHandlers();
		
		Button simulateButton = new Button("Simulate");
		simulateButton.setOnAction( e-> {
			ComponentGraph.executeGraph(5,getCurrent());
			ComponentGraph.printComponents();
		});
		Button resetButton = new Button("Reset");
		resetButton.setOnAction( e-> {
			System.out.println(right_pane.getChildren().size());
			right_pane.getChildren().clear();
			ComponentGraph.resetComponents();
			
		});
		
		
		
//		voltSlider.setShowTickLabels(true);
//		voltSlider.setShowTickMarks(true);
		currentSlider.setShowTickLabels(true);
		currentSlider.setShowTickMarks(true);
		
//		voltSlider.valueProperty().addListener( (ov,o,n)-> {
//			vLabel.setText(String.format("Voltage: %.2f",n));
//		});
		currentSlider.valueProperty().addListener( (ov,o,n)-> {
			cLabel.setText(String.format("Current: %.2f",n));
		});
		
		
		menu.setAlignment(Pos.CENTER);
		menu.getChildren().addAll(cLabel,currentSlider,simulateButton, resetButton);
		
	}
	
	private void addDragDetection(DragIcon dragIcon) {
		
		dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {

				// set drag event handlers on their respective objects
				base_pane.setOnDragOver(mIconDragOverRoot);
				right_pane.setOnDragOver(mIconDragOverRightPane);
				right_pane.setOnDragDropped(mIconDragDropped);
				
				// get a reference to the clicked DragIcon object
				DragIcon icn = (DragIcon) event.getSource();
				
				//begin drag ops
				mDragOverIcon.setType(icn.getType());
				mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));
            
				ClipboardContent content = new ClipboardContent();
				DragContainer container = new DragContainer();
				
				container.addData ("type", mDragOverIcon.getType().toString());
				content.put(DragContainer.AddNode, container);

				mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
				mDragOverIcon.setVisible(true);
				mDragOverIcon.setMouseTransparent(true);
				event.consume();					
			}
		});
	}
	
	
	public static double getVolts() {
		return Math.round(voltSlider.getValue()*100)/100;
	}
	
	public static double getCurrent() {
		return Math.round(100*voltSlider.getValue())/100;
	}
	
	private void buildDragHandlers() {
		
		//drag over transition to move widget form left pane to right pane
		mIconDragOverRoot = new EventHandler <DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				
				Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

				//turn on transfer mode and track in the right-pane's context 
				//if (and only if) the mouse cursor falls within the right pane's bounds.
				if (!right_pane.boundsInLocalProperty().get().contains(p)) {
					
					event.acceptTransferModes(TransferMode.ANY);
					mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}

				event.consume();
			}
		};
		
		mIconDragOverRightPane = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				event.acceptTransferModes(TransferMode.ANY);
				
				//convert the mouse coordinates to scene coordinates,
				//then convert back to coordinates that are relative to 
				//the parent of mDragIcon.  Since mDragIcon is a child of the root
				//pane, coodinates must be in the root pane's coordinate system to work
				//properly.
				mDragOverIcon.relocateToPoint(
								new Point2D(event.getSceneX(), event.getSceneY())
				);
				event.consume();
			}
		};
				
		mIconDragDropped = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {
				
				DragContainer container = 
						(DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
				
				container.addData("scene_coords", 
						new Point2D(event.getSceneX(), event.getSceneY()));
				
				ClipboardContent content = new ClipboardContent();
				content.put(DragContainer.AddNode, container);
				
				event.getDragboard().setContent(content);
				event.setDropCompleted(true);
			}
		};
		
		this.setOnDragDone (new EventHandler <DragEvent> (){
			
			@Override
			public void handle (DragEvent event) {
				
				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
				right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
				base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);
								
				mDragOverIcon.setVisible(false);
				
				//Create node drag operation
				DragContainer container = 
						(DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
				
				if (container != null) {
					if (container.getValue("scene_coords") != null) {
					
							DraggableNode node = new DraggableNode();
							System.out.println(DragIconType.valueOf(container.getValue("type")));
							node.setType(DragIconType.valueOf(container.getValue("type")));
							right_pane.getChildren().add(node);
	
							Point2D cursorPoint = container.getValue("scene_coords");
	
							node.relocateToPoint(
									new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
									);
							ComponentGraph.components.put(node.getId(), new Component(node.getId(), DragIconType.valueOf(container.getValue("type")), !node.isOff, node));
					}
				}
				/*
				//Move node drag operation
				container = 
						(DragContainer) event.getDragboard().getContent(DragContainer.DragNode);
				
				if (container != null) {
					if (container.getValue("type") != null)
						System.out.println ("Moved node " + container.getValue("type"));
				}
				*/

				//AddLink drag operation
				container =
						(DragContainer) event.getDragboard().getContent(DragContainer.AddLink);
				
				if (container != null) {
					
					//bind the ends of our link to the nodes whose id's are stored in the drag container
					String sourceId = container.getValue("source");
					String targetId = container.getValue("target");

					if (sourceId != null && targetId != null) {
						
						//	System.out.println(container.getData());
						NodeLink link = new NodeLink();
						
						//add our link at the top of the rendering order so it's rendered first
						right_pane.getChildren().add(0,link);
						
						DraggableNode source = null;
						DraggableNode target = null;
						
						for (Node n: right_pane.getChildren()) {
							
							if (n.getId() == null)
								continue;
							
							if (n.getId().equals(sourceId))
								source = (DraggableNode) n;
						
							if (n.getId().equals(targetId))
								target = (DraggableNode) n;
							
						}
						
						if (source != null && target != null) {
							link.bindEnds(source, target);
							ComponentGraph.components.get(sourceId).addComponent(ComponentGraph.components.get(targetId));
						}
							
					}
						
				}

				event.consume();
			}
		});		
	}
}
