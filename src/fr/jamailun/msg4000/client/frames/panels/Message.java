package fr.jamailun.msg4000.client.frames.panels;

import fr.jamailun.msg4000.client.frames.MessagesFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class Message extends JPanel {
	private final static Font font = new Font("Arial",Font.PLAIN,13);
	public final static int partHeight = 15;
	private final static int space = 3;
	public static Graphics g;
	public Message(String author, String message) {
		super();
		setFont(font);
		setLayout(null);
		setBackground(MessagesFrame.backgroundColor);
		addPart(author, Color.BLUE);
		addPart(">", Color.BLACK);
		addPart(message, Color.BLACK);
	}

	private int currentX = -space;
	public void addPart(String content, Color color) {
		currentX += space;
		JLabel label = new JLabel(content);
		label.setFont(font);
		label.setBackground(MessagesFrame.backgroundColor);
		int width = getWidthOfText(content);
		label.setBounds(currentX, 0, width, partHeight);
		label.setForeground(color);
		add(label);
		//label.setBorder(BorderFactory.createLineBorder(Color.RED));
		currentX += label.getSize().getWidth();
	}

	private int getWidthOfText(String text) {
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);
		//int textheight = (int)(font.getStringBounds(text, frc).getHeight());
		return (int)(font.getStringBounds(text, frc).getWidth()) + 2;
	}

	public Message(String broadcast) {
		super();
		addPart("[!]", Color.RED);
		addPart(">>", Color.RED);
		addPart(broadcast, Color.RED);
	}

	public int getCurrentWidth() {
		return Math.max(currentX + 10, 0);
	}

}