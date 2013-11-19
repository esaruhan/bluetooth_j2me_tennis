/*    */ import javax.microedition.lcdui.Alert;
/*    */ import javax.microedition.lcdui.AlertType;
/*    */ import javax.microedition.lcdui.Display;
/*    */ import javax.microedition.lcdui.Displayable;
/*    */ import javax.microedition.lcdui.Image;
/*    */ 
/*    */ class ErrorScreen extends Alert
/*    */ {
/*    */   private static Display display;
/*    */   private static Image image;
/* 13 */   private static ErrorScreen instance = null;
/*    */ 
/*    */   private ErrorScreen()
/*    */   {
/* 18 */     super("Error");
/* 19 */     setType(AlertType.ERROR);
/* 20 */     setTimeout(3000);
/* 21 */     setImage(image);
/*    */   }
/*    */ 
/*    */   public static void init(Image img, Display disp)
/*    */   {
/* 27 */     image = img;
/* 28 */     display = disp;
/*    */   }
/*    */ 
/*    */   public static void showError(String message)
/*    */   {
/* 34 */     if (instance == null)
/*    */     {
/* 36 */       instance = new ErrorScreen();
/*    */     }
/* 38 */     instance.setString(message);
/* 39 */     display.setCurrent(instance);
/*    */   }
/*    */ 
/*    */   public static void showError(String message, Displayable next)
/*    */   {
/* 45 */     if (instance == null)
/*    */     {
/* 47 */       instance = new ErrorScreen();
/*    */     }
/* 49 */     instance.setString(message);
/* 50 */     display.setCurrent(instance, next);
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     ErrorScreen
 * JD-Core Version:    0.6.0
 */