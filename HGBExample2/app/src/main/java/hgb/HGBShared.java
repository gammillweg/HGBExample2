package hgb;

import android.graphics.RectF;

import java.util.Arrays;

// HGBShared is a sudo Singleton (in other words NOT a Singleton; but sorta treated as one).
// (Of course, I don't really understand what a Singleton is, so what do I know about a
// sudo Singleton :-) )

// This class is meant to me create ONCE and only ONCE in the main Activity (or other higher
// method and passed on down through parameters; such that only this ONE and ONLY ONE instance exists.
// It is a common access storage location.  Many classes store their results here to be
// accessed as needed by other classes without access to the worker class.

public class HGBShared
{
	public HGBShared()
	{
		baseVertices = new float [HGBShared.SIDES][];
		basePetalOrigins = new float[HGBShared.SIDES][];
		
		hiveOrigin = new float[2];
		
		////--arrayMgr = new HGBArrayMgr(this);
		hgbAllocateCellAry = new HGBAllocateCellAry(this);
	}
	
	//private final String TAG = "TAG_Shared";
	private HGBAllocateCellAry hgbAllocateCellAry = null;
	
	//-------------------------------------------------------------
	// NOT USED
	// Return the rose index whom the input petalIndex belongs to
//	public int getPetalsRoseIndex(int petalIndex)
//	{
//		return (petalIndex - (petalIndex % 10));
//	}
	
	//-------------------------------------------------------------
	// The minimum number of rose rings
	private final int minRoseRings = 0;
	public int getMinRoseRings() { return minRoseRings; }
	
	// The maximum number of allowed rose rings
	// On my kindle 60 seems to be the absolute max it can handle
	// TODO -- figure out some way of knowing what other devices can handle
	private int maxRoseRings = 60;
	public void setMaxRoseRings(int maxRoseRings) { this.maxRoseRings = maxRoseRings; }
	public int getMaxRoseRing() { return maxRoseRings; }
	
	private int roseRings;
	public int getRoseRings() { return roseRings; }
	public void setRoseRings(int roseRings) 
	{ 
		if (roseRings < 0) roseRings = 0;
		this.roseRings = roseRings;
		// Allocate space to store cells (HGBCellPack) in hgbShared
		allocateCellAry(roseRings);
	}
	
	//-------------------------------------------------------------
	// These were originally in HGBStatics; but were moved to HBGShared
	// to help keep HGBStatics hidden from the public.
	public static final int SIDES = 6;
	public enum OrientationRadians { Landscape, Portrait }

   // side 3:  adjacentSides[3] --> int[2] 4 and 2 the sides
   // clockwise adjacent to side 3
   private static final int[][] adjacentSides =
         {
               {1, 5}, // side 0
               {2, 0}, // side 1
               {3, 1}, // side 2
               {4, 2}, // side 3
               {5, 3}, // side 4
               {0, 4}, // side 5
         };
   public int[] getAdjacentSides(int side)
   {
      if (side < 0) return null;
      if (side > 5) return null;
      int[] sides = new int[2];
      sides[0] = adjacentSides[side][0];
      sides[1] = adjacentSides[side][1];
      return sides;
   }
   //-------------------------------------------------------------
   private HGBUtils hgbUtils = null;
	public void setHGBUtils(HGBUtils hgbUtils)
	{
		this.hgbUtils = hgbUtils;
	}
   public HGBUtils getHGBUtils()
   {
      return hgbUtils;
   }

	private  HGBLocator hgbLocator = null;
	public void setHGBLocator(HGBLocator hgbLocator)
	{
		this.hgbLocator = hgbLocator;
	}
	public HGBLocator getHGBLocator()
	{
		return hgbLocator;
	}
   //-------------------------------------------------------------
	// cellAry is an array of all CellPack's
	// When filled with instances of CellPack, there are wasted members
	// in the array.  Each Rose contains 7 hexagons (cells) (The center 
	// and 6 petals); but roses increment by 10's.  Hence, indices 0, 1-6 
	// are filled with instances of CellPacks and 7,8 and 9 are left null.)
	
	protected HGBCellPack[] cellAry = null;
	
	protected HGBCellPack[] getCellAry() { return cellAry; }
	public int getCellAryLen() { return cellAry.length; }

   // if cellIndex == -1 return null;
	public HGBCellPack getCellPack(int cellIndex) 
	{
		////--if (cellIndex > cellAryLen ) { cellIndex = cellAryLen; }
		if (cellIndex > cellAry.length ) { cellIndex = cellAry.length; }
		if (cellIndex < 0) return null; /*{ cellIndex = 0; }*/
		try
		{
			return cellAry[cellIndex];
		}
		catch(Exception exc)
		{
			return null;
		}
	}

