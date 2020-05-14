package hgb;


//import android.util.Log;

// This class defines the origins or the vertices which form a base
// hexagon located at origin (0,0), and the origin of all the petals
// around that hexagon.  All hexagons are drawn
// using these base vertex coordinates.  Simply translating them to new
// locations.  And the origin of all petals of all roses are a simple
// translation of the base petal origins found within this class.
// The results are stored in HGBShared.  See HGBShared.[set,get]BaseVertices()
// and HGBShared.[set,get]BasePetalOrigins().

public class HGBHexBase
{
	public HGBHexBase(HGBShared hgbShared)
	{
		this.hgbShared = hgbShared;
		
		baseVertices = new float[HGBShared.SIDES][];
		basePetalOrigins = new float[HGBShared.SIDES][];
		
		hgbRectBase = new HGBRectBase(hgbShared);
	}
	
	// To calculate the base rectangles
	private HGBRectBase hgbRectBase = null;

	//private static final String TAG = "TAG_HexBase";
	private HGBShared hgbShared = null;
	
	// Base vertices are the vertices of a hexagon with an origin of (0,0)
	private float[][] baseVertices;
	
	// Base origins are the origins center of a hexagon who's origin is (0,0)
	// and the origins of all  6 hexagons about the that center origin.
	private float[][] basePetalOrigins;
	
	protected void setVertexRadius(double vertexRadius) 
	{ 
		// Decimals below 1 will work; but TouchLocate() will fail to
		// correctly identify hexagons as the rounded int magnitudes can
		// no longer identify unique true hexagon rings below 2f.
		// Hence, treat 2f as your minimum, unless touchLocate() is not used.
		if (vertexRadius < 2f) vertexRadius = 2f;
		
		defineCell(vertexRadius);
	}

	// Creates a hexagon per class members ANGLES and vertexRadius
	// and returns via class member vertices.
	// These are used to draw all hexagons
	private void defineCell(double vertexRadius)
	{
		double[] vertexRadiansAry = hgbShared.getVertexRadiansAry();
		for (int inx = 0; inx < HGBShared.SIDES; inx++)
		{
			//float[] coordinate = new float[2]; 
			// x = r cos theta, y = r sin theta
			baseVertices[inx] = new float[2];
			baseVertices[inx][0] = (float)(vertexRadius * Math.cos(vertexRadiansAry[inx]));
			baseVertices[inx][1] = (float)(vertexRadius * Math.sin(vertexRadiansAry[inx]));
		}

		// Calculate the length of the normal (perpendicular) to a side.
		// Find the height (altitude) of an equilateral triangle of 60 degrees.
		// sin theta = opposite / hypotenuse 
		// hypotenuse = sin theta * opposite
		// height = sin 60 * length of side
		// normalRadius = vertexRadius * sin 60
		// 60 degrees = 1.047197551196598 radians
		double normalRadius = (float)(vertexRadius * Math.sin(HGBStatics.sixtyDegreesInRadians));
		
		defineBasePetalOrigins(vertexRadius, normalRadius);
		
		// base vertices are stored in class HGBShared.
		// (get them via HGBShared.getBaseVertices())
		hgbShared.setBaseVertices(baseVertices, vertexRadius, normalRadius);
		
		// To define the base inscribed and superscribed base rectangles.
		hgbRectBase.defineRectangles(normalRadius, baseVertices);
	}

	// The center is (0,0) and finds all origins of adjacent hexagons
	// The return is via HGBShared.setBaseRoseOrigins()
	// These are used to find the origins of all petals around all roses. 
	private void defineBasePetalOrigins(double vertexRadius, double normalRadius)
	{
		basePetalOrigins[0] = new float[2];
		
		// Account for the distance between the center of the rose and its petals.
		// A distance of two radii.
		normalRadius *= 2;
		
		double[] normalRadiansAry = hgbShared.getNormalRadiansAry();
		for (int inx = 0; inx < HGBShared.SIDES; inx++)
		{
			basePetalOrigins[inx]= new float[2];
			basePetalOrigins[inx][0] = (float)(normalRadius * Math.cos(normalRadiansAry[inx]));
			basePetalOrigins[inx][1] = (float)(normalRadius * Math.sin(normalRadiansAry[inx]));
		}

		// base origins are stored in class HGBShared.
		// (get them via HGBShared.getBaseOrigins())
		hgbShared.setBasePetalOrigins(basePetalOrigins);
	}
}

