package hgb;

import android.annotation.SuppressLint;
import android.util.SparseArray;

import java.util.Arrays;

/**
 * HGBLocator (with HGBLocatorAid) is used to quickly locate the cell
 * for user interaction.  (A user click on a cell)
 *
 * @see "HBGLocatorAid
 * @see "Development comments"
 * Comment on using CellPack:  I thought it not a good idea to store the cell ID in HGBVector2D.
 * That it would be much better to store the vector in CellPack.  I wrote the code.  It works.
 * But, it is slower than the original (cell ID stored in the vector).  I did not go to the effort
 * finding out if I made some looping error.  I did not research why slower.  I abandoned the idea
 * of using CellPack and left the cell ID in the vector.

 * 150714 Rewrite locator -- Finished and renamed (twice) 150729 (works much faster and better)
 * I renamed Locater, thinking I had spelled incorrectly.  Then found Locator was OK.
 * Dictionaries say Locator may be spelled either Locator OR Locater.
 * I choose and prefer Locator.
 *
 * 200418 in preperation for publication to Code Project I found an unacceptable bug in Locator:
 * touches near the horizontal vertex of a cell might return an incorrect cellID (or -1).  I tried
 * to find and correct the problem; but in the ended decided the current was simply unworkable.
 * Hence, I reworte locater a third time (see new class HGBLocatorAid).  I think I have it right
 * now. The new locator does a good fast job of finding the touched cellID without error.
*/
public class HGBLocator
{
	@SuppressLint("UseSparseArrays")
	public HGBLocator(HGBShared hgbShared)
	{
		this.hgbShared = hgbShared;
		this.hgbProgressions = new HGBProgressions();
      this.hgbLocatorAid = new HGBLocatorAid(hgbShared);

		tmpVec = new HGBVector2D();
		touchXY = new float[2];
	}

	@SuppressWarnings("unused")
	private final String TAG = "HGBLocator";

	private HGBShared hgbShared = null;
	private HGBProgressions hgbProgressions = null;
	private HGBLocatorAid hgbLocatorAid = null;

	private HGBVector2D tmpVec = null;
   private HGBVector2D tmpVector2D = null;

	private float[] touchXY;
	private float[] hiveOrigin;
	private double vectorRadius = 0;
   private int[] orderedvecXKeysAry;
   private SparseArray<HGBVector2D[]> sortedColKeysAS;

   // ------------------------------------------------

   private int roseRings = 0;
   private int zeroColIndex;
   private int zeroRowIndex;
   private double hiveMaximumMagnitude;
   // ------------------------------------------------

   /**
    * Initialize once for the class to be used with each touch to be located.
    * (Note: reinitialized when cell size change or number of cells change.
    *        see: initHive() which calls generatedHive())
    */
	public void locatorInitialize()
	{
	   roseRings = hgbShared.getRoseRings();
      tmpVector2D = new HGBVector2D();

      hiveOrigin = hgbShared.getHiveOrigin();
      hiveMaximumMagnitude = this.determineHiveMaxMagnitude();


      /**
       * Most of the work for locator is done in HGBLocatorAid(). Here
       * initialize once for the class to be used with each touch to be
       * located.
       *
       * Get HGBVector2D's from the hive origin to each cell Store them as a
       * column of cells in a array keyed by vecX of each HGBVector2D Order
       * the keys Sort each column by vecY
       */
      hgbLocatorAid.Initialize();

		hiveOrigin = hgbShared.getHiveOrigin();
		int roseRings = hgbShared.getRoseRings();
		vectorRadius = hgbShared.getVertexRadius();
		int[] cellIndices = hgbProgressions.getCellIndices(roseRings);
      hgbShared.setCellIndices(cellIndices);

      SparseArray<Integer> vecXCountSA = new SparseArray<Integer>();
      SparseArray<Integer> vecYCountSA = new SparseArray<Integer>();

		// Create vectors from the hive origin to each cell origin
      // and count the number of (cast to int) repeated x coordinates
		SparseArray<HGBVector2D> cellVector2DSA = createCellVector2Ds(cellIndices,
            vecXCountSA, vecYCountSA);

      // The return is a sparseArray of arrays of all hive vectors with the same VecX or vecY
      // (in the same column or row of the hive) keyed by (cast to in) vecX or vecY
      SparseArray<HGBVector2D[]> keyByVecXColSA = keyByVecXorY(cellVector2DSA,
            vecXCountSA,true);

      // columns of vecX cells sorted by vecY (still keyed by vecX)
     	sortedColKeysAS = sortByVecXorY(keyByVecXColSA, true);

      // The x offset, from the origin of the hive,
      // to the origin of each cell of each column of the hive
		orderedvecXKeysAry = hgbLocatorAid.orderKeys(vecXCountSA);
   }

