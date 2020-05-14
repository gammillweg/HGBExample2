package hgb;

import android.graphics.Point;
import android.graphics.PointF;

public class HGBVector2D
{
	/**
	 * HGBVector2D Constructors (and discussion)
	 * 
	 * A vector is very much like a polar coordinate; but it isn't.
	 *
	 *    A polar coordinate is defined by a length and an angle and is
	 *    located in a 2D plane as a coordinate from some known point.
	 * 
	 *    A vector has NO location in a 2D plane. It is NOT a coordinate.
	 * 
	 * It is defined simply by an x and y value. The x determines the horizontal
	 * and the y the vertical. The vector passes through each. Once defined the
	 * vector can be moved about as desired. Even thought moved to a new physical
	 * location it remains the SAME vector. Two vectors in different locations;
	 * but defined with proportional x and y values are compared as identical.
	 * 
	 * @param x
	 *            (int, float or double)
	 * @param y
	 *            (int, float or double)
	 * 
	 * @see vPointVector() and vCartesianVector() these treat the vector more
	 *      like polar coordinates. The vector in these is defined by two points
	 *      in space and DOES have a location in space.
	 * 
	 *      WELL... time passes... (If I forget to clean up comments)
	 *      RdMagnitude DID NOT work -- failure -- I need to clean up But, not
	 *      yet.
	 * 
	 *      ID are special to CWG. ID is the HGBCellPack ID at the head of a
	 *      vCartesianVector() from hive origin to hexagon origin.
	 * 
	 *      (Side comment on this: As I thought storing the ID here
	 *      inappropriate; I wrote code to store the vector in HGBCellPack. And
	 *      use the vecX and vecY stored there to locate the touched cell rather
	 *      than storing the ID here. However, I did not keep that code. This
	 *      solution was faster!)
	 */

	public HGBVector2D()
	{
		X = 0;
		Y = 0;
		Magnitude = 0;
		this.tolerance = .005;
		this.ID = -1;
	}

	public void zeroVector2D()
	{
		this.X = 0;
		this.Y = 0;
		this.Magnitude = 0;
		this.tolerance = .005;
		this.ID = -1;
	}

	public HGBVector2D(float x, float y)
	{
		X = x;
		Y = y;
		Magnitude = vVectorMagnitude(this);
		this.tolerance = .005;
		this.ID = -1;
	}

	public HGBVector2D(double x, double y)
	{
		X = (float) x;
		Y = (float) y;
		Magnitude = vVectorMagnitude(this);
		this.tolerance = .005;
		this.ID = -1;
	}

	public HGBVector2D(int x, int y)
	{
		X = (float) x;
		Y = (float) y;
		Magnitude = vVectorMagnitude(this);
		this.tolerance = .005;
		this.ID = -1;
	}

	// ===============================================
	// TODO -- need the double and int for completeness
	public void setVector2D(float x, float y)
	{
		this.X = x;
		this.Y = y;
		Magnitude = vVectorMagnitude(this);
		this.tolerance = .005;
		this.ID = -1;
	}

	public void set2PointVector2D(float[] p0, float[] p1)
	{
		this.X = p1[0] - p0[0];
		this.Y = p1[1] - p0[1];
		Magnitude = vVectorMagnitude(this);
		this.tolerance = .005;
		this.ID = -1;
	}

	// ===============================================
	private float X;
	private float Y;
	private int ID;
	private double Magnitude;
	private double tolerance;

	public float getX()
	{
		return this.X;
	}

	public float getY()
	{
		return this.Y;
	}

	public double getTolerance()
	{
		return this.tolerance;
	}

	public double getMagnitude()
	{
		return this.Magnitude;
	}

	public int getID()
	{
		return this.ID;
	}

	public void setID(int ID)
	{
		this.ID = ID;
	}

