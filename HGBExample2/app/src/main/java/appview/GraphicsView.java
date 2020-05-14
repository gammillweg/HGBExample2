package appview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import hgb.HGBCellPack;
import hgb.HGBShared;
import hgb.HGBUtils;
import model.Shared;

public class GraphicsView extends View
{
   private final String TAG = this.getClass().getSimpleName();
   //----------------------------------------------------------------


   /**
    * @param context the rest of the application
    */
   public GraphicsView(Context context)
   {
      super(context);
      // this inflate is for when I extended PlayView as FrameLayout
      // But I think extending View is the better option.  Even so,
      // be awary that PlayView is under a FrameLayout.
      //inflate(context, R.layout.activity_main, this);
      this.context = context;
   }

   /**
    * @param context
    * @param attrs
    */
   public GraphicsView(Context context, AttributeSet attrs)
   {
      super(context, attrs);
      this.context = context;
   }

   /**
    * @param context
    * @param attrs
    * @param defStyle
    */
   public GraphicsView(Context context, AttributeSet attrs, int defStyle)
   {
      super(context, attrs, defStyle);
      this.context = context;
   }

   //----------------------------------------------------------------
   private Context context = null;

   private HGBUtils hgbUtils = null;

   private Rect borderRect = null;

   private Paint paint = new Paint();
   private float[] baseOrigin = null;
   private float[] hiveOrigin = null;

   // -----------------------------------------------------

   protected float cellX; // adjusted touch point -- centered in a cell
   protected float cellY; // adjusted touch point -- centered in a cell

   protected float touchX; // unadjusted touch point
   protected float touchY; // unadjusted touch point
   protected float lastTouchX;
   protected float lastTouchY;

   private float centerX;
   private float centerY;

   // ---------------------------
   // Hive info
   private int cellIndex = -1;
   private HGBCellPack hgbCellPack = null;
   // ---------------------------

   //----------------------------------------------------------------
   public void localInit()
   {
      setFocusableInTouchMode(true);

      baseOrigin = new float[2];
      baseOrigin[0] = 0;
      baseOrigin[1] = 0;

      hiveOrigin = new float[2];
      hiveOrigin[0] = hgbShared.getHiveOrigin()[0];
      hiveOrigin[1] = hgbShared.getHiveOrigin()[1];

      hgbUtils = hgbShared.getHGBUtils();
   }

   // ------------------------------------------------------
   // Getters and Setters
   private Shared shared = null;
   private HGBShared hgbShared = null;
   public void setShared(Shared shared)
   {
      this.shared = shared;
      this.hgbShared = shared.getHGBShared();
   }

   Path hivePath = null;

   public void setPath()
   {
      this.hivePath = shared.getHivePath();
   }

   private boolean dbNumberCellsFlg =  true; //false;
   public void setDBNumberCellsFlg(boolean dbNumberCellsFlg)
   {
      this.dbNumberCellsFlg = dbNumberCellsFlg;
   }

   //=================================================================
   @Override
   protected void onDraw(Canvas canvas)
   {
      super.onDraw(canvas);

      //---------------------------------------------------------
      int width = this.getWidth() - 2;
      int height = this.getHeight() - 2;

      if (borderRect == null)
      {
         borderRect = new Rect(0, 0, width, height);
      }

      // Draw the boarder rectangle
      paint.setStyle(Paint.Style.STROKE);
      paint.setColor(Color.BLUE);
      canvas.drawRect(borderRect, paint);

      // Draw the Hive
      drawHivePath(canvas);

      debugNumberCells(canvas, paint);
   }

   private void drawHivePath(Canvas canvas)
   {
      if (shared != null)
      {
         Path hivePath = shared.getHivePath();
         if (hivePath != null)
         {
            canvas.drawPath(hivePath, paint);
         }
      }
   }

   public void debugNumberCells(Canvas canvas, Paint paint2)
   {
      if (dbNumberCellsFlg)
      {
         paint.setColor(Color.BLACK);
         paint.setTextAlign(Paint.Align.CENTER);

         // Rough match text size with cell size
         double cellSize = shared.getCellSize();
         int textSize = (int)cellSize/2;
         paint.setTextSize(textSize);

         for (int inx = 0; inx < hgbShared.getCellAryLen(); inx++)
         {
            HGBCellPack hgbCellPack = hgbShared.getCellPack(inx);
            if (hgbCellPack == null)
               continue;

            float[] origin = hgbCellPack.getOrigin();
            canvas.drawText(String.valueOf(inx), origin[0], origin[1], paint);
         }
      }
   }

}
