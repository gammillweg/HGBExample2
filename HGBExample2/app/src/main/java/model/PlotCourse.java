package model;

import java.util.ArrayList;

import hgb.HGBShared;
import hgb.HGBUtils;

/*
 * An example of how to find ones way between cells
 * Finds all cells between the last two chosen cells
 * in the touch demo using ArrayList GraphicView.collectTouchedCells
 * And returns an array list of cell ID's
 */
public class PlotCourse
{
   public PlotCourse(Shared shared)
   {
      this.shared = shared;
      hgbShared= shared.getHGBShared();
      hgbUtils = hgbShared.getHGBUtils();

      courseAL = new ArrayList<>();
   }
   Shared shared = null;
   HGBShared hgbShared = null;
   HGBUtils hgbUtils = null;

   ArrayList<Integer> courseAL = null;

   public ArrayList getCourse(ArrayList<Integer> collectTouchedCells)
   {
      if (collectTouchedCells.size()< 2) return courseAL;

      // get the last two touches
      int currentIndex = collectTouchedCells.get(collectTouchedCells.size()-1);
      int goalIndex = collectTouchedCells.get(collectTouchedCells.size()-2);

      float[] goalOrigin = hgbUtils.getCellOrigin(goalIndex);
      do
      {
         // The origins of the two cells of interest
         float[] origin = hgbUtils.getCellOrigin(currentIndex);
         int degrees = hgbUtils.getDegreesBetweenTwoCells(origin, goalOrigin);
         int side = hgbUtils.getHexSideByDegree(degrees);
         int[] currentBonds = hgbUtils.getBondings(currentIndex);
         currentIndex = currentBonds[side];

         if (currentIndex != goalIndex) courseAL.add(currentIndex);
      }
      while (currentIndex != goalIndex);

      return courseAL;
   }
}
