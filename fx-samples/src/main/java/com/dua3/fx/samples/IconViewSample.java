package com.dua3.fx.samples;

import com.dua3.fx.icons.IconUtil;
import com.dua3.fx.icons.IconView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class IconViewSample extends Application {

    public static void main(String[] args) {
        System.out.println("available icon providers: "+ IconUtil.iconProviders());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        IconView iv = new IconView();
        iv.setIconIdentifier("fa-exclamation-triangle");
        iv.setIconSize(80);
        iv.setIconColor(Paint.valueOf("DARKBLUE"));

        StackPane root = new StackPane(iv);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("IconView");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

