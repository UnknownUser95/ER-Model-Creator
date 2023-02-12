package ermodel;

import static ermodel.Common.*;

public enum MouseObject {
	NONE(new int[] {}, 0, 0),
	ENTITY(new int[] { 0, 0, 0, DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_WIDTH, 0 }, -DEFAULT_WIDTH / 2, -DEFAULT_HEIGHT / 2),
	RELATION(new int[] { 0, -DEFAULT_HEIGHT / 2, DEFAULT_WIDTH / 2, 0, 0, DEFAULT_HEIGHT / 2, -DEFAULT_WIDTH / 2, 0 }, 0, 0),
	ATTRIBUTE(new int[] { DEFAULT_WIDTH, DEFAULT_HEIGHT }, -DEFAULT_WIDTH / 2, -DEFAULT_HEIGHT / 2);
	
	private final int[] polygon;
	public final int xOffset;
	public final int yOffset;
	
	private MouseObject(int[] polygon, int xOffset, int yOffset) {
		this.polygon = polygon;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public int[] getPolygon() {
		return polygon.clone();
	}
	
	public boolean isAllowedConnection(MouseObject other) {
		return switch(this) {
		case NONE -> false;
		case ENTITY -> other == RELATION || other == ATTRIBUTE;
		case ATTRIBUTE -> other == ENTITY || other == RELATION;
		case RELATION -> other == ENTITY || other == ATTRIBUTE;
		};
	}
}
