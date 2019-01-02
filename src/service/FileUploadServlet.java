package service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name="UploadService",urlPatterns={"/upload"})
public class FileUploadServlet extends HttpServlet
{
  private static final int WAIT_TIME = 8; 
  
  private static final long serialVersionUID = 1L;
  private static AtomicInteger counter = new AtomicInteger();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    System.out.println("Post-Request");
    
    if( counter.getAndIncrement() %2 == 0 )
    {
      System.out.println("Start Waiting");
      this.wait( WAIT_TIME );
      System.out.println("Stop Waiting");
    }
    
    InputStream inStream = request.getInputStream();
    byte[] image = this.getBytes(inStream);
    
    String fileName = "upload_"+System.currentTimeMillis() + ".jpg";  
    System.out.println(  );
    File file = new File("uploads");
    file.mkdir();
    Files.write(Paths.get(file.getAbsolutePath(), fileName), image);
    System.out.println("Saved File " + fileName + " into " + file.getAbsolutePath());
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    PrintWriter out = response.getWriter();
    response.setContentType("text/plain");
    out.println("UploadService is working [Timestamp: " + System.currentTimeMillis() + "]");
  }
  
  private byte[] getBytes(InputStream is) throws IOException {

    int len;
    int size = 1024;
    byte[] buf;

    if (is instanceof ByteArrayInputStream) {
      size = is.available();
      buf = new byte[size];
      len = is.read(buf, 0, size);
    } else {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      buf = new byte[size];
      while ((len = is.read(buf, 0, size)) != -1)
        bos.write(buf, 0, len);
      buf = bos.toByteArray();
    }
    return buf;
  }
  
  private void wait(int seconds)
  {
    try
    {
      TimeUnit.SECONDS.sleep(seconds);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

}
