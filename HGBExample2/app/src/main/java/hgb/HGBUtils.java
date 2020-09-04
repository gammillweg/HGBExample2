package hgb;

import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Set;

// Much of the content here has nothing to do with creating a hive, rather
// for code using the the contents of the hive.

// Authors note on a little inefficiency here:
// I originally thought it might be useful to know the order of the cells in the
// rings around the center cell.  So, I wrote getRingsAroundAry() returning a
// hash table value of int[], so that getRingOrder() could order the cells.
// Foolish thought!  I haven't found any use for ordered rings.  And have found
// that a hash set of indices is easier to work with.  Thus... rather than
// rewriting or throwing away code that works.  I simply wrote some conversion
// code that DID return a hash set and one that flattened those hash sets.
// more useful; but each runs through the base code of the original getRingsAroundAry().

// TODO -- fix the above inefficiency if never find a use for sorted rings

/**
 * Created by weg on 2/23/2016.
 * Useful methods
 *
 * Return -- getRingOrder()
 * Returns the array of cells in a single ring
 * Cells are ordered from the start cell (inclusive) clockwise around the ring
 * If an edge is involved (the run clockwise hits an edge).
 *    Then the order is both clockwise and counter clockwise from the start cell.
 *    1)  clockwise to an edge from the start cell, inclusive of the start cell.
 *    2)  counter clockwise to an edge from the start cell, exclusive of the start cell.
 * Cells may become isolated from the ring by the edge
 *    Isolated cells attempt to be ordered from the lest to the greatest cell index.
 *    But, where the clockwise progression of cells merge (greater with lesser), an order
 *    is not provided.
 *
 *  Return -- getAllRingOrder()
 *  Returns a hash table keyed by ring, value the int array as described in getRingOrder()
 *  for all rings.
 */
public class HGBUtils
{
   public HGBUtils(HGBShared hgbShared)
   {
      this.hgbShared = hgbShared;

      tmp1Vector2D = new HGBVector2D();
      tmp2Vector2D = new HGBVector2D();
      horizontal = new float[2];
      quadrant = new int[2];

      hgbLocator = hgbShared.getHGBLocator();
   }

   private final String TAG = this.getClass().getSimpleName();

   private HGBShared hgbShared;
   //private HGBRingsAround hgbRingsAround = null;
   private HGBProgressions  hgbProgressions = null;
   private HGBLocator hgbLocator = null;

   //------------------------------------------------
   private HGBVector2D tmp1Vector2D = null;
   private HGBVector2D tmp2Vector2D = null;
   private float[] horizontal = null;
   private int[] quadrant = null;
   //------------------------------------------------

   /**
    * Find all the cells by ring about a requested center cell by a requested
    * number of rings.  Note that the return does not include the requested
    * center cell.
    *
    * This method returns the cell indices in each ring as int arrays, keyed
    * by ring in a hashtable.  (There reason for the use of an int array is
    * so the return may be fed to getRingOrder().
    *
    * See also:  getRingsAroundHS()
    * There is a separate method which returns the cell indices in each ring
    * as a HashSet<Integer> keyed by ring in a hashtable.
    * The return from this may not be used to feed getRingOrder().
    *
    * see also: getRingsAroundCombinedHS().
    * There is a separate method which returns the cell indices in on HashSet<Integer>.
    * The return from this may not be used to feed getRingOrder().
    *
    *
    * @param centerCellIndex  -- the cell index to find the hex rings around
    * @param hexRings         -- the number of hex rings to find
    * @return  -- Hashtable<Integer, int[]> where keys are hex ring number
    *             value is int array of cell indices within each ring
    *             Note that the cells are not in any order about the ring
    *             (Call getRingOrder() get get an ordered array of cells)
    */
   //public Hashtable<Integer, int[]> getRingsAroundAry(int centerCellIndex, int hexRings)
   //{
      // Pass through to HGBRingsAround.getRingsAroundAry()
      // getRingsAroundHS() and getRingsAroundCombinedHS depend on this method

