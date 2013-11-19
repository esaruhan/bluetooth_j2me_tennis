/*    */ import java.io.IOException;
/*    */ import javax.microedition.lcdui.Canvas;
/*    */ import javax.microedition.lcdui.Display;
/*    */ import javax.microedition.lcdui.Displayable;
/*    */ import javax.microedition.lcdui.Graphics;
/*    */ import javax.microedition.lcdui.Image;
/*    */ 
/*    */ public class Puan extends Canvas
/*    */ {
/*    */   Image remotepoint;
/* 19 */   Image[] images = new Image[5];
/* 20 */   String[] point = { "0", "15", "30", "40", "50" };
/*    */   Image scoreboard;
/*    */   GameTennis root;
/*    */   int myindex;
/*    */   int rindex;
/*    */ 
/*    */   public Puan(GameTennis gt, String pMypoint, String rPoint)
/*    */   {
/*    */     try
/*    */     {
/* 35 */       this.images[0] = Image.createImage("/images/00.png");
/* 36 */       this.images[1] = Image.createImage("/images/15.png");
/* 37 */       this.images[2] = Image.createImage("/images/30.png");
/* 38 */       this.images[3] = Image.createImage("/images/40.png");
/* 39 */       this.images[4] = Image.createImage("/images/a.png");
/*    */ 
/* 41 */       this.scoreboard = Image.createImage("/images/scorecard1.png");
/*    */     } catch (IOException ex) {
/* 43 */       ex.printStackTrace();
/*    */     }
/*    */ 
/* 46 */     this.root = gt;
/*    */ 
/* 48 */     setIndex(pMypoint, rPoint);
/*    */   }
/*    */ 
/*    */   Puan(GameTennis root, int i, int i0)
/*    */   {
/*    */   }
/*    */ 
/*    */   protected void paint(Graphics g)
/*    */   {
/* 64 */     g.setColor(0, 0, 0);
/* 65 */     g.fillRect(0, 0, getWidth(), getHeight());
/* 66 */     g.drawImage(this.scoreboard, (getWidth() - this.scoreboard.getWidth()) / 2, (getHeight() - this.scoreboard.getHeight()) / 2, 20);
/*    */ 
/* 68 */     g.drawImage(this.images[this.myindex], (getWidth() - this.scoreboard.getWidth()) / 2 + 74, (getHeight() - this.scoreboard.getHeight()) / 2 + 32, 20);
/* 69 */     g.drawImage(this.images[this.rindex], (getWidth() - this.scoreboard.getWidth()) / 2 + 74, (getHeight() - this.scoreboard.getHeight()) / 2 + 51, 20);
/*    */   }
/*    */ 
/*    */   public void setIndex(String mypoint, String rpoint)
/*    */   {
/* 76 */     for (int i = 0; i < this.point.length; i++)
/*    */     {
/* 78 */       if (this.point[i].equals(mypoint))
/* 79 */         this.myindex = i;
/* 80 */       if (this.point[i].equals(rpoint))
/* 81 */         this.rindex = i;
/* 82 */       repaint();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void show()
/*    */   {
/* 90 */     this.root.display.setCurrent(this);
/*    */     try
/*    */     {
/* 93 */       Thread.sleep(5000L);
/*    */     } catch (InterruptedException ex) {
/* 95 */       ex.printStackTrace();
/*    */     }
/*    */ 
/* 98 */     this.root.display.setCurrent(this.root.canvas);
/* 99 */     this.root.canvas.ball.restartTop();
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     Puan
 * JD-Core Version:    0.6.0
 */