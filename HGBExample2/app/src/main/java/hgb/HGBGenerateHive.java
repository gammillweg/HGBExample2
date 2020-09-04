package hgb;

//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.PrintWriter;

//The work here and calls to Classes Progressions and Bonding
//Is all stored in HGBShared.cellAry packaged in class CellPack.
//Here, HGBShared.cellAry is allocated and insured that it is of sufficient
//size to hold the CellPacks created (see HGBShared.allocateCellAry()).
//Then empty CellPacks are created and stored in HGBShared.cellAry.
//All necessary roseRings are created out to the farthest edge.


//Progressions does common progressions about the rose rings.  (As in
//how the vertices assigned to rose pointing vectors rotate about
//the roses as they rotate about the rose ring.)  Progressions fill
//CellPack arrays used to help class Bonding create bonds between
//adjacent roses or petals (as stored in CellPack.bondAry.)

//Bonding fills CellPack.bondAry.  An array of HGBShared.SIDES (6)
//indexed by position holding the adjacent bondAry index.  Bonding's
//are done from the outer edge inward. Outer petal CellPack.bondAry
//members NOT bonded, as nothing is out there to bond to, are set to -1.

//Finally an offset from the hive origin to the initial cell origin
//is computed from each rose and petals and stored in HGBCellPack.

public class HGBGenerateHive
{
	public HGBGenerateHive(HGBShared hgbShared)
	{
		this.hgbShared = hgbShared;
	}

	private HGBShared hgbShared = null;
	private HGBBonding bonding = null;
	private HGBProgressions progressions = null;
	private HGBHexBase hexBase = null;

	// Used to find the origins of each cell
	private float[][] baseVertices;
	private double vertexRadius;
	private double[] originRadiansAry;

	private float[][] basePetalOrigins;
	private float[] cellOrigin = new float[2];
	private float[] cellOffset = new float[2];
	private float[] hiveOrigin;

	public boolean generateHive_Main()
	{
		// ---------------------------------------------------
		if (progressions == null)
		{
			progressions = new HGBProgressions();
		}
		if (bonding == null)
		{
			bonding = new HGBBonding(progressions, hgbShared);
		}
		if (hexBase == null)
		{
			hexBase = new HGBHexBase(hgbShared);
		}
		// Used to find cell origins
		baseVertices = hgbShared.getBaseVertices();
		vertexRadius = hgbShared.getVertexRadius();
		basePetalOrigins = hgbShared.getBasePetalOrigins();
		originRadiansAry = hgbShared.getOriginRadiansAry();
		hiveOrigin = hgbShared.getHiveOrigin();
		int roseRings = hgbShared.getRoseRings();
		if (roseRings < 0) roseRings = 0;

		// Create a base hexagon per the vertexRadius (cellSize)
		double vertexRadius = hgbShared.getVertexRadius();
		hexBase.setVertexRadius(vertexRadius);

		// Filled by calls to rosesPerRing()
		int[] roseIndicesAry;

		// ---------------------------------------------------
		// Create ALL CellPack cells out to the edge
		// fill an int array with vector vertices (one per vector)
		// fill an in array with vector to roses (one per vector)
		// fill a two dimensional array with the petals to be bonded 
		// (one per vector)
		// Finally bond adjacent sides of adjacent petals

		// NOTE: Rose 0 and it's 6 petals is counted as ring 1
		// Thus, roseRing = 0 (as index in the for loop) IS ring 1
		// Thus, if roseRings == 3, one steps through 0, 1, 2 (THREE rings)
		for (int roseRing = 0; roseRing < roseRings; roseRing++)
		{
			roseIndicesAry = rosesPerRing(roseRing);
			for (int roseIndex : roseIndicesAry)
			{
				if (createRoseBondPack(roseIndex, hgbShared) == false)
					return false;
				if (createRosePetalsBondPack(roseIndex, hgbShared) == false)
					return false;
			}
			int[] verticesAry = progressions.vectorOriginVertex(roseRing);
			int[] vectorToRoseAry = progressions.vectorToRose(roseRing);
			int[][] vectorPetalAry = progressions.petalsPerVector(verticesAry, roseRing);

			bonding.bondVectorPetals_main(verticesAry, vectorToRoseAry, vectorPetalAry, roseRing);

			// Set all origins per 0,0
			setOrigins(roseRing);
		}

		return true;
		// ---------------------------------------------------
	}

