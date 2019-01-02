package service;

import java.util.Scanner;

public class ServerMain
{

  public static void main(String[] args)
  {
    System.out.println("Start UploadService");
    UploadServiceSimulator.start();
    
    System.out.println("Press Enter to Stop Service:");
    try(Scanner scan = new Scanner(System.in))
    {
      scan.nextLine();
    }
    System.out.println("Shutdown UploadService");
    UploadServiceSimulator.shutdown();
  }

}
