package dev.chalknotes;
import android.content.*;
import android.database.*;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.*;

public class ImageProvider extends ContentProvider {
 public boolean onCreate(){return true;}
 private File checked(Uri u) throws FileNotFoundException { if(!"/shared.png".equals(u.getPath())) throw new FileNotFoundException(); return NoteStore.file(getContext(),"shared"); }
 public ParcelFileDescriptor openFile(Uri u,String mode) throws FileNotFoundException { if(!"r".equals(mode)) throw new FileNotFoundException("Read only"); return ParcelFileDescriptor.open(checked(u),ParcelFileDescriptor.MODE_READ_ONLY); }
 public String getType(Uri u){return "image/png";}
 public Cursor query(Uri u,String[] projection,String selection,String[] args,String order) {
  try { File f=checked(u); String[] cols=projection==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:projection; MatrixCursor cursor=new MatrixCursor(cols); Object[] values=new Object[cols.length]; for(int i=0;i<cols.length;i++) values[i]=OpenableColumns.DISPLAY_NAME.equals(cols[i])?"chalk-note.png":OpenableColumns.SIZE.equals(cols[i])?f.length():null; cursor.addRow(values); return cursor; } catch(IOException e){return null;}
 }
 public Uri insert(Uri u,ContentValues v){throw new UnsupportedOperationException();}
 public int update(Uri u,ContentValues v,String s,String[] a){throw new UnsupportedOperationException();}
 public int delete(Uri u,String s,String[] a){throw new UnsupportedOperationException();}
}
