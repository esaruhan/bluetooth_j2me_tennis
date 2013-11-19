/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import javax.microedition.io.Connection;
/*     */ import javax.microedition.io.Connector;
/*     */ import javax.microedition.io.InputConnection;
/*     */ import javax.microedition.io.OutputConnection;
/*     */ import javax.microedition.io.StreamConnection;
/*     */ 
/*     */ public class BluetoothConnection
/*     */ {
/*     */   private StreamConnection streamConnection;
/*     */   private InputStream inputStream;
/*     */   private OutputStream outputStream;
/*     */   private String localName;
/*     */   private String remoteName;
/*     */   private String url;
/*     */ 
/*     */   public BluetoothConnection(StreamConnection con, String ln, String rn)
/*     */     throws IOException
/*     */   {
/*  30 */     this.localName = ln;
/*  31 */     this.remoteName = rn;
/*     */ 
/*  34 */     this.url = "";
/*     */ 
/*  37 */     this.streamConnection = con;
/*     */ 
/*  40 */     openStreams();
/*     */   }
/*     */ 
/*     */   public BluetoothConnection(String urlStrings, String ln, String rn)
/*     */     throws IOException
/*     */   {
/*  47 */     this.localName = ln;
/*  48 */     this.remoteName = rn;
/*     */ 
/*  51 */     this.url = urlStrings;
/*     */ 
/*  54 */     connect();
/*     */   }
/*     */ 
/*     */   private void connect() throws IOException
/*     */   {
/*  59 */     this.streamConnection = ((StreamConnection)Connector.open(this.url));
/*     */ 
/*  62 */     openStreams();
/*     */   }
/*     */ 
/*     */   private void openStreams()
/*     */     throws IOException
/*     */   {
/*  68 */     this.inputStream = this.streamConnection.openInputStream();
/*  69 */     this.outputStream = this.streamConnection.openOutputStream();
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/*     */     try
/*     */     {
/*  77 */       this.outputStream.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  86 */       this.inputStream.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  95 */       if (this.streamConnection != null)
/*     */       {
/*  97 */         this.streamConnection.close();
/*  98 */         this.streamConnection = null;
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/* 109 */     return this.inputStream;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */   {
/* 114 */     return this.outputStream;
/*     */   }
/*     */ 
/*     */   public String getLocalName()
/*     */   {
/* 119 */     return this.localName;
/*     */   }
/*     */ 
/*     */   public String getRemoteName()
/*     */   {
/* 124 */     return this.remoteName;
/*     */   }
/*     */ 
/*     */   protected void setRemoteName(String rn)
/*     */   {
/* 130 */     this.remoteName = rn;
/*     */   }
/*     */ 
/*     */   public void writeString(String s)
/*     */     throws IOException
/*     */   {
/* 138 */     byte[] bytes = s.getBytes();
/*     */ 
/* 140 */     this.outputStream.write(bytes.length);
/*     */ 
/* 142 */     this.outputStream.write(bytes);
/*     */ 
/* 144 */     this.outputStream.flush();
/*     */   }
/*     */ 
/*     */   public String readString()
/*     */     throws IOException
/*     */   {
/* 152 */     int length = this.inputStream.read();
/* 153 */     byte[] bytes = new byte[length];
/* 154 */     read(bytes, length);
/*     */ 
/* 156 */     return new String(bytes);
/*     */   }
/*     */ 
/*     */   public void writeInt(int v)
/*     */     throws IOException
/*     */   {
/* 162 */     this.outputStream.write(v & 0xFF);
/* 163 */     this.outputStream.write(v >> 8 & 0xFF);
/* 164 */     this.outputStream.write(v >> 16 & 0xFF);
/* 165 */     this.outputStream.write(v >> 24 & 0xFF);
/*     */   }
/*     */ 
/*     */   public int readInt()
/*     */     throws IOException
/*     */   {
/* 175 */     int res = this.inputStream.read();
/* 176 */     res += (this.inputStream.read() << 8);
/* 177 */     res += (this.inputStream.read() << 16);
/* 178 */     res += (this.inputStream.read() << 24);
/* 179 */     return res;
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 188 */     return this.streamConnection == null;
/*     */   }
/*     */ 
/*     */   public void read(byte[] arr, int len)
/*     */     throws IOException
/*     */   {
/* 202 */     int offs = 0;
/* 203 */     while (len > 0)
/*     */     {
/* 206 */       int count = this.inputStream.read(arr, offs, len);
/* 207 */       len -= count;
/* 208 */       offs += count;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\LifeBook\Dropbox\TennisGame3.jar
 * Qualified Name:     BluetoothConnection
 * JD-Core Version:    0.6.0
 */