	// ===============================================
   public int getCellIndex(float touchX, float touchY)
   {
      touchXY[0] = touchX;
      touchXY[1] = touchY;
      return getCellIndex(touchXY);
   }

   public int getCellIndex(float[] touchXY)
   {
		// The vector from the hive origin to the touch point
		HGBVector2D touchVec = tmpVec.vCartesianVector(hiveOrigin, touchXY);

		// This is the first check to see if the touch is out of the hive.
      // However, it will not catch all.  If an out of bounds gets by here...
      // it will make it all the way to the end.  I chose not to figure out
      // how it makes it so far... I just catch out of bounds when I test
      // to see if the touch is within the chosen cell.
		if (touchVec.getMagnitude() > hiveMaximumMagnitude) return -1;

      int cellIndex = hgbLocatorAid.locateCellIndex(touchXY, touchVec, sortedColKeysAS);
      return cellIndex;
   }
   
	// ===============================================
	// Entry to followCellIndices
	private float[] tmpTouchXY = new float[2];

	// See HiveView.OnGestureListener.onScroll()

	HGBCellPack cellPack = null;


	// ===============================================
   /**
    * Create an HGBVector2D for each cell from the hive origin to each cell
    * origin. And counts the number in each set of equal X (cast to int) (via
    * indirect preallocate vecXCountSA).
    *
    * @param cellIndices
    * @return SparseArray<HGBVector2D>. The vector created via vCartesianVector
    *         from the hive Origin to the rose origin, keyed by cell id.
    *         vecXCountSA filled and return indirectly
    */
   protected SparseArray<HGBVector2D> createCellVector2Ds(int[] cellIndices,
                                                          SparseArray<Integer> vecXCountSA,
                                                          SparseArray<Integer> vecYCountSA)
   {
      SparseArray<HGBVector2D> cellVector2DSA = new SparseArray<HGBVector2D>(cellIndices.length);

      float[] cellOrg = null;
      int vecX = -1;
      int vecY = -1;
      int id = -1;
      int xIndex = -1;
      int yIndex = -1;
      final Integer notFound = -1;

      for (int inx = 0; inx < cellIndices.length; inx++)
      {
         HGBCellPack hgbCellPack = hgbShared.getCellPack(cellIndices[inx]);
         if (hgbCellPack != null)
         {
            // Create a vector between the hive origin and the cell origin
            // (There, when the vector is created, magnitude is calculated.)
            cellOrg = hgbCellPack.getOrigin();
            id = hgbCellPack.getIndex();

            HGBVector2D vec = tmpVector2D.vCartesianVector(hiveOrigin, cellOrg);
            vec.setID(id);
            cellVector2DSA.put(id, vec);

            // Count the cast to int vecX's  (storing the quanity of equal vec's)
            vecX = (int) vec.getX();
            xIndex = vecXCountSA.get(vecX, notFound);
            if (xIndex == -1)
            {
               // This is a new occurrence of the x value, start counting
               xIndex = 0;
            }
            vecXCountSA.put(vecX, ++xIndex);

            // Count the cast to in vexY's (storing the quanity of equal vecY's)
            vecY = (int) vec.getY();
            yIndex = vecYCountSA.get(vecY, notFound);
            if (yIndex == -1)
            {
               // This is a new occurrence of the x value, start counting
               yIndex = 0;
            }
            vecYCountSA.put(vecY, ++yIndex);

         }
      }
      return cellVector2DSA;
   }

