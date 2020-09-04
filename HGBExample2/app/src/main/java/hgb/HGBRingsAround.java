package hgb;

import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

/*
 * The following code does nothing to help create the hive.  Rather is provides
 * some idea as to how to find surrounding cells for game purposes... as in radar.
 */

/**
 * Created by weg  2/12/2016 - 2/22/2016
 * HGBRingsAround class will find all the cells in concentric hexagon rings around a provided cell.
 * Cells, per ring, are not ordered
 * <p/>
 * Public functions:
 * getRingsAroundAry(int, int)
 *       param int -- centerCell
 *       param int -- rings
 *       return Hashtable<Integer, int[]> -- key: ring; value: cell indices per ring
 *              The indices are not ordered
 *
 * getStartCells()
 *       return int[] -- start cell indices for each ring.  ([0] is wasted (-1))
 *
 * <p/>
 * Return -- getRingsAroundAry()
 * A hash table keyed by ring.  Value is an int array of cell indices members of the that
 * hex ring centered around the provided cell.
 * Rings are counted from 1.  No ring 0.
 *
 * The return array is unordered.
 * Out of bounds cells are marked -1,
 * The return array may contain many out of bound -1 flags; but they are all collected
 * at the end of the array.
 * <p/>
 * Return -- getStartCells()
 * starting cells for each ring ([0] is wasted)
 *
 * NoteOnAndroidStudio that the arrays held in the hash table returned from getRingsAroundAry are NOT
 * ordered.  If you need to have the rings ordered, use HGBUtils.getRingOrder() or
 * HGBUtils.getAllRingOrders().  These will sort the return from getRingsAroundAry() into
 * a near order.  Clockwise if no edge involved.  Both Clockwise and Counter Clockwise if
 * an edge is involved. And perhaps an unsorted side isolated by edge flagged cells.
 *
 * <p/>
 * History:
 * In the past there existed class HGBTrueHexagons which found all the "true" hexagons
 * about origin cell zero.  As it worked out, I never used the class for I found a better
 * way to do the task at hand.  Much later (over a year), working on creating land and water,
 * I though to use true hexagons and worked on modifying it to allow me to provide any
 * cell and the number of rings.  I put in lots of work trying to get true hexagons to work
 * and thought I was there; but I fell short.  The changes turned into spaghetti.  So this
 * is a rewrite of the attempt to modify HGBTrueHexagons.  Get it right from the start and
 * stop trying to condition my way through old code.
 */
public class HGBRingsAround
{
   // Constructor
   public HGBRingsAround(HGBShared hgbShared)
   {
      this.hgbShared = hgbShared;
      this.hgbUtils = new HGBUtils(hgbShared);
   }

   private HGBShared hgbShared;
   private HGBUtils hgbUtils;

   private final String TAG = this.getClass().getSimpleName();
   //========================================================================================

   private Hashtable<Integer, int[]> ringsAroundHT = null;  // The return
   
   private int[] startCellsAry = null; // first cells for each ring, away from the near edge

   private HashSet<Integer> cellsAboutOddSet = null;   // rings 1, 3 etc
   private HashSet<Integer> cellsAboutEvenSet = null;  // rings 2, 4 etc
   private HashSet<Integer> priorSet = null;           // the previous get
   private HashSet<Integer> getSet;                    // assigned in lhmChoice()
   private HashSet<Integer> putSet;                    // assigned in lhmChoice()

   private int startSide = 0;    // The side to start around each ring
   private final int SIDES = 6;

   /**
    * Get the indices of the first cell in the clockwise run of each ring.
    * So that the ring value may be used as an index into startCellsAry,
    * and rings begin with 1 (no 0 ring), [0] is wasted (set to -1)
    * @return int[] startCellsAry
    */
   public int[] getStartCells() { return startCellsAry; }

   /**
    * The entry point
    *
    * @param centerCellIndex -- The cell to be circled by hex rings
    * @param hexRings  -- The number of hex rings to find
    * @return a hash table of rings around the provided center.  Each keyed
    *          by the ring value (starting with 1 (no ring 0)) and value
    *          of int[] cell indices members of the ring.  If any edges
    *          involved, -1 will be found in the array.  Indices are not
    *          in any order.
    *          Using Hashtable rather than HashMap as is synchronized.
    */
   public Hashtable<Integer, int[]> getRingsAround(int centerCellIndex, int hexRings)
   {
      if (centerCellIndex == -1) return null;
      initArrays(hexRings);

      // Fill outwardCellAry -- those cells directly away from the near edge
      // used as a starting cell for each ring.  Insures hexRings does not exceed the
      // limits of the hive.
      int rings = fillOutwardCellAry(centerCellIndex, hexRings);

      // Loop through the rings finding all cells part of each ring
      fillRings(rings, centerCellIndex);
      return ringsAroundHT;
   }



   //------------------------------------------------------------------

