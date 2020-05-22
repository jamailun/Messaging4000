package fr.jamailun.msg4000.common.packets;

import fr.jamailun.stds.common.JavaPacket;

public class SystemMessage extends JavaPacket {
	private static final long serialVersionUID = 100205270L;

	private final String message;
	public SystemMessage(String message) {
		super("serverMessage");
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}