	/***********************************************************************
	 ** vSubtractVectors
	 * 
	 * The vSubtractVectors function subtracts the components of a two
	 * dimensional vector from another. The resultant vector c = (a1 - b1, a2 -
	 * b2). Subtracts the second from the first.
	 * 
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class v1 A Vector2D class
	 * 
	 * Return value
	 * 
	 * A Vector2D class containing the new vector obtained from the subtraction
	 * of the second (v1) from the first (v0)
	 * 
	 * Error Return null
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Return argument removed (was a remnant of
	 * classic C) : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public HGBVector2D vSubtractVectors(HGBVector2D v0, HGBVector2D v1)
	{
		try
		{
			HGBVector2D vec = new HGBVector2D();
			vec.X = v0.X - v1.X;
			vec.Y = v1.Y - v1.Y;
			return vec;
			// return (v0 - v1);
		} catch (Exception excp)
		{
			return null;
		}
	}

	/***********************************************************************
	 * vAddVectors
	 * 
	 * The vAddVectors function adds the components of a two dimensional vector
	 * from another. The resultant vector c = (a1 + b1, a2 + b2).
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the components of the first two
	 * dimensional vector. v1 A Vector2D class containing the components of the
	 * second two dimensional vector.
	 * 
	 * Return value Error return null
	 * 
	 * A Vector2D class containing the new vector obtained from the addition of
	 * the first two parameters.
	 * 
	 * Error Return null
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Return argument removed (was a remnant of
	 * classic C) : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public HGBVector2D vAddVectors(HGBVector2D v0, HGBVector2D v1)
	{
		try
		{
			HGBVector2D vec = new HGBVector2D();
			vec.X = v0.X + v1.X;
			vec.Y = v1.Y + v1.Y;
			return vec;
			// return (v0 + v1);
		} catch (Exception excp)
		{
			return null;
		}
	}

	/***********************************************************************
	 * vScaleVector
	 * 
	 * The vScaleVector function scales the components of a vector by a user
	 * supplied scaling factor. Vector2D(2,3) scaled by 3 2 * 3 = 6 3 * 3 = 9
	 * Vector2D(6,9)
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the components of the two dimensional
	 * vector to be scaled. dScaling The value by which to scale the components
	 * of v0
	 * 
	 * Return value
	 * 
	 * A Vector2D class containing the scaled vector. Error Return null
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Return argument removed (was a remanent
	 * of classic C) : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public HGBVector2D vScaleVector(HGBVector2D v0, float scaling)
	{
		try
		{
			float X = (v0.X * scaling);
			float Y = (v0.Y * scaling);
			return (new HGBVector2D(X, Y));
		} catch (Exception excp)
		{
			return null;
		}
	}

	/***********************************************************************
	 * vLinearCombination
	 * 
	 * The vLinearCombination function scales the components of two vectors and
	 * adds them together to form a new vector having the linear combination.
	 * The resultant vector is where u and v are scaling factors and a and b are
	 * vectors is c = ua + vb.
	 * 
	 * Parameters
	 * 
	 * ptScale A Vector2D class containing the scaling values v0 A Vector2D
	 * class containing the first of two vectors to be combined linearly. v1 A
	 * Vector2D class containing the second of two vectors to be combined
	 * linearly.
	 * 
	 * Return value
	 * 
	 * A Vector2D class containing a vector which is the result of the linear
	 * combination.
	 * 
	 * Error Return null
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Return argument removed (was a remanent
	 * of classic C) : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public HGBVector2D vLinearCombination(HGBVector2D vScale, HGBVector2D v0, HGBVector2D v1)
	{
		try
		{
			float X = (vScale.X * v0.X) + (vScale.Y * v1.X);
			float Y = (vScale.X * v0.Y) + (vScale.Y * v1.Y);
			return (new HGBVector2D(X, Y));
		} catch (Exception excp)
		{
			return null;
		}
	}

	/***********************************************************************
	 * vVectorSquared
	 * 
	 * The vVectorSquared function squares each of the components of the vector
	 * and adds then together to produce the squared value of the vector.
	 * SquaredValue = ((a.x * a.x) + (a.y * a.y)). Vector2D(2,4) ==> ((2*2 = 4)
	 * + (4*4 = 16)) ==> 20
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the vector upon which to determine the
	 * squared value.
	 * 
	 * Return value
	 * 
	 * A double value which is the squared value of the vector.
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public double vVectorSquared(HGBVector2D v0)
	{
		return (((double) (v0.X * v0.X)) + ((double) (v0.Y * v0.Y)));
	}

	/***********************************************************************
	 * vVectorMagnitude
	 * 
	 * The vVectorMagnitude function determines the length of a vector by
	 * summing the squares of each of the components of the vector. The
	 * magnitude is equal to ((a.x * a.x) + (a.y * a.y)). Vector2D(2,4) ((2*2)=4
	 * + (4*4)=16) = Square(20) = 4.472135955
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the vector upon which to determine the
	 * magnitude.
	 * 
	 * Return value
	 * 
	 * A double value which is the magnitude of the vector.
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public double vVectorMagnitude(HGBVector2D v0)
	{
		return Math.sqrt(vVectorSquared(v0));
	}

	/***********************************************************************
	 * vNormalizeVector
	 * 
	 * A normalized vector is a vector with a length of one. The resultant
	 * vector is often called a unit vector. The vNormalizeVector function
	 * converts a vector into a normalized vector. To normalize a vector, the
	 * vector is scaled by the reciprocal of the magnitude of the vector: cn = c
	 * * 1/|c|.
	 * 
	 * Vector2D(2,4) == Magnitude: ((2*2)=4 + (4*4)=16) = Sqrt(20) = 4.472135955
	 * X = 2/4.472135955 = .447213595 Y = 4/4.472135955 = .894427191
	 * Vector2D(.447213595, .894427191); Vector2D(5,6) =
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the vector to normalize.
	 * 
	 * Returns a normalized vector
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public HGBVector2D vNormalizeVector(HGBVector2D v0)
	{
		// Must diminish the resolution as vectors are float and magnitude is
		// double
		float magnitude = (float) vVectorMagnitude(v0);

		// protect against divide by zero
		if (magnitude == 0)
		{
			return (new HGBVector2D());
		}

		float X = v0.X / magnitude;
		float Y = v0.Y / magnitude;
		return (new HGBVector2D(X, Y));
	}

	/***********************************************************************
	 * vDotProduct
	 * 
	 * The function vDotProduct computes the dot product of two vectors. The dot
	 * product of two vectors is the sum of the products of the components of
	 * the vectors ie: for the vectors a and b, NoteOnAndroidStudio that the dot product is not
	 * two vectors drawn with their tails joined. It is the vector that joins
	 * from the tail of the first to the head of the second, with the second
	 * drawn from the head of the first.
	 * 
	 * 
	 * dotprod = a1 * a2 + b1 * b2. Vector2D(2,4), and Vector2D(5,6) (2 * 5) +
	 * (4 * 6) = (10 + 24) = 34
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the first vector used for obtaining a dot
	 * product. v1 A Vector2D class containing the second vector used for
	 * obtaining a dot product.
	 * 
	 * Return value
	 * 
	 * A float value containing the scalar dot product value.
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public float vDotProduct(HGBVector2D v0, HGBVector2D v1)
	{
		// Normalize: Vector2D(2,4) = {X = 0.4472136, Y = 0.8944272, Magnitude =
		// 1}
		// Normalize: Vector2D(5,6) = {X = 0.6401844, Y = 0.7682213, Magnitude =
		// 1}
		// DotProduct(v0, v1) ==> a1 * a2 + b1 * b2.
		// Vector2D(0.4472136,0.8944272), and Vector2D(0.6401844,0.7682213)
		// (0.4472136 * 0.6401844) + (0.8944272 * 0.7682213) =
		// 0.97341713961277776

		// double db = ((v0.X * v1.X) + (v0.Y * v1.Y));

		return ((v0.X * v1.X) + (v0.Y * v1.Y));
	}

	/***********************************************************************
	 * vNormalVector
	 * 
	 * The function vNormalVector computes the vector that is normal to a given
	 * vector. For the vector a, the normal vector n = (-ay, ax).
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the vector vector for which a normal
	 * vector is sought.
	 * 
	 * Return value
	 * 
	 * A Vector2D class containing the normal vector.
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Return argument removed (was a remanent
	 * of classic C) : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public HGBVector2D vNormalVector(HGBVector2D v0)
	{
		try
		{
			float X = -v0.Y;
			float Y = v0.X;
			return (new HGBVector2D(X, Y));
		} catch (Exception excp)
		{
			return null;
		}
	}

	/***********************************************************************
	 * vVectorAngle
	 * 
	 * The function vVectorAngle computes the cosine of the angle between two
	 * vectors. NoteOnAndroidStudio: that the angle between two vectors is not like they both
	 * start from from one point and branch away from each other. Rather the
	 * angle is between where the the tail of the second is attached to the head
	 * of the first as drawn in finding a dot product.
	 * 
	 * Normalize: Vector2D(2,4) = {X = 0.4472136, Y = 0.8944272, Magnitude = 1}
	 * Normalize: Vector2D(5,6) = {X = 0.6401844, Y = 0.7682213, Magnitude = 1}
	 * DotProduct(v0, v1) ==> a1 * a2 + b1 * b2. Vector2D(0.4472136,0.8944272),
	 * and Vector2D(0.6401844,0.7682213) (0.4472136 * 0.6401844) + (0.8944272 *
	 * 0.7682213) = 0.97341713961277776 the cosine of the angle between the the
	 * vectors
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the first vector. v1 A Vector2D class
	 * containing the second vector.
	 * 
	 * Return value
	 * 
	 * A double value indicating the cosine of the angle between the two vectors
	 * is returned.
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Converted to Java 140528 Gammill
	 * 
	 * The return is in RADIANS
	 * 
	 * Notes on obtaining an angle in degrees
	 *
	 *    Find the angle in degrees between a vector from the origin
	 *    and a horizontal vector.
	 * 
	 *    To find the angle in degrees from one point to another.
	 *    In this case from the hive origin to a given point
	 *
	 *    1) Get a vector from point 1 to point 2
	 *       The hive origin to the rose origin or last touch
	 *
	 *    2) Get a vector of a horizontal
	 *       The hive origin outward from 0 in a positive x direction
	 *       (Y constant)
	 *
	 *    3) Get the vector angle
	 *       The cosine of the angle in radians
	 *
	 *    4) Get the arcCose of the cosine
	 *
	 *    5) Multiple the arcCosine by the number of radians in a dgree.
	 *       (57.295779513082320876798 radians to 1 degree)
	 *       
	 *       The answer is an angle in degrees from 0 to 180.
	 *       Further on, we take care of the quadrants
	 *
	 *    6) The degrees may need to be adjust by the quadrant of point 2
	 *    
	 *       a) If point 2 is to the right of a vertical through point 1,
	 *          and above a horizontal through point 1, then quadrant 1
	 *          and the result is a correct angle.
	 *
	 *       b) If point 2 is to the left of the vertical and above than correct.
	 *    
	 *       c) If point 2 is to the left and below than add 90
	 *    
	 *       d) If point 2 is to the right and below than subtract from 360
	 *    
	 *       e) If on the vertical or horizontal than 0, 90, 180 or 270
	 ************************************************************************/

