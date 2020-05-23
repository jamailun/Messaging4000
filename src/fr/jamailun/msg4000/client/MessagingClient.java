package fr.jamailun.msg4000.client;

import fr.jamailun.msg4000.client.frames.ChooseServerFrame;
import fr.jamailun.msg4000.client.frames.MessagesFrame;
import fr.jamailun.msg4000.common.packets.ClientSendMessage;
import fr.jamailun.msg4000.common.packets.ServerTransferMessage;
import fr.jamailun.msg4000.common.packets.SystemMessage;
import fr.jamailun.stds.client.ClientListener;
import fr.jamailun.stds.client.JavaClient;
import fr.jamailun.stds.common.JavaPacket;
import fr.jamailun.stds.common.ServerInfo;
import fr.jamailun.stds.common.ServerList;
import fr.jamailun.stds.common.standard.DisconnectionPacket;
import fr.jamailun.stds.common.standard.RenamePacket;
import fr.jamailun.stds.common.standard.ServerResponsePacket;

import javax.swing.*;

public class MessagingClient {

    private MessagesFrame frame;
    private JavaClient client;
    private boolean isValid = false;
    private boolean skipFirstValidAnswer = true;
    public MessagingClient(String address, int port, String name) {
        client = new JavaClient(address, port);
        client.setListener(getListener());
        frame = new MessagesFrame(this);
        if(client.getServerList() == null) {
            frame.dispose();
            return;
        }
        frame.setVisible(false);
        new ChooseServerFrame(this, client.getServerList(), name);
        isValid = true;
    }

    public boolean isValid() {
        return isValid;
    }

    private ClientListener getListener() {
        return new ClientListener() {
            @Override
            public void packetReceived(JavaPacket packet) {
                if(packet instanceof SystemMessage) {
                    String msg = ((SystemMessage) packet).getMessage();
                    JOptionPane.showMessageDialog(null, msg, "Notification serveur.", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if(packet instanceof ServerTransferMessage) {
                    ServerTransferMessage message = ((ServerTransferMessage) packet);
                    //System.out.println(message.getAuthor()+" > "+message.getMessage());
                    frame.addMessage(message.getAuthor(), message.getMessage());
                    return;
                }
                if (packet instanceof ServerResponsePacket) {
                    int res = ((ServerResponsePacket)packet).getAnswer();
                    if(res == ServerResponsePacket.ACCEPTED) {
                        if(skipFirstValidAnswer) {
                            skipFirstValidAnswer = false;
                            return;
                        }
                        JOptionPane.showMessageDialog(null, "Nouveau pseudo valide.", "Succès du rename", JOptionPane.INFORMATION_MESSAGE);
                    }
                    if(res == ServerResponsePacket.NAME_TAKEN) {
                        JOptionPane.showMessageDialog(null, "Impossible d'utiliser ce pseudo car il est déjà pris.", "Echec du rename", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    return;
                }
                System.err.println("Unknown packet : " + packet+".");
            }

            @Override
            public void connectionInitializedWithVS() {
                frame.setVisible(true);
            }

            @Override
            public void serverDisconnected() {
                System.out.println("Server stopped the connection.");
                System.exit(0);
            }

            @Override
            public void serverBroadcastMessage(String s) {
                System.out.println("{BROADCAST} > " + s);
                frame.addBroadcast(s);
            }
        };
    }

    public void shutdown() {
        if( ! client.isConnectedToVirtualServer()) {
            System.out.println("Termination without ending packet : no connection found.");
            System.exit(0);
            return;
        }
        client.getProxySocket().sendPacket(new DisconnectionPacket());
        System.exit(0);
    }

    public void trySendMessage(String message) {
        System.out.println("Send message : " + message);
        if( ! client.isConnectedToVirtualServer()) {
            System.out.println("Termination without ending packet : no connection found.");
            System.exit(0);
            return;
        }
        client.getProxySocket().sendPacket(new ClientSendMessage(message));
    }

    private String roomName = "none", username = "xxx";
    public void tryRename(String name) {
        client.getProxySocket().sendPacket(new RenamePacket(name));
        username = name;
        frame.updateTitle(username, roomName);
    }

    public boolean tryConnect(int port) {
        for(ServerInfo serverInfo : client.getServerList().getServers()) {
            if(serverInfo.getPort() == port) {
                client.startConnectionToVirtualServer(serverInfo);
                roomName = serverInfo.getName();
                frame.updateTitle(username, roomName);
                return true;
            }
        }
        JOptionPane.showMessageDialog(null, "Une erreur est survenue...", "Erreur pour le port : " + port, JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public ServerList refreshServerList() {
        client.refreshServerList();
        return client.getServerList();
    }

}