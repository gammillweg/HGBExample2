package com.hgb.hgbexample2;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import appview.GraphicsView;
import hgb.HGBShared;
import model.GameBoardSetup;
import model.Shared;

public class MainActivity extends AppCompatActivity
{
   private final String TAG = this.getClass().getSimpleName();

   private GraphicsView graphicsView;
   private GameBoardSetup gameBoardSetup = null;
   private Shared shared = null;
   private HGBShared hgbShared = null;

   private boolean dbNumberCellsFlg = true;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      graphicsView = findViewById(R.id.graphicsview);

      if (createGameBoard() == false)
      {
         Toast.makeText(this, "Game failed to initiate", Toast.LENGTH_LONG).show();
         finish();
      }
   }

   /**
    * Create the game board
    *
    * @return
    */
   private boolean createGameBoard()
   {
      if (gameBoardSetup != null) { gameBoardSetup = null; }
      gameBoardSetup = new GameBoardSetup();

      if (shared != null) { shared = null; }

      // The One and Only One instance of Shared is created in
      // GameBoardSetup, stored in Shared, and passed back here
      // to pass on down to GraphicView.
      shared = gameBoardSetup.gameBoardInitialize();
      if (shared == null) return false;

      hgbShared = shared.getHGBShared();
      if (hgbShared == null) return false;

      graphicsView.setShared(shared);
      graphicsView.setPath();
      graphicsView.localInit();
      graphicsView.invalidate();
      return true;
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      int id = item.getItemId();

      try
      {
         switch (item.getItemId())
         {
            case R.id.zoomIn:
               this.zoomIn();
               return true;

            case R.id.zoomOut:
               this.zoomOut();
               return true;

            case R.id.expand:
               this.expand();
               return true;

            case R.id.contract:
               this.contract();
               return true;

            case R.id.fill:
               this.fill();
               return true;

            case R.id.report:
               report();
               return true;

            case R.id.numberCells:
               numberCells();
               return true;

             default:
               return super.onOptionsItemSelected(item);
         }
      }
      catch(Exception excp)
      {
         Log.e("FatalError:  ", excp.toString());
         return false;
      }
   }

   public void zoomIn()
   {
      double cellSize = shared.getCellSize();
      cellSize += shared.getZoomStep();
      shared.setCellSize(cellSize);

      graphicsView.clearCollectTouchedCells();
      gameBoardSetup.initHive();
      graphicsView.invalidate();
   }

   public void zoomOut()
   {
      double cellSize = shared.getCellSize();
      cellSize -= shared.getZoomStep();
      shared.setCellSize(cellSize);

      graphicsView.clearCollectTouchedCells();
      gameBoardSetup.initHive();
      graphicsView.invalidate();
   }

   public void expand()
   {
      int roseRings = shared.getRoseRings();
      roseRings++;

      int maxRoseRings = shared.getMaxRoseRings();
      roseRings = (roseRings >= maxRoseRings) ? maxRoseRings : roseRings;
      shared.setRoseRings(roseRings);

      graphicsView.clearCollectTouchedCells();
      gameBoardSetup.initHive();
      graphicsView.invalidate();
   }

   public void contract()
   {
      int roseRings = shared.getRoseRings();
      roseRings--;

      roseRings = (roseRings < 0) ? 0 : roseRings;
      shared.setRoseRings(roseRings);

      graphicsView.clearCollectTouchedCells();
      gameBoardSetup.initHive();
      graphicsView.invalidate();
   }

   public void fill()
   {
      if (graphicsView.demoFillCellsBetween() == false)
      {
         Toast.makeText(this, "Need two selected cells.", Toast.LENGTH_SHORT).show();
      }
      graphicsView.invalidate();
   }

   public void numberCells()
   {
      dbNumberCellsFlg = !dbNumberCellsFlg;
      graphicsView.setDBNumberCellsFlg(dbNumberCellsFlg);
      graphicsView.invalidate();
   }

   public void report()
   {
      // This will compute the number of cells
      //int total = hgbShared.getCellAryLen();
      //int val = total/10;
      //int waste = val * 3;
      //int cells = total - waste;

      // But the count is stored and can be retrieved
      int cells = hgbShared.getCellCount();

      //double cellSize = hgbShared.getCellSize();
      int roses = hgbShared.getRoseCount();
      int rings = hgbShared.getRoseRings();

      //String msg = cells + " cells " + roses + " roses " + rings + " rings,  Cell size:  " + cellSize;
      String msg = rings + " rings, " + roses + " roses, " + cells + " cells"; //, Cell size: " + cellSize;
      Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
   }

}
