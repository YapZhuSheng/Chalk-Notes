package dev.chalknotes;

import android.content.Context;
import android.graphics.*;
import android.util.AtomicFile;
import java.io.*;

final class NoteStore {
 static final int SIZE=640, BOARD=Color.rgb(36,53,46);
 static File file(Context c,String name) { return new File(c.getFilesDir(),name+".png"); }
 static Bitmap blank() { Bitmap b=Bitmap.createBitmap(SIZE,SIZE,Bitmap.Config.ARGB_8888); b.eraseColor(BOARD); return b; }
 static Bitmap read(Context c,String name) {
  AtomicFile f=new AtomicFile(file(c,name));
  try(FileInputStream in=f.openRead()) { return BitmapFactory.decodeStream(in); } catch(IOException e) { return null; }
 }
 static void save(Context c,String name,Bitmap b) throws IOException {
  AtomicFile f=new AtomicFile(file(c,name)); FileOutputStream out=null;
  try { out=f.startWrite(); if(!b.compress(Bitmap.CompressFormat.PNG,100,out)) throw new IOException("Image encoding failed"); f.finishWrite(out); }
  catch(IOException e) { if(out!=null) f.failWrite(out); throw e; }
 }
 static Bitmap welcome() {
  Bitmap b=blank(); Canvas canvas=new Canvas(b); Paint p=new Paint(3); p.setColor(0xffe8e5d6); p.setTextAlign(Paint.Align.CENTER); p.setTypeface(Typeface.create("casual",Typeface.NORMAL)); p.setTextSize(70); canvas.drawText("a little note",320,280,p); canvas.drawText("just for you ♡",320,370,p); return b;
 }
}