	public double vVectorAngle(HGBVector2D v0, HGBVector2D v1)
	{
		double vangle;

		HGBVector2D vec0 = vNormalizeVector(v0);
		HGBVector2D vec1 = vNormalizeVector(v1);

		// Normalize: Vector2D(2,4) = {X = 0.4472136, Y = 0.8944272, Magnitude =
		// 1}
		// Normalize: Vector2D(5,6) = {X = 0.6401844, Y = 0.7682213, Magnitude =
		// 1}
		// DotProduct(v0, v1) ==> a1 * a2 + b1 * b2.
		// Vector2D(0.4472136,0.8944272), and Vector2D(0.6401844,0.7682213)
		// (0.4472136 * 0.6401844) + (0.8944272 * 0.7682213) =
		// 0.97341716289520264

		vangle = vDotProduct(vec0, vec1);

		return vangle;
	}

	/***********************************************************************
	 * vProjectAndResolve
	 * 
	 * The function vProjectAndResolve resolves a vector into two vector
	 * components. The first is a vector obtained by projecting vector v0 onto
	 * v1. The second is a vector that is perpendicular (normal) to the
	 * projected vector. It extends from the head of the projected vector v1 to
	 * the head of the original vector v0. c = ((a * b) / (|b|^2)) * b
	 * 
	 * // Project and resolve. // Two vectors v0 and v1. // v2 is a projection
	 * of v0 onto v1 // v3 is a vector between v2 and v0 // v3 is perpendicular
	 * to v1 // the magnitude of v3 is the distance between // the point of v0
	 * and the line of v1 // the vector points down, tail at the end of v1, //
	 * head on v0.
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the first vector v1 A Vector2D class
	 * containing the second vector
	 * 
	 * Return value pProj A Projection structure containing the resolved vectors
	 * and their lengths. Error Return null
	 * 
	 * void.
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Return argument removed (was a remanent
	 * of classic C) : - Converted to Java 140528 Gammill : - Converted to Java
	 * 140528 Gammill
	 ************************************************************************/