   /**
    * Loop through the rings, calling aroundCell() for each ring
    * Ring 1 is unique and begins the effort, then the loop begins with ring 2
    * (The fact that ring 1 is a fixed clockwise progression allows the other
    * rings, which find their data off of inner rings, to progress clockwise.)
    *
    * @param rings      -- the number of rings to find
    * @param cellIndex  -- the cell index to wrap around
    */
   private void fillRings(int rings, int cellIndex)
   {
      try
      {
         startWithRing1(cellIndex);

         for (int ring = 2; ring < rings + 1; ring++)
         {
            // Collect cells which are members of the current ring
            aroundCell(ring);

            // copy the data collected into the return array (and fix the order).
            fillRingsAry(ring, cellIndex);
         }
      }
      catch (Exception excp)
      {
         Log.d(TAG, "fillRings():  " + excp.toString());
      }
   }



   //------------------------------------------------------------------
   /**
    * The input is either class member cellsAboutEvenSet or cellsAboutOddSet
    * per the rings value (odd or even ring)
    *
    * @param ring
    * return filled ringsAroundRingAry member within ringsAroundHT (for this ring)
    */
   private void fillRingsAry(int ring, int centerCell)
   {
      lhmChoice(ring);

      int[] ringsAroundRingAry = ringsAroundHT.get(ring);

      // I know it makes sense to use getSet; but
      // the data to be read has been stored in putSet
      //--Set<Integer> keySet = putSet.keySet();
      int inx = 0;
      if (ring == 1)
      {
         for (int cellIndex : putSet)
         {
            if (cellIndex == centerCell) continue;

            //Log.d(TAG, "fillRingsAry(): " + index + ", " + cellIndex);
            ringsAroundRingAry[inx++] = cellIndex;
         }
      }
      else
      {
         for (int cellIndex : putSet)
         {
            //Log.d(TAG, "fillRingsAry(): " + index + ", " + cellIndex);
            ringsAroundRingAry[inx++] = cellIndex;
         }
      }
   }

   /**
    * This is where the majority of work to find what cells to use is done.
    * Here linked hash maps are used to insure no cell is processed more than once
    * and each cells bondAry is run to examine all bonded cells.  Those not
    * previously processed are collected to an empty linked hash map which is
    * the return.  Out of bound -1 indices are not collected.
    *
    * @param ring -- the current ring
    * @Return -- cellsAboutEvenSet or cellsAboutOddSet (as used)
    */
   private void aroundCell(int ring)
   {
      // choose which linked hash map to use by the ring's odd or even value
      lhmChoice(ring);

      boolean continueFlag = false;

      try
      {
         // Run through all the elements in the hash map and collect all of their
         // bondings.  Then use the hash map key to only store those not stored.
         //--Set<Integer> keySet = getSet.keySet();
        for (int cellIndex : getSet)
         {
            if (cellIndex == -1)
            {
               continue;
            }
            HGBCellPack cellPack = hgbShared.cellAry[cellIndex];
            if (cellPack == null)
            {
               return;
            }

            int hexSide = startSide;
            int counter = 0;

            // Run around the cells bondAry checking for cells not previous processed
            while (counter < SIDES)
            {
               int index = cellPack.bondAry[hexSide];

               // We only want to collect the cell indices of the next outer ring
               // don't collect any from the current ring (getSet)
               // don't collect any from the prior inner ring (priorSet)
               // don't duplicate any which have already be entered (putSet)
               if (getSet.contains(index)) { continueFlag = true;  }
               if (priorSet.contains(index)) { continueFlag = true; }
               if (putSet.contains(index)) { continueFlag = true; }
               if (index == -1) { continueFlag = true; }
               if (continueFlag)
               {
                  hexSide++;
                  if (hexSide == SIDES) hexSide = 0;
                  counter++;
                  continueFlag = false;
                  continue;
               }

               // save for return
               putSet.add(index);

               hexSide++;
               if (hexSide == SIDES) hexSide = 0;
               counter++;
             }

         }

         // Need to get rid of the previous ring indices.
         priorSet.clear();
         priorSet.addAll(getSet);
         getSet.clear();
      }
      catch (Exception excp)
      {
         Log.d(TAG, "aroundCell():  " + excp.toString());
      }
      //Log.d(TAG, "Ring: " + ring);
   }

   /**
    * Choose by ring (odd or even value) which linked hash map to store in
    * and which to use for data.
    *
    * @param ring -- hex ring
    * Return is class member putSet or getSet assignment to either cellsAbout[Even,Odd]Set
    */
   private void lhmChoice(int ring)
   {
      // We want to get form the previous and put to the current
      if ((ring % 2) == 0)
      {
         // Even
         putSet = cellsAboutEvenSet;
         getSet = cellsAboutOddSet;
      }
      else
      {
         // Odd
         putSet = cellsAboutOddSet;
         getSet = cellsAboutEvenSet;
      }
   }

