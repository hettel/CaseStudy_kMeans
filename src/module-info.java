/**
 * 
 */
module kMeansCaseStudy
{
  opens app.ui to javafx.graphics,javafx.fxml;
  opens app to javafx.graphics,javafx.fxml;
  
  requires java.desktop;
  requires javafx.graphics;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.swing;
  requires oshi.core;
  
  requires transitive javax.servlet.api;
  requires org.eclipse.jetty.server;
  requires org.eclipse.jetty.util;
  requires org.eclipse.jetty.servlet;
}