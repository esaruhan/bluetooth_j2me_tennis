/*    */ import javax.microedition.lcdui.AlertType;
/*    */ 
/*    */ public class ClientThread extends Thread
/*    */ {
/*    */   private int searchType;
/*    */   GameTennis gt;
/*    */ 
/*    */   protected ClientThread(int st, GameTennis pgt)
/*    */   {
/* 22 */     this.searchType = st;
/* 23 */     this.gt = pgt;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/* 32 */       BluetoothConnection[] conn = this.gt.disc.searchService(this.searchType);
/* 33 */       if (conn.length != 0)
/*    */       {
/* 37 */         this.gt.setScreenSize(conn);
/*    */       }
/*    */       else
/*    */       {
/* 42 */         this.gt.startUI();
/*    */       }
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 47 */       this.gt.showAlertAndExit("Error:", e.getMessage(), AlertType.ERROR);
/* 48 */       return;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     ClientThread
 * JD-Core Version:    0.6.0
 */