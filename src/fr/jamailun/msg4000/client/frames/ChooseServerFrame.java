package fr.jamailun.msg4000.client.frames;

import fr.jamailun.msg4000.client.MessagingClient;
import fr.jamailun.msg4000.client.frames.panels.ServerPanel;
import fr.jamailun.stds.common.ServerInfo;
import fr.jamailun.stds.common.ServerList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ChooseServerFrame extends JFrame {

	private final static Font h1 = new Font("Arial", Font.BOLD, 40);
	private final static Font h3 = new Font("Arial", Font.PLAIN, 20);

	private final static int width = 800, height = 600;

	private ServerList list;
	private final MessagingClient client;
	private List<ServerPanel> connPanels = new ArrayList<>();
	private List<JButton> connButtons = new ArrayList<>();
	private final static int ORIGINAL_Y = 70;
	private final JPanel panel;
	private final String futureUsername;

	public ChooseServerFrame(MessagingClient client, ServerList list, String name) {
		super("MESSAGING4000 - Choix du serveur - ("+name+")");
		this.futureUsername = name;
		this.list = list;
		this.client = client;
		setLayout(null);
		super.setLocationRelativeTo(null);
		setSize(width, height);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		panel = new JPanel();
		panel.setBounds(0,0, width, height);
		panel.setLayout(null);
		panel.setBackground(new Color(64, 61, 110));
		add(panel);
		JLabel title = new JLabel("MESSAGING 4000");
		title.setForeground(new Color(255, 33, 48));
		title.setFont(new Font(Font.DIALOG, Font.BOLD, 40));
		title.setBounds(210, 10, 500, 41);
		panel.add(title);
		placeServersPanels();
		JButton refresh = new JButton("Rafraîchir");
		refresh.setFont(h3);
		refresh.setEnabled(true);
		refresh.setVisible(true);
		refresh.setBackground(new Color(0x4FAF19));
		refresh.addActionListener(e -> refresh());
		refresh.setForeground(Color.BLACK);
		refresh.setBounds(400-75, 500, 75*2, 50);
		panel.add(refresh);
		repaint();
	}

	private ActionListener getActionListener() {
		return action -> {
			try {
				int port = Integer.parseInt(action.getActionCommand());
				if ( client.tryConnect(port) ) {
					dispose();
					client.tryRename(futureUsername);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		};
	}

	private void refresh() {
		connPanels.forEach(p-> {
			p.setVisible(false);
			p.setEnabled(false);
			panel.remove(p);
		});
		connButtons.forEach(b-> {
			b.setVisible(false);
			b.setEnabled(false);
			panel.remove(b);
		});
		revalidate();
		repaint();
		connPanels.clear();
		connButtons.clear();
		list = client.refreshServerList();
		placeServersPanels();
	}

	private void placeServersPanels() {
		int y = ORIGINAL_Y;
		for(ServerInfo server : list.getServers()) {
			JButton button = new JButton(">");
			button.setFont(h1);
			button.setEnabled(true);
			button.setVisible(true);
			button.setBackground(new Color(0x4FAF19));
			button.setActionCommand(""+server.getPort());
			button.addActionListener(getActionListener());
			button.setForeground(Color.BLACK);
			button.setBounds(2, y, 60, 40);
			ServerPanel sp = new ServerPanel(server);
			sp.setBounds(6 + button.getWidth(), y,width -24 -button.getWidth(), 40);
			panel.add(sp);
			panel.add(button);
			connPanels.add(sp);
			connButtons.add(button);
			sp.replaceLabels();
			y += 45;
		}
		repaint();
	}

}