package fr.jamailun.msg4000.server.rooms;

import fr.jamailun.msg4000.common.StaticConfiguration;
import fr.jamailun.msg4000.common.packets.ClientSendMessage;
import fr.jamailun.msg4000.common.packets.ServerTransferMessage;
import fr.jamailun.msg4000.common.packets.SystemMessage;
import fr.jamailun.stds.common.JavaPacket;
import fr.jamailun.stds.server.ConnectedSocket;
import fr.jamailun.stds.server.ServerListener;
import fr.jamailun.stds.server.VirtualServer;

class RoomServer {

	private final VirtualServer virtualServer;
	private final RoomSaved history;

	RoomServer(VirtualServer virtualServer) {
		this.virtualServer = virtualServer;
		history = new RoomSaved(virtualServer.getPort());
		setupRoomListener();
	}

	public String getRoomName() {
		return virtualServer.getServerName();
	}
	public VirtualServer getVirtualServer() {
		return virtualServer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RoomServer that = (RoomServer) o;
		return virtualServer.getPort() == that.getVirtualServer().getPort();
	}

	private void setupRoomListener() {
		virtualServer.registerListener(new ServerListener() {
			@Override
			public void packetReceived(JavaPacket packet, ConnectedSocket connectedSocket) {
				if (packet instanceof ClientSendMessage) {
					System.out.println("Received message from ("+connectedSocket.getClientInfo().getName()+") : " + ((ClientSendMessage)packet).getMessage());
					ServerTransferMessage msg = new ServerTransferMessage(connectedSocket.getClientInfo(), ((ClientSendMessage)packet).getMessage());
					virtualServer.sendPacketToClients(msg);
					history.add(msg);
					return;
				}
				System.out.println("Received packet but unknown type : " + packet + ".");
			}

			@Override
			public void clientDisconnected(ConnectedSocket connectedSocket) {
				virtualServer.sendPacketToClients(new SystemMessage(StaticConfiguration.adapt(connectedSocket.getClientInfo(), StaticConfiguration.LEAVE_ROOM)), connectedSocket, false);
			}

			@Override
			public void clientConnected(ConnectedSocket connectedSocket) {
				virtualServer.sendPacketToClients(new SystemMessage(StaticConfiguration.ENTER_ROOM), connectedSocket, false);
			}

			@Override
			public void virtualRoomShutdowns() {
				history.save();
			}
		});
	}
}