   /**
    * Initialize cellsAbout[Odd,Even]Set by storing in it ring 1 -- the 6 cells around the center
    *
    * @param cellIndex
    */
   private void startWithRing1(int cellIndex)
   {
      HGBCellPack cellPack = hgbShared.cellAry[cellIndex];
      if (cellPack == null)
      {
         return;
      }

      // Store the center
      cellsAboutOddSet.add(cellIndex);

      // The value and the key are the same.
      int hexSide = startSide;
      int counter = 0;
      while (counter < SIDES)
      {
         int index = cellPack.bondAry[hexSide];

         if (index == -1)
         {
            hexSide++;
            if (hexSide == SIDES) hexSide = 0;
            counter++;
            continue;
         }

         cellsAboutOddSet.add(index);
         //dbCreateWorld.doDBUpdate(index, GameShared.TIES.LandGrass);

         hexSide++;
         if (hexSide == SIDES) hexSide = 0;

         counter++;
      }
      //Log.d(TAG, "Ring:  1");
//      //------------------------------------------------------
//      Set<Integer> dbKeySet = cellsAboutOddSet.keySet();
//      Log.d(TAG, "Ring:  1");
//      for (int dbindex : dbKeySet)
//      {
//         Log.d(TAG, "  " + dbindex);
//      }
//      //------------------------------------------------------

      fillRingsAry(1, cellIndex);
   }


   //------------------------------------------------------------------

   /**
    * Fill an array of cells used as the starting point for each ring.  These
    * cells are in a line on the same hex side away from the nearest edge
    *
    * @param cellIndex -- The cell to build around
    * @param hexRings  -- The number of rings to find
    * Return int rings -- returns the input hexRings or maxRingCnt-1 if hexRings it large
    * -1 on error
    */
   private int fillOutwardCellAry(int cellIndex, int hexRings)
   {
      if (cellIndex < 0) return -1;
      if (hexRings < 0) return -1;

      int rings = hexRings;

      try
      {
         // Chose a side with an outward run away from the nearest edge.
         // Chose the side that runs nearest to the hive origin
         HGBCellPack cellPack = hgbShared.cellAry[cellIndex];
         if (cellPack == null)
         {
            return -1;
         }
         float[] origin = cellPack.getOrigin();

         // Choose the side that will step away from the nearest edge
         // (Get the degrees from the origin of the chosen cell to the hive origin,
         // then the side that many degrees around the cells origin (which would
         // would be the side closest to the edge, so finally, chose the opposite
         // side (which will be the one furthest from the edge).)
         int degree = hgbUtils.getDegreesFromHiveOrigin(origin);
         int inwardSide = hgbUtils.getHexSideByDegree(degree);
         int outwardSide = HGBStatics.oppositeSides[inwardSide];

         // outwardSide is passed as needed
         // startSide is a class member and used elsewhere
         // (Legacy reason)
         startSide = outwardSide;

         // Fill outwardCellAry and insure the user has not requested
         // more rings than are available.
         int[] cellAry = hgbUtils.runOutFromSide(cellIndex, outwardSide, hexRings);
         int ringCnt = cellAry.length;
         if ((ringCnt) < hexRings)
         {
            //Opps... the user has asked for more rings than available
            // Must correct outwardCellAry
            startCellsAry = null;
            startCellsAry = new int[ringCnt + 1];
            startCellsAry[0] = -1;
            cellAry = hgbUtils.runOutFromSide(cellIndex, outwardSide, ringCnt);
            rings = cellAry.length;
         }
         for (int inx = 0; inx < cellAry.length; inx++)
         {
            startCellsAry[inx + 1] = cellAry[inx];
         }
      }
      catch (Exception excp)
      {
         Log.d(TAG, "fillOutwardCellAry");
      }
      return rings;
   }


   /**
    * Allocate necessary arrays and Hash sets and table
    *
    * @param hexRings
    */
   private void initArrays(int hexRings)
   {
      //--------------------
      // Initialize the return hash table and the arrays per member
      ringsAroundHT = new Hashtable<Integer, int[]>(hexRings);
      for (int ring = 1; ring < hexRings+1; ring++)
      {
         int[] ringsAroundRingAry = new int[SIDES * ring];
         // A -1 is a edge flag, default all to edge flag
         Arrays.fill(ringsAroundRingAry, -1);
         ringsAroundHT.put(ring, ringsAroundRingAry);
      }

      //--------------------
      // [0] is wasted so that the ring value (starting at 1) may be used as an index
      startCellsAry = new int[hexRings + 1];
      startCellsAry[0] = -1;

      //--------------------
      cellsAboutOddSet = new HashSet<Integer>();
      cellsAboutEvenSet = new HashSet<Integer>();
      priorSet = new HashSet<Integer>();
   }

}
