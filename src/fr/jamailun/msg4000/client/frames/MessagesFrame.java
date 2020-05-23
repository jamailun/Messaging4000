package fr.jamailun.msg4000.client.frames;

import fr.jamailun.msg4000.client.frames.panels.Message;
import fr.jamailun.msg4000.client.MessagingClient;
import fr.jamailun.msg4000.common.StaticConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MessagesFrame extends JFrame implements KeyListener {

	private JPanel displayPanel;
	private JTextArea textArea;
	public static final Color backgroundColor = Color.LIGHT_GRAY;

	private List<Message> messages = new ArrayList<>();

	private final MessagingClient client;
	public MessagesFrame(MessagingClient client) {
		super("MESSAGING4000 - client");
		super.setLayout(null);
		super.setLocationRelativeTo(null);
		this.client = client;
		super.setPreferredSize(new Dimension(800, 800));
		super.setSize(800, 600);
		super.setResizable(true);
		super.setVisible(false);
		super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				client.shutdown();
			}
		});
		displayPanel = new JPanel();
		displayPanel.setLayout(null);
		displayPanel.setBounds(0,0, getWidth(), 480);
		displayPanel.setBackground(backgroundColor);
		super.add(displayPanel);

		textArea = new JTextArea();
		textArea.setLayout(null);
		textArea.setBackground(Color.LIGHT_GRAY);
		textArea.setBounds(1,481,782,79);
		textArea.setFont(new Font("Arial", Font.PLAIN, 14));
		textArea.setEnabled(true);
		textArea.addKeyListener(this);
		super.add(textArea);
		repaint();
		Message.g = getGraphics();
	}

	public void addMessage(String author, String message) {
		createMessageBox(new Message(author, message));
	}

	public void addBroadcast(String message) {
		createMessageBox(new Message(message));
	}

	private void createMessageBox(Message box) {
		//box.setBorder(BorderFactory.createLineBorder(new Color(new Random().nextInt(255),new Random().nextInt(255),new Random().nextInt(255))));
		messages.add(box);
		if(messages.size() > StaticConfiguration.MAX_MESSAGES) {
			messages.get(0).setVisible(false);
			messages.get(0).setEnabled(false);
			displayPanel.remove(messages.get(0));
			messages.remove(0);
		}
		for(int i = 0; i < messages.size(); i++)
			messages.get(i).setBounds(2, (Message.partHeight+1) * (i), getWidth(), Message.partHeight);
		//box.setBounds(0, (Message.partHeight+1) * (messages.size()), getWidth(), Message.partHeight);
		displayPanel.add(box);
		repaint();
	}

	public void sendMessage() {
		String msg = textArea.getText();
		msg = msg.replaceAll("\n", "");
		if(msg.isEmpty())
			return;
		if(msg.equals("/stop")) {
			setVisible(false);
			client.shutdown();
			return;
		}
		String[] words = msg.split(" ");
		if(words[0].equals("/rename")) {
			if(words.length < 2) {
				JOptionPane.showMessageDialog(this, "Il faut préciser un nouveau pseudo !.", "Format de commande incorrect.", JOptionPane.ERROR_MESSAGE);
				textArea.setText("");
				return;
			}
			client.tryRename(words[1]);
			textArea.setText("");
			return;
		}
		client.trySendMessage(msg);
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		textArea.setText("");
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {}

	private boolean shift = false;
	@Override
	public void keyPressed(KeyEvent keyEvent) {
		if(keyEvent.getKeyCode() == KeyEvent.VK_SHIFT)
			shift = true;
		if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER && !shift)
			sendMessage();
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		if(keyEvent.getKeyCode() == KeyEvent.VK_SHIFT)
			shift = false;
	}

	public void updateTitle(String name, String room) {
		setTitle("MESSAGING4000 - " + name + " : [" + room + "]");
	}
}