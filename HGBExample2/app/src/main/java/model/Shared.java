package model;

import android.graphics.Path;

import hgb.HGBLocator;
import hgb.HGBShared;

/*
 * There should be ONE and ONLY ONE instance of Shared.
 * This is not enforced.
 *
 * In this app a single instance of Shared is created in GameBoardSetup.gameBoardInitialize().
 * Passed back to MainActivity as the return and passed down as need to all other classes.
*/

public class Shared
{
   private final String TAG = this.getClass().getSimpleName();

   public Shared(HGBShared hgbShared)
   {
      this.hgbShared = hgbShared;

      //===================================================================
      // Here Set Defaults
      this.maxRoseRings = 60;

      this.defaultRoseRings  = 4;
      this.setRoseRings(defaultRoseRings);

      this.defaultCellSize = 25f;
      this.setCellSize(defaultCellSize);

      // Note that in GraphicsView, setHiveOrigin() is called to
      // center the hive, so these defaults are not used.
      this.defaultHiveOrigin = new float[2];
      this.defaultHiveOrigin[0] = 850f;
      this.defaultHiveOrigin[1] = 550f;

      this.hiveOrigin = new float[2];
      this.setHiveOrigin(defaultHiveOrigin);

      this.zoomStep = 5d;
    }

   private HGBShared hgbShared = null;
   public HGBShared getHGBShared() { return hgbShared; }

   private HGBLocator hgbLocator = null;
   public void setHGBLocator(HGBLocator hgbLocator) { this.hgbLocator = hgbLocator; }
   public HGBLocator getHGBLocator() { return hgbLocator; }

   //==================== Hive Data =====================
   private int defaultRoseRings;
   public int getDefaultRoseRings() { return this.defaultRoseRings; }

   private double defaultCellSize;
   public double getDefaultCellSize() { return this.defaultCellSize;  }

   private float[] defaultHiveOrigin;
   public float[] getDefaultHiveOrigin() { return this.defaultHiveOrigin; }

   //-----------------------------------------------------------------
   // define the hive...
   // passed through to HGBShared

   private int maxRoseRings;
   public int getMaxRoseRings() { return this.maxRoseRings; }
   public void setMaxRoseRings(int maxRoseRings)
   {
      this.maxRoseRings = maxRoseRings;
      hgbShared.setMaxRoseRings(maxRoseRings);
   }

   private int roseRings;
   public int getRoseRings() { return roseRings; }
   public void setRoseRings(int roseRings)
   {
      // Insure rules are obeyed (max and min number of roses)
      this.roseRings = roseRings;
      if (roseRings > this.maxRoseRings) this.roseRings = this.maxRoseRings;
      if (roseRings < 0) this.roseRings = 0;

      // Pass on through the HGB
      hgbShared.setRoseRings(roseRings);
   }

   private double cellSize;
   public double getCellSize() { return cellSize; }
   public void setCellSize(double cellSize)
   {
      if (cellSize < 0) cellSize = 0;
      this.cellSize = cellSize;
      hgbShared.setCellSize(cellSize);
   }

   private float[] hiveOrigin;
   public float[] getHiveOrigin() { return hiveOrigin; }
   public void setHiveOrigin(float[] hiveOrigin)
   {
      this.hiveOrigin[0] = hiveOrigin[0];
      this.hiveOrigin[1] = hiveOrigin[1];
      hgbShared.setHiveOrigin(hiveOrigin);
   }

   //-----------------------------------------------------------------
   private double zoomStep;
   public double getZoomStep() { return zoomStep; }
   public void setZoomStep(double zoomStep) { this.zoomStep = zoomStep; }

   //-----------------------------------------------------------------

   private Path hivePath = null;
   private Path basePath = null;
   public Path getHivePath() { return hivePath; }
   public Path getBasePath() { return basePath; }

   public void setHivePath(Path hivePath, Path basePath)
   {
      this.hivePath = hivePath;
      this.basePath = basePath;
   }
   //-----------------------------------------------------------------
}
