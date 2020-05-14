package hgb;

// A helper class for HGBVector2D.vDistFromPointToLine()
// Is the return of a call the HGBVector2D.vDistFromPointToLine()
// For access to the single var LenPerpProj


//--------------------------------------------
//Project and resolve.  
//Class Projection is the return from vProjectAndResolve(Vector2D v0, Vector2D v1)
//Two vectors v0 and v1.
//   v2 is a projection of v0 onto v1
//   v3 is a vector between v2 and v0 
//       v3 is perpendicular to v1
//       the magnitude of v3 is the distance between
//       the point of v0 and the line v1
public class HGBProjection
{
	
//	protected HGBProjection()
//	{
//		vProj = new HGBVector2D();
//		vPerpProj = new HGBVector2D();
//		LenProj = 0;
//		LenPerpProj = 0;
//	}
	
	@SuppressWarnings("unused")	private HGBVector2D vProj;
	@SuppressWarnings("unused")	private HGBVector2D vPerpProj;
	@SuppressWarnings("unused")	private double LenProj;

   // The is the important return
	protected double LenPerpProj;

	protected HGBProjection(HGBVector2D vproj, HGBVector2D vperpproj, double lenproj, double lenperpproj)
	{
		if ((vproj != null) && (vperpproj != null))
		{
			this.vProj = vproj;
			this.vPerpProj = vperpproj;
			this.LenProj = lenproj;
			this.LenPerpProj = lenperpproj;
		}
		else
		{
			this.vProj = new HGBVector2D();
			this.vPerpProj = new HGBVector2D();
			this.LenProj = 0;
			this.LenPerpProj = 0;
		}
	}
}