      //if (centerCellIndex == -1) return null;
      //if (hgbRingsAround == null)
      //{
         //hgbRingsAround = new HGBRingsAround(hgbShared);
      //}
      //return hgbRingsAround.getRingsAround(centerCellIndex, hexRings);
   //}

   /**
    * Find all the cells by ring about a requested center cell by a requested
    * number of rings.  Note that the return does not include the requested
    * center cell.
    *
    * see also gerRingsAround(), getRingsAroundCombinedHS() and getRingOrder()
    *
    * @param centerCellIndex  -- the cell index to find the hex rings around
    * @param hexRings         -- the number of hex rings to find
    * @return  -- Hashtable<Integer, HashSet<Integer></Integer>> where keys
    *             are hex ring number value is HashSet indices within each ring
    *             Note that the cells are not in any order about the ring
    */
   //public Hashtable<Integer, HashSet<Integer>> getRingsAroundHS(
   //      int centerCellIndex, int hexRings)
   //{
      // All this does is convert the int arrays of the Hashtable to HashSets
      // getRingsAroundCombinedHS depends upon this method

      //if (hgbRingsAround == null)
      //{
         //hgbRingsAround = new HGBRingsAround(hgbShared);
      //}
      //Hashtable<Integer, int[]> ringsAroundHT1 =
      //      hgbRingsAround.getRingsAround(centerCellIndex, hexRings);

      //Hashtable<Integer, HashSet<Integer>> ringsAroundHT2 =
      //      new Hashtable<Integer, HashSet<Integer>>(ringsAroundHT1.size());
      //Set<Integer> keySet = ringsAroundHT1.keySet();
      //for (int key : keySet)
      //{
         //int[] ary = ringsAroundHT1.get(key);
         //HashSet<Integer> hs = new HashSet<Integer>(ary.length);
         //for (int inx : ary)
         //{
            //hs.add(inx);
         //}
         //ringsAroundHT2.put(key, hs);
      //}
      //return ringsAroundHT2;
   //}

   /**
    * Find all the cells by ring about a requested center cell by a requested
    * number of rings.  Note that the return does not include the requested
    * center cell.  All the cell indices of all the rings are "flattened"
    * into a single hash set.
    *
    * see also gerRingsAround(), getRingsAroundHS() and getRingOrder()
    *
    * @param centerCellIndex  -- the cell index to find the hex rings around
    * @param hexRings         -- the number of hex rings to find
    * @return  -- Hashtable<Integer, HashSet<Integer></Integer>> where keys
    *             are hex ring number value is HashSet indices within each ring
    *             Note that the cells are not in any order about the ring
    */
   //public HashSet<Integer> getRingsAroundCombinedHS(int centerCellIndex, int hexRings)
   //{
      // All this does is flatten getRingsAroundHS()'s HashSets into one set.

      //Hashtable<Integer, HashSet<Integer>> ringsAroundHT =
      //      getRingsAroundHS(centerCellIndex, hexRings);

      //HashSet<Integer> ringsAroundHS = new HashSet<Integer>();
      //Set<Integer> keySet = ringsAroundHT.keySet();
      //for (int key : keySet)
      //{
         //HashSet<Integer> hs = ringsAroundHT.get(key);
         //ringsAroundHS.addAll(hs);
      //}
      //return ringsAroundHS;
   //}

   //------------------------------------------------
   // Pass through of public access to protected access of Locators getCellIndex()
   public int getCellIndex(float[] touchXY)
   {
      return hgbLocator.getCellIndex(touchXY);
   }

   public int getCellIndex(float touchX, float touchY)
   {
      return hgbLocator.getCellIndex(touchX, touchY);
   }

