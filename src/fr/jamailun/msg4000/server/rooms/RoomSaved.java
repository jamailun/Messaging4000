package fr.jamailun.msg4000.server.rooms;

import fr.jamailun.msg4000.common.packets.ServerTransferMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomSaved {

	private final List<ServerTransferMessage> history;

	private final String uri;
	public RoomSaved(int port) {
		history = new ArrayList<>();
		uri = System.getProperty("user.dir") + port + ".data";
		File file = new File(uri);
		if( ! file.exists()) {
			try {
				if ( ! file.createNewFile() )
					System.err.println("File " + uri + " could not be created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			readFile();
		}

	}

	private void readFile() {
		history.clear();
		try {
			new FileWriter(uri, false).close();

			BufferedReader reader = new BufferedReader(new FileReader(uri));
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
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void add(ServerTransferMessage msg) {
		history.add(msg);
	}

	public void save() {
		try {
			new FileWriter(uri, false).close();

			BufferedWriter writer = new BufferedWriter(new FileWriter(uri));
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

}