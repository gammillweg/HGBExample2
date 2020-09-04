package hgb;

// Bond (link) adjacent cells  via cellPack.bondAry
public class HGBBonding
{
	protected HGBBonding(HGBProgressions progressions, HGBShared hgbShared)
	{
		this.progressions = progressions;
		this.hgbShared = hgbShared;
	}
	private HGBShared hgbShared;
	private HGBProgressions progressions;
	
	// Bond the cells together per established vectors stored in HGBShared.cellAry
	// The return is modified HGBShared.cellAry
	protected void bondVectorPetals_main(int[] verticesAry, int[] vectorToRoseAry, int[][] vectorPetalAry, int roseRing)
	{
		try
		{
			HGBCellPack roseBondPack = null;
			if (roseRing == 0)
			{
				// no vectors as part of roseRing 0
				roseBondPack = hgbShared.cellAry[0];
				bondPetalsAboutRose(roseBondPack);
				bondPetalsToRose(roseBondPack);
				return;
			}

			int vertices = 0;
			int vectorToRose = 0;
			int roseIndex = 0;

			//------------------------------------------------------
			// The first and last rose and allocate the return
			int[] range = progressions.roseRingRange(roseRing);
			int roseCnt = ((range[1] - range[0]) / 10) + 1;

			// There are three vectors per rose in the rose ring,
         // except the 6 rose ring vertices, which only contain 2 vectors each.
			int vectorCnt = (roseCnt * 3) - 6;
			//------------------------------------------------------

			int[] roseIndices = progressions.rosesPerRing(range);
			if (roseIndices == null) return;
			
			for (int index = 0; index < roseIndices.length; index++)
			{
				//-----------------------------------
				roseIndex = roseIndices[index];
				roseBondPack = hgbShared.cellAry[roseIndex];

				bondPetalsAboutRose(roseBondPack);
				bondPetalsToRose(roseBondPack);
				//-----------------------------------
			}	
			//------------------------------------------------------

			for (int inx = 0; inx < vectorCnt; inx++)
			{
				vertices = verticesAry[inx];
				vectorToRose = vectorToRoseAry[inx];
				int[] vectorPetals = vectorPetalAry[inx];

				// pass the vertex origin of the vector, rose pointed to and the bond sides
				bondPeltal(vertices, vectorToRose, vectorPetals);
			}
		}
		catch (Exception exc)
		{
			return;
		}
	}
	
	// Bond two adjacent petal, 2 sides together for each.
	// The output is modified HGBShared.cellAry
	private void bondPeltal(int vertex, int toRose, int[] vectorPetals)
	{
		try
		{
			int petalIndex = vectorPetals[0];
			int[] vectorSideBonds = HGBStatics.vectorSideBonds[vertex];
			int[] targetUnits = HGBStatics.targetUnits[vertex];

			HGBCellPack outerPetalBondPack = hgbShared.cellAry[petalIndex];
			HGBCellPack innerPetalBondPack = null;

			// Bond the vector direct petals
			for (int inx = 0; inx < 2; inx++)
			{
				int innerPetalIndex = toRose + targetUnits[inx];
				innerPetalBondPack = hgbShared.cellAry[innerPetalIndex];
				int oppositeSide = HGBStatics.oppositeSides[vectorSideBonds[inx]];

				// Bond these two sides
				outerPetalBondPack.bondAry[vectorSideBonds[inx]] = innerPetalIndex;
				innerPetalBondPack.bondAry[oppositeSide] = petalIndex;
			}

			// Bond the vector indirect petals
			petalIndex = vectorPetals[1];
			outerPetalBondPack = hgbShared.cellAry[petalIndex];

			int innerPetalIndex = toRose + targetUnits[1];
			innerPetalBondPack = hgbShared.cellAry[innerPetalIndex];

			int oppositeSide = HGBStatics.oppositeSides[vectorSideBonds[0]];

			// Bond these two sides
			outerPetalBondPack.bondAry[vectorSideBonds[0]] = innerPetalIndex;
			innerPetalBondPack.bondAry[oppositeSide] = petalIndex;
		}
		catch (Exception exc)
		{
			return;
		}
	}

	// Each rose is bonded to the petals about it (clockwise)
	private void bondPetalsToRose(HGBCellPack cellPack)
	{
		try
		{
			int roseIndex = cellPack.getIndex();
			int petalIndex = roseIndex;
			for (int inx = 0; inx < HGBShared.SIDES; inx++)
			{
				cellPack.bondAry[inx] = ++petalIndex;

				HGBCellPack petalPack = hgbShared.cellAry[petalIndex];
				int index = HGBStatics.oppositeSides[inx];	
				petalPack.bondAry[index] = roseIndex;
			}
		}
		catch (Exception exc)
		{
			return;
		}
	}

	// Each petal about the rose is bonded with petals on either side.
	private void bondPetalsAboutRose(HGBCellPack cellPack)
	{
		int roseIndex = cellPack.getIndex();
		int petalIndex = roseIndex;
		int firstPetalIndex = roseIndex + 1;
		int lastPetalIndex = roseIndex + 6;
		int side = 2;

		try
		{
			for (int inx = 0; inx < HGBShared.SIDES; inx++)
			{
				petalIndex++;
				HGBCellPack petalPack = hgbShared.cellAry[petalIndex];

				int nextPetalIndex = petalIndex + 1;
				if (nextPetalIndex > lastPetalIndex) nextPetalIndex = firstPetalIndex;
				HGBCellPack nextPetalPack = hgbShared.cellAry[nextPetalIndex];

				petalPack.bondAry[side] = nextPetalIndex;
				int index = HGBStatics.oppositeSides[side];
				nextPetalPack.bondAry[index] = petalIndex;

				side++;
				side = (side == 6) ? 0 : side;
				//System.out.println("");
			}
		}
		catch (Exception exc)
		{
			return;
		}
	}
	
	//------------------------------------------------------------
}