   /**
    *
    * @param centerCellIndex -- The center of the rings to be ordered
    * @param ring -- The ring within ringsAroundHT to process
    * @param startCell -- the cell on the ring to start ordering the ring
    * @param ringsAroundHT -- The return HGBRingsAround.getRingsAroundAry().  A hash table keyed by
    *                      ring, with value of an unordered array of cell indices, all
    *                      members of one hex ring about centerCellIndex.
    *
    * return  A set of ordered indices in the ring, such that the progress is in an ordered
    *          fashion away from the start cell.
    *          If no edge is involved, then from the start cell (including) clockwise all
    *          the way around the ring.
    *          If an edge is involved, then from the start cell (including) clockwise to one edge,
    *             then from the start cell (excluding) counter clockwise to the other edge
    *          If there are cells isolated from the ring by an edge, then the isolated side
    *          is ordered from the lest valued cell index toward the highest valued cell index.
    *          An error returns NULL
    *          If any member of ringAry is found NOT to be a member of the hex ring,
    *          then null is returned.
    *          Return null on error
    */

   public int[] getRingOrder(
         int centerCellIndex,
         int ring,
         int startCell,
         Hashtable<Integer, int[]> ringsAroundHT)
   {
      int[] ringAry = null;
      if (ringsAroundHT.containsKey(ring))
      {
         ringAry = ringsAroundHT.get(ring);
         if (ringAry == null) return null;
      }
      else
      {
         return null;
      }

      int[] orderedAry = new int[ringAry.length];
      Arrays.fill(orderedAry, -1);
      Set<Integer> ringSet = new HashSet<Integer>(ringAry.length);
      LinkedHashMap<Integer, Object> ringLHM = new LinkedHashMap<Integer, Object>(ringAry.length);

      try
      {
         HGBCellPack cellPack = null;

         // Count the valid (not an edge flag of -1) cells in the ring
         int validCellCnt = 0;
         for (int inx : ringAry)
         {
            if (inx != -1) validCellCnt++;
         }

         // Fill the initial ring hash map
         for (int inx = 0; inx < ringAry.length; inx++)
         {
            int cellIndex = ringAry[inx];
            if (cellIndex == -1) continue;

            cellPack = hgbShared.cellAry[cellIndex];
            if (cellPack == null) return null;

            ringSet.add(cellIndex);
         }

         int[] bondAry = null;

         // Start with the provided startCell
         // Get the bondIndex of the first cell and (below) run through
         // int looking of an adjacent cell in the ring.  Then switch
         // to a new bond array to search

         if (ringSet.contains(startCell) == false) return null;
         cellPack = hgbShared.cellAry[startCell];
         if (cellPack == null) return null;
         bondAry = cellPack.getBondAry();
         ringLHM.put(startCell, null);

         boolean edgeFlag = false;
         // Run through the current bond array and find if any of
         // these are adjacent to the current cell.
         int ringCnt = 0;
         while (ringCnt < validCellCnt)
         {
            for (int bondIndex : bondAry)
            {
               if (bondIndex == -1)
               {
                  edgeFlag = true;
                  continue;
               }
               if (ringLHM.containsKey(bondIndex)) continue;
               if (bondIndex == centerCellIndex) continue;

               if (ringSet.contains(bondIndex))
               {
                  // this cell is adjacent to the current cell
                  cellPack = hgbShared.cellAry[bondIndex];
                  if (cellPack == null) return null;

                  // get the next bond array to be searched
                  bondAry = cellPack.getBondAry();
                  ringLHM.put(bondIndex, null);
                  break;
               }
            }
            if (edgeFlag)
            {
               // Start over at startCell but work counter clockwise
               cellPack = hgbShared.cellAry[startCell];
               if (cellPack == null) return null;
               bondAry = cellPack.getBondAry();
               for (int bondIndex : bondAry)
               {
                  // The bondIndex Must be a member of the ring
                  // and must not have been previously processed
                  // (If previously process, would be the clockwise adjacent just run above)
                  if ((ringSet.contains(bondIndex) == true) && (ringLHM.containsKey(bondIndex) == false))
                  {
                     // this cell is adjacent, on the counter clockwise side, to the start cell
                     cellPack = hgbShared.cellAry[bondIndex];
                     if (cellPack == null) return null;

                     // get the next bond ary so it may be searched
                     bondAry = cellPack.getBondAry();
                     ringLHM.put(bondIndex, null);
                     edgeFlag = false;
                     break;
                  }
               }
            }

            ringCnt++;
            //Log.d(TAG, "RingOrder()    ringCnt:  " + ringCnt);
         }

         //-----------------------------------------------------------------
         // Special case
         // There may exist an isolated set of cells.  Isolated from the ring
         // by edge flags.  A bounds of -1 edge flag breaks the chain.

         if (ringLHM.size() < validCellCnt)
         {
            // Then we have not finished.
            // There are some number of isolated cells.  Cells, which are part of the ring;
            // but separated from the ring by edge flags of -1.

            Set<Integer> isolatedSet = new HashSet<Integer>(validCellCnt - ringLHM.size());
            Set<Integer> chkDoneSet = new HashSet<Integer>(validCellCnt - ringLHM.size());

            // Find the isolated cells, and a start point, the minimum cell index in the set
            int minCellIndex = Integer.MAX_VALUE;
            for (int cellIndex : ringSet)
            {
               if (ringLHM.containsKey(cellIndex)) continue;

               if (cellIndex < minCellIndex) minCellIndex = cellIndex;
               isolatedSet.add(cellIndex);
            }

            // Order the isolated cells using the minimum cell in the group as an arbitrary
            // starting point.  The minimum will be on on end or the other.

            //-----------  begin basically duplicated code from above ------------

            cellPack = hgbShared.cellAry[minCellIndex];
            if (cellPack == null) return null;
            bondAry = cellPack.getBondAry();
            ringLHM.put(minCellIndex, null);
            chkDoneSet.add(minCellIndex);
            edgeFlag = false;

            ringCnt = 0;
            validCellCnt = isolatedSet.size();

            while (validCellCnt > ringCnt)
            {
               for (int bondIndex : bondAry)
               {
                  if (bondIndex == -1)
                  {
                     edgeFlag = true;
                     continue;
                  }
                  if (ringLHM.containsKey(bondIndex)) continue;
                  if (bondIndex == centerCellIndex) continue;

                  if (isolatedSet.contains(bondIndex))
                  {
                     // this cell is adjacent to the current cell
                     cellPack = hgbShared.cellAry[bondIndex];
                     if (cellPack == null) return null;

                     // get the next bond array to be searched
                     bondAry = cellPack.getBondAry();
                     ringLHM.put(bondIndex, null);
                     chkDoneSet.add(bondIndex);
                     break;
                  }
               }
               //----------- end basically duplicated code from above ------------
               ringCnt++;
            }

            // At the bounds of the extents as roses rotate clockwise around the hive,
            // where the starting indices merge with the ending indices, there may be
            // an isolated set of cells where the minimum above does not work.

            // In this special case:  The return is NOT ordered.
            // These
            if (chkDoneSet.size() < isolatedSet.size())
            {
               for (int key : isolatedSet)
               {
                  ringLHM.put(key, null);
               }
            }
         }
      }
      catch (Exception excp)
      {
         Log.d(TAG, "ringOrder() failed: " + excp.toString());
         return null;
      }
      Set<Integer> keySet = ringLHM.keySet();
      int inx = 0;
      for (int cellIndex : keySet)
      {
         orderedAry[inx++] = cellIndex;
      }

      return orderedAry;
   }

