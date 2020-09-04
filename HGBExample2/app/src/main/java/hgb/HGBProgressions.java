package hgb;

// A helper class for HGB
public class HGBProgressions
{
	//========================================================================================
   // simple counting of roses and cells
   
 	// Count the number of roses by roseRing
	// NOTE: Rose 0 is COUNTED as a rose ring, thus rose ring 1 is cell 0,
   // rose ring 2 is cells 10-60, and ring 3 cells 70-180 ), 4 cells 190-360 for a total of 37 roses.
   // (And, as there are 7 cells per rose, 4 rings will contain 259 cells.)
	protected int countAllRoses(int roseRings)
	{
		int roseCnt = 0;
		for (int inx = 0; inx < roseRings; inx++)
		{
			roseCnt += countRosesPerRing(inx);
		}
		return roseCnt;
	}
	
	protected int countRosesPerRing(int roseRing)
	{
		int[] range = roseRingRange(roseRing);
		return ((range[1] - range[0]) / 10) + 1;
		// int[] roseIndicesAry = progressions.rosesPerRing(range);
		// return roseIndicesAry.length;
	}
	
	/**
	 * Compute the cell indices (a simple incremental loop). (Recall that roses
	 * are in increments of 10 (from 0) and there 6 sides around each rose for
	 * a total of 7 cells.)
	 * 
	 * @return an array of cell indices exclusive of unused array indecies
     *        (Does not include unused cells cell 10 is stored in rtn[7]
	 */
	protected int[] getCellIndices(int roseRings)
	{
		int roseCnt = countAllRoses(roseRings);
		int[] cellIndices = new int[roseCnt * (HGBShared.SIDES + 1)];
		int roseIndex;
		int index = 0;
		for (int inx = 0; inx < roseCnt; inx++)
		{
			roseIndex = inx * 10;
			for (int side = 0; side < HGBShared.SIDES + 1; side++)
			{
				cellIndices[index++] = roseIndex + side;
			}
		}

		return cellIndices;
	}

	// The return is a int array of length == 2, where  
	//	[0] = the first rose in the rose ring
	// [1] = the last rose in the rose ring
   // rose ring == 1: will return [0]==10, [1]==60
   // as 10 is the fist rose and 60 the last rose in the ring
   // (Note that the results never change.  Rose ring 1 will always return
   // [0]==10, [1]==60.  I chose to calculate rather than look up constants.)
	// rose ring == 0 is special:  [0] = 0, and [1] = 0 will be returned
	// as rose ring == 0 is not a true rose ring, rather the center of a rose
   // Rose ring 0 has peddles 1-6
	protected int[] roseRingRange(int roseRing)
	{
		// This is a very short counting loop.  It would be very nice
		// if I could figure out the math to simply calculate the number of
		// roses in a rose ring and some way to know the index of the first rose.  
		// BUT... I don't see it.  On the other hand, I have previously written 
		// code in a loop to step from one rose ring to the next as I filled a 
		// SparseArray with all the ranges for all the rose rings.  Hence, here, 
		// use previously tested code, and start form 0 and loop upwards 
		// till I get the rose ring I want.

		int cnt = 0;
		int start = 0;
		int end = 0;

		for (int inx = 0; inx < roseRing; inx++)
		{
			start = end + 10;
			cnt += 6;
			end = (((start/10) + cnt) * 10) - 10;
		}
		
		int[] range = new int[2];
		range[0] = start;
		range[1] = end;
		
		return range;
	}
	
	// Return all roses with the provided range in a single rose ring
	// including start and stop
   // input [0] start rose, [1] stop rose (see roseRingRange())
	protected int[] rosesPerRing(int[] range)
	{
		if (range.length != 2) return null;
		int first = range[0];
		int last  = range[1];
		
		//---------------------------------------
		// 0 is a special case
		if ((first == 0) && (last == 0))
		{
			int[] roseZero = new int[1];
			roseZero[0] = 0;
			return roseZero;
		}
		
		//---------------------------------------
		// See discussion of this equation in verticesPerRing
		int skip = (((last - (first + 50)) / HGBShared.SIDES))/10;
		int len = (skip * 6) + 6;
		int[] roses = new int[len];
		
		// The normal processing
		int value = first;
		for (int inx = 0; inx < len; inx++)
		{
			roses[inx] = value;
			value += 10;
		}
		return roses;
	}

	//========================================================================================
   // Finding vectors for location of cells
	
