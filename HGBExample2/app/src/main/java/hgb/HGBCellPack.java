package hgb;

import java.util.Arrays;

//This class is created and stored in HGBShared.cellAry with index as the key.
//This class stores data about hexagon cells.
public class HGBCellPack
{
	protected HGBCellPack()
	{
		bondAry = new int[HGBShared.SIDES];
		hgbShared = null;
	}

	protected HGBCellPack(int index, HGBShared hgbShared)
	{
		this.index = index;
		this.hgbShared = hgbShared;

		origin = new float[2];		// the origin of the cell is not stored
		offset = new float[2];		// rather, the origin is calculated via offset

		bondAry = new int[HGBShared.SIDES];

		// This -1 fill IS NEEDED and DEPENDED upon elsewhere.
		Arrays.fill(bondAry, 0, HGBShared.SIDES, -1);
	}

	private HGBShared hgbShared = null;

	// ------------------------------------------------------------------------------
	// The cells index.  An index into HGBShared.cellAry
	private int index = -1;
	protected int getIndex()
	{
		return index;
	}

	// protected void setIndex(int index) { this.index = index; }
	// ------------------------------------------------------------------------------
	// An array of hexagon indices of the adjacent hexagon on
	// the current hexagons side by the index into the array.
	protected int[] bondAry = null;
	protected int[] getBondAry()
	{
		return bondAry;
	}
	
	// Public access protects the pointer into bondAry by
	// coping into a new array.
	public int[] getBondings()
	{
		//int[] rtnAry = new int[bondAry.length];
		int[] rtnAry = Arrays.copyOf(bondAry, bondAry.length);
		return rtnAry;
	}

   /**
    * Returns the cell index of the cell the side is bound with
    * NoteOnAndroidStudio that if bound to an edge, -1 will be returned.
    * @param side
    * @return returns a bound cell index or -1 on error
    */
   public int getBondToSide(int side)
   {
      if ((side >= 0) && (side < bondAry.length)) return bondAry[side];
      return -1;
   }

	// ------------------------------------------------------------------------------
	// This class does not store the origin of each cell. Rather it holds
	// an offset from the hive origin to its initial origin, and uses that
	// to compute the the current origin as related to the hive origin, 
	// with each call to getOrigin().  The offset from the initial origin
	// is computed from the hive origin in HGBGenerateHive.calculateRoseOrigins()
	// and originRosePetals(). Throughout any game, the offset from each cell
	// to the hive origin remains constant.  Thus, as the hive is translated, 
	// only the hive origin is updated.  The offset is corrected when the hive
	// is regenerated due a new game, the number of cells in the hive changed, 
	// or to size of cells being changed.  Each of which will call 
	// HGBGenerateHive.generateHive_Main()
	
	private float[] origin;
	private float[] hiveOrigin;
	private float[] offset;
	
	public float[] getOrigin()
	{
		hiveOrigin = hgbShared.getHiveOrigin();
		origin[0] = hiveOrigin[0] - offset[0];
		origin[1] = hiveOrigin[1] - offset[1];
		return origin;
	}

	protected void setOffsetFromHiveOrigin(float[] offset)
	{
		this.offset[0] = offset[0];
		this.offset[1] = offset[1];
	}

	// ------------------------------------------------------------------------------
   // CodeProject note:  I think the following is debug stuff
   // at any rate the code is not used.  So commented out.
   
   
	// The vertices used as the origin of vectors used to locate adjacent roses.
	// Only roses contain an allocated vectorOriginVertex array. (see
	// Progressions.vectorOriginVertex())
	// protected int[] vectorOriginVertex = null;

	// The rose the vector vertex points to
	// Only roses contain an allocated vectorVertices array. (see
	// Progressions.vectorVertices())
	// protected int[] vectorToRose = null;

	// protected String ToString()
	// {
	// 	String str = "Index [" + index + "]\n";

	// 	// ------------------------------------------------
	// 	if (bondAry != null)
	// 	{
	// 		str += "bondAry [";
	// 		str += bondAry[0] + ", ";
	// 		str += bondAry[1] + ", ";
	// 		str += bondAry[2] + ", ";
	// 		str += bondAry[3] + ", ";
	// 		str += bondAry[4] + ", ";
	// 		str += bondAry[5] + "]\n";
	// 	}
	// 	else
	// 	{
	// 		str += "boundAry [null]\n";
	// 	}

	// 	// ------------------------------------------------
	// 	if (origin != null)
	// 	{
	// 		str += "orgin [";
	// 		str += this.getOrigin()[0] + ", ";
	// 		str += this.getOrigin()[1] + "]\n";
	// 	}
	// 	else
	// 	{
	// 		str += "origin [null]\n";
	// 	}

	// 	// ------------------------------------------------
	// 	if (offset != null)
	// 	{
	// 		str += "offSet [";
	// 		str += this.offset[0] + ", ";
	// 		str += this.offset[1] + "]\n";
	// 	}
	// 	else
	// 	{
	// 		str += "offset [null]\n";
	// 	}

	// 	// ------------------------------------------------
	// 	// -- the follow TWO conditions are identical
	// 	// except for the variable -- could be a function

	// 	// if (vectorOriginVertex != null)
	// 	// {
	// 	// str += writeArray("vectorOriginVertex", vectorOriginVertex);
	// 	// }

	// 	// ------------------------------------------------
	// 	// if (vectorToRose != null)
	// 	// {
	// 	// str += writeArray("vectorToRose", vectorToRose);
	// 	// }

	// 	// ------------------------------------------------

	// 	return str;
	// }

	// // private String writeArray(String str, int[] ary)
	// // {
	// // str += " [";
	// // for (int inx = 0; inx < ary.length; inx++)
	// // {
	// // if (ary.length == 2)
	// // {
	// // switch (inx)
	// // {
	// // case 0:
	// // str += ary[inx] + ", ";
	// // break;
	// //
	// // case 1:
	// // str += ary[inx];
	// // break;
	// // }
	// // }
	// // else if (ary.length == 3)
	// // {
	// // switch (inx)
	// // {
	// // case 0:
	// // case 1:
	// // str += ary[inx] + ", ";
	// // break;
	// //
	// // case 2:
	// // str += ary[inx];
	// // break;
	// // }
	// // }
	// // else
	// // {
	// // str += ary[inx] + ", ";
	// // }
	// // }
	// // str += "]\n";
	// // return str;
	// // }

}
