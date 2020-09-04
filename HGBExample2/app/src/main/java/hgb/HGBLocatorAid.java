package hgb;

import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;

// Note that vectors are NOT coordinates.  The vectors found by
// HGBVector2D.CartesianVector may not be used as a coordinate.
// An example of the error of such a thought:
//    The origin is (450, 340) and the point is (640, 830)
//    vecX = 640 - 450 == 190 vecY = 830 - 340 == 490
//    So the vecX from the origin to point is 190
//    and the vecY from origin to point is 400
//    and the distance (magnitude) between is 525.547...
// You can see that the effect is that the vector comes from (0,0)
// So, all the vectors from the hive origin to cell origins are
// about (0,0) and any vecX or vecY is NOT the coordinate of the cell.

public class HGBLocatorAid
{
   private final String TAG = this.getClass().getSimpleName();

   public HGBLocatorAid(HGBShared hgbShared)
   {
      this.hgbShared = hgbShared;
   }

   // ------------------------------------------------
   private HGBShared hgbShared = null;
   private HGBProgressions hgbProgressions = null;

   private HGBVector2D tmpVector2D = null;
   private int zeroColIndex;
   private double vertexRadius;
   private double boxHalfWidth;
   int[] orderedvecXKeysAry;

   // ------------------------------------------------

   protected void Initialize()
   {
      tmpVector2D = new HGBVector2D();
      vertexRadius = hgbShared.getVertexRadius();
      boxHalfWidth = vertexRadius / 2;
   }

   //======================================================

   /**
    * The Main entry to LocatorAid
    * @param touchXY:  the users touch coordinates
    * @param touchVec  the users touch coordinates converted to a Vector
    * @param sortedColKeysAS  a SparseArray of arrays of all cell vectors keyed by vecX
    *                         cast to int.  These are columns of cells found by their vecX
    * @return cellIndex
    */

   protected int locateCellIndex(float[] touchXY, HGBVector2D touchVec,
                                 SparseArray<HGBVector2D[]> sortedColKeysAS)
   {
      int vecX = (int)touchVec.getX();
      int[] colKeys = findColumns(touchXY, vecX);

      int cellID = resolve(colKeys, touchXY, touchVec, sortedColKeysAS);

      return cellID;
   }

   /**
    * Param colKeys holds two col, one of which contains the cell to be found.  We get the first
    * vector in each column and the the magnitude between those two cells and the touch vector Y
    * (vecY).  Find the number of cells between, which will be an index into each vector array.
    * This will give us 2 cells to chose from.  Then test these for the touch within
    * a cell.  Which cell is the cellID to return.
    * @param colKeys The two columns chosen
    * @param touchXY The users touch coordinate
    * @param touchVec the users touch coordinate as a Vector
    * @param sortedColKeysAS a SparseArray of arrays of all cell vectors keyed by vecX
    *                        cast to int.  These are columns of cells found by their vecX
    * @return the cell ID found
    */
   //======================================================

