package kmeans.imagedata.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import kmeans.image.PixelData;

public final class Tools
{
  private Tools() {}
  
  
  public static KMeansImage getKMeanImage(Image image)
  {
    PixelReader pReader = image.getPixelReader();    
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    
    List<PixelData> dataSet = new ArrayList<>(width*height);
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        int pixel = pReader.getArgb(x, y);
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        dataSet.add( new PixelData(x, y, alpha, red, green, blue) );
      }
    }
    
    return new KMeansImage(width, height, dataSet);
  }
  
  public static KMeansImage loadDataSetFromImage(File file) throws IOException
  {
    //JPG-Bild einlesen
    BufferedImage image = ImageIO.read(file);

    int width = image.getWidth();
    int height = image.getHeight();
    
    List<PixelData> dataSet = new ArrayList<>(width*height);
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        int pixel = image.getRGB(x, y);
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        dataSet.add( new PixelData(x, y, alpha, red, green, blue) );
      }
    }
    
    return new KMeansImage(width, height, dataSet);
  }
  
  public static  void storeDataSetFromImage(File file, KMeansImage image) throws IOException
  { 
    BufferedImage imageOut = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
    
    image.pixels.stream().forEach( data -> {
      int rgb = data.getRgbColor();
      imageOut.setRGB(data.x, data.y, rgb);
    } );
    
    ImageIO.write(imageOut, "jpg", file);
    
    return;
  }
}
