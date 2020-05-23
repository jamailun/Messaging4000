package fr.jamailun.msg4000.server.rooms;

import fr.jamailun.msg4000.common.StaticConfiguration;
import fr.jamailun.msg4000.common.packets.ServerTransferMessage;
import fr.jamailun.msg4000.server.MessagingServer;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RoomHistory {

	private final List<ServerTransferMessage> history;

	private final String uri;
	public RoomHistory(int port) {
		String uri1;
		history = new ArrayList<>();
		try {
			String uriDir = new File(MessagingServer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/rooms/";
			new File(uriDir).mkdirs();
			uri1 = uriDir + "room_"+port+".data";
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

	private void readFile() {
		history.clear();
		try(BufferedReader reader = new BufferedReader(new FileReader(uri))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ", 3);
				if(parts.length < 3) {
					System.err.println("Bad line : " + line+".");
					continue;
				}
				long time;
				try {
					time = Long.parseLong(parts[0]);
				} catch (NumberFormatException nfe) {
					System.err.println("Bad timestamp : " + line+".");
					continue;
				}
				ServerTransferMessage pak = new ServerTransferMessage(time, parts[1], parts[2]);
				add(pak);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void add(ServerTransferMessage msg) {
		history.add(msg);
		if(history.size() > StaticConfiguration.MAX_MESSAGES)
			history.remove(0);
	}

	public void save() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(uri, false));
			history.forEach(msg -> {
				try {
					String m = msg.getMessage().replaceAll("\n", "");
					writer.write(msg.getTimeStamp()+" "+msg.getAuthor()+" "+m);
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<ServerTransferMessage> getList() {
		return new ArrayList<>(history);
	}
}