   /**
    * tests if the touch is "in the box" or near a horizontal vertex.  If in the box then
    * the chosen column is correct.  If near a vertex, one need to determine if on the right
    * or left side of the cell, then return both the chosen column and the column to the right
    * or left.
    *
    * The box is defined by the top and bottom of the cell with right and left sides vertical
    * from the top vertices.  Any touch within the box will belong to the chosen column.
    *
    * @param touchXY users touch coordinates
    * @param vecX the X value of the touch coordinates as a HGDVector2D
    * @return colKeys keys into sortedColKeysAS
    */
   private int[] findColumns(float[] touchXY, int vecX)
   {
      // Get the X column by the touch vecX (cast to in)

      // The return
      // Coding:  [0] The chosen column.
      //          [1] The column to the right or left of the chosen column.
      int[] colKeys = new int[2];
      
      int offset = -1;
      int val = -1;
      int inx = -1;
      int prev = Integer.MAX_VALUE;

      if (vecX < 0)
      {
         // working (from left to right) the left side (the negative side)
         
         // The +1 is so the code will work for column 0
         for (inx = 0; inx < (zeroColIndex + 1); inx++)
         {
            // working the left side (negative)
            val = orderedvecXKeysAry[inx];
            offset = val - vecX;
            //System.out.println("neg side:  " + inx + ", " + val + " - " + vecX + " = " + offset );

            // A minimum has been found.
            // The values go down as we work right, when the value is greater than prev
            // then prev is a minimum
            if (prev <= offset) break;
            prev = Math.abs(offset);
         }
         // the prev is stored in prev
         int colKey = orderedvecXKeysAry[--inx];
         ////System.out.println("neg side  inx:  "+ inx + ", offset:  " +  offset + ", prev:  " + prev  + ", colKey:  " + colKey + ", vecX:  " + vecX);

         // If in the box, then the keyCol found is the correct column
         // If left or right of the box than then two columns must be saved.
         // The column to the left or right plus the current column.
         colKeys[0] = colKey;
         colKeys[1] = -1;

         // lcr:
         //      l --> -1: left of the box
         //      c -->  0: in the box,
         //      r -->  1: right of the box
         int lcr = testCol(vecX, colKey);
         //System.out.println("neg side:  lcr" +  lcr );
         switch (lcr)
         {
            case -1: //left of box
               if (inx == 0) break;
               colKeys[1] = orderedvecXKeysAry[inx-1];
               break;
            case 0: // in box
               // The correct column has been selected
               break;
            case 1: // right of box
               colKeys[1] = orderedvecXKeysAry[inx+1];
               break;
         }
         //System.out.println("neg side  colKey:  " + colKey + ", vecX:  " + vecX +
         //                   ", inx:  "+ inx + ", lcr:  "  + lcr);
      //System.out.println("------------------------------");
      }
      else if (vecX > 0)
      {
         // working (left to right) the right side (the positive side)
         for (inx = zeroColIndex + 1; inx < orderedvecXKeysAry.length; inx++)
         {
            // working the right side (negative)
            val = orderedvecXKeysAry[inx];
            offset = val - vecX;
            //System.out.println("neg side:  " + inx + ", " + val + " - " + vecX + " = " + offset );

            // A minimum has been found.
            // The values go down as we work right, when the value is greater than prev
            // then prev is a minimum
            if (prev <= offset) break;
            prev = Math.abs(offset);
         }
         int colKey = orderedvecXKeysAry[--inx];
         ////System.out.println("neg side  inx:  "+ inx + ", offset:  " +  offset + ", prev:  " + prev  + ", colKey:  " + colKey + ", vecX:  " + vecX);

         // If in the box, then the keyCol found is the correct column
         // If left or right of the box than then two columns must be saved.
         // The column to the left or right plus the current column.
         colKeys[0] = colKey;
         colKeys[1] = -1;

         // lcr:
         //      l --> -1: left of the box
         //      c -->  0: in the box,
         //      r -->  1: right of the box
         int lcr = testCol(vecX, colKey);
         //System.out.println("neg side:  lcr" +  lcr );
         switch (lcr)
         {
            case -1: //left of box
               colKeys[1] = orderedvecXKeysAry[inx-1];
               break;
            case 0: // in box
               // The correct column has been selected
               break;
            case 1: // right of box
               if (inx == orderedvecXKeysAry.length - 1) break;
               colKeys[1] = orderedvecXKeysAry[inx+1];
               break;
         }
         //System.out.println("neg side  colKey:  " + colKey + ", vecX:  " + vecX +
         //                   ", inx:  "+ inx + ", lcr:  "  + lcr);
      }
      else
      {
         // The exact y-Axis center line (0)
         colKeys[0] = 0;
         colKeys[1] = -1;
      }
//System.out.println("colKeys: [0]:  " + colKeys[0] + ", [1]:  " + colKeys[1]);
//System.out.println("------------------------------");
      return colKeys;
   }

   /**
    * Determines if the touch is near a horizontal vertex (right or left) or "in the box"
    * @param vecX the users touch coordinates converted to a vector: the X member of the vector
    * @param colKey The key into sortedColKeysAS
    * @return set -1:left of box, 0:centered, 1:right of box (lcr: Left Center Right)
    */
   private int testCol(int vecX, int colKey)
   {
      double leftEdge = -1;
      double rightEdge = -1;

      if (colKey < 0)
      {
         // The left (negitive) side
         
         leftEdge = Math.abs(colKey) + boxHalfWidth;
         rightEdge = Math.abs(colKey) - boxHalfWidth;

         // test is left of the box
         if (Math.abs(vecX) > leftEdge) return -1;

         // test is right of the box
         else if (Math.abs(vecX) < rightEdge) return 1;

         // else the touch is within the box
         else return 0;
      }
      else
      {
         // The right (positive) side
         
         leftEdge = colKey - boxHalfWidth;
         rightEdge = colKey + boxHalfWidth;

         // test is left of the box
         if (vecX < leftEdge) return -1;

         // test is right of the box
         else if (vecX > rightEdge) return 1;

         // else the touch is within the box
         else return 0;
      }
   }

   //============================================================================
   private int resolve(int[] colKeys, float[] touchXY, HGBVector2D touchVec,
                       SparseArray<HGBVector2D[]> sortedColKeysAS)
   {
      double normalRadius = hgbShared.getNormalRadius();

      // get the vector col arrays per chosen columns
      HGBVector2D[] colVecAry = sortedColKeysAS.get(colKeys[0]);

      int[] cellIDs = new int[2];
      cellIDs[1] = -1;
      cellIDs[0] = findCell(colVecAry, touchVec, normalRadius);

      // If in the box, the colKeys[1] will be returned == -1
      // else an alternate vector key choice will be in [1].
      if (colKeys[1] != -1)
      {
         colVecAry = sortedColKeysAS.get(colKeys[1]);
         cellIDs[1] = findCell(colVecAry, touchVec, normalRadius);
      }
      //System.out.println("cellIDs [0]:  " + cellIDs[0] + ", [1]:  " + cellIDs[1]);

      try
      {
         // Had a number of if conditions... changed to try/catch (cleaner code)

         HGBCellPack cellPack = hgbShared.cellAry[cellIDs[0]];
         tmpVector2D.set2PointVector2D(cellPack.getOrigin(), touchXY);
         int magnitude = (int) (Math.round(tmpVector2D.getMagnitude()));
         if (magnitude < vertexRadius) { return cellIDs[0]; }

         cellPack = hgbShared.cellAry[cellIDs[1]];
         tmpVector2D.set2PointVector2D(cellPack.getOrigin(), touchXY);
         magnitude = (int) (Math.round(tmpVector2D.getMagnitude()));
         if (magnitude < vertexRadius) { return cellIDs[1]; }
      }
      catch (Exception exc)
      {
         //Log.d(TAG, exc.toString());
         return -1;
      }

      return -1;
   }

