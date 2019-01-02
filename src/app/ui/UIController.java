package app.ui;

import static kmeans.KMeans.pack;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import kmeans.image.Centroid;
import kmeans.imagedata.util.KMeansImage;
import kmeans.imagedata.util.Tools;
import util.FileIOHelper;
import util.PreviewImage;
import util.hal.CpuInfoPublisher;

public class UIController implements Initializable
{
  @FXML
  private BorderPane mainWindow;

  @FXML
  private VBox kMeanImageContainer;

  @FXML
  private ImageView mainImageView;

  @FXML
  private ImageView kmeanImageView;

  @FXML
  private HBox imageBox;

  @FXML
  private Label color1;
  @FXML
  private Label color2;
  @FXML
  private Label color3;
  @FXML
  private Label color4;
  @FXML
  private Label color5;
  @FXML
  private Label color6;
  @FXML
  private Label color7;
  @FXML
  private Label color8;
  @FXML
  private Label color9;
  
  @FXML
  private Button startBtn;

  private Label[] colorMap;

  @FXML
  private ChoiceBox<Integer> clusterSelect;
  private ObservableList<Integer> clusterCount = FXCollections.observableArrayList();

  @FXML
  private Rectangle cpuLoadBar;
  @FXML
  private Label cpuLabel;

  @FXML
  public void open()
  {
    DirectoryChooser dirChooser = new DirectoryChooser();
    dirChooser.setTitle("Select Gallery");
    dirChooser.setInitialDirectory(Paths.get("./gallery").toFile());
    File file = dirChooser.showDialog(mainWindow.getScene().getWindow());

    if (file != null && file.isDirectory())
    {
      // Delete old image content
      // mainImageView.setImage(null);
      imageBox.getChildren().clear();

      long time = System.currentTimeMillis();
      List<ImageView> imageViews = FileIOHelper.loadPreViewImages(file);
      System.out.println("Time to load preview images : " + (System.currentTimeMillis() - time) + "[ms]");
      
      for (ImageView imageView : imageViews)
      {
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
          @Override
          public void handle(MouseEvent mouseEvent)
          {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
            {
              if (mouseEvent.getClickCount() == 1)
              {
                File file = ((PreviewImage) imageView.getImage()).getFile();
                showImage(file);
              }
            }
          }
        });
      }

