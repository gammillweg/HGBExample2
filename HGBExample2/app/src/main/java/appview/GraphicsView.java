package appview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.core.view.GestureDetectorCompat;
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
   // Hive info
   private int cellIndex = -1;
   private HGBCellPack hgbCellPack = null;
   // ---------------------------

   // ------------------------------------------------------
   // See implementations at bottom of file
   // private OnGestureListener gestureListener = new OnGestureListener()
   protected GestureDetectorCompat mDetector = null;

   //----------------------------------------------------------------
   public void localInit()
   {
      setFocusableInTouchMode(true);
      if (mDetector == null)
      {
         mDetector = new GestureDetectorCompat(context, gestureListener);
      }

      baseOrigin = new float[2];
      baseOrigin[0] = 0;
      baseOrigin[1] = 0;

      hiveOrigin = new float[2];
      hiveOrigin[0] = hgbShared.getHiveOrigin()[0];
      hiveOrigin[1] = hgbShared.getHiveOrigin()[1];

      hgbUtils = hgbShared.getHGBUtils();

      collectTouchedCells = new ArrayList<Integer>();
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
   Path basePath = null;
   Path cellPath = null;

   public void setPath()
   {
      this.hivePath = shared.getHivePath();
      this.basePath = shared.getBasePath();
      this.cellPath = new Path();
   }

   private boolean dbNumberCellsFlg =  true; //false;
   public void setDBNumberCellsFlg(boolean dbNumberCellsFlg)
   {
      this.dbNumberCellsFlg = dbNumberCellsFlg;
   }

   private ArrayList<Integer> collectTouchedCells = null;
   public void clearCollectTouchedCells()
   {
      collectTouchedCells.clear();
      cellPath.reset();
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
         centerHiveOrigin(width, height);
      }

      // Draw the boarder rectangle
      paint.setStyle(Paint.Style.STROKE);
      paint.setColor(Color.BLUE);
      canvas.drawRect(borderRect, paint);

      drawHivePath(canvas);

      debugFillCells(canvas);
      debugNumberCells(canvas, paint);
   }

   private void centerHiveOrigin(int width, int height)
   {
      float centerX = (float)width/2;
      float centerY = (float)height/2;

      float distanceX = hiveOrigin[0] - centerX;
      float distanceY = hiveOrigin[1] - centerY;
      Matrix hiveMatrix = new Matrix();
      hiveMatrix.setTranslate(-distanceX, -distanceY);
      onTranslateHive(hiveMatrix, -distanceX, -distanceY);

      hiveOrigin[0] = centerX;
      hiveOrigin[1] = centerY;

      if (shared != null)
      {
         shared.setHiveOrigin(hiveOrigin);
      }
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

         // The cell counted as in the loop is the cell ID
         // One could write:  int ID = hgbCellPack.getID()
         // but is a waste of cycles... just use inx.
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

   private void debugFillACell(Canvas canvas/*, Paint paint, int cell*/)
   {
      // ----------------------------------------------
      // DEBUG... show here how to fill a cell
      HGBCellPack hgbCellPack = hgbShared.getCellPack(cellIndex/*cell*/);
      if (hgbCellPack != null)
      {
         float[] org = hgbCellPack.getOrigin();
         cellPath.reset();
         cellPath.addPath(basePath, org[0], org[1]);

         int origColor = paint.getColor();
         paint.setColor(Color.RED);
         paint.setStyle(Paint.Style.FILL);
         canvas.drawPath(cellPath, paint);

         paint.setColor(origColor);
         paint.setStyle(Paint.Style.STROKE);
      }
      // ----------------------------------------------

   }

   private void debugFillCells(Canvas canvas/*, Paint paint, int cell*/)
   {
      int origColor = paint.getColor();
      paint.setColor(Color.RED);
      paint.setStyle(Paint.Style.FILL);
      //canvas.drawPath(cellPath, paint);

      // DEBUG... show here how to fill a cell
      for (int index : collectTouchedCells)
      {
         HGBCellPack hgbCellPack = hgbShared.getCellPack(index);
         if (hgbCellPack != null)
         {
            float[] org = hgbCellPack.getOrigin();
            cellPath.addPath(basePath, org[0], org[1]);
         }
         canvas.drawPath(cellPath, paint);
      }
   }
   //=================================================================

   protected void onTranslateHive(Matrix matrix, float dx, float dy)
   {
      // Translate the hivePath
      hivePath.transform(matrix);
      hiveOrigin[0] += dx;
      hiveOrigin[1] += dy;

      // Keep the selected cells synchronized with the hive
      cellPath.transform(matrix);

      hgbShared.setHiveOrigin(hiveOrigin);
   }

   // ===========================================================================

   /*
   This will capture any touch before any others
   I had a bug where ontouchEven() (below) returned mDetector; which I had not created.
   As it was null, the app crashed on any touch of the screen with no stack trace.  Was
   The devil to find, until I found this on stackoverflow
   http://stackoverflow.com/questions/12168254/catch-ontouch-event-by-parent-handle-it-and-pass-to-children
   and then the touch even below (I was looking in the activity!), and handled the
   crash correctly.
   @Override
   public boolean dispatchTouchEvent (MotionEvent ev)
   {
      //return true;
      return super.dispatchTouchEvent(ev);
   }
*/


   /**
    * Here is a pertinent comment I read while reading about onDragEvent that
    * improves my lack of understanding on onTouchEvent.
    *
    * You can have both a listener and a callback method for View object. If
    * this occurs, the system first calls the listener. The system doesn't call
    * the callback method unless the listener returns false.
    *
    * The combination of the onDragEvent(DragEvent) method and
    * View.OnDragListener is analogous to the combination of the onTouchEvent()
    * and View.OnTouchListener used with touch events.
    */
   @SuppressLint("ClickableViewAccessibility")
   // ///////////////////////////////////////////////////////////////////////
   // This is only called if the onTouchListener returns false.
   @Override
   public boolean onTouchEvent(MotionEvent event)
   {
      //Log.d(TAG, "onTouchEvent: " + event.getX() + ". " + event.getY());

      // mDetectLor is not implemented yet
      return mDetector.onTouchEvent(event);
   }

   /**
    * Implement GestureDetectorCompat gestureListener mDetector = new
    * GestureDetectorCompat(context, gestureListener);
    */
   private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener()
   {
      // Used in onScroll to keep the cursor from initially jumping on the
      // FIRST scroll event
      private Matrix hiveMatrix = new Matrix();

      @Override
      public boolean onDown(MotionEvent event)
      {
         //Log.d(TAG, "onDown()" + event.getX() + ". " + event.getY());
         return true;
      }

      @Override
      public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY)
      {
         return false;
      }

      @Override
      public void onLongPress(MotionEvent event)
      {
      }

      @Override
      public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY)
      {
         //Log.d(TAG, "onScroll()");

         hiveMatrix.setTranslate(-distanceX, -distanceY);
         onTranslateHive(hiveMatrix, -distanceX, -distanceY);

         invalidate();
         return true;
      }

      @Override
      public void onShowPress(MotionEvent event)
      {
         // Log.d(TAG, "onShowPress()");
      }

      @Override
      public boolean onSingleTapUp(MotionEvent event)
      {
         //Log.d(TAG, "onSingleTapUp()" + event.getX() + ". " + event.getY());
         cellIndex = hgbUtils.getCellIndex(event.getX(), event.getY());

         if (cellIndex != -1)
         {
            collectTouchedCells.add(cellIndex);
         }
         else
         {
            // click outside of the hive used to clear collected touched cells
            clearCollectTouchedCells();
         }

         invalidate();

         return true;
      }
   };
}
