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
        RichTextArea rta = new RichTextArea();

        RichTextBuilder rtb = new RichTextBuilder();
        rtb.append("Hello, ");
        rtb.push(Style.BOLD);
        rtb.push(Style.RED);
        rtb.append("world");
        rtb.pop(Style.RED);
        rtb.pop(Style.BOLD);
        rtb.append("!");
        rtb.append("\n");
        rtb.append("(This is ");
        rtb.push(Style.LINE_THROUGH);
        rtb.append("just");
        rtb.pop(Style.LINE_THROUGH);
        rtb.append(" a test.)");
        rtb.append("\n");
        rtb.push(Style.BLUE);
        rtb.append("I'm a blue boy in a blue world.\n");
        rtb.pop(Style.BLUE);
        rtb.push(Style.GREEN);
        rtb.append("Go green!\n");
        rtb.pop(Style.GREEN);
        
        rta.setText(rtb.toRichText());
        
        Scene scene = new Scene(rta, 300, 250);

        primaryStage.setTitle("RichTextArea");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

