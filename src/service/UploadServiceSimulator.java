package service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public final class UploadServiceSimulator
{
  private UploadServiceSimulator()
  {
  }

  private static Server jettyServer;

  public static void start()
  {
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    context.setContextPath("/service");
    context.addServlet(FileUploadServlet.class, "/");

    jettyServer = new Server(8080);
    jettyServer.setHandler(context);

    try
    {
      jettyServer.start();
      System.out.println("Upload-Server ist running and waiting");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void shutdown()
  {
    try
    {
      jettyServer.stop();
      jettyServer.destroy();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