	public HGBProjection vProjectAndResolve(HGBVector2D v0, HGBVector2D v1)
	{
		try
		{
			// obtain projection vector
			//
			// c = a * b
			// ----- b
			// |b|^2
			//
			float proj1 = vDotProduct(v0, v1) / vDotProduct(v1, v1);
			float X = v1.X * proj1;
			float Y = v1.Y * proj1;
			HGBVector2D vProjection = new HGBVector2D(X, Y);

			// obtain perpendicular projection : e = a - c
			// Vector2D vOrthogonal = v0 - vProjection; // the perpendicular
			// vetor
			HGBVector2D vOrthogonal = this.vSubtractVectors(v0, vProjection);
			if (vOrthogonal == null)
				return null;

			double lenProj = vVectorMagnitude(vProjection);
			double lenPerpProj = vVectorMagnitude(vOrthogonal);

			// The return package: Class Projection
			return (new HGBProjection(vProjection, vOrthogonal, lenProj, lenPerpProj));
		} catch (Exception excp)
		{
			return null;
		}
	}

	/***********************************************************************
	 * vIsPerpendicular
	 * 
	 * The function vIsPerpendicular determines if two vectors are perpendicular
	 * to one another. This is done by testing the dot product of the two
	 * vectors. If the dot product is zero then the vectors are perpendicular.
	 * 
	 * Parameters
	 * 
	 * v0 A Vector2D class containing the first vector. v1 A Vector2D class
	 * containing the second vector.
	 * 
	 * Return value
	 * 
	 * A boolean value. true if the two vectors are perpendicular. false if the
	 * vectors are not perpendicular.
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public Boolean vIsPerpendicular(HGBVector2D v0, HGBVector2D v1)
	{
		return (vDotProduct(v0, v1) == 0.0);
	}

	public boolean vIsParallel(HGBVector2D v0, HGBVector2D v1)
	{
		double angle = vVectorAngle(v0, v1);
		if ((angle < 1) && (angle >= .999999))
			angle = 1.0;
		return (angle == 1.0);
	}

	/***********************************************************************
	 * vDistFromPointToLine
	 * 
	 * The function vDistFromPointToLine computes the distance from the point
	 * ptTest to the line defined by endpoints pt0 and pt1. This is done by
	 * resolving the the vector from pt0 to ptTest into its components. The
	 * length of the component vector that is attached to the head of the vector
	 * from pt0 to ptTest is the distance of ptTest from the line.
	 * 
	 * Parameters
	 * 
	 * p0 A GDI+ PointF containing the first endpoint of the line. p1 A GDI+
	 * PointF containing the second endpoint of the line. pnt A GDI+ PointF
	 * containing the point for which the distance from the line is to be
	 * computed.
	 * 
	 * Return value error return: -1
	 * 
	 * A double value that contains the distance of pnt to the line defined by
	 * the endpoints p0 and p1.
	 * 
	 * HISTORY : - created - denniscr : - Converted C++/CLI 061023 Gammill : -
	 * Converted CS 080123 Gammill : - Converted to Java 140528 Gammill
	 ************************************************************************/

