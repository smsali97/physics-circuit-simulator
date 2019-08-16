package application.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Message {
	private static Button OK = new Button("OK");
	private static Label label;
	private static Image yellowSign = ImageLoader.warningImage;
	private static ImageView yellowLabel = new ImageView(yellowSign);

	public static void display(String text) {
		label = new Label(text);
		label.setTextFill(Color.ANTIQUEWHITE);
		label.setFont(Font.font("Helvetica"));
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Message");
		
		stage.getIcons().add(ImageLoader.errorImage);
		

		BorderPane bp = new BorderPane();
		bp.setTop(label);
		bp.setCenter(yellowLabel);
		bp.setBottom(OK);
		bp.resize(400, 600);
		BorderPane.setAlignment(OK, Pos.CENTER);

		yellowLabel.setFitHeight(50);
		yellowLabel.setFitWidth(50);

		HBox hb = new HBox(10);
		hb.getChildren().addAll(yellowLabel, label, OK);
		hb.setPadding(new Insets(10, 10, 10, 10));
		hb.setAlignment(Pos.CENTER);

		hb.setStyle("-fx-background-color: #336699;");

		OK.setStyle("-fx-background-color: #ffbb02;");

		Scene scene = new Scene(hb);

		// if button clicked
		OK.setOnAction(e -> {
			stage.close();
		});

		OK.setOnMouseEntered(e -> {
			OK.setEffect(new DropShadow());
		});

		OK.setOnMouseExited(e -> {
			OK.setEffect(new DropShadow(0, null));
		});

		yellowLabel.setOnMouseEntered(e -> {
			yellowLabel.setEffect(new DropShadow());
		});

		yellowLabel.setOnMouseExited(e -> {
			yellowLabel.setEffect(new DropShadow(0, null));
		});

		stage.setScene(scene);
		stage.showAndWait();
	}
}
