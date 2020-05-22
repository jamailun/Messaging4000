package fr.jamailun.msg4000.client.frames;

import fr.jamailun.msg4000.client.MessagingClient;

import javax.swing.*;
import java.awt.*;

public class ConnectFrame extends JFrame {
	private final static Font fontN = new Font(Font.DIALOG_INPUT, Font.PLAIN, 15);
	private final static Font fontA = new Font("Arial", Font.BOLD, 23);

	private final JTextField addressF, nameF;

	private final SavedPreferences prefs;
	public ConnectFrame(SavedPreferences prefs) {
		super("MESSAGING4000 - Connexion");
		this.prefs = prefs;
		setLayout(null);
		super.setLocationRelativeTo(null);
		setSize(800, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		JPanel panel = new JPanel();
		panel.setBounds(0,0,800,600);
		panel.setBackground(new Color(168, 168, 168));
		panel.setLayout(null);

		JLabel title = new JLabel("MESSAGING 4000");
		title.setForeground(new Color(255, 33, 48));
		title.setFont(new Font(Font.DIALOG, Font.BOLD, 40));
		title.setBounds(210, 10, 500, 41);
		panel.add(title);

		JLabel addressL = new JLabel("Adresse du serveur :");
		addressL.setForeground(Color.BLACK);
		addressL.setFont(fontA);
		addressL.setBounds(80, 120, 400, 24);
		panel.add(addressL);

		JLabel nameL = new JLabel("Pseudo demandé :");
		nameL.setForeground(Color.BLACK);
		nameL.setFont(fontA);
		nameL.setBounds(80, 200, 400, 20);
		panel.add(nameL);

		addressF = new JTextField(prefs.getPreference(SavedPreferences.KEY_ADDRESS));
		addressF.setBounds(80, 150, 640, 34);
		addressF.setForeground(Color.BLACK);
		addressF.setBackground(Color.WHITE);
		addressF.setFont(fontN);
		panel.add(addressF);

		nameF = new JTextField(prefs.getPreference(SavedPreferences.KEY_USERNAME));
		nameF.setBounds(80, 230, 640, 34);
		nameF.setForeground(Color.BLACK);
		nameF.setBackground(Color.WHITE);
		nameF.setFont(fontN);
		panel.add(nameF);

		JButton validate = new JButton("Connexion");
		validate.setBounds(350, 300, 100, 40);
		validate.setForeground(Color.BLACK);
		validate.setBackground(new Color(41, 203, 71));
		validate.addActionListener(e -> {
			if(canPress) {
				canPress = false;
				tryConnect();
			}
		});
		validate.setVisible(true);
		validate.setEnabled(true);
		panel.add(validate);

		add(panel);
		repaint();
	}

	private boolean canPress = true;
	private void tryConnect() {
		String[] parts = addressF.getText().split(":");
		if(parts.length < 2) {
			wrongAddressFormat();
			return;
		}
		if(nameF.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Choisissez un pseudo !", "Pseudo absent", JOptionPane.ERROR_MESSAGE);
			canPress = true;
			return;
		}
		String address = parts[0];
		int port;
		try {
			port = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			wrongAddressFormat();
			return;
		}
		MessagingClient messagingClient = new MessagingClient(address, port, nameF.getText());
		if(messagingClient.isValid()) {
			dispose();
			prefs.changePreference(SavedPreferences.KEY_ADDRESS, addressF.getText());
			prefs.changePreference(SavedPreferences.KEY_USERNAME, nameF.getText());
		} else {
			JOptionPane.showMessageDialog(null, "Impossible de se connecter à cette adresse !", "Serveur introuvable", JOptionPane.ERROR_MESSAGE);
			canPress = true;
		}
	}

	private void wrongAddressFormat() {
		JOptionPane.showMessageDialog(null, "Le format est : <ip>:<port> !", "Addresse incorrecte", JOptionPane.ERROR_MESSAGE);
		canPress = true;
	}

	public static void main(String[] a) {
		SavedPreferences prefs = new SavedPreferences();
		new ConnectFrame(prefs);
	}
}