	public double vDistFromPointToLine(PointF p0, PointF p1, PointF pnt)
	{
		try
		{
			// The defined line as a vector
			float X = p1.x - p0.x;
			float Y = p1.y - p0.y;
			HGBVector2D vLine = new HGBVector2D(X, Y);

			// A vector from the end of the line to the point to test
			X = pnt.x - p0.x;
			Y = pnt.y - p0.y;
			HGBVector2D vTest = new HGBVector2D(X, Y);

			HGBProjection pProjection = vProjectAndResolve(vTest, vLine);
			if (pProjection == null)
				return -1;

			double rtn = pProjection.LenPerpProj;

			// delete vLine;
			// delete vTest;
			// delete pProjection;

			return rtn;
		} catch (Exception excp)
		{
			return -1;
		}
	}

	// ------------------------------
	// Reduce the magnitude of the vector by value
	// Vector2D(2,4) reduced by 2
	// {X = 2, Y = 4, Magnitude = 4.47213595499958}
	// Normalized: Vector2D(2,4) = Vector2D(.447213595, .894427191);\
	// Scaled by 2: Vector2D(Vector2D(.447213595, .894427191), 2) =
	// Vector2D = {X = 0.8944272, Y = 1.788854, Magnitude = 1.9999999760524}
	// Reduce Magnitude = 4.47213595499958 by Magnitude = 1.9999999760524
	// Vector2D = {X = 1.105573, Y = 2.211146, Magnitude = 2.47213597894718}
	public HGBVector2D vReduceMagnitude(HGBVector2D v0, float value)
	{
		try
		{
			// Create a vector value in magnitude
			HGBVector2D vec = vNormalizeVector(v0);
			vec = vScaleVector(vec, value);

			float X = v0.X - vec.X;
			float Y = v0.Y - vec.Y;
			return (new HGBVector2D(X, Y));
		} catch (Exception excp)
		{
			return null;
		}
	}

