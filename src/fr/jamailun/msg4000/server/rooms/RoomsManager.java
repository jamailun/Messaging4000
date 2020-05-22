package fr.jamailun.msg4000.server.rooms;

import fr.jamailun.stds.server.JavaServer;
import fr.jamailun.stds.server.VirtualServer;

import java.util.ArrayList;
import java.util.List;

public class RoomsManager {

	private List<RoomServer> rooms;
	private final JavaServer server;

	public RoomsManager(JavaServer server) {
		this.server = server;
		rooms = new ArrayList<>();
	}

	public void registerNewRoom(VirtualServer virtualServer) {
		for(RoomServer r : rooms) {
			if(r.getVirtualServer().getPort() == virtualServer.getPort()) {
				throw new IllegalArgumentException("There is already a room on the port " + r.getVirtualServer().getPort()+" !");
			}
		}
		rooms.add(new RoomServer(virtualServer));
	}

	public void unregisterNewRoom(int port) {
		if( ! server.removeVirtualServer(port) )
			throw new IllegalArgumentException("No virtual server have been found with port " + port+".");

		if( ! rooms.removeIf(room -> room.getVirtualServer().getPort() == port) )
			throw new IllegalArgumentException("There is no room on the port " + port+" !");
	}

}
