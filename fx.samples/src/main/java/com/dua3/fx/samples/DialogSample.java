package com.dua3.fx.samples;

import java.util.List;
import java.util.Optional;

import com.dua3.fx.util.Dialogs;
import com.dua3.utility.io.CsvIo;

import javafx.application.Application;
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
    btn.setPrefWidth(120);
    return btn;
  }

  @Override
  public void start(Stage primaryStage) {
    VBox container = new VBox();

    // Information
    container.getChildren().add(createButton("Info", () -> {
      Dialogs.information()
          .title("Info")
          .header("This is an information.")
          .text("And this is an additional text providing details.")
          .showAndWait();
    }));

    // Confirmation
    container.getChildren().add(createButton("Confirmation", () -> {
      Dialogs.confirmation()
          .title("Confimration")
          .header("This is a conformation dialog.")
          .text("And this is an additional text providing details.")
          .showAndWait();
    }));

    // Prompt
    container.getChildren().add(createButton("Prompt", () -> {
      Dialogs.prompt()
          .title("Prompt")
          .header("This is a prompt dialog.")
          .text("Enter txt here: ")
          .showAndWait();
    }));

    // Input
    container.getChildren().add(createButton("Input", () -> {
      Dialogs.input()
          .title("Input")
          .header("This is an input dialog.")
          .text("Enter data here: ")
          .text("txt", "enter text", "dflt")
          .integer("integer", "enter number", 0)
          .integer("integer from 4 to 7", "enter number", 0,
              i -> i >= 4 && i <= 7 ? Optional.empty() : Optional.of(i + " is not between 4 and 7"))
          .comboBox("list", "choose one", "Maybe", String.class, List.of("Yes", "No", "Maybe"))
          .checkBox("bool", "Yes or No:", false, "yes")
          .showAndWait();
    }));

    // Options
    container.getChildren().add(createButton("Options", () -> {
      var rc = Dialogs.options()
          .options(CsvIo.getCommonOptions())
          .title("Options")
          .header("This is an options dialog.")
          .showAndWait();
      rc.ifPresent(ops -> System.out.println("RESULT:\n" + ops));
    }));

    // Wizard
    container.getChildren().add(createButton("Wizard", () -> {
      var rc = Dialogs.wizard()
          .title("Database Connection Wizard")
          .page("start",
              Dialogs.informationPane()
                  .header("Create new database connection")
                  .text(
                      "This wizard helps you to define a new database conection.%n" +
                          "%n" +
                          "You will need the following information:%n" +
                          "- the vendor or manufacturer name of your database system%n" +
                          "- the server name and port"))
          .page("dbms",
              Dialogs.inputPane()
                  .header("Choose your Database from the list below.")
                  .radioList("rdmbs", "Database", null, String.class, "H2", "PostgreSQL", "MySQL")
          		  .resultHandler((p,b) -> true))
          .showAndWait();
      System.out.format("Dialog result:%n%s%n", rc);
    }));

    StackPane root = new StackPane(container);

    Scene scene = new Scene(root);

    primaryStage.setTitle("Dialogs");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
