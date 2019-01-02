package kmeans.imagedata.util;

import java.util.List;
import java.util.stream.Collectors;

import kmeans.image.PixelData;

public class KMeansImage
{
  public final int width;
  public final int height;
  public final List<PixelData> pixels;
  
  public KMeansImage(int width, int height, List<PixelData> pixels)
  {
    this.width = width;
    this.height = height;
    this.pixels = pixels;
  }
  
  public int getPixelCount()
  {
    return this.pixels.size();
  }
  
  public KMeansImage copy()
  {
    List<PixelData> pixelCopy = this.pixels.parallelStream().map( p -> p.copy() ).collect( Collectors.toList() );
    return new KMeansImage(this.width, this.height, pixelCopy);
  }
}
