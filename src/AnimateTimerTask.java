/*    */ import java.util.TimerTask;
/*    */ import javax.microedition.lcdui.Display;
/*    */ 
/*    */ class AnimateTimerTask extends TimerTask
/*    */ {
/*    */   Display dp;
/*    */ 
/*    */   public AnimateTimerTask(Display dpx)
/*    */   {
/* 19 */     this.dp = dpx;
/*    */   }
/*    */ 
/*    */   public final void run() {
/* 23 */     this.dp.flashBacklight(1000);
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     AnimateTimerTask
 * JD-Core Version:    0.6.0
 */