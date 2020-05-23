package com.chunruo.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistacneUtil {
	private final static double PI = 3.14159265358979323; // 圆周率
    private final static double R = 6371229; // 地球的半径
    private final static Integer CONFINES=100;//范围值
    private static Map<Integer,String> distanceMap=new HashMap<Integer,String>();
    public static double getDistacne(String coordinates1, String coordinates2){
    	List<String> coordinates1List = new ArrayList<String>();
    	List<String> coordinates2List = new ArrayList<String>();
    	coordinates1List = StringUtil.strToStrList(coordinates1, ",");
    	coordinates2List = StringUtil.strToStrList(coordinates2, ",");
    	if (coordinates1List != null 
    			&& coordinates1List.size() == 2
    			&& coordinates2List != null 
    			&& coordinates2List.size() == 2){
    		double longt1 = StringUtil.nullToDouble(coordinates1List.get(0));
        	double longt2 = StringUtil.nullToDouble(coordinates2List.get(0));
        	double lat1 = StringUtil.nullToDouble(coordinates1List.get(1));
        	double lat2 = StringUtil.nullToDouble(coordinates2List.get(1));
        	return getDistance(longt1,lat1,longt2,lat2);
    	}
    	return 0.0;
    }
    
    /**
     * 两经纬度值计算距离
     * @param longt1
     * @param lat1
     * @param longt2
     * @param lat2
     * @return
     */
    public static double getDistance(double longt1, double lat1, double longt2, double lat2) {
        double x, y, distance;
        x = (longt2 - longt1) * PI * R * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
        y = (lat2 - lat1) * PI * R / 180;
        distance = Math.hypot(x, y);
        return distance;
    }
    
    static class Rectangle {
		double west;
		double north;
		double east;
		double south;

		public Rectangle(double lng1, double lat1, double lng2, double lat2) {
			this.west = Math.min(lng1, lng2);
			this.north = Math.max(lat1, lat2);
			this.east = Math.max(lng1, lng2);
			this.south = Math.min(lat1, lat2);
		}
	}

	public static boolean isInRect(Rectangle rect, double lon, double lat) {
		return rect.west <= lon && rect.east >= lon && rect.north >= lat && rect.south <= lat;
	}

	// China region - raw data
	static Rectangle[] region = new Rectangle[] { new Rectangle(79.446200, 49.220400, 96.330000, 42.889900),
			new Rectangle(109.687200, 54.141500, 135.000200, 39.374200),
			new Rectangle(73.124600, 42.889900, 124.143255, 29.529700),
			new Rectangle(82.968400, 29.529700, 97.035200, 26.718600),
			new Rectangle(97.025300, 29.529700, 124.367395, 20.414096),
			new Rectangle(107.975793, 20.414096, 111.744104, 17.871542) };

	// China excluded region - raw data
	static Rectangle[] exclude = new Rectangle[] { new Rectangle(119.921265, 25.398623, 122.497559, 21.785006),
		new Rectangle(101.865200, 22.284000, 106.665000, 20.098800),
		new Rectangle(106.452500, 21.542200, 108.051000, 20.487800),
		new Rectangle(109.032300, 55.817500, 119.127000, 50.325700),
		new Rectangle(127.456800, 55.817500, 137.022700, 49.557400),
		new Rectangle(131.266200, 44.892200, 137.022700, 42.569200) 
	};

	/**
	 * 检查经纬度是否在中国
	 * @param lon
	 * @param lat
	 * @return
	 */
	public static boolean isInChina(double lon, double lat) {
		for (int i = 0; i < region.length; i++) {
			if (isInRect(region[i], lon, lat)) {
				for (int j = 0; j < exclude.length; j++) {
					if (isInRect(exclude[j], lon, lat)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检查经纬度是否在中国
	 * @param coordinates
	 * @return
	 */
	public static boolean isInChina(String coordinates){
		List<String> coordinatesList = StringUtil.strToStrList(coordinates, ",");
		if(coordinatesList != null && coordinatesList.size() == 2){
			double longt = StringUtil.nullToDouble(coordinatesList.get(1));
        	double lat = StringUtil.nullToDouble(coordinatesList.get(0));
        	return isInChina(longt, lat);
		}
		return false;
	}
	/**
	 * 根据距离得到一个范围值
	 * @param distance
	 * @return
	 */
	public static String getDistance(Double distance){
		Integer result=distance.intValue() / CONFINES;
		//如果是在1公里内则返回 几百米内 如果大于1公里则返回 几公里内
		if (result<9){
			if (distanceMap.containsKey(result))
				return distanceMap.get(result);
			String distanceString=(result+1)*CONFINES+"米以内";
			distanceMap.put(result, distanceString);
			return distanceString;
		}else {
			result /=10;
			Integer key=result+11;
			if (distanceMap.containsKey(key))
				return distanceMap.get(key);
			String distanceString=(result+1)+"公里以内";
			distanceMap.put(key, distanceString);
			return distanceString;
		}
	}
	
//	public static void main(String[] args){
//		System.out.println(isInChina("31.16119774504019,121.394001218931"));
//	}
}
