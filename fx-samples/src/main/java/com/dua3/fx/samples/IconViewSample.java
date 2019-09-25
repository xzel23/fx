package com.dua3.fx.samples;

import com.dua3.fx.icons.IconUtil;
import com.dua3.fx.icons.IconView;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class IconViewSample extends Application {

    public static void main(String[] args) {
        System.out.println("available icon providers: "+ IconUtil.iconProviders());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        IconView iv = new IconView("fa-exclamation-triangle");

        StackPane root = new StackPane();
        root.getChildren().add(iv);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