	//-------------------------------------------------------------
	// Can not use cellAry.Length as there may be unused members in the arrays.
	
	// cellAry is allocated in blocks of 10.  cellAryLen will be 3 past last
	// filled cell as only 7 of each block of 10 are used.
	////--private int cellAryLen = 0;
	////--public void setCellAryLen(int cellAryLen)	{ this.cellAryLen = cellAryLen; }
	////--public int getCellAryLen() { return cellAryLen; }	
	

	// lastLiveRose is the index to the last rose actively used
	//public int getLastLiveRose() { return arrayMgr.getLastLiveRose(); }
	
	// cellCnt is a true count of filled array member.  Although allocated in
	// blocks of 10 with 3 wasted in each block, only the 7 filled are part
	// of cellCnt.
	public int getCellCount() { return hgbAllocateCellAry.getCellCnt(); }
   public int getRoseCount() { return hgbAllocateCellAry.getRoseCnt(); }
	
	protected boolean allocateCellAry(int roseRings)
	{
		return hgbAllocateCellAry.allocateCellAry(roseRings);
	}

   // cell Indices are the cell index of all cells correctly numbered
   // such that the indices count as the cells are indexed (0,1,2...,6...90,91...97,100 etc)
   // NoteOnAndroidStudio that wasted cells are not included here.  ([0]=0...[6]=6, [7]=10
   private int[] cellIndices = null;
   protected void setCellIndices(int[] cellIndices)
   {
      // To protect the true array... a copy is returned
      // this.cellIndices = new int[cellIndices.length];
      // It would appear that I do not have to pre-allocate the array (that Arrays does it).
      this.cellIndices = Arrays.copyOf(cellIndices, cellIndices.length);
   }
   public int[] getCellIndices() { return cellIndices; }
	
	//-------------------------------------------------------------
	// vertexRadians0, vertexRaians90, normalRadians0 and normalRadians90
	// defined as public static final double[] in HexStatic.  As the
	// device is rotated one or the other is chosen to keep the flat
	// side of a true hexagon found within the rose hive parallel to the
	// top of the current orientation of the device.
	
	// The active arrays are stored here
	private double[] vertexRadiansAry;
	private double[] normalRadiansAry;
	private double[] originRadiansAry;
	public void setVertexRadians(OrientationRadians orientationRadians) 
	{
		switch (orientationRadians)
		{
			case Landscape:
				vertexRadiansAry = HGBStatics.vertexRadiansLandscape;
				normalRadiansAry = HGBStatics.normalRadiansLandscape;
				originRadiansAry = HGBStatics.originRadiansLandscape;
				break;
			
			case Portrait:
				vertexRadiansAry = HGBStatics.vertexRadiansPortrait;
				normalRadiansAry = HGBStatics.normalRadiansPortrait;
				originRadiansAry = HGBStatics.originRadiansPortrait;
				break;
				
			default:
				vertexRadiansAry = HGBStatics.vertexRadiansLandscape;
				normalRadiansAry = HGBStatics.normalRadiansLandscape;
				originRadiansAry = HGBStatics.originRadiansLandscape;
		}
   }
	protected double[] getVertexRadiansAry() { return vertexRadiansAry; }
	protected double[] getNormalRadiansAry() { return normalRadiansAry; }
	protected double[] getOriginRadiansAry() { return originRadiansAry; }

	//-------------------------------------------------------------
	// There isn't a setVertexRadius() or setNormalRadius() as class HexBase must 
	// recalculate the baseVerices and the basePetalOrigins if the vertex radius 
	// is changed.  Thus, see setBaseVertices() where vertexRadius is passed
	// forward from class HexBase.  
	
	// vertex radius is the magnitude of a vector from the origin of a cell
	// to any vertex of that cell.  The name cellSize is known to the public
	// and vertexRadius is used internally.
	private double vertexRadius;
	protected double getVertexRadius() { return vertexRadius; }
	public void setCellSize(double cellSize) { vertexRadius = cellSize; }
	public double getCellSize() { return vertexRadius; }
	
	// normal radius is the magnitude of a vector from the origin of a cell
	// to the center of any side of that cell.  (The height of the isosceles triangle.)
	private double normalRadius;
	protected double getNormalRadius() { return normalRadius; }

