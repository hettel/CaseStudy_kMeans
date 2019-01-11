package kmeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import kmeans.image.Centroid;
import kmeans.image.PixelData;
import kmeans.imagedata.util.KMeansImage;

public final class KMeans
{
  private KMeans()
  {
  };

  /**
   * Reduces the colors of the image using the k-means algorithm. k corresponds
   * to the number of colors used after image transformation. Attention, the
   * image will be changed (in place reduction).
   * 
   * @param image
   *          - image to reduce
   * @param k
   *          - number of clusters (main colors)
   * @return Collection of Centroids (main colors)
   */
  public static Collection<Centroid> pack(KMeansImage image, final int k)
  {
    System.out.println("Start k-Means");
    long time = System.currentTimeMillis();

    Map<Centroid, List<PixelData>> clusterMap = buildCluster(image, k);

    // Assign every pixel the new color
    for (Entry<Centroid, List<PixelData>> entry : clusterMap.entrySet())
    {
      int rgbColor = entry.getKey().getRgbColor();
      for (PixelData datapoint : entry.getValue())
      {
        datapoint.setRgbColor(rgbColor);
      }
    }

    System.out.println("done " + (System.currentTimeMillis() - time) + " [ms]");
    return clusterMap.keySet();
  }

  private static Map<Centroid, List<PixelData>> buildCluster(KMeansImage image, final int k)
  {
    List<PixelData> dataSet = image.pixels;
    
    // Get alle different colors
    List<Integer> rgbColors = dataSet.stream()
                                    .map(dp -> dp.getRgbColor() )
                                    .distinct()
                                    .sorted()
                                    .collect( Collectors.toList() );
    
    System.out.println("Amount of different colors : " + rgbColors.size() );
    int chunkSize = rgbColors.size()/k;
    
    // Determine equaly distributed colors for centroids
    Centroid[] centroids = new Centroid[k];
    for(int centroidId=0; centroidId<k; centroidId++)
    {
      int rgb = rgbColors.get(centroidId*chunkSize);
      centroids[centroidId] = new Centroid(rgb);
    }
    
    // count variable for data points that change a cluster
    int changes;
    do
    {
      changes = 0;

      for (PixelData dataPoint : dataSet)
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
      
      if( changes == 0 )
        break;
      
      // Calculate mean for every cluster and return a new array
      centroids = getNewCentroids(dataSet, centroids);
      
    }
    while (true);

    // Create Map: Centroid ==> List<PixelData>
    Map<Centroid, List<PixelData>> clusterMap = new HashMap<>();
    for (PixelData pixel : dataSet)
    {
      Centroid centroid = centroids[pixel.centroidId];
      List<PixelData> clusterSet = clusterMap.get(centroid);
      if (clusterSet == null)
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

  // Calculates means in a non performant way
  // We can do better
  private static Centroid[] getNewCentroids(List<PixelData> dataList, Centroid[] centroids)
  {
    List<Centroid> centroidList = new ArrayList<>();
 
    for (int clusterID = 0; clusterID < centroids.length; clusterID++)
    {
      double alpha = 0;
      double red = 0;
      double green = 0;
      double blue = 0;
      int count = 0;

      for (PixelData dataPoint : dataList)
      {
        if (dataPoint.centroidId == clusterID)
        {
          alpha += dataPoint.alpha;
          red += dataPoint.red;
          green += dataPoint.green;
          blue += dataPoint.blue;
          count++;
        }
      }

      if (count == 0)
      {
        System.out.println("Empty Cluster found : " + clusterID);
        continue;
      }

      Centroid centroid = new Centroid(alpha / count, red / count, green / count, blue / count);
      centroidList.add( centroid );
    }

    return centroidList.toArray(new Centroid[0]);
  }

  private static int getNearestCentroid(PixelData pixel, Centroid[] centroids)
  {
    assert centroids.length > 0;

    int nearestClusterId = 0;
    double minDistance = rgbDistance(pixel, centroids[0]);
    for (int i = 1; i < centroids.length; i++)
    {
      double distance = rgbDistance(pixel, centroids[i]);
      if (distance < minDistance)
      {
        minDistance = distance;
        nearestClusterId = i;
      }
    }

    return nearestClusterId;
  }
}
