package application;


import java.io.IOException;

import application.util.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class DragIcon extends AnchorPane{
	
	
	private Image bulbImage = ImageLoader.bulbImage;
	private Image ammeterImage = ImageLoader.ammeterImage;
	private Image junctionImage = ImageLoader.junctionImage;
	private Image switchImage = ImageLoader.switchImage;
	private Image batteryImage = ImageLoader.batteryImage;
	
	
	@FXML AnchorPane root_pane;

	private DragIconType mType = null;
	
	public DragIcon() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/DragIcon.fxml")
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
	private void initialize() {}
	
	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		Point2D localCoords = getParent().sceneToLocal(p);
		
		relocate ( 
				(int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
				(int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
			);
	}
	
	public DragIconType getType () { return mType; }
	
	public void setType (DragIconType type) {
		
		mType = type;
		
		getStyleClass().clear();
		getStyleClass().add("dragicon");
		
		//added because the cubic curve will persist into other icons
		if (this.getChildren().size() > 0)
			getChildren().clear();
		
		ImageView iv = new ImageView();
		iv.setFitHeight(35);
		iv.setFitWidth(65);
		iv.setLayoutX(0);
		iv.setLayoutY(5);
		
		Label title_bar = new Label();
		title_bar.setAlignment(Pos.BOTTOM_RIGHT);
		title_bar.setLayoutY(40);
		title_bar.setLayoutX(10);
		title_bar.setStyle("-fx-font-weight: bold");
//		iv.setPreserveRatio(false);
		switch (mType) {
		
		
		case BATTERY:
			iv.setImage(this.batteryImage);							
			title_bar.setText("Battery");
			this.getChildren().add(iv);

			getStyleClass().add("icon-white");
//			getStyleClass().add("icon-blue");			
		break;
		case BULB:
			iv.setImage(this.bulbImage);							
			title_bar.setText("Bulb");
			this.getChildren().add(iv);
			getStyleClass().add("icon-white");
//			getStyleClass().add("icon-yellow");
		break;
			
		case JUNCTION:
			iv.setImage(this.junctionImage);							
			title_bar.setText("Junction");
			this.getChildren().add(iv);
			getStyleClass().add("icon-white");
//			this.getChildren().add(new ImageView(this.junctionImage));
//			getStyleClass().add("icon-grey");			
		break;
		case AMMETER:
			iv.setImage(this.ammeterImage);							
			title_bar.setText("Ammeter");
			this.getChildren().add(iv);
			getStyleClass().add("icon-white");
//			this.getChildren().add(new ImageView(this.ammeterImage));
//			getStyleClass().add("icon-purple");
		break;
		
		case SWITCH:
			iv.setImage(this.switchImage);
			title_bar.setText("Switch");
			this.getChildren().add(iv);
			getStyleClass().add("icon-white");
			//				this.getChildren().add(new ImageView(this.switchImage));
//			getStyleClass().add("icon-red");
		break;
		
		default:
		break;
		
		
		}
		this.getChildren().add(title_bar);
		
	}
}