   /**
    * @param centerCellIndex -- The center of the rings to be ordered
    * @param startCellsAry  -- the cells on the ring to start ordering each ring
    * @param ringsAroundHT -- The return HGBRingsAround.getRingsAroundAry().  A hash table keyed by
    *                      ring, with value of an unordered array of cell indices, all
    *                      members of one hex ring about centerCellIndex.
    *
    * Calls getRingOrder() for each ring to do the real work.
    *
    * return A hash table keyed by rings and values of ordered rings.
    *          A set of ordered indices in the ring, such that the progress is in an ordered
    *          fashion away from the start cell.
    *          If no edge is involved, then from the start cell (including) clockwise all
    *          the way around the ring.
    *          If an edge is involved, then from the start cell (including) clockwise to one edge,
    *             then from the start cell (excluding) counter clockwise to the other edge
    *          If there are cells isolated from the ring by an edge, then the isolated side
    *          is ordered from the lest valued cell index toward the highest valued cell index.
    *          An error returns NULL
    *          If any member of ringAry is found NOT to be a member of the hex ring,
    *          then null is returned.
    *          Return null on error
    */
   public Hashtable<Integer, int[]> getAllRingOrders(int centerCellIndex, int[] startCellsAry,
                                                     Hashtable<Integer, int[]> ringsAroundHT)
   {
      Hashtable<Integer, int[]> orderedRingsAroundHT =
            new Hashtable<Integer, int[]>(ringsAroundHT.size());

      try
      {
         Set<Integer> keySet = ringsAroundHT.keySet();
         for (int ring : keySet)
         {
            int[] unOrderedRingAry = ringsAroundHT.get(ring);
            int[] orderedRingAry = getRingOrder(centerCellIndex, ring, startCellsAry[ring],
                  ringsAroundHT);
            if (orderedRingAry != null)
            {
               orderedRingsAroundHT.put(ring, orderedRingAry);
            }
         }
      }
      catch (Exception excp)
      {
         return null;
      }

      return orderedRingsAroundHT;
   }