	// base vertices are the "points" of the hexagon about the origin (0,0)
	private float[][] baseVertices;
	public float[][] getBaseVertices() { return baseVertices; }
	protected void setBaseVertices(float[][] baseVertices, double vertexRadius, double normalRadius)
	{
		// see the comment above about the exclusion of a setVertexRadius() or setNormalRadius() methods 
		this.vertexRadius = vertexRadius; 
		this.normalRadius = normalRadius;
		
		for (int inx = 0; inx < HGBShared.SIDES; inx++)
		{
			this.baseVertices[inx] = new float[2];
			this.baseVertices[inx][0] = baseVertices[inx][0]; 
			this.baseVertices[inx][1] = baseVertices[inx][1];
		}
	}

	//-------------------------------------------------------------
	private RectF baseSuperscribedRectF = null;
	private float halfWidthSSRF = 0;
	private float halfHeightSSRF = 0;

	public void setBaseSuperscribedRectF(float left, float top, float right, float bottom)
	{	
		// As will be used to define a bitmap, the rectangle needs to be defined
		// from (0,0).  Use the baseVertices to find the width and height and
		// as the bitmap asks for int (rather than float), create the rectangle as
		// an integer rectangle
		int width = Math.round(-left + right);
		int height = Math.round(-top + bottom);
		
	   halfWidthSSRF = width/2;
		halfHeightSSRF = height/2;

      baseSuperscribedRectF = new RectF(0, 0, width, height);
	}
	
	public RectF getBaseSuperscribedRectF()
	{
		return baseSuperscribedRectF;
	}
	public float getHalfWidthSSRF() { return halfWidthSSRF; }
	public float getHalfHeightSSRF() { return halfHeightSSRF; }
	
	//-------------------------------------------------------------
	private RectF baseInscribedRectF;
	private float halfWidthISRF = 0;
	private float halfHeightISRF = 0;

	public void setBaseInscribedRectF(float left, float top, float right, float bottom)
	{
		// As will be used to define a bitmap, the rectangle needs to be defined
		// from (0,0).  Use the baseVertices to find the width and height and
		// as the bitmap asks for int (rather than float), create the rectangle as
		// an integer rectangle

		int width = Math.round(-left + right);
		int height = Math.round(-top + bottom);
		
		halfWidthISRF = width/2;
		halfHeightISRF = height/2;

		baseInscribedRectF = new RectF(0, 0, width, height);
	}

	public RectF getBaseInscribeRectF()
	{
		return baseInscribedRectF;
	}
	public float getHalfWidthISRF() { return halfWidthISRF; }
	public float getHalfHeightISRF() { return halfHeightISRF; }

	//-------------------------------------------------------------
	// The origins of all the hexagons about the base rose whose origin is (0,0).
	// These hexagons about the rose center are called "petals".
	// base origins are the origins of a rose: the center origin (0,0) and all
	// adjacent hexagons about the center (total of 7 origins).
	private float[][] basePetalOrigins;
	public float[][] getBasePetalOrigins() { return basePetalOrigins; }
	protected void setBasePetalOrigins(float[][] basePetalOrigins)
	{
		for (int inx = 0; inx < HGBShared.SIDES; inx++)
		{
			this.basePetalOrigins[inx] = new float[2];
			this.basePetalOrigins[inx][0] = basePetalOrigins[inx][0]; 
			this.basePetalOrigins[inx][1] = basePetalOrigins[inx][1];
		}
	}
	
	//-------------------------------------------------------------
	// The hive is the main play grid of hexagons.
	// Only the origin of the single cell at the center of the hive is set: hiveOrigin
	// All other cells have there origins calculated from an offset from the hiveOrigin.
	// (see HGBCellPack.setOffset() (computed in HGBGeneratedHive))

	// FIXME -- This does NOT change the origin of the current path.
	// rather it changes the origin of the NEXT path to be created (I think).
	// Hence, as attempted to used here, does not work.  The hive has been
	// generated when this call is made. and the next call to draw the path
	// draws in the originally generated origin.  Needs a translate.
   //
   // DID I FIX THIS??? I don't know.  Need to look into it.

	private float[] hiveOrigin;
	public float[] getHiveOrigin() { return hiveOrigin;	}
	public void setHiveOrigin(float[] hiveOrigin) 
	{ 
		this.hiveOrigin[0] = hiveOrigin[0];	
		this.hiveOrigin[1] = hiveOrigin[1];
	} 
	//-------------------------------------------------------------
	// DEBUG
//	public float[] lastTouch = null;
//	public float lastTouchX = -1;
//	public float lastTouchY = -1;
//	public void setLastTouch(float lastTouchX, float lastTouchY)
//	{
//		this.lastTouchX = lastTouchX;
//		this.lastTouchY = lastTouchY;
//		
//		if (lastTouch == null)
//		{
//			lastTouch = new float[2];
//		}
//		lastTouch[0] = lastTouchX;
//		lastTouch[1] = lastTouchY;
//	}
	//-------------------------------------------------------------

}
