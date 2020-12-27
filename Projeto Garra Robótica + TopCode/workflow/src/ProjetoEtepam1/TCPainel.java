package ProjetoEtepam1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.videoInputLib.videoInput;

import topcodes.TopCode;



public class TCPainel extends JPanel implements Runnable
{
  // dimensions of panel == dimensions of webcam image
  private static final int WIDTH = 640;  
  private static final int HEIGHT = 480;

  private static final int DELAY = 50;  // time (ms) between redraws of the panel

  private static final int CAMERA_ID = 1;


  //Iniciando Comunicação com Arduino (Biblioteca RXTX)
  private static CommArduino gui1 = new CommArduino("COM8", 9600);

  private static final double DIST_DIA = 210 * 70;   
     /* for my camera a topcode has diameter 70 pixels when it is
        full-size, and this occurs when my hand is 210 mm from the camera,
        so a diameter is mapped to a z-distance using 210*70 
     */

  private BufferedImage im = null;    // current webcam image
  private volatile boolean isRunning;
  private volatile boolean isFinished;

  // topcode variables
  private topcodes.Scanner scanner;
  private java.util.List<TopCode> topCodes = null;

 
  public Dimension getPreferredSize()   {   return new Dimension(WIDTH, HEIGHT); }

  public TCPainel() {
	  														//Propriedades do Background TCPainel
	setBackground(Color.white);
															//Procurar por TopCodes na Imagem (Escanear)
    scanner = new topcodes.Scanner();
     														//Atualizar a imagem do Painel em Thread
    														//Thread -> Ultilizando uma nova linha de código
    new Thread(this).start();  
  }


  public void run() { 
    FrameGrabber grabber = initGrabber(CAMERA_ID);
    if (grabber == null)
      return;

    IplImage snapIm;
    long duration;
    isRunning = true;
    isFinished = false;

    while (isRunning) {
      long startTime = System.currentTimeMillis();

      snapIm = picGrab(grabber, CAMERA_ID); 
      im = snapIm.getBufferedImage();
      topCodes = scanner.scan(im);  // find topcodes in the image
      trackFingers(topCodes);
      repaint();

      duration = System.currentTimeMillis() - startTime;
      if (duration < DELAY) {
        try {
          Thread.sleep(DELAY-duration);  // wait until DELAY time has passed
        } 
        catch (Exception ex) {}
      }
    }
    closeGrabber(grabber, CAMERA_ID);
    System.out.println("Execution terminated");
    isFinished = true;
  }  // end of run()


  private FrameGrabber initGrabber(int ID)  {
    FrameGrabber grabber = null;
    System.out.println("Initializing grabber for " + videoInput.getDeviceName(ID) + " ...");
    try {
      grabber = FrameGrabber.createDefault(ID);
      grabber.setFormat("dshow");       // using DirectShow
      grabber.setImageWidth(WIDTH);     // default is too small: 320x240
      grabber.setImageHeight(HEIGHT);
      grabber.start();
    }
    catch(Exception e) 
    {  System.out.println("Could not start grabber");  
       System.out.println(e);
       System.exit(1);
    }
    return grabber;
  }  // end of initGrabber()


  private IplImage picGrab(FrameGrabber grabber, int ID)  {
    IplImage im = null;
    try {
      im = grabber.grab();  // take a snap
    }
    catch(Exception e) 
    {  System.out.println("Problem grabbing image for camera " + ID);  }
    return im;
  }  // end of picGrab()



  private void closeGrabber(FrameGrabber grabber, int ID) {
    try {
      grabber.stop();
      grabber.release();
    }
    catch(Exception e) 
    {  System.out.println("Problem stopping grabbing for camera " + ID);  }
  }  // end of closeGrabber()



  // ----------------- finger tracking -------------------------

  private void trackFingers(java.util.List<TopCode> topCodes) 
  {
    for (TopCode tc : topCodes) {
      int id = tc.getCode();

  } 
  }// end of trackFingers()



  // ------------------------- painting ----------------------------