	// ------------------------------
	// Increase the magnitude of the vector by value
	// Vector2D(2,4) Increase by 2
	// {X = 2, Y = 4, Magnitude = 4.47213595499958}
	// Normalized: Vector2D(2,4) = Vector2D(.447213595, .894427191);\
	// Scaled by 2: Vector2D(Vector2D(.447213595, .894427191), 2) =
	// Vector2D = {X = 0.8944272, Y = 1.788854, Magnitude = 1.9999999760524}
	// Reduce Magnitude = 4.47213595499958 by Magnitude = 1.9999999760524
	// Vector2D = {X = 2.8944273, Y = 5.7888546, }

	public HGBVector2D vIncreaseMagnitude(HGBVector2D v0, float value)
	{
		try
		{
			// Create a vector value in magnitude
			HGBVector2D vec = vNormalizeVector(v0);
			vec = vScaleVector(vec, value);
			if (vec == null)
				return null;

			float X = v0.X + vec.X;
			float Y = v0.Y + vec.Y;
			return (new HGBVector2D(X, Y));
		} catch (Exception excp)
		{
			return null;
		}
	}

   //-----------------------------------------------------------
	// / <summary>
	// / Return a Vector2D defined by two points
	// / Vectors do not have location, only displacement.
   // / These vectors may be defined by coordinates; but they
   // / may not then be used to find a coordinate.  They are
   // / NOT coordinates!
	public HGBVector2D vPointVector(PointF p0, PointF p1)
	{
		// The defined line as a vector
		float X = p1.x - p0.x;
		float Y = p1.y - p0.y;
		return (new HGBVector2D(X, Y));
	}

	public HGBVector2D vCartesianVector(float[] p0, float[] p1)
	{
		float X = p1[0] - p0[0];
		float Y = p1[1] - p0[1];
		return (new HGBVector2D(X, Y));
	}

	public HGBVector2D vPointVector(Point p0, Point p1)
	{
		// The defined line as a vector
		float X = (float) p1.x - (float) p0.x;
		float Y = (float) p1.y - (float) p0.y;
		return (new HGBVector2D(X, Y));
	}
   //-----------------------------------------------------------

