package fr.jamailun.msg4000.client.frames.panels;

import fr.jamailun.stds.common.ServerInfo;

import javax.swing.*;
import java.awt.*;

public class ServerPanel extends JPanel {

	private final static Color bg = new Color(35, 47, 155);

	private final JLabel name, conn;

	public ServerPanel(ServerInfo serverInfo) {
		setLayout(null);
		setBorder(BorderFactory.createLineBorder(Color.YELLOW,2));
		setBackground(bg);

		name = new JLabel(serverInfo.getName());
		name.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
		name.setForeground(Color.RED);
		name.setBackground(bg);
		add(name);

		conn = new JLabel(serverInfo.getConnected() + "/" + serverInfo.getMaxConnected() + " connectés");
		conn.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
		conn.setForeground(Color.WHITE);
		conn.setBackground(bg);
		add(conn);
	}

	public void replaceLabels() {
		name.setBounds(10,3, getWidth()-20, getHeight()/2);
		conn.setBounds(getWidth()-180,getHeight()/2-3,250, getHeight()/2);
	}

}