   /**
    * compute the angle, in degrees between a horizontal line passing through
    * two give points. (The two points may be he origin of two cells)
    *
    * @param fromPoint
    * @param toPoint
    * @return  The return is integer degrees (not radians)
    */
   public int getDegreesBetweenTwoCells(float[] fromPoint, float[] toPoint)
   {
      tmp1Vector2D.zeroVector2D();
      tmp2Vector2D.zeroVector2D();
      HGBVector2D vHorizontal = null;

      //float[] horizontal = new float[2];
      horizontal[0] = fromPoint[0] + 100f;
      horizontal[1] = fromPoint[1];
      vHorizontal = tmp1Vector2D.vCartesianVector(fromPoint, horizontal);

      // Find the angle in degrees between a vector from the origin
      // and a horizontal vector.

      // To find the angle in degrees from one point to another.
      // In this case between two cell origins.
      // 1) Get a vector from point 1 to point 2
      //    (may be: The hive origin to the rose origin or last touch)
      // 2) Get a vector of a horizontal
      //    (may be: The hive origin outward from 0 in a positive x direction (Y constant))
      // 3) Get the vector angle
      //    which is: The cosine of the angle
      // 4) The the arcCose of the cosine
      // 5) Multiple the arcCosine by the number of radians in a degree
      //    (57.295779513082320876798 radians to 1 degree)
      //    The answer is an angle in degrees from 0 to 180.
      //    Further on, we take care of the quadrants
      // 6) If point 2 is to the right of a vertical through point 1,
      //    and above a horizontal through point 1, then quadrant 1
      //    and the result is a correct angle.
      // 7) If point 2 is to the left of the vertical and above than correct.
      // 8) If point 2 is to the left and below than add 90
      // 9) If point 2 is to the right and below than subtract from 360
      // 10) If on the vertical or horizontal than 0, 90, 180 or 270

      // A vector from the origin to the users touch point.
      tmp2Vector2D.set2PointVector2D(fromPoint, toPoint);

      double cos = tmp1Vector2D.vVectorAngle(tmp2Vector2D, vHorizontal);
      double arcCos = Math.acos(cos);
      int degrees = (int) Math.round(Math.toDegrees(arcCos));

      // The degrees are always 0-180 whether above or below the horizontal
      // The degrees are correct in quadrants 1 and 2;
      // but incorrect for 3 and 4
      // Below we correct to the proper quadrant.

      quadrant[0] = (int) Math.signum(toPoint[0] - fromPoint[0]);
      quadrant[1] = (int) Math.signum(fromPoint[1] - toPoint[1]);
      return correctByQuadrant(degrees, quadrant);
   }

   /**
    * Compute the angle, in degrees, between a horizontal line passing through
    * the hive origin and a give point. The return is rounded to the nearest
    * degree.  Origin is the origin of a cell.
    *
    * @param origin -- the origin of the cell
    * @return int degrees (not radians)
    */
   public int getDegreesFromHiveOrigin(float[] origin)
   {
      float[] hiveOrigin = hgbShared.getHiveOrigin();
      return getDegreesBetweenTwoCells(hiveOrigin, origin);
   }

