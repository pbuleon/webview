package com.pbup.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class FXApp extends Application {

	private DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	private Label label;

	// A Bridge class and must a public class
	public class Bridge {

		public void printReply(String val) {
			System.out.println("printReply");

			label.setText("message received : " + val);
		}
	}

	@Override
	public void start(Stage stage) throws Exception {

		label = new Label("-");

		Button button = new Button("set weview bacgrounf yellow");

		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();

		// Enable Javascript.
		webEngine.setJavaScriptEnabled(true);

		// A Worker load the page
		Worker<Void> worker = webEngine.getLoadWorker();
		// Listening to the status of worker
		worker.stateProperty().addListener(new ChangeListener<State>() {

			@Override
			public void changed(ObservableValue<? extends State> observable, //
					State oldValue, State newValue) {

				// When load successed.
				if (newValue == Worker.State.SUCCEEDED) {
					// Get window object of page.
					JSObject jsobj = (JSObject) webEngine.executeScript("window");

					// Set member for 'window' object.
					// In Javascript access: window.myJavaMember....
					jsobj.setMember("myJavaMember", new Bridge());
				}
			}
		});

		webEngine.load("http://127.0.0.1");

		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// Call a JavaScript function of the current page
				webEngine.executeScript("changeBgColor();");
			}
		});

		VBox root = new VBox();
		root.setPadding(new Insets(5));
		root.setSpacing(5);
		root.getChildren().addAll(label, button, browser);

		Scene scene = new Scene(root);

		stage.setTitle("JavaFX WebView");
		stage.setScene(scene);
		stage.setWidth(450);
		stage.setHeight(300);

		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
