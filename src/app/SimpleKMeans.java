package app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class SimpleKMeans extends Application
{
  @Override
  public void start(Stage primaryStage)
  {
    long time = System.currentTimeMillis();
    try
    {
      BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("ui/ui.fxml"));
      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("ui/ui.css").toExternalForm());
      primaryStage.setTitle("Simple Version");
      primaryStage.setScene(scene);
      primaryStage.show();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.out.println("Application startup time : " + (System.currentTimeMillis() - time) + " [ms]");
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
