package fr.jamailun.msg4000.common.packets;

import fr.jamailun.stds.common.ClientInfo;
import fr.jamailun.stds.common.JavaPacket;

public class ServerTransferMessage extends JavaPacket {

	private final long emission;
	private final String author, message;
	public ServerTransferMessage(ClientInfo client, String message) {
		super("serverTransferMessage");
		this.author = client.getName();
		this.message = message;
		emission = System.currentTimeMillis();
	}

	public String getAuthor() {
		return author;
	}

	public String getMessage() {
		return message;
	}

	public long getEmission() {
		return emission;
	}

	public ServerTransferMessage(long timestamp, String author, String message) {
		super("serverTransferMessage");
		emission = timestamp;
		this.author = author;
		this.message = message;
	}
}