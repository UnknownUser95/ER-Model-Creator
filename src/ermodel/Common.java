package ermodel;

import org.eclipse.swt.graphics.*;

public abstract class Common {
	public static final int DEFAULT_WIDTH = 200;
	public static final int DEFAULT_HEIGHT = 20;
	
	public static final int FONT_Y_OFFSET = DEFAULT_HEIGHT / 3;
	public static final int FONT_X_CHARCTER_OFFSET = 8;
	
	public static final Color COLOUR_SELECTED = new Color(255, 0, 0);
	public static final Color COLOUR_DEFAULT = new Color(255, 255, 255);
	public static final Color COLOUR_OTHER = new Color(0, 255, 0);
	
	public static double distanceOfPoints(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}
}
