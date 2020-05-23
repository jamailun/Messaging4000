package fr.jamailun.msg4000.server.rooms;

import fr.jamailun.msg4000.server.MessagingServer;
import fr.jamailun.stds.server.JavaServer;
import fr.jamailun.stds.server.VirtualServer;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RoomsManager {

	private List<RoomServer> rooms;
	private final JavaServer server;
	private final String uri;

	public RoomsManager(JavaServer server) {
		this.server = server;
		rooms = new ArrayList<>();
		String uri1;
		try {
			String uriDir = new File(MessagingServer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/saves/";
			new File(uriDir).mkdirs();
			uri1 = uriDir + "roomsData.data";
		} catch (URISyntaxException e) {
			e.printStackTrace();
			uri1 = "";
		}
		uri = uri1;

		File file = new File(uri);
		if( ! file.exists()) {
			try {
				if ( ! file.createNewFile() )
					System.err.println("File " + uri + " could not be created.");
				else
					System.out.println("File " + uri + " created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			readFile();
		}
	}

	public void registerNewRoom(VirtualServer virtualServer) {
		for(RoomServer r : rooms) {
			if(r.getVirtualServer().getPort() == virtualServer.getPort()) {
				throw new IllegalArgumentException("There is already a room on the port " + r.getVirtualServer().getPort()+" !");
			}
		}
		RoomServer rs = new RoomServer(virtualServer);
		rooms.add(rs);
		writeServer(rs);
	}

	public void unregisterNewRoom(int port) {
		if( ! server.removeVirtualServer(port) )
			throw new IllegalArgumentException("No virtual server have been found with port " + port+".");

		if( ! rooms.removeIf(room -> room.getVirtualServer().getPort() == port) )
			throw new IllegalArgumentException("There is no room on the port " + port+" !");

		clearFile();

		for(RoomServer rs : rooms)
			writeServer(rs);
	}

	private void clearFile() {
		try {
			new FileWriter(uri, false).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeServer(RoomServer rs) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(uri, true));
			try {
				writer.write(rs.getVirtualServer().getPort()+" "+rs.getVirtualServer().getMaxConnected()+" "+rs.getVirtualServer().getServerName());
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void readFile() {
		try(BufferedReader reader = new BufferedReader(new FileReader(uri))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ", 3);
				if(parts.length < 3) {
					System.err.println("Bad line : " + line+".");
					continue;
				}
				int port;
				try {
					port = Integer.parseInt(parts[0]);
				} catch (NumberFormatException nfe) {
					System.err.println("Bad port : " + line+".");
					continue;
				}
				int max;
				try {
					max = Integer.parseInt(parts[1]);
				} catch (NumberFormatException nfe) {
					System.err.println("Bad max connected : " + line+".");
					continue;
				}
				System.out.println("Read server data : port="+port+", name={"+parts[2]+"}.");
				rooms.add(new RoomServer(server.addNewVirtualServer(parts[2], port, max)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}