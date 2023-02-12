package ermodel;

import java.util.*;

import org.eclipse.swt.graphics.*;

public class Connector {
	public final Point position;
	public final MouseObject owner;
	private final ArrayList<Connection> connections = new ArrayList<>();
	
	public Connector(Point position, MouseObject owner) {
		super();
		this.position = position;
		this.owner = owner;
	}
	
//	public ArrayList<Connection> getConnections() {
//		return connections;
//	}
	
	public void addConnection(Connection con) {
		connections.add(con);
	}
	
	public void removeConnection(Connection con) {
		connections.remove(con);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(owner, position);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(obj instanceof Connector other) {
			return owner == other.owner && position.equals(other.position);
		}
		return false;
	}
}
