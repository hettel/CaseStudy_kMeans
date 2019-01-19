package app.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.ImageView;

public final class FileIOHelper
{
  private FileIOHelper()
  {
  }

  public static List<ImageView> loadPreViewImages(File imageFolder)
  {
    File[] listOfFiles = imageFolder.listFiles( (File dir, String name) -> name.endsWith(".jpg") );

    List<ImageView> imageViews = new ArrayList<>();
    try
    {
      for (final File imageFile : listOfFiles)
      {
        byte[] bytes = Files.readAllBytes(Paths.get(imageFile.getAbsolutePath()));
        InputStream iStream = new ByteArrayInputStream(bytes);

        PreviewImage image = new PreviewImage(iStream, imageFile);

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageViews.add(imageView);
      }
    }
    catch (IOException exce)
    {
      exce.printStackTrace();
    }

    return imageViews;
  }
}
