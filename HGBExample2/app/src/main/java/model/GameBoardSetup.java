package model;

import android.graphics.Path;

import hgb.HGBCellPack;
import hgb.HGBGenerateHive;
import hgb.HGBShared;
import hgb.HGBUtils;

public class GameBoardSetup
{
   private final String TAG = this.getClass().getSimpleName();

   //-----------------------------------------
   private HGBGenerateHive hgbGenerateHive = null;

   // Should be the only instance of HGBLocator.
   // Other calls should pass through HGBUtils or HGBShared
   // But here the locator must be initialized

   // Locator is not used in Example2
   // private HGBLocator hgbLocator = null;
   private HGBUtils hgbUtils = null;

   private Path basePath = null;
   private Path hivePath = null;

   // Not used in Example2
   //private RectF baseInscribedRect = null;
   //private RectF baseSuberscribedRectF = null;

   //-----------------------------------------
   private Shared shared = null;  // Common data
   private HGBShared hgbShared = null;  // Common data within isolated classes of HGB
   //-----------------------------------------

   /**
    * Begins a chain of calls to create the hive and a hivePath
    * to allow the graphics of the drawn.
    *
    * @return instance of Shared
    */
   public Shared gameBoardInitialize()
   {
      //====================================================
      // One and ONLY ONE instance of HGBShared and Shared
      // Note that HGBShared and Shared are not Singletons.
      // The presence of One and Only is NOT enforced.
      if (hgbShared != null) hgbShared = null;
      this.hgbShared = new HGBShared();

      if (shared != null) shared = null;
      this.shared = new Shared(hgbShared);
      //========================================================

      // Here do the initial setup for the HGB and filling of hgbShared
      initHive();

      return shared;
   }

   /**
    * Initialize all hive data
    */
   public void initHive()
   {
      // Important: Rose 0 and it's 6 petals is counted as ring 1.
      // It follows ...
      // In GenerateHive.generateHive_Main() one finds two loops
      // "for (int roseRing = 0; roseRing < roseRings; roseRing++)."
      // Thus, roseRing = 0 (as index in the for loop) IS ring 1.
      // Thus, if roseRings == 3, one steps through 0, 1, 2 (THREE rings).
      // (This long note -- as I did once become confused and wondered
      // why I needed roseRing = 4 to generate 3 rings. I was NOT
      // properly counting rose 0 as a ring.)

      // this.roseRings = 2; // 49 cells, arrayLen 70
      // this.roseRings = 3; // 133 cells, arrayLen 190
      // this.roseRings = 4; // 259 cells, arrayLen 370
      // this.roseRings = 5; // 427 cells, arrayLen 610
      // this.roseRings = 6; // 637 cells, arrayLen 910
      // this.roseRings = 7; // 889 cells, arrayLen 1270

      // ------------------------------------------

      // ----------------
      /*
       * from manifest <activity android:name=".MainActivity"
       * android:label="@string/app_name" android:hardwareAccelerated="false"
       * --- working orientation ---
       * android:screenOrientation="sensorLandscape">
       */
      // ----------------

      // Set for horizontal ordination of the device.
      // (OrientationRadians.Landscape)
      // Support orientation Portrait was never written.
      hgbShared.setVertexRadians(HGBShared.OrientationRadians.Landscape);

      // set the default cellSize, pass it onto hgbHexBase() and
      // create a basePath (a hexagon path about (0,0))
      double cellSize = shared.getCellSize();
      hgbShared.setCellSize(cellSize);
      // =========================================================

      generateHive();
   }

   private void generateHive()
   {
      if (hgbGenerateHive == null)
      {
         hgbGenerateHive = new HGBGenerateHive(hgbShared);
      }

      // generateHive gets roseRings and size of cell from hgbShared.
      // and... hgbShared gets roseRings and cellSize from gameShared
      hgbGenerateHive.generateHive_Main();

      // Gets roseRings and hiveOrigin and from hgbShared (which gets
      // roseRings and cellSize and hiveOrigin from shared

/*
      // The locator is not used in Example2
      // The locator is code to find touched cells.

      // The locator can not be created until the hive has been generated.
      // Is passed to shared for distribution.
      if (hgbLocator == null)
      {
         hgbLocator = new HGBLocator(hgbShared);
         hgbShared.setHGBLocator(hgbLocator);

         hgbUtils = new HGBUtils(hgbShared);
         hgbShared.setHGBUtils(hgbUtils);
      }
      hgbLocator.locatorInitialize();
*/

      createHivePath();
   }

   /**
    * Copy the basePath about to each cell:  offset each per the
    * origin of each.  Each offset cell is added to the hivePath
    *
    * The return is storing the path in shared
    */
   private void createHivePath()
   {
      if (hivePath == null)
      {
         hivePath = new Path();
      }
      else
      {
         hivePath.reset();
      }

      // Create a single base hexagon centered around (0,0) to be copied about
      createBaseSingleHexagonPath();

      for (int inx = 0; inx < hgbShared.getCellAryLen(); inx++)
      {
         HGBCellPack hgbCellPack = hgbShared.getCellPack(inx);

         if (hgbCellPack == null)
            continue;
         float[] origin = hgbCellPack.getOrigin();
         if (origin == null)
            continue;

         // Offset the origin as added to the path
         hivePath.addPath(basePath, origin[0], origin[1]);
      }

      // Store the hivePath in shared
      shared.setHivePath(hivePath, basePath);
   }

   /**
    * Create a single base hexagon
    */
   private void createBaseSingleHexagonPath()
   {
      if (basePath == null) { basePath = new Path(); }
      else { basePath.reset(); }

      float[][] baseVertices = hgbShared.getBaseVertices();

      basePath.moveTo(baseVertices[0][0], baseVertices[0][1]);

      for (int inx = 0; inx < HGBShared.SIDES - 1; inx++)
      {
         basePath.lineTo(baseVertices[inx + 1][0], baseVertices[inx + 1][1]);
      }

      // the last line, to close it up.
      basePath.lineTo(
            baseVertices[HGBShared.SIDES - 1][0],
            baseVertices[HGBShared.SIDES - 1][1]);

      basePath.lineTo(baseVertices[0][0], baseVertices[0][1]);
      basePath.close();
   }
}