  public void paintComponent(Graphics g)
  // draw the webcam image and all the topcodes
  { 
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                       RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    if (im != null) {
      g2.drawImage(im, 0, 0, this);
      drawTopCodes(g2, topCodes);
    }
    else
      g2.drawString("Loading from camera " + CAMERA_ID + "...", 20, HEIGHT/2);
  } // end of paintComponent()



  private void drawTopCodes(Graphics2D g2,
                            java.util.List<TopCode> topCodes)
  // draw all the topcodes
  {
    if ((topCodes == null) || (topCodes.size() == 0))   // no topcodes
      return;

    for (TopCode tc : topCodes) {
      tc.draw(g2);    // draw a topcode image at its location on the image
      drawID(g2, tc);
      drawPos(g2, tc);     // draw (x,y,z) and orientation line
    }
  }  // end of drawTopCodes()



  private void drawID(Graphics2D g2, TopCode tc)
  // draw the topcode ID number in a text box below the topcode image
  {
    String idStr = String.valueOf( tc.getCode() );
    int y = (int)(tc.getCenterY() + tc.getDiameter()*0.7 + 8);

    drawTextBox(g2, idStr, (int)tc.getCenterX(), y);
  }  // end of drawID()




  private void drawTextBox(Graphics2D g2, String msg, int x, int y)
  // draw the msg in black inside a white box centered at (x,y)
  {
    int strWidth = g2.getFontMetrics().stringWidth(msg);

    g2.setColor(Color.WHITE);    // white box
    g2.fillRect( x - strWidth/2 - 3,  y - 16, strWidth + 6, 16);

    g2.setColor(Color.BLACK);    // black text
    g2.drawString(msg, x - strWidth/2, y - 4);
  }  // end of drawTextBox()



  private void drawPos(Graphics2D g2, TopCode tc)
  /* draw the (x,y,z) coordinate and angle to the vertical for 
     the topcode; (x, y) are pixel positions in the webcam image, 
     but z is a millimeter distance from the camera; the angle is
     in degrees, and measured clockwise from straight up.
  */
  {
    int xc = (int) tc.getCenterX();    // in pixels
    int yc = (int) tc.getCenterY();
    
    int cx = xc/4;
    
    
    int zDist = -1;
    float dia = tc.getDiameter();   // diameter in pixels
    if (dia > 0)
      zDist = (int)(DIST_DIA / dia);   // z-distance in mm from camera

    int angle = 360 + (int) Math.toDegrees(tc.getOrientation());
             // since topcodes orientation varies from -360 to 3
    angle = angle % 360;

    // calculate a rotated point using the angle and (xc, yc)
    int xEnd = xc + (int)( 0.9 * dia * Math.sin(Math.toRadians(angle)));
    int yEnd = yc - (int)( 0.9 * dia * Math.cos(Math.toRadians(angle)));

    // draw topcode coordinate in a text box
    String coord = String.valueOf("(" + xc + ", " + yc + ", " + zDist + ")");
    drawTextBox(g2, coord, xEnd, yEnd);

    // draw a angled line going to the box
    g2.setColor(Color.RED);  // rounded, thick red line
    g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));   
    g2.drawLine(xc, yc, xEnd, yEnd);
    
    // ----------------------------------------------------------------------
    
    if (cx >= 18 && cx <= 36){
    	gui1.enviaDados(cx);
        int id = tc.getCode();
        System.out.println("Enviando Código:"+" ->"+id);
        gui1.enviaDados(id);
        try {
    		Thread.sleep(2000);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }
    else {
    System.out.println("Enviando Coordenada X:"+ xc+" ->"+cx);
    gui1.enviaDados(cx);
    try {
		Thread.sleep(20);
	} catch (InterruptedException e) {
		e.printStackTrace();
		}
    }
    //--------------------------------------------------------------------------
  }  // end of drawPos()


  // --------------- called from the top-level JFrame ------------------

  public void closeDown()
  /* Terminate run() and wait for it to finish.
     This stops the application from exiting until everything
     has finished. */
  { 
    isRunning = false;
    while (!isFinished) {
      try {
        Thread.sleep(DELAY);
      } 
      catch (Exception ex) {}
    }
  } // end of closeDown()


} // end of TCPanel class

