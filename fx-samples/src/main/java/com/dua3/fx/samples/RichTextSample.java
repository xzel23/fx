package com.dua3.fx.samples;

import com.dua3.fx.editor.RichTextArea;
import com.dua3.fx.icons.IconUtil;
import com.dua3.fx.icons.IconView;
import com.dua3.utility.text.Font;
import com.dua3.utility.text.RichText;
import com.dua3.utility.text.RichTextBuilder;
import com.dua3.utility.text.Style;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class RichTextSample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        RichTextArea rta = new RichTextArea(new Font("Arial-18.0"));

        RichTextBuilder rtb = new RichTextBuilder();
        rtb.append("Hello, ");
        rtb.push(Style.BOLD);
        rtb.append("world");
        rtb.pop(Style.BOLD);
        rtb.append("!");
        
        rta.setText(rtb.toRichText());
        
        Scene scene = new Scene(rta, 300, 250);

        primaryStage.setTitle("RichTextArea");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