   private int correctByQuadrant(int degrees, int[] quadrant)
   {
      // quadrant[0] is to the left or right of a vertical through hiveOrigin
      // (right==1, left==-1, center==0)
      // quadrant[1] is above or below a horizontal through hiveOrigin
      // (above==1, below==-1, center==0)
      // If exactly on the horizontal or vertical (0, 90, 180, or 270),
      // Math.signum() return == 0

      // Correct the angle by quadrant.
      switch (quadrant[0])
      {
         case 0:
            // On the vertical
            switch (quadrant[1])
            {
               case 0: // on the horizontal
                  degrees = 0;
                  break;
               case 1: // above the horizontal
                  degrees = 90;
                  break;
               case -1: // below the horizontal
                  degrees = 270;
                  break;
            }
            break;

         // To the right of the vertical
         case 1:
            switch (quadrant[1])
            {
               case 0: // on the horizontal
                  degrees = 0;
                  break;
               case 1: // above the horizontal
                  // angle remains unchanged
                  break;
               case -1: // below the horizontal
                  degrees = 360 - degrees;
                  break;
            }
            break;

         // To the left of the vertical
         case -1:
            switch (quadrant[1])
            {
               case 0: // on the horizontal
                  degrees = 180;
                  break;
               case 1: // above the horizontal
                  // angle remains unchanged
                  break;
               case -1: // below the horizontal
                  degrees = 180 + (180 - degrees);
                  break;
            }
            break;
      }

      if (degrees == 360)
      {
         degrees = 0;
      }
      return degrees;

   }

   public int getHexSideByDegree(int degree)
   {
      if ((degree >= 0) && (degree < 60))
      {
         return 5;
      }
      else if ((degree >= 60) && (degree < 120))
      {
         return 4;
      }
      else if ((degree >= 120) && (degree < 180))
      {
         return 3;
      }
      else if ((degree >= 180) && (degree < 240))
      {
         return 2;
      }
      else if ((degree >= 240) && (degree < 300))
      {
         return 1;
      }
      else if ((degree >= 300) && (degree < 360))
      {
         return 0;
      }
      else
      {
         //Log.d(TAG, "Failed to determine which side");
         //Log.d(TAG, "degree:  " + degree);
         return -1;
      }
   }

   /**
    * runs from the input cell away from the cell from the hex side provided
    * the the number of cells of runLength.
    * If the edge is closer than runLength, then returns only to the edge.
    *
    * @param cell -- the starting cell
    * @param hexSide -- the side of the cell to run out from
    * @param runLength -- the number of cells to run out
    *                  If runLength < 0 then uses the length to the edge.
    * @return -- A an array of runLength of cell indices.
    *            If the run hits an edge,
    *            An array of cells runLength is returned padded to the end with -1
    *            The output is exclusive of the input cell.
    */
   public int[] runOutFromSide(int cell, int hexSide, int runLength)
   {
      if (runLength < 0)
      {
         runLength = countCellsToEdge(cell, hexSide);
      }

      int[] cellAry = new int[runLength];
      Arrays.fill(cellAry, -1);

      try
      {
         int cellIndex = cell;
         HGBCellPack cellPack = hgbShared.cellAry[cellIndex];
         if (cellPack == null)
         {
            return cellAry;
         }
         if (cellPack.bondAry[hexSide] == -1)
         {
            return cellAry;
         }

         for (int inx = 0; inx < runLength; inx++)
         {
            // check to see if we have reached the outer edge
            // The extreme edges are bonded as -1
            if (cellPack.bondAry[hexSide] == -1)
            {
               return cellAry;
            }

            cellIndex = cellPack.bondAry[hexSide];
            cellAry[inx] = cellIndex;

            cellPack = hgbShared.cellAry[cellIndex];
            if (cellPack == null)
            {
               break;
            }
         }
      }
      catch (Exception excp)
      {
         //Log.d(TAG, excp.getMessage());
         return cellAry;
      }
      return cellAry;
   }

