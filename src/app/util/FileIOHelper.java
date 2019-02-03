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
    File[] listOfFiles = imageFolder.listFiles((File dir, String name) -> name.endsWith(".png") || name.endsWith(".jpg") );

    List<ImageView> imageViews = new ArrayList<>();
    for (final File imageFile : listOfFiles)
    {
      PreviewImage image = createPreviewImage(imageFile);
      ImageView imageView = createImageView(image);
      imageViews.add(imageView);
    }

    return imageViews;
  }

  private static PreviewImage createPreviewImage(File imageFile)
  {
    try
    {
      byte[] bytes = Files.readAllBytes(Paths.get(imageFile.getAbsolutePath()));
      InputStream iStream = new ByteArrayInputStream(bytes);

      return new PreviewImage(iStream, imageFile);
    }
    catch (IOException exce)
    {
      exce.printStackTrace();
      throw new RuntimeException(exce);
    }
  }

  private static ImageView createImageView(PreviewImage image)
  {
    ImageView imageView = new ImageView(image);
    imageView.setFitHeight(80);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setCache(true);
    return imageView;
  }
}
