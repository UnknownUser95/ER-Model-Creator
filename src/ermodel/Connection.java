package ermodel;

public class Connection {
	public final Connector from;
	public final Connector to;
	
	public Connection(Connector from, Connector to) {
		super();
		this.from = from;
		this.to = to;
	}
	
	public boolean isConnectedTo(DrawObject obj) {
		for(Connector conn : obj.connectors) {
			if(conn.equals(from) || conn.equals(to)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(obj instanceof Connection other) {
			return (from.equals(other.from) && to.equals(other.to)) || (from.equals(other.to) && to.equals(other.from));
		}
		return false;
	}
}
