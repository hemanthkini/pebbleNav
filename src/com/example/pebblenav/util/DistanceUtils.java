package com.example.pebblenav.util;


public class DistanceUtils {

	public static double minimum_distance(vec2 v, vec2 w, vec2 p) {
		  // Return minimum distance between line segment vw and point p
		  double l2 = w.sub(v).mag2();  // i.e. |w-v|^2 -  avoid a sqrt
		  if (l2 == 0.0) return v.sub(p).magnitude();   // v == w case
		  // Consider the line extending the segment, parameterized as v + t (w - v).
		  // We find projection of point p onto the line. 
		  // It falls where t = [(p-v) . (w-v)] / |w-v|^2
		  double t = p.sub(v).dot(w.sub(v)) / l2;
		  if (t < 0.0) return v.sub(p).magnitude();     // Beyond the 'v' end of the segment
		  else if (t > 1.0) return w.sub(p).magnitude(); // Beyond the 'w' end of the segment
		  vec2 projection = v.add((w.sub(v)).scale(t));  // Projection falls on the segment
		  return projection.sub(p).magnitude();
		}
	
  public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;
      if (unit == 'K') {
        dist = dist * 1.609344;
      } else if (unit == 'N') {
        dist = dist * 0.8684;
        }
      return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
    }

	
}
