package uk.co.marshmallow_zombies.Game3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import uk.co.marshmallow_zombies.rj2dgl.framework.Keyboard;
import uk.co.marshmallow_zombies.rj2dgl.framework.Keys;
import uk.co.marshmallow_zombies.tmxloader.Map;
import uk.co.marshmallow_zombies.tmxloader.MapObject;

public class Player {

	private Map map = null;
	private Color color = Color.RED;
	private int x = 0, y = 10 * 32;
	private int speed = 4;
	private String signtext = "";
	private int signx, signy;

	public Player(Map map) {
		this.map = map;
	}

	public void pollInput() {
		color = Color.RED;

		if (Keyboard.isKeyDown(Keys.SPACE)) {
			color = Color.BLUE;
		}
		if (Keyboard.isKeyDown(Keys.D)) {
			x += speed;
		}
		if (Keyboard.isKeyDown(Keys.A)) {
			x -= speed;
		}

		if (Keyboard.isKeyDown(Keys.W)) {
			y -= speed;
		}
		if (Keyboard.isKeyDown(Keys.S)) {
			y += speed;
		}
	}

	public void pollPosition() {
		Rectangle rect = new Rectangle(x, y + 5, 32, 32);

		MapObject[] objects = map.getCollisionObject(rect);
		signtext = "";

		for (MapObject object : objects) {
			if (object == null) {
				return;
			}

			if (object.getType().equals("sign")) {
				signtext = object.getPropertyString("content");
				signx = object.getX();
				signy = object.getY() - 40;
			}
		}
	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x, y, 32, 32);
		
		if(!signtext.isEmpty()) {
			Font font = new Font("Arial", Font.PLAIN, 16);
			Dimension size = measureFont(g, font, signtext);

			g.setColor(Color.WHITE);
			g.fillRoundRect(signx - (int)size.getWidth() / 2 - 15, signy - (int)size.getHeight() / 2 - 15, (int)size.getWidth() + 35, (int)size.getHeight() + 15, 32, 32);
			g.setColor(Color.BLACK);
			g.drawRoundRect(signx - (int)size.getWidth() / 2 - 16, signy - (int)size.getHeight() / 2 - 16, (int)size.getWidth() + 37, (int)size.getHeight() + 17, 32, 32);
			g.setFont(font);
			g.drawString(signtext, signx - (int)size.getWidth() / 2, signy - (int)size.getHeight() / 4);
		}
	}

	private Dimension measureFont(Graphics g, Font font, String text) {
		FontMetrics metrics = g.getFontMetrics(font);
		int hgt = metrics.getHeight();
		int adv = metrics.stringWidth(text);
		Dimension size = new Dimension(adv + 2, hgt + 2);

		return size;
	}

};
