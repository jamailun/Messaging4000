package fr.jamailun.msg4000.common;

import fr.jamailun.stds.common.ClientInfo;
import fr.jamailun.stds.common.Version;

public final class StaticConfiguration {
	private StaticConfiguration() {}
	public static final Version version = new Version("1.0");

	public static final int port = 9000;

	public static final String address = "46.105.92.228";
	public final static int MAX_MESSAGES = 30;

	public static final String ENTER_ROOM = "[+] Someone connected to the room.";
	public static final String LEAVE_ROOM = "[-] (CLIENT) disconnected from the room.";

	public static String adapt(ClientInfo client, String message) {
		return message.replace("(CLIENT)", client.getName());
	}
}