	// The progression of the rose rings vectors origins by their origin vertex.
	// The return is a HashMap where the key is an Integer rose index
	// and the values are rose vertices.
	protected int[] vectorOriginVertex(int roseRing)
	{
		// rose ring == 0 has no vectors
		if (roseRing == 0) return null;

		// The first and last rose and allocate the return
		int[] range = roseRingRange(roseRing);
		int roseCnt = ((range[1] - range[0]) / 10) + 1;
		
		// There are three vectors per rose in the rose ring, except the
		// 6 rose ring vertices, which only contain 2 vectors each.
      int vectorCnt = (roseCnt * 3) - 6;
		int[] verticesAmy = new int[vectorCnt];
		
		// The vector sets progressing about the rose ring form their own hexagon.
		// The vertex rose has 2 vectors, the side runs have 3.  The number
		// of roses in a side run is 1 less than the rose ring value.
		int sideRun = roseRing - 1;
		
		// counts up to sideRun then resets
		int cnt = -1;
		
		// The rose ring vertices vector vertex always starts at 4 and decrements as the
		// vertices rotate counter clockwise.  And the sideRun vertex starts at 5;
		int vertex = 4;
		int sideRunVertex = 5;
		
		// Run around the rose ring.  Roses increment by 10.
		vectorCnt = 0;
		for (int roseIndex = range[0]; roseIndex < (range[1] + 10); roseIndex += 10)
		{
			if (cnt == -1)
			{
				// The first is the rose ring vertex, and contains 2 vertices
				//Integer[] vertices = new Integer[2];
				for (int inx = 0; inx < 2; inx++)
				{
					verticesAmy[vectorCnt++] = vertex;
					// when the vertex passes 0 it begins again from 5.
					vertex = ((vertex - 1) < 0) ? 5 : vertex - 1;
				}
				cnt++;
			}
			else // is a side run
			{
				// side runs all start at the same vertex, 2 clock wise from the
				// rose ring vertex last stored vector vertex
				vertex = sideRunVertex;
				
				// Side Runs contain 3 vertices
				for (int inx = 0; inx < 3; inx++)
				{
					verticesAmy[vectorCnt++] = vertex;
					// when the vertex passes 0 it begins again from 5.
					vertex = ((vertex - 1) < 0) ? 5 : vertex - 1;
				}
				cnt++;
			}
			
			if (cnt == sideRun)
			{
				// We have finished one side run.
				cnt = -1;
				
				// rotate that vertex and sideRunVertex to start a new side
				// as we run around the vector hexagon
				vertex = sideRunVertex;
				sideRunVertex = ((vertex + 1) > 5) ? 0 : vertex + 1;
			}
		}
		
		return verticesAmy;
	}

	protected int[] vectorToRose(int roseRing)
	{
		// rose ring == 0 has no vectors
		if (roseRing == 0) return null;

		// The first and last rose and allocate the return
		int[] range = roseRingRange(roseRing);
		int roseCnt = ((range[1] - range[0]) / 10) + 1;
		
		// There are three vectors per rose in the rose ring, except the
		// 6 rose ring vertices, which only contain 2 vectors each.
		int vectorCnt = (roseCnt * 3) - 6;
		int[] vectorToRoseAry = new int[vectorCnt];
		
		// The vectors point either radially to a rose within the current rose ring, or
		// to a rose within the next inner rose ring.
		int[] innerRange = roseRingRange(roseRing-1);
		if (innerRange == null) return null;
		
		int[] innerRoseIndexAry = rosesPerRing(innerRange);
		
		// index into innerRoseIndexAry
		// NoteOnAndroidStudio that there are obviously more roses on the outer rose ring than the inner.
		// But, as a rose ring vertex is rounded, the same inner rose is the target of 3 vectors.
		int index = 0; 	 
		
		// The vector sets progressing about the rose ring form their own hexagon.
		// The vertex rose has 2 vectors, the side runs have 3.  The number
		// number of roses in a side run is 1 less than the rose ring value.
		int sideRun = roseRing - 1;
		
		// counts up to sideRun then resets
		int cnt = -1;			
		
		// The first toRadialRoseIndex is always the next rose in the rose ring
		int toRadialRoseIndex = range[0] + 10;
		
		// Run around the rose ring.  Roses increment by 10.
		vectorCnt = 0;
		for (int roseIndex = range[0]; roseIndex < (range[1] + 10); roseIndex += 10)
		{
			if (cnt == -1)
			{
				// The first is the rose ring vectorToRose, and contains 2 rose indices
				vectorToRoseAry[vectorCnt++] = innerRoseIndexAry[index];
				
				if (roseRing == 1)
				{
					// Special case that applies only to rose ring 1.
					// In rose ring 1, the else case is never touched as there aren't any side runs.
					// Hence, must make this check here
					if ((roseIndex + 10) > range[1])
					{
						// Then we have finished the run around the rose ring
						toRadialRoseIndex = range[0];
					}
				}
				vectorToRoseAry[vectorCnt++] = toRadialRoseIndex;
				cnt++;
			}
			else	// is a side run
			{
				// Side run vectors point, in order:  
				// 1) the previous inner rose,
				// 2) the next inner rose from (1), and 
				// 3) the next radial rose
				//Integer[] vectorToRose = new Integer[3];
				vectorToRoseAry[vectorCnt++] = innerRoseIndexAry[index++];
				
				if ((roseIndex + 10) > range[1])
				{
					// Then we have reached the end of the rose ring and need to start over
					// (And... when we reach this point we are done with the loop as well.)
					vectorToRoseAry[vectorCnt++] = innerRoseIndexAry[0];
					vectorToRoseAry[vectorCnt++] = range[0]; 
				}
				else
				{
					vectorToRoseAry[vectorCnt++] = innerRoseIndexAry[index];
					toRadialRoseIndex += 10;
					vectorToRoseAry[vectorCnt++] = toRadialRoseIndex;
				}
				cnt++;
			}

			if (cnt == sideRun)
			{
				// We have finished one side run.
				cnt = -1;
				
				// Step forward to the next current rose index
				toRadialRoseIndex += 10;
			}
		}
		return vectorToRoseAry;
	}
	
