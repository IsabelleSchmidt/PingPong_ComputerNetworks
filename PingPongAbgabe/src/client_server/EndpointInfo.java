package client_server;

import java.net.InetAddress;
import java.util.Objects;

/**
 * Klasse repräsentiert Informationen zum Endpoint (IP Adresse, port). 
 * Diese Daten bekommt z.B. Client, um zu wissen, an welche Server er Pings schicken soll.
 * @author
 *
 */
public class EndpointInfo {

	private InetAddress address;
	private int port;
	
	public EndpointInfo(InetAddress address, int port) {
	    this.address = address;
	    this.port = port;
	}
	public InetAddress getAddress() {
	    return address;
	}
	
	public int getPort() {
	    return port;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    EndpointInfo endpoint = (EndpointInfo) o;
	    return port == endpoint.port &&
	            address.equals(endpoint.address);
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(address, port);
	}
	
	@Override
	public String toString() {
	    return "Endpoint{" +
	            "address=" + address +
	            ", port=" + port +
	            '}';
	
	}

}
