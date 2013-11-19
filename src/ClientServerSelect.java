/*    */ import javax.microedition.lcdui.Command;
/*    */ import javax.microedition.lcdui.CommandListener;
/*    */ import javax.microedition.lcdui.Display;
/*    */ import javax.microedition.lcdui.Displayable;
/*    */ import javax.microedition.lcdui.List;
/*    */ 
/*    */ public class ClientServerSelect extends List
/*    */   implements CommandListener
/*    */ {
/*    */   GameTennis gt;
/*    */ 
/*    */   public ClientServerSelect(GameTennis pGt)
/*    */   {
/* 25 */     super("Select Role:", 3);
/* 26 */     this.gt = pGt;
/*    */ 
/* 30 */     setFitPolicy(1);
/*    */ 
/* 32 */     append("Client (FIRST_FOUND)", null);
/* 33 */     append("Client (SELECT_ONE)", null);
/*    */ 
/* 37 */     addCommand(new Command("Select", 4, 1));
/* 38 */     addCommand(new Command("BACK", 2, 1));
/* 39 */     setCommandListener(this);
/*    */   }
/*    */ 
/*    */   public void commandAction(Command c, Displayable d)
/*    */   {
/* 46 */     this.gt.type = 2;
/*    */ 
/* 48 */     if ((c.equals(List.SELECT_COMMAND)) || (c.getCommandType() == 4))
/*    */     {
/* 50 */       int i = getSelectedIndex();
/* 51 */       String s = getString(i);
/*    */ 
/* 54 */       if (s.equals("Client (FIRST_FOUND)"))
/*    */       {
/* 56 */         ClientThread ct = new ClientThread(1, this.gt);
/* 57 */         ct.start();
/*    */       }
/*    */ 
/* 60 */       if (s.equals("Client (SELECT_ONE)"))
/*    */       {
/* 62 */         ClientThread ct = new ClientThread(3, this.gt);
/* 63 */         ct.start();
/*    */       }
/*    */ 
/*    */     }
/* 67 */     else if (c.getCommandType() == 2)
/*    */     {
/* 70 */       this.gt.menu.show();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void show()
/*    */   {
/* 78 */     this.gt.display.setCurrent(this);
/*    */   }
/*    */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     ClientServerSelect
 * JD-Core Version:    0.6.0
 */