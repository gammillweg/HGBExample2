package hgb;

import java.lang.reflect.Array;

// In a previous revision, the class HGBArrayMgr allocated cellAry
// but did not allocate HGBCellPack for each cell. But in that class,
// the array only grew and the array was never destroyed and reallocated.  
// That was a needless complexity to the program and cause problems 
// with "stale" bitter ends.  This class is a rewrite of that.  
// Here, if the array is made smaller, it's stale allocation is nulled.

// cellAry is allocated in blocks of 10, only 7 of which are used
// (the other 3 left empty (null) and wasted.)
public class HGBAllocateCellAry
{
	protected HGBAllocateCellAry(HGBShared hgbShared)
	{
		this.hgbShared = hgbShared;
	}

	private HGBShared hgbShared = null;
	private HGBProgressions progressions = null;

	// cellCnt is a true count of filled array member. Although allocated in
	// blocks of 10 with 3 wasted in each block, only the 7 filled are part
	// of cellCnt.
	private int cellCnt = 0;
   private int roseCnt = 0;
	protected int getCellCnt() { return cellCnt; }
   protected int getRoseCnt() { return roseCnt; }

	private HGBCellPack[] cellAry = null;

	// -------------------------------------------------------------
	// Only allocates, DOES NOT assign a CellPack to any member on allocation.
	// Sets this.cellAryLen and this.cellCnt;
	// Return is true on success, and false on failure.
	@SuppressWarnings("rawtypes")
	protected boolean allocateCellAry(int roseRings)
	{
		if (progressions == null)
		{
			progressions = new HGBProgressions();
		}
		roseCnt = progressions.countAllRoses(roseRings);
		
		cellAry = hgbShared.getCellAry();

		// cellAry is allocated in blocks of 10. cellAryLen will be 3 past last
		// filled cell as only 7 of each block of 10 are used.

		// There are 7 cells per rose
		cellCnt = roseCnt * 7;

		// There are 10 array members per rose (3 are wasted)
		int tmpCellAryLen = roseCnt * 10;

		if (cellAry == null)
		{
			try
			{
				// first pass -- simply allocate the array
				hgbShared.cellAry = new HGBCellPack[tmpCellAryLen];
			} catch (Exception exc)
			{
				return false;
			}
			return true;
		}
		else if (cellAry.length == tmpCellAryLen)
		{
			// Then the array can remain unchanged.
			// no allocation necessary
			return true;
		}
		else if (cellAry.length > tmpCellAryLen)
		{
			// The need is greater than the array previously allocate
			// the array is to be destroyed and re-allocated.
         // (nulled to let java trash collection clean it up)
			hgbShared.cellAry = null;  
			hgbShared.cellAry = new HGBCellPack[tmpCellAryLen];
			return true;
		}
		else if (cellAry.length < tmpCellAryLen)
		{
			// Then the array has to grow
			// Allocate and copy.
			// The idea from Android ArrayUtils.appendElement()

			try
			{
				// Allocate additional space, then get a component type for use
				// in Array.newInstance
				HGBCellPack[] growAry = new HGBCellPack[tmpCellAryLen - cellAry.length];
				Class growAryClass = growAry.getClass();
				Class growAryType = growAryClass.getComponentType();

				final HGBCellPack[] result = (HGBCellPack[]) Array.newInstance(growAryType,
						tmpCellAryLen);
				System.arraycopy(hgbShared.cellAry, 0, result, 0, cellAry.length);
				hgbShared.cellAry = result;
			} catch (Exception exc)
			{
				return false;
			}
			// -------------------------------------------------------------------------------

			return true;
		}
		else
		{
			return false;
		}
	}
}
