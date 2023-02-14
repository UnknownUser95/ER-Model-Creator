package ermodel;

import static ermodel.Common.*;

import java.util.*;

import org.eclipse.swt.graphics.*;

public class DrawObject {
	private final int[] shape;
	public final Point position;
	public final MouseObject type;
	public final Connector[] connectors = new Connector[4];
	private String text = "";
	private byte tags = 0;
	
	public DrawObject(MouseObject shape, Point position) {
		super();
		this.position = position;
		
		if(shape == MouseObject.ATTRIBUTE) {
			this.shape = shape.getPolygon();
		} else {
			this.shape = ModelDrawer.movePolygon(shape.getPolygon(), shape.xOffset + position.x, shape.yOffset + position.y);
		}
		type = shape;
		
		for(int i = 0; i < 4; i++) {
			int x = position.x;
			int y = position.y;
			
			switch(i) {
			case 0 -> x -= 100;
			case 1 -> y -= 10;
			case 2 -> x += 100;
			case 3 -> y += 10;
			}
			
			connectors[i] = new Connector(new Point(x, y), type);
		}
	}
	
	public int[] getPolygon() {
		return shape.clone();
	}
	
	public Optional<Rectangle> getBounds() {
		return switch(type) {
		case ENTITY -> Optional.of(new Rectangle(shape[0], shape[1], DEFAULT_WIDTH, DEFAULT_HEIGHT));
		case RELATION -> Optional.of(new Rectangle(shape[6], shape[1], DEFAULT_WIDTH, DEFAULT_HEIGHT));
		case ATTRIBUTE -> Optional.of(new Rectangle(position.x + MouseObject.ATTRIBUTE.xOffset, position.y + MouseObject.ATTRIBUTE.yOffset, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		default -> Optional.empty();
		};
	}
	
	public Connection connectTo(DrawObject other) {
		double distance = -1;
		Connector[] conns = new Connector[2];
		
		// get the closest connection
		for(Connector con1 : connectors) {
			for(Connector con2 : other.connectors) {
				double dist = distanceOfPoints(con1.position, con2.position);
//				System.out.printf("%f: %s - %s", dist, con1.position, con2.position);
				if(dist < distance || distance == -1) {
//					System.out.print(" <<");
					conns[0] = con1;
					conns[1] = con2;
					distance = dist;
				}
//				System.out.println();
			}
//			System.out.println();
		}
		
		return new Connection(conns[0], conns[1]);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void addCharacter(char c) {
		setText(getText() + c);
	}
	
	public void removeLastChar() {
		if(text.length() > 0) {
			setText(getText().substring(0, text.length() - 1));
		}
	}
	
	public boolean hasTag(final byte tag) {
		return (tags & tag) != 0;
	}
	
	public void setTag(final byte tag) {
		if(!hasTag(tag)) {
			tags |= tag;
		}
	}
	
	public void toggleTag(final byte tag) {
		tags ^= tag;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(obj == this) {
			return true;
		}
		if(obj instanceof DrawObject o) {
			return o.type == type && o.position.equals(position);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("DrawObject(position=(%d,%d), type=%s)", position.x, position.y, type);
	}
}
