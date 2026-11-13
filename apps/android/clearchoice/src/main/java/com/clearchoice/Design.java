package com.clearchoice;
import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.view.*;import android.widget.*;
/** Shared native visual language from the approved generated references. */
final class Design {
 static final int BG=0xfff5f6fc,CARD=0xffffffff,WHITE=0xff101429,MUTED=0xff52596f,PURPLE=0xff5642ff;
 /** Dark control-center palette (purple restyle). Contrast ratios are listed in docs/qa/ui-review.md. */
 static final int NIGHT=0xff0d0b12,GLOW=0x387c3aed,SURFACE=0xe01e1a2a,SURFACE_HEAD=0x0dffffff,EDGE=0x1fffffff,TEXT=0xfff5f2fa,SUBTLE=0xffaca6bb,LILAC=0xffc9a0ff,LILAC_SOFT=0xffdcc3ff,VIOLET=0xff8247e6,PINK=0xffad3a96,RAISED=0xff2b2638,RAISED_EDGE=0x38ffffff,NAV=0xff17141f,NAV_ACTIVE=0xff3a2a5a,CHIP_TEXT=0xffddd7ea,GREEN=0xff8de5af,GREEN_BG=0xff17382a,TRACK_ON=0xff8b5cf6,TRACK_OFF=0xff6e6882,RIPPLE=0x33c9a0ff;
 /** Near-black page with a faint violet glow at the top and a weaker one at the lower left. */
 static Drawable night(Context c){GradientDrawable top=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{GLOW,0x00000000});top.setGradientType(GradientDrawable.RADIAL_GRADIENT);top.setGradientCenter(.5f,0f);top.setGradientRadius(dp(c,520));GradientDrawable low=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0x1f7c3aed,0x00000000});low.setGradientType(GradientDrawable.RADIAL_GRADIENT);low.setGradientCenter(0f,1f);low.setGradientRadius(dp(c,420));return new LayerDrawable(new Drawable[]{new ColorDrawable(NIGHT),top,low});}
 /** Dark translucent rounded surface with a thin subtle border. */
 static GradientDrawable surface(Context c,int radius){GradientDrawable d=shape(SURFACE,dp(c,radius));d.setStroke(dp(c,1),EDGE);return d;}
 static int dp(Context c,float n){return (int)(n*c.getResources().getDisplayMetrics().density+.5f);}
 static GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
 static GradientDrawable outline(Context c,int color,int radius){GradientDrawable d=shape(color,dp(c,radius));d.setStroke(dp(c,1),0xffe0e4f0);return d;}
 static TextView text(Context c,String s,int size,int color,boolean bold){TextView t=new TextView(c);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setFontFeatureSettings("kern");t.setTypeface(android.graphics.Typeface.create("sans-serif",bold?1:0));t.setIncludeFontPadding(false);return t;}
 static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(1);return l;}
 static LinearLayout card(Context c){LinearLayout l=column(c);int p=dp(c,18);l.setPadding(p,p,p,p);GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xffffffff,0xfffafbff});d.setCornerRadius(dp(c,14));d.setStroke(dp(c,1),0xffe1e5f0);l.setBackground(d);return l;}
 /** Light QA overlay button: routed through the shared glass factory (light regular glass, purple label). */
 static TextView button(Context c,String s,Runnable action){TextView t=text(c,s,14,PURPLE,true);t.setGravity(17);t.setPadding(dp(c,16),dp(c,11),dp(c,16),dp(c,11));t.setOnClickListener(v->action.run());return glass(t,GLASS_LIGHT,10,0);}
 /** Liquid glass button styles. REGULAR = secondary, PROMINENT = tinted violet to pink. LIGHT variants are for the light QA overlay. */
 static final int GLASS_REGULAR=0,GLASS_PROMINENT=1,GLASS_LIGHT=2,GLASS_LIGHT_PROMINENT=3,RIPPLE_LIGHT=0x295642ff;
 /**
  * The single shared button factory. Every tappable button in the app gets its material, press ripple,
  * focus ring, 48dp minimum target and label colour here. insetDp insets the glass vertically inside a
  * taller touch target. Large text still wraps: no maxLines or fixed height is set.
  */
 static <T extends View> T glass(T v,int style,int radiusDp,int insetDp){Context c=v.getContext();int in=dp(c,insetDp);Drawable fill=new Glass(c,style,radiusDp,true);Drawable ripple=new RippleDrawable(android.content.res.ColorStateList.valueOf(style>=GLASS_LIGHT?RIPPLE_LIGHT:RIPPLE),null,shape(0xff000000,dp(c,radiusDp)));v.setBackground(in>0?new InsetDrawable(fill,0,in,0,in):fill);v.setForeground(in>0?new InsetDrawable(ripple,0,in,0,in):ripple);v.setMinimumHeight(dp(c,48));v.setMinimumWidth(dp(c,48));v.setClickable(true);v.setFocusable(true);if(v instanceof TextView)((TextView)v).setTextColor(style==GLASS_LIGHT?PURPLE:style==GLASS_REGULAR?TEXT:0xffffffff);return v;}
 /** Non-interactive regular glass for a group of buttons (tab track, nav bar): the Android stand-in for GlassEffectContainer. Its children stay clear so glass is not nested; only the selected child gets a glass lens. */
 static Drawable glassBar(Context c,int radiusDp){return new Glass(c,GLASS_REGULAR,radiusDp,false);}
 /**
  * Liquid glass material drawn with real Canvas APIs only. There is no backdrop blur: Android has no
  * in-window background blur for views, so the "glass" is a translucent tint plus light effects.
  * Layers: translucent tinted fill, soft inner highlight on the top 55%, 1dp top-lit specular rim
  * (white gradient, brighter at the top), and a 2dp lilac ring when focused. Pressed brightens the rim
  * (prominent also deepens its fill); selected deepens the regular tint; disabled draws at 45% alpha.
  * Contrast numbers for every colour here are in docs/qa/ui-review.md ("Liquid glass buttons").
  */
 static final class Glass extends Drawable {
  final int style;final float radius,rim,ring;final boolean interactive;boolean pressed,focused,selected,enabled=true;int alpha=255;
  final Paint fill=new Paint(Paint.ANTI_ALIAS_FLAG),sheen=new Paint(Paint.ANTI_ALIAS_FLAG),edge=new Paint(Paint.ANTI_ALIAS_FLAG),focus=new Paint(Paint.ANTI_ALIAS_FLAG),shade=new Paint(Paint.ANTI_ALIAS_FLAG);
  final RectF box=new RectF();final Path clip=new Path();
  Glass(Context c,int style,int radiusDp,boolean interactive){this.style=style;this.interactive=interactive;radius=dp(c,radiusDp);rim=Math.max(1,dp(c,1));ring=dp(c,2);edge.setStyle(Paint.Style.STROKE);edge.setStrokeWidth(rim);focus.setStyle(Paint.Style.STROKE);focus.setStrokeWidth(ring);focus.setColor(light()?PURPLE:LILAC);shade.setColor(0x1f000000);}
  boolean prominent(){return style==GLASS_PROMINENT||style==GLASS_LIGHT_PROMINENT;}
  boolean light(){return style>=GLASS_LIGHT;}
  float r(){return Math.min(radius,box.height()/2f);}
  void shade(){Rect b=getBounds();box.set(b);box.inset(rim/2f,rim/2f);if(box.isEmpty())return;
   if(prominent()){int a=light()?0xf5:0xeb;fill.setShader(new LinearGradient(box.left,0,box.right,0,(a<<24)|(VIOLET&0xffffff),(a<<24)|(PINK&0xffffff),Shader.TileMode.CLAMP));}
   else{fill.setShader(null);fill.setColor(light()?(selected?0x335642ff:0x1a5642ff):(selected?0x4d8b5cf6:0x2e8b5cf6));}
   int glint=light()?(prominent()?0:0x80ffffff):0x14ffffff;sheen.setShader(glint==0?null:new LinearGradient(0,box.top,0,box.top+box.height()*.55f,glint,glint&0xffffff,Shader.TileMode.CLAMP));sheen.setColor(glint==0?0:0xff000000);
   int top=light()?0xcc5642ff:pressed?0xe6ffffff:0xb3ffffff,bottom=light()?0xff5642ff:pressed?0x80ffffff:0x66ffffff;edge.setShader(new LinearGradient(0,box.top,0,box.bottom,top,bottom,Shader.TileMode.CLAMP));}
  @Override protected void onBoundsChange(Rect b){shade();}
  @Override public void draw(Canvas c){if(box.isEmpty())return;int a=enabled?alpha:alpha*115/255;int layer=a<255?c.saveLayerAlpha(new RectF(getBounds()),a):-1;float r=r();
   c.drawRoundRect(box,r,r,fill);if(pressed&&prominent())c.drawRoundRect(box,r,r,shade);
   if(sheen.getShader()!=null){c.save();clip.reset();clip.addRoundRect(box,r,r,Path.Direction.CW);c.clipPath(clip);c.drawRect(box.left,box.top,box.right,box.top+box.height()*.55f,sheen);c.restore();}
   c.drawRoundRect(box,r,r,edge);
   if(focused){RectF f=new RectF(box);f.inset(ring/2f-rim/2f,ring/2f-rim/2f);float fr=Math.max(0,r-(ring/2f-rim/2f));c.drawRoundRect(f,fr,fr,focus);}
   if(layer>=0)c.restoreToCount(layer);}
  @Override public boolean isStateful(){return interactive;}
  @Override protected boolean onStateChange(int[] state){boolean p=false,f=false,s=false,e=false;for(int x:state){if(x==android.R.attr.state_pressed)p=true;else if(x==android.R.attr.state_focused)f=true;else if(x==android.R.attr.state_selected)s=true;else if(x==android.R.attr.state_enabled)e=true;}
   if(p==pressed&&f==focused&&s==selected&&e==enabled)return false;pressed=p;focused=f;selected=s;enabled=e;shade();invalidateSelf();return true;}
  @Override public void setAlpha(int a){alpha=a;invalidateSelf();}
  @Override public void setColorFilter(ColorFilter cf){fill.setColorFilter(cf);sheen.setColorFilter(cf);edge.setColorFilter(cf);invalidateSelf();}
  @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
  @Override public void getOutline(Outline o){Rect b=getBounds();o.setRoundRect(b,Math.min(radius,b.height()/2f));}
 }
 static class Icon extends View {String kind;int color;Paint p=new Paint(3);Icon(Context c,String k,int col){super(c);kind=k;color=col;setContentDescription(k);}protected void onDraw(Canvas raw){super.onDraw(raw);raw.save();raw.scale(getWidth()/32f,getHeight()/32f);p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.8f);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);Path q=new Path();switch(kind){case "windows":raw.drawRoundRect(3,10,23,28,3,3,p);raw.drawLine(10,5,27,5,p);raw.drawLine(27,5,27,22,p);break;case "tag":q.moveTo(3,20);q.lineTo(19,4);q.lineTo(28,4);q.lineTo(28,13);q.lineTo(12,29);q.close();raw.drawPath(q,p);raw.drawCircle(23,9,1,p);break;case "person":raw.drawCircle(16,6,4,p);raw.drawLine(4,13,16,16,p);raw.drawLine(16,16,28,13,p);raw.drawLine(16,16,12,29,p);raw.drawLine(16,16,21,29,p);break;case "Apps":for(int x=4;x<25;x+=14)for(int y=4;y<25;y+=14)raw.drawRoundRect(x,y,x+9,y+9,2,2,p);break;case "Activity":raw.drawCircle(16,16,12,p);raw.drawLine(16,8,16,17,p);raw.drawLine(16,17,22,21,p);break;case "lock":raw.drawRoundRect(6,14,26,29,3,3,p);raw.drawArc(10,2,22,24,180,180,false,p);raw.drawCircle(16,21,1,p);break;case "back":raw.drawLine(22,5,10,16,p);raw.drawLine(10,16,22,27,p);break;case "chevron":raw.drawLine(12,7,21,16,p);raw.drawLine(21,16,12,25,p);break;case "shield":case "brand":q.moveTo(16,2);q.lineTo(28,7);q.lineTo(27,20);q.quadTo(24,27,16,30);q.quadTo(8,27,5,20);q.lineTo(4,7);q.close();raw.drawPath(q,p);raw.drawLine(10,15,14,19,p);raw.drawLine(14,19,22,11,p);break;case "settings":raw.drawCircle(16,16,6,p);for(int i=0;i<8;i++){raw.save();raw.rotate(i*45,16,16);raw.drawLine(16,3,16,7,p);raw.restore();}raw.drawCircle(16,16,12,p);break;default:q.moveTo(16,2);q.quadTo(18,13,30,16);q.quadTo(18,19,16,30);q.quadTo(13,19,2,16);q.quadTo(13,13,16,2);raw.drawPath(q,p);}raw.restore();}}
}
