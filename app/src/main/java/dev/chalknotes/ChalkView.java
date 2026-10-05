package dev.chalknotes;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.util.ArrayDeque;

class ChalkView extends View {
 Bitmap bitmap; Canvas ink; Paint brush=new Paint(3), display=new Paint(3); int color=0xfff1ecda; float width=7; boolean eraser=false; float lastX,lastY; boolean drawing;
 ArrayDeque<Bitmap> undo=new ArrayDeque<>(),redo=new ArrayDeque<>(); Runnable changed=()->{};
 ChalkView(Context c){super(c); Bitmap saved=NoteStore.read(c,"draft"); bitmap=saved==null?NoteStore.blank():saved.copy(Bitmap.Config.ARGB_8888,true); ink=new Canvas(bitmap); brush.setStrokeCap(Paint.Cap.ROUND); brush.setStrokeJoin(Paint.Join.ROUND); setContentDescription("Chalk drawing board. Draw with one finger."); setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
 protected void onMeasure(int w,int h){int size=MeasureSpec.getSize(w); setMeasuredDimension(size,size);}
 protected void onDraw(Canvas c){super.onDraw(c); Path clip=new Path(); clip.addRoundRect(0,0,getWidth(),getHeight(),24,24,Path.Direction.CW); c.save(); c.clipPath(clip); c.drawBitmap(bitmap,null,new Rect(0,0,getWidth(),getHeight()),display); c.restore();}
 void checkpoint(){undo.addLast(bitmap.copy(Bitmap.Config.ARGB_8888,false)); if(undo.size()>15)undo.removeFirst().recycle(); clearStack(redo);}
 void clearStack(ArrayDeque<Bitmap> stack){while(!stack.isEmpty())stack.removeFirst().recycle();}
 void replace(Bitmap b){bitmap.recycle(); bitmap=b.copy(Bitmap.Config.ARGB_8888,true); ink=new Canvas(bitmap); invalidate(); changed.run();}
 void undo(){if(undo.isEmpty())return; redo.addLast(bitmap.copy(Bitmap.Config.ARGB_8888,false)); Bitmap b=undo.removeLast(); replace(b); b.recycle();}
 void redo(){if(redo.isEmpty())return; undo.addLast(bitmap.copy(Bitmap.Config.ARGB_8888,false)); Bitmap b=redo.removeLast(); replace(b); b.recycle();}
 void clear(){checkpoint(); bitmap.eraseColor(NoteStore.BOARD); invalidate(); changed.run();}
 void importImage(Bitmap b){checkpoint(); Bitmap next=NoteStore.blank(); Canvas c=new Canvas(next); float scale=Math.min(640f/b.getWidth(),640f/b.getHeight()); float w=b.getWidth()*scale,h=b.getHeight()*scale; c.drawBitmap(b,null,new RectF((640-w)/2,(640-h)/2,(640+w)/2,(640+h)/2),display); replace(next); next.recycle();}
 void line(float x,float y){brush.setColor(eraser?NoteStore.BOARD:color); brush.setAlpha(eraser?255:205); brush.setStrokeWidth(eraser?width*4:width); ink.drawLine(lastX,lastY,x,y,brush); if(!eraser){brush.setStrokeWidth(Math.max(1,width*.2f)); brush.setAlpha(95); ink.drawLine(lastX+width*.22f,lastY,x+width*.22f,y,brush);}lastX=x;lastY=y;}
 public boolean onTouchEvent(android.view.MotionEvent e){float x=e.getX()*640/getWidth(),y=e.getY()*640/getHeight(); switch(e.getActionMasked()){
  case MotionEvent.ACTION_DOWN:getParent().requestDisallowInterceptTouchEvent(true);checkpoint();drawing=true;lastX=x;lastY=y;line(x+.01f,y);invalidate();return true;
  case MotionEvent.ACTION_MOVE:if(drawing){for(int i=0;i<e.getHistorySize();i++)line(e.getHistoricalX(i)*640/getWidth(),e.getHistoricalY(i)*640/getHeight());line(x,y);invalidate();}return true;
  case MotionEvent.ACTION_UP:case MotionEvent.ACTION_CANCEL:if(drawing){drawing=false;getParent().requestDisallowInterceptTouchEvent(false);changed.run();performClick();}return true;
 }return true;}
 public boolean performClick(){super.performClick();return true;}
}
