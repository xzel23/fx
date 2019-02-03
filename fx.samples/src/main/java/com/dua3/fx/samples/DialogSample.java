package com.dua3.fx.samples;

import com.dua3.fx.util.Dialogs;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** 
 * Sample Application.
 */
public class DialogSample extends Application {

    private Button createButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();

        // Information
        root.getChildren().add(createButton("Info", () -> {
            Dialogs.information()
                .title("Info")
                .header("This is an information.")
                .text("And this is an additional text providing details.")
                .showAndWait();
        }));

        // Confirmation
        root.getChildren().add(createButton("Confirmation", () -> {
            Dialogs.confirmation()
                .title("Confimration")
                .header("This is a conformation dialog.")
                .text("And this is an additional text providing details.")
                .showAndWait();
        }));

        // Prompt
        root.getChildren().add(createButton("Prompt", () -> {
            Dialogs.prompt()
                .title("Prompt")
                .header("This is a prompt dialog.")
                .text("Enter txt here: ")
                .showAndWait();
        }));

        // Input
        root.getChildren().add(createButton("Input", () -> {
            Dialogs.input()
                .title("Input")
                .header("This is an input dialog.")
                .text("Enter data here: ")
                .text("txt", "enter text", "dflt")
                .integer("integer", "enter number", 0)
                .showAndWait();
        }));

     Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 public static void main(String[] args) {
        launch(args);
    }
}
