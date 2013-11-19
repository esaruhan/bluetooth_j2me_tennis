/*    */ import javax.microedition.lcdui.AlertType;
/*    */ 
/*    */ public class ServerThread extends Thread
/*    */ {
/*    */   GameTennis gt;
/*    */ 
/*    */   public ServerThread(GameTennis pgt)
/*    */   {
/* 20 */     this.gt = pgt;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/* 30 */       BluetoothConnection[] con = this.gt.disc.waitOnConnection();
/* 31 */       if (con[0] == null)
/*    */       {
/* 33 */         this.gt.menu.show();
/* 34 */         return;
/*    */       }
/*    */ 
/* 39 */       this.gt.setScreenSize(con);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 44 */       this.gt.showAlertAndExit("Error:", e.getMessage(), AlertType.ERROR);
/* 45 */       return;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     ServerThread
 * JD-Core Version:    0.6.0
 */