   /**
    * Work down the column in a loop to find which cell is the correct distance form
    * the top cell of the column array and the users touch per the magnitude between the two.
    *
    * @param colVecAry  An array of HGDVector2D's, one for each cell in the column
    * @param touchVec   The users touch coordinate converted to a HGDVector2D
    * @param normalRadius The magnitude between the cell origin and the mid point of any side.
    * @return a cellID
    */
   private int findCell(HGBVector2D[] colVecAry, HGBVector2D touchVec, double normalRadius)
   {
      // get the vector at the top of the column
      HGBVector2D colTopVec = colVecAry[0];

      // compute the magnitude between the touch vector for each top vector
      float[] p0 = new float[2];
      float[] p1 = new float[2];
      p0[0] = touchVec.getX();
      p0[1] = touchVec.getY();
      p1[0] = colTopVec.getX();
      p1[1] = colTopVec.getY();
      HGBVector2D tmpVec = tmpVector2D.vCartesianVector(p0, p1);
      double colMag = tmpVec.getMagnitude();

      // Find the cell down the column
      for (int inx = 0; inx < colVecAry.length; inx++)
      {
         tmpVec = colVecAry[inx];
         if (inx == 0)
         {
            colMag -= normalRadius;
            if (colMag < 0) break;
         }
         else
         {
            colMag -= normalRadius * 2;
            if (colMag < 0) break;
         }
      }
      int cellID = tmpVec.getID();

      //System.out.println("cellID:  " + cellID);

      return cellID;
   }
   //============================================================================

   /**
	 * Sort the keys found in vecXCountSA or vecYCountSA
    * and return in a sorted array of int
	 *
	 * @param countSA
	 *            SparseArray of int counts of keys used for allocating stored
	 *            arrays of Vector2D data
    * @return The keys in sorted order, also sets class member zeroColIndex or zeroRowIndex
    *         depending of param xyFlg
	 */
	protected int[] orderKeys(SparseArray<Integer> countSA)
	{
		int[] orderedKeyAry = new int[countSA.size()];
		for (int inx = 0; inx < countSA.size(); inx++)
		{
			orderedKeyAry[inx] = countSA.keyAt(inx);
		}

		// As it works out... the keys are already sorted.
      // Is wasted effort... but do it anyway for insurance.
		Arrays.sort(orderedKeyAry);

		// the index of the "center" of the array
      this.orderedvecXKeysAry = orderedKeyAry;
      zeroColIndex = findZeroIndex(orderedKeyAry);

      return orderedKeyAry;
	}

   /**
	 * Finds vecX == 0, and sets zeroIndex equal to its index location within
	 * orderedKeyAry
	 * 
	 * @param orderedKeyAry
	 *            int array of vex X keys
	 * @return returns the index of the value 0 in orderedKeyAry
	 */
	private int findZeroIndex(int[] orderedKeyAry)
	{
		for (int inx = 0; inx < orderedKeyAry.length; inx++)
		{
			if (orderedKeyAry[inx] == 0)
				return inx;
		}
		return -1;
	}

//   /**
//	 * Gets the origin of the cell and instance a HGBVecor2D from the origin of
//	 * the cell to the touch point. Then tests to see if this magnitude is less
//	 * than or equal to the provided test magnitude.
//	 *
//	 * If the point is within the cell (or the rose) then the vector magnitude
//	 * MUST be less than or equal to to the test magnitude.
//	 *
//	 * @param cellIndex
//	 * @param touch The coordinates of the touch
//	 * @param testMag The magnitude to test against
//	 * @return The index of the cell if within range -1 if out of range
//	 */
//	private int testCell(int cellIndex, float[] touch, double testMag)
//	{
//		float[] org = null;
//		double mag = 0;
//		HGBCellPack hgbCellPack = hgbShared.getCellPack(cellIndex);
//		if (hgbCellPack != null)
//		{
//			org = hgbCellPack.getOrigin();
//			HGBVector2D vec = tmpVector2D.vCartesianVector(org, touch);
//			mag = vec.getMagnitude();
//			return (mag <= testMag) ? cellIndex : -1;
//		}
//		return -1;
//	}
}