	// Create any cells (CellPack's) needed as input rose indices
	// The return is modified HGBShared.cellAry
	private boolean createRoseBondPack(int roseIndex, HGBShared hgbShared)
	{
		try
		{
			HGBCellPack cellPack = new HGBCellPack(roseIndex, hgbShared);
			hgbShared.cellAry[roseIndex] = cellPack;
		} catch (Exception exc)
		{
			return false;
		}
		return true;
	}

	// Create the petals about the rose index provided
	// The return is modified HGBShared.cellAry
	private boolean createRosePetalsBondPack(int roseIndex, HGBShared hgbShared)
	{
		try
		{
			for (int inx = 1; inx < HGBShared.SIDES + 1; inx++)
			{
				int petalIndex = roseIndex + inx;
				HGBCellPack cellPack = new HGBCellPack(petalIndex, hgbShared);
				hgbShared.cellAry[petalIndex] = cellPack;
			}
		} catch (Exception exc)
		{
			return false;
		}
		return true;
	}

	// ---------------------------------------------------------------------------
	// ---------------------------------------------------------------------------
	// Calculate the rose index of each rose in a single rose ring
	// (Progression.roseRingRange() and rosesPerRing() are both short fast
	// integer loops, and, are called often.)
	// Return is an array of rose indices (as filled by
	// Progressions.rosesPerRing(range)
	private int[] rosesPerRing(int roseRing)
	{
		int[] range = progressions.roseRingRange(roseRing);
		int[] roseIndicesAry = progressions.rosesPerRing(range);
		return roseIndicesAry;
	}

//	// Count the number of roses by roseRing
//	// NOTE: Rose 0 is COUNTED as a rose ring, thus rose ring 1 is 0, 2 is
//	// 10-60, 3 70-180, 4 190-360
//	// for a total of 37 roses.
//	private int countAllRoses(int roseRings)
//	{
//		int roseCnt = 0;
//		for (int inx = 0; inx < roseRings; inx++)
//		{
//			roseCnt += countRosesPerRing(inx);
//		}
//		return roseCnt;
//	}
//
//	private int countRosesPerRing(int roseRing)
//	{
//		int[] range = progressions.roseRingRange(roseRing);
//		return ((range[1] - range[0]) / 10) + 1;
//		// int[] roseIndicesAry = progressions.rosesPerRing(range);
//		// return roseIndicesAry.length;
//	}

	// ========================================================================================
	// Origin calculations

	private void setOrigins(int roseRing)
	{
		// progressions.setRoseZeroOrigin(), calcualteRoseRingOneOrigins() and
		// calculateRoseRingOrigins() further call
		// progressions.calaculatePetals()
		// which defines the origin of all the petals of that rose.
		if (roseRing == 0)
		{
			// hexagon 0 lives at the center of rose ring 1
			// lives at hiveOrigin.
			setRoseZeroOrigin();
			return;
		}
		int[] vertices = progressions.originVertices(roseRing);
		int[][] rosePairs = progressions.originRosePairs(roseRing, vertices);
		if (rosePairs == null)
			return;

		calculateRoseOrigins(vertices, rosePairs);
	}

	// Rose Zero is special it lives at the hive origin
	private void setRoseZeroOrigin()
	{
		HGBCellPack cellPack = hgbShared.cellAry[0];
		cellOffset[0] = 0;
		cellOffset[1] = 0;
		cellPack.setOffsetFromHiveOrigin(cellOffset);

		// The petals around the zero rose
		originRosePetals(cellPack);
	}

