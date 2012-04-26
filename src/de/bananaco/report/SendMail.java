package de.bananaco.report;
// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import java.io.*;
import java.net.*;

/**
 * This program sends e-mail using a mailto: URL
 **/
public class SendMail {
  public static void mail(String host, String to, String from, String subject, String line) {
    try {

      // Establish a network connection for sending mail
      URL u = new URL("mailto:" + to);      // Create a mailto: URL 
      URLConnection c = u.openConnection(); // Create a URLConnection for it
      c.setDoInput(false);                  // Specify no input from this URL
      c.setDoOutput(true);                  // Specify we'll do output
      System.out.println("Connecting...");  // Tell the user what's happening
      System.out.flush();                   // Tell them right now
      c.connect();                          // Connect to mail host
      PrintWriter out =                     // Get output stream to mail host
        new PrintWriter(new OutputStreamWriter(c.getOutputStream()));

      // Write out mail headers.  Don't let users fake the From address
      out.println("From: \"" + from + "\" <" +
                  System.getProperty("user.name") + "@" + 
                  InetAddress.getLocalHost().getHostName() + ">");
      out.println("To: " + to);
      out.println("Subject: " + subject);
      out.println();  // blank line to end the list of headers

      // Now ask the user to enter the body of the message
      System.out.println("Enter the message. " + 
                         "End with a '.' on a line by itself.");
      // Print our message
      out.println(line);

      // Close the stream to terminate the message 
      out.close();
      // Tell the user it was successfully sent.
      System.out.println("Message sent.");
      System.out.flush();
    }
    catch (Exception e) {  // Handle any exceptions, print error message.
      System.err.println(e);
      System.err.println("Is there a mail server setup on this host?");
    }
  }
}
