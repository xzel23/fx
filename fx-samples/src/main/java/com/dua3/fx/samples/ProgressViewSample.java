package com.dua3.fx.samples;

import com.dua3.fx.controls.ProgressView;
import com.dua3.fx.icons.IconUtil;
import com.dua3.utility.concurrent.ProgressTracker;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressViewSample extends Application {

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(String[] args) {
        System.out.println("available icon providers: "+ IconUtil.iconProviders());
        launch(args);
    }
    
    record SampleTask(String name, boolean indeterminate, int max) {
        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        ProgressView<SampleTask> pv = new ProgressView<>();
        pv.setMaxWidth(Double.POSITIVE_INFINITY);
        pv.setPrefWidth(5000);
        pv.setMaxHeight(Double.POSITIVE_INFINITY);
        
        StackPane root = new StackPane(pv);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("ProgressView");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        addTask(pv, "SampleTask 1", 100, ProgressTracker.State.COMPLETED_SUCCESS);
        addTask(pv, "SampleTask 2", 50, ProgressTracker.State.COMPLETED_SUCCESS);
        addTask(pv, "SampleTask 3", -75, ProgressTracker.State.COMPLETED_SUCCESS);
    }
    
    ExecutorService pool = Executors.newFixedThreadPool(3);
    
    private void addTask(ProgressView<SampleTask> pv, String name, int steps, ProgressTracker.State s) {
        boolean indeterminate = steps < 0;
        int max = steps >= 0 ? steps : -steps;
        
        SampleTask task = new SampleTask(name, indeterminate, max);
        
        pv.schedule(task);
        
        pool.submit(() -> {
            sleep(500);
           pv.start(task);
           sleep(500);
           for (int i=0;i<=max;i++) {
               if (!indeterminate) {
                   pv.update(task, max, i);
               }
               sleep(100);
           }
           pv.finish(task, s);
        });
    }
    
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

