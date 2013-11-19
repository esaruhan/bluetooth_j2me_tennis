/*    */ import java.io.IOException;
/*    */ 
/*    */ public class ReceiveThread extends Thread
/*    */ {
/*    */   int index;
/*    */   BluetoothConnection[] btConnections;
/*    */   GameTennis gt;
/*    */ 
/*    */   public ReceiveThread(int i, GameTennis pgt, BluetoothConnection[] bc)
/*    */   {
/* 25 */     this.index = i;
/* 26 */     this.btConnections = bc;
/* 27 */     this.gt = pgt;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     do {
/*    */       try
/*    */       {
/* 43 */         String inp2 = this.btConnections[this.index].readString();
/*    */ 
/* 45 */         if (inp2.startsWith("sc")) {
/* 46 */           this.gt.mesaj = inp2;
/*    */         }
/*    */ 
/* 49 */         if (inp2.startsWith("p"))
/* 50 */           this.gt.canvas.updateLocalPlayer(inp2);
/* 51 */         if (inp2.startsWith("b2")) {
/* 52 */           this.gt.canvas.updateBall(inp2);
/*    */         }
/*    */ 
/*    */       }
/*    */       catch (IOException e)
/*    */       {
/* 62 */         this.btConnections[this.index].close();
/*    */ 
/* 68 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 72 */     while (!this.btConnections[this.index].isClosed());
/*    */ 
/* 74 */     this.btConnections[this.index].close();
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     ReceiveThread
 * JD-Core Version:    0.6.0
 */