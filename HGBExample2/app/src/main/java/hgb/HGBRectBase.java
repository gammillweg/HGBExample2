package hgb;

// The purpose of this class is to ease the layering of a rectangle
// based image or my resizable vector flat figure inside of or on
// top of a given hexagon.

// This class defines the base inscribed and superscribed rectangles 
// within or about the base hexagon. This is a support class to
// HGBHexBase.  Once the base hexagon is computed the two base
// rectangles are computed.

// An inscribed rectangle is defined here with the top left corner
// on the midpoint of side 3 and the bottom right on the midpoint of
// side 0.  Centered  or the origin of the base hexagon.
// A superscribed rectangle is defined here as a rectangle with
// a height and width of two times the vertexRadius (or cellSize)
// (a square).  Centered  or the origin of the base hexagon.

// At the moment... only normalRadiansLandscape or originRadiansLandscape
// are considered.  Portrait is ignored.

//The results are stored in HGBShared.  See HGBShared.[set,get]BaseInscribedRect()
//and HGBShared.[set,get]BaseSuperscribedRect().

public class HGBRectBase
{
	public HGBRectBase(HGBShared hgbShared)
	{
		this.hgbShared = hgbShared;
		
		//baseVertices = new float[HGBShared.SIDES][];
		//basePetalOrigins = new float[HGBShared.SIDES][];
	}

	//private static final String TAG = "TAG_RectBase";
	private HGBShared hgbShared = null;

	protected void defineRectangles(double normalRadius, float[][] baseVertices)
	{
		defineInscribedRect(normalRadius);
		defineSuperscribedRect(baseVertices);
	}

	private void defineInscribedRect(double normalRadius)
	{
		// I know the normalRadius... the distance from the origin of
		// the hexagon to the center of any side.  And, the vertexRadius...
		// the distance from the origin to any vertex.
		// All I need is the width and height of the rectangle.  
		
		float[][] corners;
		corners = new float[4][];
		double[] normalRadiansAry = hgbShared.getNormalRadiansAry();
//		for (int inx = 0, cnt = 0; inx < HGBShared.SIDES; inx++)
//		{
//			if (inx == 1) continue;  //point not part of the rectangle
//			if (inx == 4) continue;  //point not part of the rectangle
//			
//			corners[cnt]= new float[2];
//			corners[cnt][0] = (float)(normalRadius * Math.cos(normalRadiansAry[inx]));
//			corners[cnt][1] = (float)(normalRadius * Math.sin(normalRadiansAry[inx]));
//			cnt++;
//		}
		// The results from above show I only need ONE and then work with signs for the others
		//[[45.0, 25.980762], [-45.0, 25.980762], [-45.0, -25.980762], [45.0, -25.980762]]
		
		// More lines of code and not so elegant looking; but, I think, faster
		// I only use [0][] and [2][] below... so fill only the two used.
		corners[0]= new float[2];
		corners[0][0] = (float)(normalRadius * Math.cos(normalRadiansAry[0]));
		corners[0][1] = (float)(normalRadius * Math.sin(normalRadiansAry[0]));
		//corners[1]= new float[2];
		//corners[1][0] = -corners[0][0];
		//corners[1][1] = corners[0][1];
		corners[2]= new float[2];
		corners[2][0] = -corners[0][0];
		corners[2][1] = -corners[0][1];
		//corners[3]= new float[2];
		//corners[3][0] = corners[0][0];
		//corners[3][1] = -corners[0][1];
		
		// Then, of course, I COULD eliminate corners and plug directly
		// into the below; but I decline.
		
		float left = corners[2][0];
		float top = corners[2][1];
		float right = corners[0][0];
		float bottom = corners[0][1];
		
		hgbShared.setBaseInscribedRectF(left, top, right, bottom);

	}

	// Compute the top left and bottom right coordinates of the
	// base superscribed rectangle.  The return is via hgbShared
	private void defineSuperscribedRect(float[][] baseVertices)
	{
		// we ignore the portrait and compute on the landscape
		// Upper Left x or vertex 3 and y of vertex 4
		// Lower Right x of vertex 0 and y of vertex 1

		float left = baseVertices[3][0];
		float top = baseVertices[4][1];
		float right = baseVertices[0][0];
		float bottom = baseVertices[1][1];
		
		// Pass the coordinates to HGBShared.
		// hgbShared creates an integer Rectangle at (0,0) by
		// calculating the width and height and rounding to an integer
		hgbShared.setBaseSuperscribedRectF(left, top, right, bottom);
	}


}
