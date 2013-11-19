/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*     */ import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Player;
import javax.microedition.midlet.MIDlet;
/*     */ 
/*     */ public class GameTennis extends MIDlet
/*     */   implements CommandListener
/*     */ {
/*     */   public Display display;
/*     */   public Command exit;
/*     */   public Command run;
/*     */   public GameTennis.AnimationCanvas canvas;
/*     */   public BluetoothDiscovery disc;
/*     */   public BluetoothConnection[] btcon;
/*     */   public String keyText;
/*     */   public FrmSplash fsplash;
/*     */   public Menu menu;
/*  42 */   public int type = -1;
/*     */   public GameTennis root;
/*  45 */   public Image saha = null;
/*  46 */   public Image top = null;
/*  47 */   public Image myraket = null;
/*  48 */   public Image remoteraket = null;
/*  49 */   public int stype = -1;
/*  50 */   public int rstype = -1;
/*     */   public int remotescreenheight;
/*     */   public int remotescreenwidth;
/*     */   public int myscreenwidth;
/*     */   public int myscreenheight;
/*     */   public int gamewidth;
/*     */   public int gameheight;
/*     */   public int xdif;
/*     */   public int ydif;
/*     */   public ClientServerSelect select;
/*  64 */   public String mesaj = null;
/*     */   public int mypoint;
/*     */   public int remotepoint;
/*     */   Player player;
/*     */   Player player2;
/*     */   Thread tb;
/*     */   Loading ld;
/*     */ 
/*     */   public GameTennis()
/*     */   {
/*  78 */     this.display = Display.getDisplay(this);
/*  79 */     this.root = this;
/*  80 */     this.disc = new BluetoothDiscovery(this.display);
/*     */ 
/*  82 */     this.exit = new Command("Exit", 7, 1);
/*  83 */     this.run = new Command("Run", 1, 1);
/*     */ 
/*  88 */     ErrorScreen.init(null, this.display);
/*     */   }
/*     */ 
/*     */   public void startApp()
/*     */   {
/*  97 */     this.select = new ClientServerSelect(this.root);
/*  98 */     this.fsplash = new FrmSplash(this);
/*  99 */     this.menu = new Menu(this);
/*     */ 
/* 102 */     setBluetooth();
/*     */ 
/* 104 */     this.fsplash.show();
/*     */   }
/*     */ 
/*     */   public void pauseApp()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void destroyApp(boolean unconditional)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setScreenSize(BluetoothConnection[] bconn)
/*     */   {
/* 125 */     this.btcon = bconn;
/* 126 */     this.canvas = new GameTennis.AnimationCanvas();
/*     */ 
/* 128 */     this.myscreenheight = this.canvas.getHeight();
/* 129 */     this.myscreenwidth = this.canvas.getWidth();
/*     */ 
/* 131 */     String msg1 = "scw" + this.myscreenwidth + "h" + this.myscreenheight;
/*     */ 
/* 134 */     ReceiveThread th = new ReceiveThread(0, this.root, this.btcon);
/* 135 */     th.start();
/*     */ 
/* 145 */     this.ld = new Loading(this.root);
/* 146 */     this.ld.show();
/*     */ 
/* 150 */     setCanvas();
/*     */   }
/*     */ 
/*     */   public void setCanvas()
/*     */   {
/* 158 */     setScreenSize(this.mesaj);
/* 159 */     this.canvas.createPaddle();
/* 160 */     this.display.setCurrent(this.canvas);
/* 161 */     this.canvas.createBall();
/*     */   }
/*     */ 
/*     */   public void setScreenSize(String m)
/*     */   {
/* 172 */     int i1 = m.indexOf("w") + 1;
/* 173 */     int i2 = m.indexOf("h") + 1;
/*     */ 
/* 176 */     this.remotescreenwidth = Integer.parseInt(m.substring(i1, m.indexOf("h")));
/* 177 */     this.remotescreenheight = Integer.parseInt(m.substring(i2, m.length()));
/*     */ 
/* 180 */     if (this.myscreenheight <= this.remotescreenheight)
/*     */     {
/* 182 */       this.gameheight = this.myscreenheight;
/* 183 */       this.gamewidth = this.myscreenwidth;
/* 184 */       this.stype = 1;
/* 185 */       this.rstype = 2;
/*     */ 
/* 187 */       if (this.myscreenheight == this.remotescreenheight)
/*     */       {
/* 189 */         this.rstype = 1;
/*     */       }
/*     */ 
/* 192 */       this.xdif = 0;
/* 193 */       this.ydif = 0;
/*     */     }
/*     */     else
/*     */     {
/* 198 */       this.gameheight = this.remotescreenheight;
/* 199 */       this.gamewidth = this.remotescreenwidth;
/*     */ 
/* 201 */       this.stype = 2;
/* 202 */       this.rstype = 1;
/*     */ 
/* 205 */       this.xdif = Math.abs(this.remotescreenwidth - this.myscreenwidth);
/* 206 */       this.ydif = Math.abs(this.remotescreenheight - this.myscreenheight);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 217 */       this.saha = resizeImage(Image.createImage("/images/saha2.png"), this.gamewidth, this.gameheight);
/* 218 */       this.top = Image.createImage("/images/ball.png");
/* 219 */       this.myraket = Image.createImage("/images/myraket.png");
/* 220 */       this.remoteraket = Image.createImage("/images/remoteraket.png");
/*     */     }
/*     */     catch (IOException ex) {
/* 223 */       ex.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startUI()
/*     */   {
/* 232 */     this.select.show();
/*     */   }
/*     */ 
/*     */   public void setBluetooth()
/*     */   {
/* 240 */     this.disc.setServiceUUID("20000000000010008000006057028C19");
/*     */     String name;
/*     */     try {
/* 245 */       name = LocalDevice.getLocalDevice().getFriendlyName();
/*     */     }
/*     */     catch (BluetoothStateException e)
/*     */     {
/* 249 */       showAlertAndExit("", "Please switch Bluetooth on!", AlertType.ERROR);
/* 250 */       return;
/*     */     }
/*     */ 
/* 254 */     this.disc.setName(name);
/*     */   }
/*     */ 
/*     */   public void CreateServer()
/*     */   {
/* 260 */     ServerThread st = new ServerThread(this.root);
/* 261 */     st.start();
/*     */   }
/*     */ 
/*     */   public void Exit()
/*     */   {
/* 267 */     destroyApp(false);
/* 268 */     notifyDestroyed();
/*     */   }
/*     */ 
/*     */   public void commandAction(Command c, Displayable d)
/*     */   {
/* 273 */     String label = c.getLabel();
/*     */ 
/* 275 */     if (label.equals("Exit"))
/*     */     {
/* 277 */       notifyDestroyed();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void showAlertAndExit(String t, String s, AlertType type)
/*     */   {
/* 284 */     Alert a = new Alert(t, s, null, type);
/* 285 */     a.addCommand(new Command("Exit", 7, 1));
/*     */ 
/* 290 */     a.setCommandListener(this);
/* 291 */     this.display.setCurrent(a);
/*     */   }
/*     */ 
/*     */   private Image resizeImage(Image src, int screenWidth, int screenHeight)
/*     */   {
/* 296 */     int srcWidth = src.getWidth();
/* 297 */     int srcHeight = src.getHeight();
/*     */ 
/* 299 */     Image tmp = Image.createImage(screenWidth, srcHeight);
/* 300 */     Graphics g = tmp.getGraphics();
/* 301 */     int ratio = (srcWidth << 16) / screenWidth;
/* 302 */     int pos = ratio / 2;
/*     */ 
/* 306 */     for (int x = 0; x < screenWidth; x++) {
/* 307 */       g.setClip(x, 0, 1, srcHeight);
/* 308 */       g.drawImage(src, x - (pos >> 16), 0, 20);
/* 309 */       pos += ratio;
/*     */     }
/*     */ 
/* 312 */     Image resizedImage = Image.createImage(screenWidth, screenHeight);
/* 313 */     g = resizedImage.getGraphics();
/* 314 */     ratio = (srcHeight << 16) / screenHeight;
/* 315 */     pos = ratio / 2;
/*     */ 
/* 319 */     for (int y = 0; y < screenHeight; y++) {
/* 320 */       g.setClip(0, y, screenWidth, 1);
/* 321 */       g.drawImage(tmp, 0, y - (pos >> 16), 20);
/* 322 */       pos += ratio;
/*     */     }
/* 324 */     return resizedImage;
/*     */   }
/* 331 */   public class AnimationCanvas extends Canvas implements CommandListener { protected int width = getWidth();
/* 332 */     protected int height = getHeight();
/*     */     protected int SIZEX;
/*     */     protected int SIZEY;
/* 336 */     public boolean gameOver = true;
/*     */     GameTennis.AnimationCanvas.Paddle mypaddle;
/*     */     GameTennis.AnimationCanvas.RemotePaddle rpaddle;
/*     */     GameTennis.AnimationCanvas.Ball ball;
/*     */ 
/* 344 */     public AnimationCanvas() { this.SIZEX = 30;
/* 345 */       this.SIZEY = 12;
/* 346 */       addCommand(GameTennis.this.exit);
/* 347 */       setCommandListener(this);
/*     */     }
/*     */ 
/*     */     public void createBall()
/*     */     {
/* 353 */       this.ball = new GameTennis.AnimationCanvas.Ball(GameTennis.this.gamewidth / 4 + GameTennis.this.xdif / 2, GameTennis.this.gameheight / 4 + GameTennis.this.ydif / 2, 1, 3);
/* 354 */       GameTennis.this.tb = new Thread(this.ball);
/* 355 */       GameTennis.this.tb.start();
/*     */     }
/*     */ 
/*     */     public void createPaddle()
/*     */     {
/* 364 */       if (GameTennis.this.stype == 1)
/*     */       {
/* 366 */         this.mypaddle = new GameTennis.AnimationCanvas.Paddle(30, GameTennis.this.gameheight - 10, 4, 4);
/* 367 */         this.rpaddle = new GameTennis.AnimationCanvas.RemotePaddle(30, 0, 4, 4);
/*     */       }
/* 370 */       else if (GameTennis.this.stype == 2)
/*     */       {
/* 372 */         this.mypaddle = new GameTennis.AnimationCanvas.Paddle(GameTennis.this.xdif / 2 + 30, GameTennis.this.ydif / 2 + GameTennis.this.gameheight - 10, 4, 4);
/* 373 */         this.rpaddle = new GameTennis.AnimationCanvas.RemotePaddle(GameTennis.this.xdif / 2 + 30, GameTennis.this.ydif / 2, 4, 4);
/*     */       }
/*     */ 
/* 379 */       Thread th = new Thread(this.mypaddle);
/* 380 */       th.start();
/* 381 */       Thread th1 = new Thread(this.rpaddle);
/* 382 */       th1.start();
/*     */     }
/*     */ 
/*     */     protected void keyPressed(int keyCode)
/*     */     {
/* 389 */       GameTennis.this.keyText = getKeyName(keyCode);
/*     */     }
/*     */ 
/*     */     public void paint(Graphics g)
/*     */     {
/* 396 */       if (GameTennis.this.stype == 1)
/* 397 */         g.drawImage(GameTennis.this.saha, GameTennis.this.xdif / 2, GameTennis.this.ydif / 2, 0x10 | 0x4);
/* 398 */       else if (GameTennis.this.stype == 2) {
/* 399 */         g.drawImage(GameTennis.this.saha, GameTennis.this.xdif / 2, GameTennis.this.ydif / 2, 0x10 | 0x4);
/*     */       }
/* 401 */       g.drawImage(GameTennis.this.myraket, this.mypaddle.x, this.mypaddle.y, 0x10 | 0x4);
/*     */ 
/* 403 */       g.drawImage(GameTennis.this.remoteraket, this.rpaddle.x, this.rpaddle.y, 0x10 | 0x4);
/*     */ 
/* 405 */       g.drawImage(GameTennis.this.top, this.ball.x, this.ball.y, 0x10 | 0x4);
/*     */     }
/*     */ 
/*     */     public void commandAction(Command c, Displayable d)
/*     */     {
/* 416 */       if (c == GameTennis.this.exit)
/* 417 */         GameTennis.this.Exit();
/*     */     }
/*     */ 
/*     */     public void updateRemotePlayer(String c)
/*     */     {
/*     */       try
/*     */       {
/* 634 */         GameTennis.this.btcon[0].writeString(c);
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 640 */         GameTennis.this.btcon[0].close();
/*     */ 
/* 642 */         if (GameTennis.this.btcon[0].isClosed())
/*     */         {
/* 644 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void updateBall(String c)
/*     */     {
/* 652 */       if (c.startsWith("b2"))
/*     */       {
/* 654 */         int i1 = c.indexOf("xc") + 2;
/* 655 */         int i2 = c.indexOf("yc") + 2;
/* 656 */         int i3 = c.indexOf("ys");
/* 657 */         int i4 = c.indexOf("ys") + 2;
/*     */ 
/* 659 */         int xb = Integer.parseInt(c.substring(i1, c.indexOf("yc")));
/* 660 */         int yb = Integer.parseInt(c.substring(i2, i3));
/* 661 */         int yspeed = Integer.parseInt(c.substring(i4, c.length()));
/*     */ 
/* 663 */         this.ball.x = (GameTennis.this.xdif / 2 + xb);
/* 664 */         this.ball.y = (GameTennis.this.ydif / 2 + yb);
/*     */ 
/* 666 */         repaint();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void updateLocalPlayer(String c)
/*     */     {
/* 676 */       if (GameTennis.this.type == 1)
/*     */       {
/* 678 */         if (c.startsWith("p"))
/*     */         {
/* 681 */           String s = c.substring(1);
/* 682 */           int x = Integer.parseInt(s);
/* 683 */           updatePaddle1(x);
/*     */ 
/* 685 */           repaint();
/*     */         }
/*     */ 
/*     */       }
/* 693 */       else if (GameTennis.this.type == 2)
/*     */       {
/* 696 */         if (c.startsWith("p"))
/*     */         {
/* 699 */           String s = c.substring(1);
/* 700 */           int x = Integer.parseInt(s);
/* 701 */           updatePaddle2(x);
/*     */ 
/* 703 */           repaint();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void updatePaddle1(int x)
/*     */     {
/* 717 */       if ((GameTennis.this.stype == 1) && (GameTennis.this.rstype == 2))
/*     */       {
/* 719 */         this.rpaddle.x = (x - GameTennis.this.xdif / 2);
/*     */       }
/* 721 */       else if ((GameTennis.this.stype == 2) && (GameTennis.this.rstype == 1))
/*     */       {
/* 723 */         this.rpaddle.x = (x + GameTennis.this.xdif / 2);
/*     */       }
/* 725 */       else if ((GameTennis.this.stype == 1) && (GameTennis.this.rstype == 1))
/*     */       {
/* 727 */         this.rpaddle.x = x;
/*     */       }
/* 729 */       repaint();
/*     */     }
/*     */ 
/*     */     public void updatePaddle2(int x)
/*     */     {
/* 736 */       if ((GameTennis.this.stype == 1) && (GameTennis.this.rstype == 2))
/*     */       {
/* 738 */         this.mypaddle.x = (x - GameTennis.this.xdif / 2);
/*     */       }
/* 740 */       else if ((GameTennis.this.stype == 2) && (GameTennis.this.rstype == 1))
/*     */       {
/* 742 */         this.mypaddle.x = (x + GameTennis.this.xdif / 2);
/*     */       }
/* 744 */       else if ((GameTennis.this.stype == 1) && (GameTennis.this.rstype == 1))
/*     */       {
/* 746 */         this.mypaddle.x = x;
/* 747 */       }repaint();
/*     */     }
/*     */ 
/*     */     public class Ball
/*     */       implements Runnable
/*     */     {
/*     */       int x;
/*     */       int y;
/*     */       int sizex;
/*     */       int sizey;
/*     */       int xSpeed;
/*     */       int ySpeed;
/*     */ 
/*     */       Ball(int x, int y, int xSpeed, int ySpeed)
/*     */       {
/* 769 */         this.x = x;
/* 770 */         this.y = y;
/*     */ 
/* 772 */         this.sizex = 8;
/* 773 */         this.sizey = 8;
/*     */ 
/* 775 */         this.xSpeed = xSpeed;
/* 776 */         this.ySpeed = ySpeed;
/*     */       }
/*     */ 
/*     */       public void restartTop()
/*     */       {
/* 783 */         this.x = (GameTennis.this.gamewidth / 4 + GameTennis.this.xdif / 2);
/* 784 */         this.y = (GameTennis.this.gameheight / 4 + GameTennis.this.ydif / 2);
/* 785 */         this.xSpeed = 1;
/* 786 */         this.ySpeed = 3;
/*     */       }
/*     */ 
/*     */       public void move()
/*     */       {
/* 792 */         this.x += this.xSpeed;
/* 793 */         this.y += this.ySpeed;
/*     */ 
/* 797 */         if ((this.y + GameTennis.AnimationCanvas.this.ball.sizey / 2 >= GameTennis.AnimationCanvas.this.mypaddle.y) && (this.y + GameTennis.AnimationCanvas.this.ball.sizey / 2 < GameTennis.AnimationCanvas.this.mypaddle.y + GameTennis.AnimationCanvas.this.mypaddle.sizey) && (((this.y + GameTennis.AnimationCanvas.this.ball.sizey / 2 == GameTennis.AnimationCanvas.this.mypaddle.y) && (this.x > GameTennis.AnimationCanvas.this.mypaddle.x) && (this.x < GameTennis.AnimationCanvas.this.mypaddle.x + GameTennis.AnimationCanvas.this.SIZEX)) || ((this.x + GameTennis.AnimationCanvas.this.ball.sizex > GameTennis.AnimationCanvas.this.mypaddle.x) && (this.x + GameTennis.AnimationCanvas.this.ball.sizex < GameTennis.AnimationCanvas.this.mypaddle.x + GameTennis.AnimationCanvas.this.SIZEX))))
/*     */         {
/* 799 */           String msg1 = "b2xc" + this.x + "yc" + this.y + "ys" + this.ySpeed;
/* 800 */           if (GameTennis.this.type == 1)
/* 801 */             GameTennis.AnimationCanvas.this.updateRemotePlayer(msg1);
/* 802 */           this.ySpeed = (-this.ySpeed);
/*     */ 
/* 804 */           GameTennis.AnimationCanvas.this.repaint();
/*     */         }
/*     */ 
/* 808 */         if ((this.y + GameTennis.AnimationCanvas.this.ball.sizey / 2 >= GameTennis.AnimationCanvas.this.rpaddle.y) && (this.y + GameTennis.AnimationCanvas.this.ball.sizey / 2 < GameTennis.AnimationCanvas.this.rpaddle.y + GameTennis.AnimationCanvas.this.rpaddle.sizey) && (((this.y == GameTennis.AnimationCanvas.this.rpaddle.y + GameTennis.AnimationCanvas.this.rpaddle.sizey) && (this.x > GameTennis.AnimationCanvas.this.rpaddle.x) && (this.x < GameTennis.AnimationCanvas.this.rpaddle.x + GameTennis.AnimationCanvas.this.SIZEX)) || ((this.x + GameTennis.AnimationCanvas.this.ball.sizex > GameTennis.AnimationCanvas.this.rpaddle.x) && (this.x + GameTennis.AnimationCanvas.this.ball.sizex < GameTennis.AnimationCanvas.this.rpaddle.x + GameTennis.AnimationCanvas.this.SIZEX))))
/*     */         {
/* 810 */           String msg2 = "b2xc" + this.x + "yc" + this.y + "ys" + this.ySpeed;
/* 811 */           if (GameTennis.this.type == 1)
/* 812 */             GameTennis.AnimationCanvas.this.updateRemotePlayer(msg2);
/* 813 */           this.ySpeed = (-this.ySpeed);
/*     */ 
/* 815 */           GameTennis.AnimationCanvas.this.repaint();
/*     */         }
/*     */ 
/* 821 */         if ((this.x <= GameTennis.this.xdif / 2) || (this.x + GameTennis.AnimationCanvas.this.ball.sizex >= GameTennis.this.gamewidth + GameTennis.this.xdif / 2))
/*     */         {
/* 823 */           this.xSpeed = (-this.xSpeed);
/* 824 */           GameTennis.AnimationCanvas.this.repaint();
/*     */         }
/*     */ 
/* 827 */         if (this.y + GameTennis.AnimationCanvas.this.ball.sizey / 2 > GameTennis.AnimationCanvas.this.mypaddle.y + GameTennis.AnimationCanvas.this.mypaddle.sizey)
/*     */         {
/* 830 */           if (GameTennis.this.type == 2) {
/* 831 */             if (GameTennis.this.mypoint < 30)
/* 832 */               GameTennis.this.mypoint += 15;
/* 833 */             else if (GameTennis.this.mypoint == 30)
/* 834 */               GameTennis.this.mypoint = 40;
/* 835 */             else if (GameTennis.this.mypoint == 40) {
/* 836 */               GameTennis.this.mypoint = 50;
/*     */             }
/*     */           }
/* 839 */           else if (GameTennis.this.remotepoint < 30)
/* 840 */             GameTennis.this.remotepoint += 15;
/* 841 */           else if (GameTennis.this.remotepoint == 30)
/* 842 */             GameTennis.this.remotepoint = 40;
/* 843 */           else if (GameTennis.this.remotepoint == 40) {
/* 844 */             GameTennis.this.remotepoint = 50;
/*     */           }
/*     */ 
/* 847 */           Puan pu1 = new Puan(GameTennis.this.root, "" + GameTennis.this.mypoint, "" + GameTennis.this.remotepoint);
/* 848 */           pu1.show();
/* 849 */           GameTennis.AnimationCanvas.this.repaint();
/*     */         }
/*     */ 
/* 852 */         if (this.y - GameTennis.AnimationCanvas.this.ball.sizey / 2 < GameTennis.AnimationCanvas.this.rpaddle.y)
/*     */         {
/* 855 */           if (GameTennis.this.type == 1) {
/* 856 */             if (GameTennis.this.mypoint < 30)
/* 857 */               GameTennis.this.mypoint += 15;
/* 858 */             else if (GameTennis.this.mypoint == 30)
/* 859 */               GameTennis.this.mypoint = 40;
/* 860 */             else if (GameTennis.this.mypoint == 40) {
/* 861 */               GameTennis.this.mypoint = 50;
/*     */             }
/*     */ 
/*     */           }
/* 866 */           else if (GameTennis.this.remotepoint < 30)
/* 867 */             GameTennis.this.remotepoint += 15;
/* 868 */           else if (GameTennis.this.remotepoint == 30)
/* 869 */             GameTennis.this.remotepoint = 40;
/* 870 */           else if (GameTennis.this.remotepoint == 40) {
/* 871 */             GameTennis.this.remotepoint = 50;
/*     */           }
/*     */ 
/* 874 */           Puan pu2 = new Puan(GameTennis.this.root, "" + GameTennis.this.mypoint, "" + GameTennis.this.remotepoint);
/* 875 */           pu2.show();
/*     */ 
/* 877 */           GameTennis.AnimationCanvas.this.repaint();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void run()
/*     */       {
/* 888 */         while (GameTennis.AnimationCanvas.this.gameOver)
/*     */         {
/* 890 */           move();
/*     */ 
/* 892 */           GameTennis.AnimationCanvas.this.repaint();
/*     */           try {
/* 894 */             Thread.sleep(300L);
/*     */           }
/*     */           catch (InterruptedException e)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public class RemotePaddle
/*     */       implements Runnable
/*     */     {
/*     */       int x;
/*     */       int y;
/*     */       int sizex;
/*     */       int sizey;
/*     */       int xSpeed;
/*     */       int ySpeed;
/*     */ 
/*     */       RemotePaddle(int x, int y, int xSpeed, int ySpeed)
/*     */       {
/* 545 */         this.x = x;
/* 546 */         this.y = y;
/* 547 */         this.xSpeed = xSpeed;
/* 548 */         this.ySpeed = ySpeed;
/*     */ 
/* 552 */         this.sizex = 30;
/* 553 */         this.sizey = 12;
/*     */       }
/*     */ 
/*     */       void move()
/*     */       {
/* 561 */         if (GameTennis.this.type == 2)
/*     */         {
/* 563 */           if (GameTennis.this.keyText != null)
/*     */           {
/* 565 */             if ((GameTennis.this.keyText.equals("RIGHT")) && (this.x + GameTennis.AnimationCanvas.this.SIZEX < GameTennis.this.gamewidth + GameTennis.this.xdif / 2))
/*     */             {
/* 567 */               this.x += this.xSpeed;
/* 568 */               GameTennis.this.keyText = null;
/* 569 */               String bx = "p" + this.x;
/* 570 */               GameTennis.AnimationCanvas.this.updateRemotePlayer(bx);
/*     */             }
/* 573 */             else if ((GameTennis.this.keyText.equals("6")) && (this.x + GameTennis.AnimationCanvas.this.SIZEX < GameTennis.this.gamewidth + GameTennis.this.xdif / 2))
/*     */             {
/* 575 */               this.x += this.xSpeed;
/* 576 */               GameTennis.this.keyText = null;
/* 577 */               String bx = "p" + this.x;
/* 578 */               GameTennis.AnimationCanvas.this.updateRemotePlayer(bx);
/*     */             }
/* 580 */             else if ((GameTennis.this.keyText.equals("4")) && (this.x > GameTennis.this.xdif / 2))
/*     */             {
/* 582 */               this.x -= this.xSpeed;
/* 583 */               GameTennis.this.keyText = null;
/* 584 */               String bx = "p" + this.x;
/* 585 */               GameTennis.AnimationCanvas.this.updateRemotePlayer(bx);
/*     */             }
/* 587 */             else if ((GameTennis.this.keyText.equals("LEFT")) && (this.x > GameTennis.this.xdif / 2))
/*     */             {
/* 590 */               this.x -= this.xSpeed;
/* 591 */               GameTennis.this.keyText = null;
/* 592 */               String bx = "p" + this.x;
/* 593 */               GameTennis.AnimationCanvas.this.updateRemotePlayer(bx);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */       public void run()
/*     */       {
/* 610 */         while (GameTennis.AnimationCanvas.this.gameOver)
/*     */         {
/* 612 */           move();
/*     */ 
/* 614 */           GameTennis.AnimationCanvas.this.repaint();
/*     */           try {
/* 616 */             Thread.sleep(5L);
/*     */           }
/*     */           catch (InterruptedException e)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public class Paddle
/*     */       implements Runnable
/*     */     {
/*     */       int x;
/*     */       int y;
/*     */       int sizex;
/*     */       int sizey;
/*     */       int xSpeed;
/*     */       int ySpeed;
/*     */ 
/*     */       Paddle(int x, int y, int xSpeed, int ySpeed)
/*     */       {
/* 440 */         this.x = x;
/* 441 */         this.y = y;
/* 442 */         this.xSpeed = xSpeed;
/* 443 */         this.ySpeed = ySpeed;
/*     */ 
/* 447 */         this.sizex = 30;
/* 448 */         this.sizey = 12;
/*     */       }
/*     */ 
/*     */       void move()
/*     */       {
/* 456 */         if (GameTennis.this.type == 1)
/*     */         {
/* 458 */           if (GameTennis.this.keyText != null)
/*     */           {
/* 460 */             if ((GameTennis.this.keyText.equals("RIGHT")) && (this.x + GameTennis.AnimationCanvas.this.SIZEX < GameTennis.this.gamewidth + GameTennis.this.xdif / 2))
/*     */             {
/* 462 */               this.x += this.xSpeed;
/* 463 */               GameTennis.this.keyText = null;
/* 464 */               String bx = "p" + this.x;
/* 465 */               GameTennis.AnimationCanvas.this.updateRemotePlayer(bx);
/*     */             }
/* 468 */             else if ((GameTennis.this.keyText.equals("6")) && (this.x + GameTennis.AnimationCanvas.this.SIZEX < GameTennis.this.gamewidth + GameTennis.this.xdif / 2))
/*     */             {
/* 470 */               this.x += this.xSpeed;
/* 471 */               GameTennis.this.keyText = null;
/* 472 */               String bx = "p" + this.x;
/* 473 */               GameTennis.AnimationCanvas.this.updateRemotePlayer(bx);
/*     */             }
/* 475 */             else if ((GameTennis.this.keyText.equals("4")) && (this.x > GameTennis.this.xdif / 2))
/*     */             {
/* 477 */               this.x -= this.xSpeed;
/* 478 */               GameTennis.this.keyText = null;
/* 479 */               String bx = "p" + this.x;
/* 480 */               GameTennis.AnimationCanvas.this.updateRemotePlayer(bx);
/*     */             }
/* 482 */             else if ((GameTennis.this.keyText.equals("LEFT")) && (this.x > GameTennis.this.xdif / 2))
/*     */             {
/* 485 */               this.x -= this.xSpeed;
/* 486 */               GameTennis.this.keyText = null;
/* 487 */               String bx = "p" + this.x;
/* 488 */               GameTennis.AnimationCanvas.this.updateRemotePlayer(bx);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */       public void run()
/*     */       {
/* 504 */         while (GameTennis.AnimationCanvas.this.gameOver)
/*     */         {
/* 507 */           move();
/*     */ 
/* 509 */           GameTennis.AnimationCanvas.this.repaint();
/*     */           try {
/* 511 */             Thread.sleep(5L);
/*     */           }
/*     */           catch (InterruptedException e)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     GameTennis
 * JD-Core Version:    0.6.0
 */
