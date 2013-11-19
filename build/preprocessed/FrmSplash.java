/*    */ import javax.microedition.lcdui.Canvas;
/*    */ import javax.microedition.lcdui.Display;
/*    */ import javax.microedition.lcdui.Displayable;
/*    */ import javax.microedition.lcdui.Graphics;
/*    */ import javax.microedition.lcdui.Image;
/*    */ 
/*    */ public class FrmSplash extends Canvas
/*    */ {
/*    */   private GameTennis gt;
/*    */   private Image iSplash;
/*    */ 
/*    */   public FrmSplash(GameTennis pgt)
/*    */   {
/* 21 */     this.gt = pgt;
/*    */     try
/*    */     {
/* 24 */       this.iSplash = Image.createImage("/images/splash.gif");
/*    */     }
/*    */     catch (Exception e) {
/*    */     }
/* 28 */     setFullScreenMode(true);
/*    */   }
/*    */ 
/*    */   public void show()
/*    */   {
/* 33 */     this.gt.display.setCurrent(this);
/*    */     try {
/* 35 */       Thread.sleep(500L);
/*    */     } catch (InterruptedException ex) {
/* 37 */       ex.printStackTrace();
/*    */     }
/*    */ 
/* 40 */     this.gt.menu.show();
/*    */   }
/*    */ 
/*    */   public void paint(Graphics g) {
/* 44 */     g.setColor(0, 0, 0);
/* 45 */     g.fillRect(0, 0, getWidth(), getHeight());
/* 46 */     g.drawImage(this.iSplash, (getWidth() - this.iSplash.getWidth()) / 2, (getHeight() - this.iSplash.getHeight()) / 2, 0);
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     FrmSplash
 * JD-Core Version:    0.6.0
 */