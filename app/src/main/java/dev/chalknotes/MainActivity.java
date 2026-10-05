package dev.chalknotes;

import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class MainActivity extends Activity {
 final int bg=0xff121c19,muted=0xff9faea6,cream=0xfff4eedf,mint=0xffbbe2cd; ChalkView board; TextView status; Button eraser; LinearLayout root; int selected=0; Button[] swatches;
 int dp(float n){return (int)(getResources().getDisplayMetrics().density*n+.5f);}
 GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 TextView label(String text,int size,int color){TextView t=new TextView(this);t.setText(text);t.setTextSize(size);t.setTextColor(color);return t;}
 void gap(int n){Space s=new Space(this);root.addView(s,new LinearLayout.LayoutParams(1,dp(n)));}
 Button button(String text,boolean primary,Runnable action){Button b=new Button(this);b.setText(text);b.setAllCaps(false);b.setTextSize(14);b.setTextColor(primary?bg:cream);b.setBackground(shape(primary?mint:0xff26362e,16));b.setMinHeight(dp(48));b.setMinimumWidth(0);b.setPadding(dp(8),0,dp(8),0);b.setOnClickListener(v->action.run());return b;}
 void row(Button...buttons){LinearLayout row=new LinearLayout(this);for(Button b:buttons){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(50),1);p.setMargins(dp(3),0,dp(3),0);row.addView(b,p);}root.addView(row);}
 public void onCreate(Bundle saved){super.onCreate(saved);getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg);
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(bg);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(24),dp(20),dp(24),dp(28));scroll.addView(root);setContentView(scroll);
  scroll.setOnApplyWindowInsetsListener((v,insets)->{if(Build.VERSION.SDK_INT>=30){Insets bars=insets.getInsets(WindowInsets.Type.systemBars());v.setPadding(bars.left,bars.top,bars.right,bars.bottom);}return insets;});
  TextView eyebrow=label("CHALK NOTES   /   YOUR LITTLE CORNER",11,mint);eyebrow.setLetterSpacing(.12f);root.addView(eyebrow);gap(18);
  TextView title=label("Small notes.\nBig feelings.",36,cream);title.setTypeface(Typeface.create("serif",Typeface.NORMAL));root.addView(title);gap(10);
  root.addView(label("A thought, a doodle, a little love. Make it yours.",14,muted));gap(22);
  board=new ChalkView(this);root.addView(board,new LinearLayout.LayoutParams(-1,-2));board.changed=()->{status.setText("Draft saved · publish when you’re ready");saveDraft();};gap(16);
  int[] colors={0xfff1ecda,0xfff2b3c5,0xfff1ce79,0xffb5d9c7,0xff9bc4e8,0xffccafe8};String[] names={"Cream","Rose","Honey","Mint","Sky","Lilac"};swatches=new Button[colors.length];LinearLayout palette=new LinearLayout(this);
  for(int i=0;i<colors.length;i++){final int ix=i;Button b=button(i==0?"✓":"",false,()->{selected=ix;board.color=colors[ix];board.eraser=false;eraser.setText("Eraser");for(int j=0;j<swatches.length;j++)swatches[j].setText(j==ix?"✓":"");});b.setBackground(shape(colors[i],50));b.setTextColor(bg);b.setContentDescription(names[i]+" chalk");LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(44),1);p.setMargins(dp(4),0,dp(4),0);palette.addView(b,p);swatches[i]=b;}root.addView(palette);gap(10);
  LinearLayout sizeRow=new LinearLayout(this);sizeRow.setGravity(Gravity.CENTER_VERTICAL);sizeRow.addView(label("Chalk size",12,muted));SeekBar seek=new SeekBar(this);seek.setMax(17);seek.setProgress(5);seek.setContentDescription("Chalk size");seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean u){board.width=p+2;}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});sizeRow.addView(seek,new LinearLayout.LayoutParams(0,dp(44),1));root.addView(sizeRow);gap(8);
  eraser=button("Eraser",false,()->{board.eraser=!board.eraser;eraser.setText(board.eraser?"Drawing":"Eraser");});row(button("Undo",false,()->board.undo()),button("Redo",false,()->board.redo()),eraser,button("Clear",false,()->new AlertDialog.Builder(this).setTitle("Clear this drawing?").setMessage("You can undo this while the app stays open.").setNegativeButton("Keep",null).setPositiveButton("Clear",(d,w)->board.clear()).show()));gap(18);
  row(button("Put on my widget",true,()->publish()));gap(10);row(button("Share drawing",false,()->share()),button("Open image",false,()->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,10);}));gap(10);
  row(button("Add home-screen widget",false,()->pin()));gap(14);status=label("Just you and a blank board. Say something sweet.",12,muted);status.setGravity(Gravity.CENTER);root.addView(status);gap(18);
  TextView help=label("FREE, ALWAYS. NO ADS.\n\nShare a drawing with someone you love. They can open it in Chalk Notes and tap ‘Put on my widget’. Sharing is manual; there’s no account or automatic sync.",12,muted);help.setLineSpacing(dp(3),1);root.addView(help);
  if(Intent.ACTION_SEND.equals(getIntent().getAction())){Uri u=getIntent().getParcelableExtra(Intent.EXTRA_STREAM);if(u!=null)confirmImport(u);}
 }
 void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
 void saveDraft(){try{NoteStore.save(this,"draft",board.bitmap);}catch(IOException e){status.setText("Couldn’t save draft. Check your device storage.");}}
 protected void onPause(){super.onPause();if(board!=null)saveDraft();}
 void publish(){try{NoteStore.save(this,"published",board.bitmap);NoteWidget.refresh(this);status.setText("On your home screen, with love. ♡");if(AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this,NoteWidget.class)).length==0)pin();else toast("Widget updated");}catch(Exception e){toast("Couldn’t update widget. Please try again.");}}
 void pin(){AppWidgetManager m=AppWidgetManager.getInstance(this);if(m.isRequestPinAppWidgetSupported())m.requestPinAppWidget(new ComponentName(this,NoteWidget.class),null,null);else new AlertDialog.Builder(this).setTitle("Add your widget").setMessage("Touch and hold an empty spot on your home screen. Choose Widgets, find Chalk Notes, then drag it onto your home screen.").setPositiveButton("Got it",null).show();}
 void share(){try{NoteStore.save(this,"shared",board.bitmap);Uri uri=Uri.parse("content://dev.chalknotes.images/shared.png");Intent i=new Intent(Intent.ACTION_SEND);i.setType("image/png");i.putExtra(Intent.EXTRA_STREAM,uri);i.setClipData(ClipData.newRawUri("Chalk note",uri));i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(Intent.createChooser(i,"Send a little note"));}catch(Exception e){toast("Couldn’t share the drawing.");}}
 protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==10&&result==RESULT_OK&&data!=null&&data.getData()!=null)confirmImport(data.getData());}
 void confirmImport(Uri uri){new AlertDialog.Builder(this).setTitle("Open this drawing?").setMessage("This replaces your draft. You can undo it before closing the app.").setNegativeButton("Cancel",null).setPositiveButton("Open",(d,w)->importImage(uri)).show();}
 void importImage(Uri uri){try{BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;try(InputStream in=getContentResolver().openInputStream(uri)){BitmapFactory.decodeStream(in,null,o);}if(o.outWidth<=0||o.outHeight<=0)throw new IOException();o.inSampleSize=1;while(o.outWidth/o.inSampleSize>1280||o.outHeight/o.inSampleSize>1280)o.inSampleSize*=2;o.inJustDecodeBounds=false;Bitmap b;try(InputStream in=getContentResolver().openInputStream(uri)){b=BitmapFactory.decodeStream(in,null,o);}if(b==null)throw new IOException();board.importImage(b);b.recycle();status.setText("Drawing opened · tap ‘Put on my widget’ to use it");}catch(Exception e){toast("Couldn’t open that image. Try a PNG or JPG.");}}
}