   /**
    * Count the number of cells EXCLUSIVE of param cell to the edge of the hive
    *
    * @param cell
    * @param hexSide
    * @return
    */
   public int countCellsToEdge(int cell, int hexSide)
   {
      int runLength = 0;
      try
      {
         int cellIndex = cell;
         HGBCellPack cellPack = hgbShared.cellAry[cellIndex];
         if (cellPack == null)
         {
            return runLength;
         }
         if (cellPack.bondAry[hexSide] == -1)
         {
            return runLength;
         }

         while (true)
         {
            cellIndex = cellPack.bondAry[hexSide];
            if (cellIndex == -1) break;
            runLength++;

            cellPack = hgbShared.cellAry[cellIndex];
            if (cellPack == null)
            {
               break;
            }
         }
      }
      catch (Exception excp)
      {
         //Log.d(TAG, excp.getMessage());
         return runLength;
      }

      return runLength;
   }

   public int getSideTowardHiveOrigin(int cellIndex)
   {
      HGBCellPack cellPack = hgbShared.getCellPack(cellIndex);
      if (cellPack == null) return -1;

      float[] cellOrigin = cellPack.getOrigin();
      int degreesToHiveOrigin = getDegreesFromHiveOrigin(cellOrigin);
      int side = getHexSideByDegree(degreesToHiveOrigin);
      return HGBStatics.oppositeSides[side];
   }

   /**
    * Store all the cells found by getRingsAroundAry() stored in a HashTable of
    * ring arrays into one HashSet.
    * Note:  if you need any array:  (Integer array)
    *    Integer[] cellsAroundAry = cellsAroundHS.toArray(new Integer[cellsAroundHS.size()]);
    * @param ringsAroundHT the return from getRingsAroundAry()
    * @return all cells in one hash set
    */
   public HashSet<Integer> flattenRingsAround(Hashtable<Integer, int[]> ringsAroundHT)
   {
      // Flatten the hash table
      HashSet<Integer> ringsAroundHS = new HashSet<Integer>();
      if (ringsAroundHT == null) return ringsAroundHS;

      Set<Integer> keySet = ringsAroundHT.keySet();
      for (int key : keySet)
      {
         int[] ary = ringsAroundHT.get(key);
         for (int index : ary)
         {
            ringsAroundHS.add(index);
         }
      }
      return ringsAroundHS;
   }

   //===========================================================================
   /**
    * Get the side of the cell that faces closest to the hive origin.
    * @param cellIndex
    * @return a side of the cell (hexagon)
    */
   public int getInwardSideFromHiveOrigin(int cellIndex)
   {
      HGBCellPack pack = hgbShared.getCellPack(cellIndex);
      if (pack == null) return -1;
      float[] origin = pack.getOrigin();
      int degree = getDegreesFromHiveOrigin(origin);
      int inwardSide = getHexSideByDegree(degree);
      return inwardSide;
   }

   //===========================================================================
   /**
    * Get the side of the cell that faces mostly away form the hive origin
    * @param cellIndex
    * @return a side of the cell (hexagon)
    */
   public int getOutwardSideFromHiveOrigin(int cellIndex)
   {
      int inwardSide = getInwardSideFromHiveOrigin(cellIndex);
      return HGBStatics.oppositeSides[inwardSide];
   }

   //===========================================================================
   /**
    * Get the cell bound to input cell on input side
    *
    * @param cellIndex cell index to process
    * @param side side to work outward from
    * @return  from side, the adjacent cell to cellIndex
    */
   public int getBoundCellBySide(int cellIndex, int side)
   {
      HGBCellPack pack = hgbShared.getCellPack(cellIndex);
      if (pack == null) return -1;
      int boundCell = pack.getBondToSide(side);
      return boundCell;
   }

   //===========================================================================
   /**
    * get the cells bound to the param cellIndex
    * @param cellIndex
    * @return The 6 cells about cellIndex
    */
   public int[] getBondings(int cellIndex)
   {
      HGBCellPack pack = hgbShared.getCellPack(cellIndex);
      if (pack == null) return null;
      int[] boundAry = pack.getBondings();
      return boundAry;
   }

   //===========================================================================