   /**
    * Separates the input hive origin to cell origin vectors
    * int arrays of the same vecX or vecY component (cast to int) stored
    * in a sparse array keyed by the working vector (vecX or vecY).
    *
    * @param cellVector2DSA
    *            HGBVector2D instance defined for every rose via
    *            vCartesianVector(), the hiveOrigin to each cell origin.
    * @param vecCountSA
    *        Count of the number of cells in each column of VecX or row of VecY
    *        (This count is used to allocate returned arrays)
    * @param xyFlg
    *        process (true) xVec (false) yVec
    * @return SparseArray of array of vecX or vecY, keyed by vecX or vecY
    *         of all hive vectors with the same (cast to int) vecX or vecY
    */
   protected SparseArray<HGBVector2D[]> keyByVecXorY(SparseArray<HGBVector2D> cellVector2DSA,
                                                     SparseArray<Integer> vecCountSA,
                                                     boolean xyFlg)
   {
      // Arrays of vectors of the same Vector XorY (cast to int)
      SparseArray<HGBVector2D[]> keyByVecSA = new SparseArray<HGBVector2D[]>();

      // Index into allocated vector XorY (cast to int) arrays
      // Somewhere to store the indexes to all the arrays
      SparseArray<Integer> indexSA = new SparseArray<Integer>();

      HGBVector2D[] vecAry = null;
      int vec = -1;
      int inx = -1;
      int cnt = -1;

      // parse though all vectors from the hive origin to each cell
      // Hunt for (cast to int) vecX or VecY that are equal: count them and
      // store the vectors in a sparse array keyed by vecX or vecY
      for (int index = 0; index < cellVector2DSA.size(); index++)
      {
         int key = cellVector2DSA.keyAt(index);
         HGBVector2D tmpVec = cellVector2DSA.get(key);
         vec = (xyFlg) ? (int) tmpVec.getX() : (int) tmpVec.getY();
         vecAry = keyByVecSA.get(vec);
         if (vecAry == null)
         {
            // The the key has not yet been stored
            cnt = vecCountSA.get(vec);
            vecAry = new HGBVector2D[cnt];
            inx = 0;
            vecAry[inx] = tmpVec;
            indexSA.put(vec, inx);
            keyByVecSA.put(vec, vecAry);
         }
         else
         {
            // Then this key was previously stored, get it and add to it.

            // indexSA stores the index into this vecX's array
            inx = indexSA.get(vec);
            vecAry[++inx] = tmpVec;

            // store the incremented new index into vecAry in indexSA
            // Overwrites the previously stored index for this vecX
            indexSA.put(vec, inx);
         }
      }

      return keyByVecSA;
   }

   /**
    * Sorts all vecX columns form top to bottom or vecY rows left to right cells vecY or vecX.
    * Loops through input SA and feed each individual array to sortCellVecAry() which
    * does the sort.
    *
    * @param keyByVecSA
    *            : SparseArray keyed by vecX of all cells with the same
    *            HGBVector2D X value (cast to int) or vecY
    * @return sortedVec_KeyByVecXorYSA
    *
    */
   public SparseArray<HGBVector2D[]> sortByVecXorY(SparseArray<HGBVector2D[]> keyByVecSA, boolean xyFlg)
   {
      SparseArray<HGBVector2D[]> sortedVec_KeyByVecXorYSA = new SparseArray<HGBVector2D[]>(keyByVecSA.size());

      int key;
      HGBVector2D[] sortedbyVec;
      for (int inx = 0; inx < keyByVecSA.size(); inx++)
      {
         key = keyByVecSA.keyAt(inx);
         HGBVector2D[] vec = keyByVecSA.get(key);
         sortedbyVec = sortCellVecAry(vec, xyFlg);
         sortedVec_KeyByVecXorYSA.put(key, sortedbyVec);
      }

      return sortedVec_KeyByVecXorYSA;
   }

