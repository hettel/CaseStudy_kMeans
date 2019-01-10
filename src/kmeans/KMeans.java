package kmeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import kmeans.image.Centroid;
import kmeans.image.PixelData;
import kmeans.imagedata.util.KMeansImage;

public final class KMeans
{
  private KMeans()
  {
  };

  /**
   * Reduces the colors of the image using the k-means algorithm.  
   * k corresponds to the number of colors used after image transformation.
   * Attention, the image will be changed (in place reduction).  
   * 
   * @param image - image to reduce
   * @param k - number of clusters (main colors)
   * @return Collection of Centroids (main colors)
   */
  public static Collection<Centroid> pack(KMeansImage image, final int k)
  {
    System.out.println("Start k-Means");
    long time = System.currentTimeMillis();
    
    Map<Centroid, List<PixelData>> clusterMap = buildCluster(image, k);
    
    // Assign every pixel the new color
    for( Entry<Centroid, List<PixelData>>  entry : clusterMap.entrySet() )
    {
      int rgbColor = entry.getKey().getRgbColor();
      for(PixelData datapoint : entry.getValue() )
      {
        datapoint.setRgbColor( rgbColor );
      }
    }
   
    System.out.println("done " + (System.currentTimeMillis() - time) + " [ms]");
    return clusterMap.keySet();
  }

  private static Map<Centroid, List<PixelData>> buildCluster(KMeansImage image, final int k)
  {   
    // Randomly assign every data point (image pixel) a cluster
    List<PixelData> dataSet = image.pixels;
    for(PixelData pixel :  dataSet)
    {
      pixel.centroidId = ThreadLocalRandom.current().nextInt(k);
    }
    
    // count variable for data points that change a cluster
    int changes;
    Centroid[] centroids;
    do
    {
      changes = 0;
      
      // Calculate mean for every cluster
      centroids = getNewCentroids(dataSet, k);
      
      for(PixelData dataPoint : dataSet)
      {
        // Get the nearest cluster for data point
        int newClusterID = getNearestCentroid(dataPoint, centroids);
        
        // Check if data point has to change the cluster
        if (dataPoint.centroidId != newClusterID)
        {
          dataPoint.centroidId = newClusterID;
          changes++;
        }
      }
    }
    while (changes != 0);

    // Create Map: Centroid ==> List<PixelData>
    Map<Centroid, List<PixelData>> clusterMap = new HashMap<>();
    for(PixelData pixel : dataSet )
    {
      Centroid centroid = centroids[ pixel.centroidId ];
      List<PixelData> clusterSet = clusterMap.get(centroid);
      if( clusterSet == null )
      {
        clusterSet = new ArrayList<>();
        clusterMap.put(centroid, clusterSet);
      }
      clusterSet.add(pixel);
    }
    
    return clusterMap;
  }
  


  private static double rgbDistance(PixelData c1, Centroid c2)
  {
    double rDif = c1.red - c2.red;
    double gDif = c1.green - c2.green;
    double bDif = c1.blue - c2.blue;

    // In general we don't need the square root by comparing distances
    // because we are only interested in relative relations
    return Math.sqrt(rDif * rDif + gDif * gDif + bDif * bDif);
  }

  
  // Calculates means in an in a non performant way
  // We can do better
  private static Centroid[] getNewCentroids(List<PixelData> dataList, final int k)
  { 
    Centroid[] centroids = new Centroid[k];
    
    for(int clusterID=0; clusterID < k; clusterID++)
    {
      double alpha = 0;
      double red = 0;
      double green = 0;
      double blue = 0;
      double count = 0;
      
      for(PixelData dataPoint : dataList )
      {
        if( dataPoint.centroidId == clusterID )
        {
          alpha += dataPoint.alpha;
          red   += dataPoint.red;
          green += dataPoint.green;
          blue  += dataPoint.blue;
          count++;
        }
      }
      
      if( count == 0 )
      {
        System.out.println("Empty CLuster found");
        continue;
      }
      
      Centroid centroid = new Centroid(alpha/count, red/count, green/count, blue/count);
      centroids[clusterID] = centroid;
    }
    
    return centroids;
  }

  private static int getNearestCentroid(PixelData pixel, Centroid[] centroids)
  {    
    assert centroids.length > 0;
    
    int nearestClusterId = 0;
    double minDistance = rgbDistance(pixel, centroids[0]);
    for (int i = 1; i < centroids.length; i++)
    {
      double distance = rgbDistance(pixel, centroids[i]);
      if( distance < minDistance )
      {
        minDistance = distance;
        nearestClusterId = i;
      }
    }
    
    return nearestClusterId;
  }
}
