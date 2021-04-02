package com.booking.recruitment.hotel.util;

public class DistanceCalculator {

	private final static double RADIUS_OF_EARTH = 6371.0;
	
	/**
	 * Returns the distance between two latitudes and longitudes
	 * using the Haversine formula
	 * REFERENCE : https://gist.github.com/vananth22/888ed9a22105670e7a4092bdcf0d72e4
	 * @param longitude1
	 * @param latitude1
	 * @param longitude2
	 * @param latitude2
	 * @return
	 */

	public static double getDistance(double longitude1, double latitude1,
			double longitude2, double latitude2) {
		double latitudeDistance = toRad(latitude2-latitude1);
		double longitudeDistance = toRad(longitude2-longitude1);
		double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2) + 
				Math.cos(toRad(latitude1)) * Math.cos(toRad(latitude2)) * 
				Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double distance = RADIUS_OF_EARTH * c;
		return distance;
	}

	private static Double toRad(Double value) {
		return value * Math.PI / 180;
	}


}