	protected int[][] petalsPerVector(int[] verticesAry, int roseRing)
	{
		// rose ring 0 has no vectors to process
		if (roseRing == 0) return null;
		
		// The first and last rose and allocate the return
		int[] range = roseRingRange(roseRing);
		int roseCnt = ((range[1] - range[0]) / 10) + 1;
		
		// There are three vectors per rose in the rose ring,
      // except the 6 rose ring vertices, which only contain 2 vectors each.
		int vectorCnt = (roseCnt * 3) - 6;
		int[][] vectorPetalAry = new int[vectorCnt][];
		
		// The vector sets progressing about the rose ring form their own hexagon.
		// The vertex rose has 2 vectors, the side runs have 3.  The number
		// number of roses in a side run is 1 less than the rose ring value.
		int sideRun = roseRing - 1;
		
		// counts up to sideRun then resets
		int cnt = -1;

		// The rose ring source petals on a vector always starts (roseIndex + 3) and decrements as the
		// vertices rotate counter clockwise.  And the sideRuns starts (roseIndex + 4).  There are
		// always 2 petals: the first with 2 direct bond sides and the second with 1 indirect bond side.
		int sourcePetalIndex;
		int sourcePetalUnit = 4;
		
		// Run around the rose ring.  Roses increment by 10.
		vectorCnt = 0;
		for (int roseIndex = range[0]; roseIndex < (range[1] + 10); roseIndex += 10)
		{
			if (cnt == -1)
			{
				//---------------------------------------------------------------
				for (int inx = 0; inx < 2; inx++)
				{
					int[] vectorPetals = new int[2];

					// The direct bonds sides
					sourcePetalUnit = (verticesAry[vectorCnt] == 0) ? 6 : verticesAry[vectorCnt];
					sourcePetalIndex = roseIndex + sourcePetalUnit;

					// Package the retrieved data
					vectorPetals[0] = sourcePetalIndex;

					// The indirect bond is in another petal
					sourcePetalUnit = ((sourcePetalUnit - 1) == 0) ?  6 : sourcePetalUnit - 1;
					sourcePetalIndex = roseIndex + sourcePetalUnit;
					vectorPetals[1] = sourcePetalIndex;

					vectorPetalAry[vectorCnt] = vectorPetals;
					vectorCnt++;
				}
				cnt++;
			}
			else // is a side run
			{
				//---------------------------------------------------------------
				for (int inx = 0; inx < 3; inx++)
				{
					int[] vectorPetals = new int[2];

					// The direct bonds sides
					sourcePetalUnit = (verticesAry[vectorCnt] == 0) ? 6 : verticesAry[vectorCnt];
					sourcePetalIndex = roseIndex + sourcePetalUnit;

					// Package the retrieved data
					vectorPetals[0] = sourcePetalIndex;

					// The indirect bond is in another petal
					sourcePetalUnit = ((sourcePetalUnit - 1) == 0) ?  6 : sourcePetalUnit - 1;
					sourcePetalIndex = roseIndex + sourcePetalUnit;
					vectorPetals[1] = sourcePetalIndex;

					vectorPetalAry[vectorCnt] = vectorPetals;
					vectorCnt++;
				}
				cnt++;
			}
			
			if (cnt == sideRun)
			{
				// We have finished one side run.
				cnt = -1;
			}
		}
		
		return vectorPetalAry;
	}