	// Set the origins in CellPack for all roses in rosePairs[0]
	private void calculateRoseOrigins(int[] vertices, int[][] rosePairs)
	{
		try
		{
			HGBCellPack cellPack = null;
			for (int inx = 0; inx < vertices.length; inx++)
			{
				int rose = rosePairs[inx][0];
				int mate = rosePairs[inx][1];
				int vertex = vertices[inx];

				// Get the origin of the inner roses vertex used to
				// locate the origin of the outer rose.
				float[] mateOrigin = hgbShared.cellAry[mate].getOrigin();
				float[] vertexCoordinate = new float[2];
				vertexCoordinate[0] = baseVertices[vertex][0] + mateOrigin[0];
				vertexCoordinate[1] = baseVertices[vertex][1] + mateOrigin[1];

				cellOrigin[0] = (float) ((HGBStatics.radiiBetweenRoses * vertexRadius) * Math
						.cos(originRadiansAry[vertex])) + vertexCoordinate[0];
				cellOrigin[1] = (float) ((HGBStatics.radiiBetweenRoses * vertexRadius) * Math
						.sin(originRadiansAry[vertex])) + vertexCoordinate[1];

				// This is where each rose offset is defined
				cellPack = hgbShared.cellAry[rose];
				cellOffset[0] = hiveOrigin[0] - cellOrigin[0];
				cellOffset[1] = hiveOrigin[1] - cellOrigin[1];
				cellPack.setOffsetFromHiveOrigin(cellOffset);

				// The petals around this rose
				originRosePetals(cellPack);
			}
		} catch (Exception exc)
		{
			return;
		}

	}

	private void originRosePetals(HGBCellPack rosePack)
	{
		// All the work for finding the origins of the petals about a rose
		// has been done in HexBase.defineBasePetalOrigins(). All we have
		// to do is offset the one base petal set of origins.

		HGBCellPack cellPack = null;
		float[] roseOrigin = rosePack.getOrigin();
		for (int inx = 0; inx < rosePack.bondAry.length; inx++)
		{
			int index = rosePack.bondAry[inx];
			cellPack = hgbShared.cellAry[index];

			cellOrigin[0] = basePetalOrigins[inx][0] + roseOrigin[0];
			cellOrigin[1] = basePetalOrigins[inx][1] + roseOrigin[1];

			// This is where each petals offset is defined
			cellOffset[0] = hiveOrigin[0] - cellOrigin[0];
			cellOffset[1] = hiveOrigin[1] - cellOrigin[1];
			cellPack.setOffsetFromHiveOrigin(cellOffset);
		}
	}
	// ========================================================================================

	// =========== DEBUG data generated ======================

	// protected void writeToFile_cellAry(HGBShared hgbShared)
	// {
	// int cellAryLen = hgbShared.getCellAryLen();
	//
	// //------------------------------------------------------------------------------
	// FileOutputStream fos;
	// try
	// {
	// fos = new FileOutputStream(outPutFilePath);
	//
	// PrintWriter printWriter = new PrintWriter(fos, true);
	// //printWriter.println("Test2 append to file");
	// //printWriter.close();
	//
	//
	// // There are Many wasted indices -- we need to loop through the last rose
	// index plus 1 to get the last.
	// for (int dbinx = 0; dbinx < cellAryLen ; dbinx++)
	// {
	// CellPack cellPack = hgbShared.cellAry[dbinx];
	// if (cellPack == null)
	// {
	// String str = "cellAry[" + dbinx + "]:  null\n";
	// printWriter.println(str);
	// continue; // there are 3 empty (null) array members per rose
	// }
	//
	// String str = "cellAry[" + dbinx + "]:  " + cellPack.ToString();
	// //String str = cellPack.ToString();
	// //System.out.println(str);
	// printWriter.println(str);
	// }
	//
	// //------------------------------
	// printWriter.println("================= past cellAryLen =================");
	// // hgbShared.cellAry.length -- will give every cell, even stale cell.
	// for (int dbinx = cellAryLen; dbinx < hgbShared.cellAry.length ; dbinx++)
	// {
	// CellPack cellPack = hgbShared.cellAry[dbinx];
	// if (cellPack == null)
	// {
	// String str = "cellAry[" + dbinx + "]:  null\n";
	// printWriter.println(str);
	// continue; // there are 3 empty (null) array members per rose
	// }
	//
	// String str = "cellAry[" + dbinx + "]:  " + cellPack.ToString();
	// //String str = cellPack.ToString();
	// //System.out.println(str);
	// printWriter.println(str);
	// }
	//
	//
	// // This IS CR,LF; but still get normalization message from VS on opening
	// as test
	// //printWriter.println("\r\n");
	//
	// printWriter.close();
	// } catch (FileNotFoundException e)
	// {
	// e.printStackTrace();
	// }
	// System.out.println("writeToFile_cellAry(): Done");
	// //-------------------------------------------------------------------------------
	// }
	// //===============================================================
}