      this.imageBox.getChildren().addAll(imageViews);
    }
  }

  @FXML
  public void saveAs()
  {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save File");
    fileChooser.setInitialDirectory(Paths.get(".").toFile());
    File file = fileChooser.showSaveDialog(mainWindow.getScene().getWindow());

    if (this.kmeanImageView == null || this.kmeanImageView.getImage() == null)
      return;

    try
    {
      Image image = this.kmeanImageView.getImage();
      BufferedImage outImage = this.getBufferedImage(image);
      ImageIO.write(outImage, "jpg", file);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  @FXML
  public void upload()
  {
    if (this.kmeanImageView == null || this.kmeanImageView.getImage() == null)
      return;

    Image image = this.kmeanImageView.getImage();

    try
    {
      uploadImageToService(image);
    }
    catch (Exception exce)
    {
      this.showDialog("Sorry, Exception occured");
    }
  }

  @FXML
  public void applyKMean()
  {
    if (this.mainImageView.getImage() == null)
      return;

    showProgressIndicator();
    
    int k = clusterSelect.getSelectionModel().getSelectedItem();

    KMeansImage imageCopy = Tools.getKMeanImage(this.mainImageView.getImage());

    System.out.println("Amount of pixels : " + imageCopy.getPixelCount());

    Collection<Centroid> centroids = pack(imageCopy, k);
    showColorMap(centroids);

    BufferedImage imageOut = new BufferedImage(imageCopy.width, imageCopy.height, BufferedImage.TYPE_INT_RGB);
    imageCopy.pixels.stream().forEach(data -> {
      int rgb = (data.alpha << 24) | (data.red << 16) | (data.green << 8) | data.blue;
      imageOut.setRGB(data.x, data.y, rgb);
    });
    this.kmeanImageView.setImage(SwingFXUtils.toFXImage(imageOut, null));
    
    closeProgressIndicator();
  }

  @FXML
  public void exit()
  {
    Platform.exit();
  }

  private CpuInfoPublisher publisher;
  private DecimalFormat df = new DecimalFormat("#0.0");

  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    clusterCount.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9);
    clusterSelect.setItems(clusterCount);
    clusterSelect.getSelectionModel().selectFirst();
    this.colorMap = new Label[] { color1, color2, color3, color4, color5, color6, color7, color8, color9 };
    cpuLoadBar.setFill(Color.GREEN);

    publisher = CpuInfoPublisher.getInstance();
    publisher.subscribe(value -> Platform.runLater(() -> {
      repaintGradient(value);
      cpuLabel.setText(df.format(100 * value) + " %");
    }));
  }

  // Farbgradienten für Statusbalken
  private final Stop[] stops = new Stop[] { new Stop(0, Color.GREEN), new Stop(1, Color.RED) };

  private void repaintGradient(double value)
  {
    LinearGradient linGradient = new LinearGradient(0, 0, 0, 2 * (1 - value) * cpuLoadBar.getHeight(), false, CycleMethod.NO_CYCLE, stops);
    cpuLoadBar.setFill(linGradient);
  }

  private void showImage(File file)
  {
    try
    {
      byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
      InputStream iStream = new ByteArrayInputStream(bytes);
      mainImageView.setImage(new Image(iStream));
      mainImageView.setPreserveRatio(true);
      mainImageView.setSmooth(true);
      mainImageView.setCache(true);

      iStream = new ByteArrayInputStream(bytes);
      kmeanImageView.setImage(new Image(iStream));
      kmeanImageView.setPreserveRatio(true);
      kmeanImageView.setSmooth(true);
      kmeanImageView.setCache(true);
    }
    catch (IOException exce)
    {
      exce.printStackTrace();
    }
  }

  private void showDialog(String text)
  {
    Alert error = new Alert(AlertType.ERROR);
    error.setTitle("ERROR");
    error.setContentText(text);

    DialogPane dialogPane = error.getDialogPane();
    dialogPane.getStylesheets().addAll(this.mainWindow.getScene().getStylesheets());

    error.showAndWait();
  }

  private void showColorMap(Collection<Centroid> centroids)
  {
    Color cornsilk = new Color(255 / 255.0, 248 / 255.0, 220 / 255.0, 1.0);
    for (Label colorBox : colorMap)
    {
      colorBox.setBackground(new Background(new BackgroundFill(cornsilk, null, new Insets(1))));
      colorBox.setStyle("-fx-border-color: cornsilk;");
    }

    int index = 0;
    try
    {
      for (Centroid centroid : centroids)
      {
        double red = centroid.red;
        double green = centroid.green;
        double blue = centroid.blue;

        Color color = new Color(red / 255, green / 255, blue / 255, 1.0);
        colorMap[index].setBackground(new Background(new BackgroundFill(color, null, new Insets(1))));
        colorMap[index].setStyle("-fx-border-color: black;");
        index++;
      }
    }
    catch (Exception exce)
    {
      exce.printStackTrace();
    }
  }

  private BufferedImage getBufferedImage(Image image)
  {
    PixelReader pReader = image.getPixelReader();
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();

    BufferedImage imageOut = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        int rgb = pReader.getArgb(x, y);
        imageOut.setRGB(x, y, rgb);
      }
    }

    return imageOut;
  }

  private boolean uploadImageToService(Image image)
  {
    BufferedImage imageOut = this.getBufferedImage(image);

    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(imageOut, "jpg", baos);
      baos.flush();

      byte[] content = baos.toByteArray();

      URL url = new URL("http://localhost:8080/service/upload");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setUseCaches(false);
      connection.setRequestProperty("Content-Type", "image/jpg");
      connection.setRequestProperty("Content-Length", String.valueOf(content.length));

      DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
      writer.write(content);
      writer.flush();

      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

      for (String line; (line = reader.readLine()) != null;)
      {
        System.out.println(line);
      }

      writer.close();
      reader.close();

      System.out.println("Upload done");
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }

    return true;
  }

  private BorderPane progress;

  private void showProgressIndicator()
  {
    try
    {
      progress = FXMLLoader.load(getClass().getResource("progress.fxml"));
      progress.getStylesheets().add(getClass().getResource("ui.css").toExternalForm());

      this.startBtn.setDisable(true);
      this.kMeanImageContainer.getChildren().remove(kmeanImageView);
      this.kMeanImageContainer.getChildren().add(progress);
    }
    catch (IOException exce)
    {
      exce.printStackTrace();
    }
  }

  private void closeProgressIndicator()
  {
    this.startBtn.setDisable(false);
    if (progress != null)
    {
      this.kMeanImageContainer.getChildren().remove(progress);
      this.kMeanImageContainer.getChildren().add(kmeanImageView);
      this.progress = null;
    }
  }
}
