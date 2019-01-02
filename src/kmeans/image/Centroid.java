package kmeans.image;


public class Centroid
{
  public double alpha;
  public double red,green,blue;
  
  public Centroid(double alpha, double red, double green, double blue)
  {
    super();
    this.alpha = alpha;
    this.red = red;
    this.green = green;
    this.blue = blue;
  }
  
  
  public int getRgbColor()
  {
    return ( (int)this.alpha << 24) | ((int)this.red << 16) | ((int)this.green << 8) | (int)this.blue;
  }
  
  
  @Override
  public String toString()
  {
    return "(rgb) = (" + red + "/" + green + "/" + blue +")";
  }
}
