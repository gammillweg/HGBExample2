package hgb;

public final class HGBStatics
{
	// MOVED TO HGBShared to help keep HGBStatics hidden from the public
	// 6 sides to a hexagon
	// //public static final int SIDES = 6;

	// The hexagon side opposite the side index into the array.
	// (The side opposite of 0 is 3; 1 is 4 etc)
	public static final int[] oppositeSides = { 3, 4, 5, 0, 1, 2 };

	// The number of radius's between a vertex of a rose and the
	// origin of another rose
	protected static final int radiiBetweenRoses = 4;

	// Math.cos and Math.sin require angles expressed in radians

	// ----------------------------------------------------------------------------
	// Angles here define a hexagon with points on the horizontal and flats on
	// the vertical
	// Angles defines the polar coordinate (along with vertexRadius) of each
	// vertex
	// from an origin of (0,0)

	protected static final double sixtyDegreesInRadians = 1.047197551196598;

	// MOVED TO HGBShared to help keep HGBStatics hidden from the public
	// //public static enum OrientationRadians { Vertical, Horizontal };

	// No rotation of the hive
	// This so the true hexagon rings found in the hive will align with
	// a flat side on the top and bottom and points on the ends when the
	// device is held vertically.
	protected static final double[] vertexRadiansLandscape =
	{
			0, // 0
			1.047197551196598, // 60
			2.094395102393195, // 120
			3.141592653589793, // 180
			4.188790204786391, // 240
			5.235987755982988 // 300
	};

	// Field normalRADIANS is the angle, in radians, for polar coordinates to
	// find adjacent hexagons.
	// It defines the normal (perpendicular) from a side to the origin.
	protected static final double[] normalRadiansLandscape =
	{
			0.5235987755982988, // 30
			1.570796326794897, // 90
			2.617993877991494, // 150
			3.665191429188092, // 210
			4.71238898038469, // 270
			5.759586531581287 // 330
	};

	// The angle in radians for polar coordinates to find the origin of
	// rosePairs.
	// Used to find the origin of the current outer rose from an associated
	// inner rose by a vector form its origin to a vertex on the inner rose.
	protected static final double[] originRadiansLandscape =
	{
			5.235987755982988, // 300
			0, // 0
			1.047197551196598, // 60
			2.094395102393195, // 120
			3.141592653589793, // 180
			4.188790204786391, // 240
	};

	// ----------------------------------------------------------------------------
	// Rotate the entire hive 90 degrees
	// This so the true hexagon rings found in the hive will align with
	// a flat side on the top and bottom and points on the ends when the
	// device is rotated horizontally.

	protected static final double[] vertexRadiansPortrait =
	{
			1.570796326794897, // 90
			2.617993877991494, // 150
			3.665191429188092, // 210
			4.71238898038469, // 270
			5.759586531581287, // 330
			0.5235987755982988 // 30
	};

	protected static final double[] normalRadiansPortrait =
	{
			2.094395102393195, // 120
			3.141592653589793, // 180
			4.188790204786391, // 240
			5.235987755982988, // 300
			0.0, // 0
			1.047197551196598 // 60
	};

	// The angle in radians for polar coordinates to find the origin of
	// rosePairs.
	// Used to find the origin of the current outer rose from an associated
	// inner rose by a vector form its origin to a vertex on the inner rose.
	protected static final double[] originRadiansPortrait =
	{
			0.5235987755982988, // 30
			1.570796326794897, // 90
			2.617993877991494, // 150
			3.665191429188092, // 210
			4.71238898038469, // 270
			5.759586531581287, // 330
	};

	// ---------------------------------------------------
	// The following are matched pairs. Read explanation
	// below.

	// The vertex vector (origin by vertex [0]) passes thought
	// an opposite vertex of a petal. On each side of the vertex
	// is a bond side, 2 dimensional, stored in [1]
	// static double[][] d = { { 4.6, 8.5, 3.2, 5.1 }, { 6.4, 11.1, 4.1, 6.2 }
	// };
	protected static final int[][] vectorSideBonds =
	{
			{ 5, 4 }, // vertex 0
			{ 0, 5 }, // vertex 1
			{ 1, 0 }, // vertex 2
			{ 2, 1 }, // vertex 3
			{ 3, 2 }, // vertex 4
			{ 4, 3 } // vertex 5
	};

	// Is a matched pair with vectorSideBond (I thought about
	// writing as 4 members rather than 2 with 2). The vector
	// from vertex 0 is bracketed on either side by hexagon
	// sides 5 and 4; and 5 is adjacent to a hexagon with a
	// unit value of 2 and 4 with 3. The unit value may be
	// combined with a rose such that from rose 130, the vector
	// goes through 136 with sides 5 and 4 -- then onto rose
	// 140. Rose 140's petals 142 is adjacent to petal 136
	// and 143 is also adjacent to 136. Opposite sides may
	// be used to establish the bonds (136,5 --> 142,2)
	// In addition, 143 is adjacent to 135 which contains
	// bond side 5, thus 135,5 bonds with 143,2.
	protected static final int[][] targetUnits =
	{
			{ 2, 3 }, // from rose vertex 0
			{ 3, 4 }, // from rose vertex 1
			{ 4, 5 }, // from rose vertex 2
			{ 5, 6 }, // from rose vertex 3
			{ 6, 1 }, // from rose vertex 4
			{ 1, 2 }  // from rose vertex 5
	};

	// ---------------------------------------------------

}