	// Origin progressions
	
	
	// Returns an array of each vertex of an inner rose used as 
	// an offset origin to compute the origin of the outer rose.
	// The two:  originVertices() and originRosePairs() are dependent and their returns
	// are used together -- marching down their returned arrays of equal length.
	protected int[] originVertices(int roseRing)
	{
		// Always start point at vertex 1, then progresses, in order, around
		// in a sequence of the ring number per vertices.
		// ring 1 has 1; ring 2, 2... ring 5, 5
		// Vertices pointed to
		// 1: 1, 2, 3, 4, 5, 0
		// 2: 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 0, 0
		// 3: 1, 1, 1, 2, 2, 2,... 0, 0, 0
		
		int len = roseRing * HGBShared.SIDES;
		int[] vertices = new int[len];
		
		// Always start with 1
		int vertex = 1;
		int inx = 0;
		do
		{
			for (int index = 0; index < roseRing; index++)
			{
				vertices[inx++] = vertex;
			}
			vertex++;
			//vertex = ((vertex + 1) == Statics.SIDES) ? 0 : vertex + 1;
			
		} while (inx < len-roseRing);
		
		// This little optimization: the array always ended in zeros.
		// so no need for the conditional test in the loop.  Just add the zeros.
		vertex = 0;
		for (int index = 0; index < roseRing; index++)
		{
			vertices[inx++] = vertex;
		}
		
		return vertices;
	}

	// Each outer rose comes from a vertex on a single inner rose.
	// They are matched pairs.  (originVertices() returns the input vertices
	// for this call.)
	// The two:  originVertices() and originRosePairs() are dependent and their returns
	// are used together -- marching down their returned arrays of equal length.
 int[][] originRosePairs(int roseRing, int[] vertices)
	{
		// The length of array int[] vertices returned from originVertices() is used 
		// to define the length of the return rosePairs to insure that the two arrays
		// (vertices and rosePairs) are the same length.
		int[][] rosePairs = new int[vertices.length][]; 

		// Pairs are found inward
		
		// The current rings roses:
		// The first and last rose of the current ring
		int[] currentRange = roseRingRange(roseRing);
		//int roseCnt = ((range[1] - range[0]) / 10) + 1;
		int[] currentRingRoseIndicesAry = rosesPerRing(currentRange);
		if (currentRingRoseIndicesAry == null) return null;
		
		// The inner rings roses:
		int[] innerRange = roseRingRange(roseRing-1);
		int[] innerRingRoseIndicesAry = rosesPerRing(innerRange);
		
		// Roses are ordered as found by roseIndices in both rings
		// with a repeat of the previously used rose at each corner 
		// (which keeps the count of the two rings synchronized).
		// Sequence:
		// roseRing 1: all to 0
		// roseRing 2: 70-10, 80-20, then the corner 90-20, 100-30, then the corner 110-30
		// roseRing 3: 190-70, 200-80, 210-90 the the corner 220-90
		
		// Start out on the straight away
		int vertex = vertices[0];
		int prevVertex = vertices[0];
		
		int currentIndex = 0;
		int innerIndex = 0;
		for (int inx = 0; inx < vertices.length; inx++)
		{
			prevVertex = vertex;
			vertex = vertices[inx];
			
			if (prevVertex == vertex)
			{
				// The we are on the straight away
				rosePairs[inx] = new int[2];
				rosePairs[inx][0] = currentRingRoseIndicesAry[currentIndex];
				
				// The circle must be closed.  When we reach the end of the current rose ring
				// we will be on the straight away; but in reality it is a corner.  
				// Check for the end of the run
				if (innerIndex == innerRingRoseIndicesAry.length) innerIndex = 0;
				
				rosePairs[inx][1] = innerRingRoseIndicesAry[innerIndex];
			}
			else
			{
				// The vertices have changed, we are rounding the corner.
				// We skip a beat here and repeat the last used inner rose.
				innerIndex--;
				
				rosePairs[inx] = new int[2];
				rosePairs[inx][0] = currentRingRoseIndicesAry[currentIndex];
				rosePairs[inx][1] = innerRingRoseIndicesAry[innerIndex];
			}
			//currentIndex = ((currentIndex + 1) > currentRingRoseIndicesAry.length) ? 0 : currentIndex + 1;
			currentIndex++;
			innerIndex++;
		}
		
		return rosePairs;
	}
}