   /**
    * Sorts the input vecX (or vecY) arrays by each cells vecY (or vecX)
    * from top to bottom (or left to right).
    *
    * @param cellVecAry
    *            : A none sorted vecX (or vecY) array of cells from
    *            keyByVecSA  (or keyByVecYRowSA)
    * @param xyFlg
    *            : true:  sort by X, else by Y
    * @return The input array stored into a new sorted array.
    *
    *
    * @see class Pair
    */
   private HGBVector2D[] sortCellVecAry(HGBVector2D[] cellVecAry, boolean xyFlg)
   {
      HGBVector2D[] sortedCellVecAry = new HGBVector2D[cellVecAry.length];

      // Pair is an internal private class to allow an Arrays.sort()
      Pair[] tmpAry = new Pair[cellVecAry.length];

      // store the vector Y value and it's position in the cellVecAry
      for (int inx = 0; inx < cellVecAry.length; inx++)
      {
         // I needed to sort on the vectors Y val but retain the
         // index. To do so, I had to create a comparable class
         // (I could not sort on a 2 dimensional int array. Just
         // have to eat the overhead; but is done during initialize.)
         Pair pair = (xyFlg)
               ?  new Pair((int) cellVecAry[inx].getY(), inx)
               :  new Pair((int) cellVecAry[inx].getX(), inx);
         tmpAry[inx] = pair;
      }
      // Sort from smallest to largest (the negatives are on top)
      // A simple Arrays.sort() works perfect!
      Arrays.sort(tmpAry);

      // Fill the sortedCellVecAry with the column of cells sorted from top to
      // bottom
      for (int inx = 0; inx < tmpAry.length; inx++)
      {
         Pair pair = tmpAry[inx];
         sortedCellVecAry[inx] = cellVecAry[pair.index];
      }

      return sortedCellVecAry;
   }

   /**
    * Internal class to allow the use of Arrays.sort() I need to sort a two
    * dimensional array; but Arrays.sort() will not. I need to keep the cell
    * index and it's vecY together as I sorted an array of vecY. To do so I had
    * to create this class which implements Comparable(Pair)
    */
   private class Pair implements Comparable<Pair>
   {
      public Pair(int Y, int index)
      {
         this.Y = Y;
         this.index = index;
      }

      private int Y;
      private int index;

      @Override
      public int compareTo(Pair pair)
      {
         return this.Y - pair.Y;
      }
   }

   /**
    * I define (here) the hive magnitude as the distance to the furthest
    * vertex in the hive.
    *
    * There are a set of 6 "hive vertices".  After the center rose, these
    * vertices appear in pairs at the extremes of the hive.  The pair off
    * of the first rose in the last rose ring are easiest to find.  Use
    * Petal 1 vertex 0 to find the hive magnitude.
    *
    * @return double hive maximum magnitude Returns -1 on error.
    */
   protected double determineHiveMaxMagnitude()
   {
      int roseRings = hgbShared.getRoseRings();

      // Confession: I am at a loss as to why I need to subtract 1.
      // I suppose it has to do with a confusion early on about whether to
      // count rose 0 as a ring. At any rate... the right answer for the
      // last ring is one less.
      int[] range = hgbProgressions.roseRingRange(roseRings - 1);
      int petal1Index = range[0] + 1;
      float[][] baseVertices = hgbShared.getBaseVertices();

      HGBCellPack hgbCellPack = hgbShared.getCellPack(petal1Index);
      if (hgbCellPack != null)
      {
         // Get the origin, offset to vertex 0 and create a vector2D form the
         // hive origin to vertex 0 of petal 1 and get the magnitude.
         float[] org = hgbCellPack.getOrigin();
         float[] vertexCoordinate = new float[2];
         vertexCoordinate[0] = baseVertices[0][0] + org[0];
         vertexCoordinate[1] = baseVertices[0][1] + org[1];
         HGBVector2D vec = tmpVector2D.vCartesianVector(hiveOrigin,
               vertexCoordinate);
         double mag = vec.getMagnitude();
         return mag;
      }

      return -1;
   }


}
