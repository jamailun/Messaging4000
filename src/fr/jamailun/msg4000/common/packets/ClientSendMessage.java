package fr.jamailun.msg4000.common.packets;

import fr.jamailun.stds.common.JavaPacket;

public class ClientSendMessage extends JavaPacket {
	private static final long serialVersionUID = 100205270L;

	private final String message;
	public ClientSendMessage(String message) {
		super("clientMessage");
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}