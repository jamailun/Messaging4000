package fr.jamailun.msg4000.server;

import fr.jamailun.msg4000.common.StaticConfiguration;
import fr.jamailun.msg4000.server.rooms.RoomsManager;
import fr.jamailun.stds.server.JavaServer;
import fr.jamailun.stds.server.VirtualServer;

public class MessagingServer {

	private final RoomsManager rooms;

    private MessagingServer() {
        JavaServer server = new JavaServer(StaticConfiguration.port);
		rooms = new RoomsManager(server);
		rooms.registerNewRoom(server.addNewVirtualServer("room1", StaticConfiguration.port+1, 10));
		rooms.registerNewRoom(server.addNewVirtualServer("room2", StaticConfiguration.port+2, 20));
		rooms.registerNewRoom(server.addNewVirtualServer("room3", StaticConfiguration.port+3, 50));
        registerCommands(server);
        server.start();
    }

    private void registerCommands(JavaServer server) {
        if(server.usafeGetMultipleCommandExecutor() == null)
            System.err.println("Le server ne tourne pas sous MultipleCommandExecutor");
        server.usafeGetMultipleCommandExecutor().registerCommand("createRoom", args -> {
			if (args.length < 2) {
				System.err.println("[CMD ERROR] Usage : /createVS <port> <name>.");
				return;
			}
			int port;
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException ignored) {
				System.err.println("[CMD ERROR] Bad number format. Usage : /createVS <port> <name>.");
				return;
			}
			if (port < 1000) {
				System.err.println("[CMD ERROR] Port < 1000 is unsafe.");
				return;
			}
			if (port > 90000) {
				System.err.println("[CMD ERROR] Port > 90000 is illegal.");
				return;
			}
			StringBuilder b = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				b.append(args[i]);
				if (i < args.length - 1)
					b.append(" ");
			}
			VirtualServer vs = server.addNewVirtualServer(b.toString(), port, 100);
			if (vs == null) {
				System.err.println("[CMD ERROR] Unknown error. Maybe port is taken ? Maybe name is not correct ?");
			} else {
				rooms.registerNewRoom(vs);
				System.out.println("[CMD >] The new virtual room ("+b.toString()+") has been created on port " + port + ".");
			}
        }, "Create a new room. /+ <port> <name>", "+");

		server.usafeGetMultipleCommandExecutor().registerCommand("removeRoom", args -> {
			if(args.length < 1) {
				System.err.println("[CMD ERROR] Usage : '- <port>'.");
				return;
			}
			int port;
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException ignored) {
				System.err.println("[CMD ERROR] Bad number format. Usage : /createVS <port>.");
				return;
			}

			try {
				rooms.unregisterNewRoom(port);
				System.out.println("[CMD >] Success : virtual server on port " + port + " has been deleted.");
			} catch (IllegalArgumentException iae) {
				System.err.println("[CMD ERROR] Error on deleting server on port " + port+" : " + iae.getMessage());
			}
		}, "Remove a new Virtual Server. Usage : /- <portID>", "-");
    }

    public static void main(String[] args) {
        if( ! JavaServer.verbose())
          JavaServer.toggleVerbose();
        new MessagingServer();
    }
}