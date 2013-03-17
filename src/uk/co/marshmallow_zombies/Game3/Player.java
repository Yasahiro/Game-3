package uk.co.marshmallow_zombies.Game3;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import uk.co.marshmallow_zombies.Game3.level.Level;
import uk.co.marshmallow_zombies.rj2dgl.framework.Vector2;
import uk.co.marshmallow_zombies.rj2dgl.physics.Rigidbody;

/**
 * Represents a player.
 * 
 * @author Oliver Davenport
 */
public class Player extends Rigidbody implements IDrawable, IUpdateable {

	private BufferedImage image = null; // Drawing image
	private BufferedImage imageSet = null; // Full image

	private Level level; // Parent level
	private Vector2 oldPosition; // Old position, for collision stuff

	public Player(Level level) {
		this.setMass(1f);
		this.level = level;

		try {
			imageSet = ImageIO.read(new File("res/charas/player.png"));
			image = imageSet.getSubimage(32, 64, 32, 32);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}

	@Override
	public void setImage(BufferedImage v) {
		this.image = v;
	}

	@Override
	public void tick(long delta) {
		oldPosition = getPosition().clone();

		// Physics!
		doGravity();
		doRigidbodyCollision(); // Collides with another object?
		doBorderCollision(); // Collides with the map edge?
	}

	private void doRigidbodyCollision() {
		// Get all rigidbodies and iterate through them
		Rigidbody[] rigidbodies = level.getRigidbodies();
		for (int i = 0; i < rigidbodies.length; i++) {
			if (rigidbodies[i] == this)
				// Skip this body. It's the player
				continue;

			// Check if we are colliding with one
			if (collidesWith(rigidbodies[i])) {
				// If we are, reset the position
				setPosition(oldPosition);
			}
		}
	}

	private void doBorderCollision() {
		if (getPosition().x >= Game3.SCREEN_WIDTH - level.getMap().getTileSize().getWidth()) {
			// Set position
			int x = Game3.SCREEN_WIDTH - (int) level.getMap().getTileSize().getWidth();
			getPosition().x = x;
		}

		if (getPosition().x <= 0) {
			// Set position
			getPosition().x = 0;
		}
	}

	@Override
	public void draw(Graphics g, long delta) {
		g.drawImage(image, (int) getPosition().x, (int) getPosition().y, null);
	}

};
