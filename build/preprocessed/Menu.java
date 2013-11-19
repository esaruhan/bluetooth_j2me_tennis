/*    */ import java.io.PrintStream;
/*    */ import javax.microedition.lcdui.Command;
/*    */ import javax.microedition.lcdui.CommandListener;
/*    */ import javax.microedition.lcdui.Display;
/*    */ import javax.microedition.lcdui.Displayable;
/*    */ import javax.microedition.lcdui.Image;
/*    */ import javax.microedition.lcdui.List;
/*    */ 
/*    */ public class Menu extends List
/*    */   implements CommandListener
/*    */ {
/*    */   private GameTennis gt;
/*    */   private Command back;
/*    */   private Command select;
/*    */ 
/*    */   public Menu(GameTennis gt)
/*    */   {
/* 16 */     super("Tennis By Ertugrul", 3);
/* 17 */     this.gt = gt;
/* 18 */     this.back = new Command("QUIT", 7, 0);
/* 19 */     this.select = new Command("SELECT", 1, 1);
/*    */     try
/*    */     {
/* 22 */       append("Server", Image.createImage("/images/server.png"));
/* 23 */       append("Client", Image.createImage("/images/client.png"));
/*    */ 
/* 26 */       addCommand(this.back);
/* 27 */       addCommand(this.select);
/*    */ 
/* 29 */       setCommandListener(this);
/*    */     }
/*    */     catch (Exception e) {
/* 32 */       System.out.println("Error:\\n" + e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void commandAction(Command c, Displayable displayable)
/*    */   {
/* 39 */     if ((c == this.select) || (c.equals(List.SELECT_COMMAND)) || (c.getCommandType() == 4)) {
/* 40 */       if (getSelectedIndex() == 0)
/*    */       {
/* 43 */         this.gt.type = 1;
/*    */ 
/* 45 */         this.gt.CreateServer();
/*    */       }
/*    */ 
/* 50 */       if (getSelectedIndex() == 1)
/*    */       {
/* 52 */         this.gt.startUI();
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 57 */     if (c.getCommandType() == 7)
/*    */     {
/* 59 */       this.gt.Exit();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void show()
/*    */   {
/* 65 */     this.gt.display.setCurrent(this);
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     Menu
 * JD-Core Version:    0.6.0
 */