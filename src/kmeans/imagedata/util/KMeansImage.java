package kmeans.imagedata.util;

import java.util.List;
import java.util.stream.Collectors;

import kmeans.image.PixelData;

/**
 * Abstraction for the image
 * 
 * Represent the data necessary for applying the k-means algorithm
 *
 */
public class KMeansImage
{
  public final int width;
  public final int height;
  public final List<PixelData> pixels;
  public final List<Integer> colorList;
  
  public KMeansImage(int width, int height, List<PixelData> pixels)
  {
    this.width = width;
    this.height = height;
    this.pixels = pixels;
    
    this.colorList = pixels.stream()
                      .map(dp -> dp.getRgbColor() )
                      .distinct()
                      .sorted()
                      .collect( Collectors.toUnmodifiableList() );
  }
  
  public List<Integer> getSortedRgbColors()
  {
    return this.colorList;
  }
  
  public int getColorCount()
  {
    return this.colorList.size();
  }
  
  public int getPixelCount()
  {
    return this.pixels.size();
  }
  
}