	// / <summary>
	// / Use an array of point to see if all point are in line
	// / Are part of a set of collinear vectors located in space
	// / by the points (point vectors).
	// /
	// / Input
	// / pnt -- tail (first point) to locate in space v0
	// / v0 -- vector2d
	// / pnts[] -- array of points to be tested
	// /
	// / Breaks from the loop and returns false on the first occurrence
	// / of a false parallel return.
	// / </summary>
	// /
	public boolean vIsCollinear(PointF pnt, HGBVector2D v0, PointF[] pnts)
	{
		if (v0 != null)
		{
			for (PointF pt : pnts)
			{
				HGBVector2D v1 = vPointVector(pnt, pt);
				if (vIsParallel(v0, v1) == false)
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	// /<summary>
	// / Input: p0 -- tail of vector (start point)
	// / v0 -- combine p0 and v0 for a point vector (a line)
	// / p1 -- head of a vector (end point) p0 to p1 creates v0
	// / pnt -- Point to be tested on line
	// / tolerance -- maximum distance from the line
	// / to be thought of as "on the line"
	// / Default (property Tolerance) is: .005
	// / Output: boolean -- true if distance pnt from line is less than or equal
	// to tolerance
	// /</summary>
	// /<remarks>
	// / boolean vIsPointOnLine(PointF p0, Vector2D v0, PointF pnt, double
	// tolerance)
	// / boolean vIsPointOnLine(PointF p0, Vector2D v0, PointF pnt)
	// / boolean vIsPointOnLine(PointF p0, PointF p1, PointF pnt, double
	// tolerance)
	// / boolean vIsPointOnLine(PointF p0, PointF p1, PointF pnt)
	// /</remarks>

	public boolean vIsPointOnLine(PointF p0, HGBVector2D v0, PointF pnt, double tolerance)
	{
		// Get the vector from the tail of vector (v0) to the free point (pnt)
		HGBVector2D v1 = vPointVector(p0, pnt);
		HGBProjection proj = vProjectAndResolve(v0, v1);
		if (proj == null)
			return false;

		// return ((proj.LenPerpProj <= tolerance) && (proj.LenProj <=
		// v0.Magnitude)) ? true : false;

		if (proj.LenPerpProj <= tolerance)
		{
         return v1.Magnitude <= v0.Magnitude;
      }
		return false;

	}

	public boolean vIsPointOnLine(PointF p0, HGBVector2D v0, PointF pnt)
	{
		// Get the vector from the tail of vector (v0) to the free point (pnt)
		HGBVector2D v1 = vPointVector(p0, pnt);
		if (v1 == null)
			return false;

		HGBProjection proj = vProjectAndResolve(v0, v1);
		// return ((proj.LenPerpProj <= tolerance) && (proj.LenProj <=
		// v0.Magnitude)) ? true : false;

		if (proj.LenPerpProj <= this.tolerance)
		{
         return v1.Magnitude <= v0.Magnitude;
      }
		return false;
	}

	public boolean vIsPointOnLine(PointF p0, PointF p1, PointF pnt, double tolerance)
	{
		// The point vector created by the two given points
		HGBVector2D v0 = vPointVector(p0, p1);
		// Get the vector from the tail of vector (v0) to the free point (pnt)
		HGBVector2D v1 = vPointVector(p0, pnt);
		if (v1 == null)
			return false;

		HGBProjection proj = vProjectAndResolve(v0, v1);
		if (proj == null)
			return false;

		// return ((proj.LenPerpProj <= tolerance) && (proj.LenProj <=
		// v0.Magnitude)) ? true : false;

		if (proj.LenPerpProj <= tolerance)
		{
         return v1.Magnitude <= v0.Magnitude;
      }
		return false;
	}

	public boolean vIsPointOnLine(PointF p0, PointF p1, PointF pnt)
	{
		// The point vector created by the two given points
		HGBVector2D v0 = vPointVector(p0, p1);
		// Get the vector from the tail of vector (v0) to the free point (pnt)
		HGBVector2D v1 = vPointVector(p0, pnt);
		if (v1 == null)
			return false;

		HGBProjection proj = vProjectAndResolve(v0, v1);
		// return ((proj.LenPerpProj <= tolerance) && (proj.LenProj <=
		// v0.Magnitude)) ? true : false;

		if (proj.LenPerpProj <= this.tolerance)
		{
         return v1.Magnitude <= v0.Magnitude;
      }
		return false;
	}
	// --------------------------------------------------------------------
}
