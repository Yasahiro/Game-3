package uk.co.marshmallow_zombies.Game3;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import uk.co.marshmallow_zombies.libtiledload.framework.Map;
import uk.co.marshmallow_zombies.rj2dgl.framework.Vector2;

public class Cloud implements IDrawable, IUpdateable {

	private static Random random = new Random();

	private Vector2 position = Vector2.ZERO;
	private BufferedImage image;
	private Map map;

	private float speed = (float) ((random.nextInt(10) + 1) / 10);

	public Cloud(Map map) {
		this.map = map;

		position.x = (float) (map.getSize().getWidth() * map.getTileSize().getWidth());
		position.y = random.nextInt(100) + 50;
	}

	@Override
	public void tick(long delta) {
		position.x -= speed;

		if (position.x < 0 - image.getWidth())
			position.x = (int) (map.getTileSize().getWidth() * map.getTileSize().getWidth());
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}

	@Override
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	public void draw(Graphics g, long delta) {
		// Convert to x and y to int
		int x = (int) position.x;
		int y = (int) position.y;

		// Draw the cloud
		g.drawImage(image, x, y, null);
	}
};