   public float[] getCellOrigin(int cellIndex)
   {
      HGBCellPack pack = hgbShared.getCellPack(cellIndex);
      if (pack == null) return null;
      return pack.getOrigin();
   }

   /**
    * Returns the opposite side
    * (Can be obtained from HGBStatics as well.)
    * @param side
    * @return
    */
   public int getOppositeSide(int side)
   {
      if (side < 0) side = 0;
      if (side > HGBShared.SIDES) side = HGBShared.SIDES;
      return HGBStatics.oppositeSides[side];
   }
   //===========================================================================

   public double distanceBetweenCells(int cell1, int cell2)
   {
      float[] origCell1 = this.getCellOrigin(cell1);
      float[] origCell2 = this.getCellOrigin(cell2);
      HGBVector2D vec = new HGBVector2D();
      vec.set2PointVector2D(origCell1, origCell2);
      return vec.getMagnitude();
   }

   public double distanceBetweenCells(float[] orig1, float[] orig2)
   {
      HGBVector2D vec = new HGBVector2D();
      vec.set2PointVector2D(orig1, orig2);
      return vec.getMagnitude();
   }
   //===========================================================================

   /**
    * There exists on each hive (visually) a pair of cells at the vertices of the hive.
    * One of the pair is on the vertex of the hive.
    * @return  array of 6 cellIndices
    */
   public int[] getHiveVertices()
   {
      int[] hiveVertices = new int[6];

      // Find the hive vertices by progressing backward through cellIndices.
      // A quick loop around the outer ring progressing from Vertex to Vertex.

      HGBProgressions hgbProgressions = new HGBProgressions();
      int rings = hgbShared.getRoseRings() - 1;
      int[] roseRingRange = hgbProgressions.roseRingRange(rings);
      int keySide = 1;
      int counter = 0;
      int index = 0;
      for (int rose = roseRingRange[0]; rose <= roseRingRange[1]; rose += 10)
      {
         if (counter == 0)
         {
            hiveVertices[index++] = rose + keySide;
            keySide++;
            if (keySide == 7) keySide = 1;
         }
         counter++;
         if (counter == rings) counter = 0;
      }

      return hiveVertices;
   }

   /**
    * Compute the distance between the origin of two cells
    * @param cell1
    * @param cell2
    * @return double distance
    */
   public double getDistance(int cell1, int cell2)
   {
      HGBCellPack cellPack1 = hgbShared.getCellPack(cell1);
      if (cellPack1 == null) { return -1d; }

      HGBCellPack cellPack2 = hgbShared.getCellPack(cell2);
      if (cellPack2 == null) { return -1d; }

      float[] cell1Origin = cellPack1.getOrigin();
      float[] cell2Origin = cellPack2.getOrigin();

      float sub1 = Math.abs(cell1Origin[0] - cell2Origin[0]);
      float sub2 = Math.abs(cell1Origin[1] - cell2Origin[1]);
      float square1 = sub1 * sub1;
      float square2 = sub2 * sub2;
      float val = square1 + square2;
      double distance = Math.sqrt(val);
      return distance;
   }

   /**
	 * Gets the origin of the cell and instance a HGBVecor2D from the origin of
	 * the cell to the touch point. Then tests to see if this magnitude is less
	 * than or equal to the provided test magnitude.
	 * 
	 * If the point is within the cell (or the rose) then the vector magnitude
	 * MUST be less than or equal to to the test magnitude.
	 * 
	 * @param cellIndex
	 * @param touch
	 *            The coordinates of the touch
	 * @param testMag
	 *            The magnitude to test against
	 * @return The index of the cell if within range -1 if out of range
	 */
	private int testWithinCell(int cellIndex, float[] touch, double testMag)
	{
		float[] org = null;
		double mag = 0;
		HGBCellPack hgbCellPack = hgbShared.getCellPack(cellIndex);
		if (hgbCellPack != null)
		{
			org = hgbCellPack.getOrigin();
			HGBVector2D vec = tmp1Vector2D.vCartesianVector(org, touch);
			mag = vec.getMagnitude();
			return (mag <= testMag) ? cellIndex : -1;
		}
		return -1;
	}
}

