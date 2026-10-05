package dev.chalknotes;
import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.graphics.*;
import android.widget.RemoteViews;

public class NoteWidget extends AppWidgetProvider {
 public void onUpdate(Context c,AppWidgetManager manager,int[] ids) { render(c,manager,ids); }
 static void refresh(Context c) { AppWidgetManager m=AppWidgetManager.getInstance(c); render(c,m,m.getAppWidgetIds(new ComponentName(c,NoteWidget.class))); }
 static void render(Context c,AppWidgetManager manager,int[] ids) {
  Bitmap source=NoteStore.read(c,"published"); if(source==null) source=NoteStore.welcome();
  Bitmap rounded=Bitmap.createBitmap(640,640,Bitmap.Config.ARGB_8888); Canvas canvas=new Canvas(rounded); Paint p=new Paint(3); p.setShader(new BitmapShader(source,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)); canvas.drawRoundRect(0,0,640,640,42,42,p);
  for(int id:ids) { RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget); v.setImageViewBitmap(R.id.note_image,rounded); v.setOnClickPendingIntent(R.id.note_image,PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE)); manager.updateAppWidget(id,v); }
 }
}
