package uk.co.marshmallow_zombies.Game3;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import uk.co.marshmallow_zombies.libtiledload.framework.BooleanProperty;
import uk.co.marshmallow_zombies.libtiledload.framework.EllipseObject;
import uk.co.marshmallow_zombies.libtiledload.framework.Layer;
import uk.co.marshmallow_zombies.libtiledload.framework.Map;
import uk.co.marshmallow_zombies.libtiledload.framework.MapObject;
import uk.co.marshmallow_zombies.libtiledload.framework.ObjectGroup;
import uk.co.marshmallow_zombies.libtiledload.framework.PolygonObject;
import uk.co.marshmallow_zombies.libtiledload.framework.RectangleObject;
import uk.co.marshmallow_zombies.libtiledload.framework.StringProperty;
import uk.co.marshmallow_zombies.libtiledload.framework.TileObject;
import uk.co.marshmallow_zombies.rj2dgl.framework.Keyboard;
import uk.co.marshmallow_zombies.rj2dgl.framework.KeyboardState;
import uk.co.marshmallow_zombies.rj2dgl.framework.Keys;
import uk.co.marshmallow_zombies.rj2dgl.framework.Vector2;

public class PlayerOld implements IDrawable, IUpdateable {

	private Vector2 position = Vector2.ZERO;
	private Vector2 oldPosition = new Vector2(position.x, position.y);
	private int scale = 32;
	private float movementSpeed = 4;
	private Map map;
	private BufferedImage fullImage, image;
	private KeyboardState oldKeybState;

	private Direction facingDirection = Direction.RIGHT;
	private long lastJump = System.nanoTime();
	private boolean isMoving = false;
	private boolean isJumping = false;
	private boolean isDoubleJumping = false;

	private Game3Old game;

	private boolean gotKey;

	public PlayerOld(Game3Old game) {
		this.game = game;

		try {
			fullImage = ImageIO.read(new File("res/charas/player.png"));
			image = fullImage.getSubimage(32, 64, 32, 32);
		} catch (IOException e) {
		}

		oldKeybState = Keyboard.getState();

		Thread animationThread = new Thread() {
			public void run() {
				int frame = 0;
				while (true) {
					if (isMoving) {
						if (facingDirection == Direction.RIGHT) {
							image = fullImage.getSubimage(32 * frame, 64, 32, 32);
						} else if (facingDirection == Direction.LEFT) {
							image = fullImage.getSubimage(32 * frame, 32, 32, 32);
						}
					} else {
						if (facingDirection == Direction.RIGHT) {
							image = fullImage.getSubimage(32, 64, 32, 32);
						} else if (facingDirection == Direction.LEFT) {
							image = fullImage.getSubimage(32, 32, 32, 32);
						}
					}

					try {
						Thread.sleep(80);
					} catch (InterruptedException e) {
					}
					frame++;
					if (frame > 2)
						frame = 0;
				}
			}
		};

		animationThread.start();
	}

	@Override
	public BufferedImage getImage() {
		return this.image;
	}

	@Override
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void setMap(Map map) {
		this.map = map;
		scale = (int) map.getTileSize().getWidth();

		// Get map part property
		BooleanProperty mapPart = (BooleanProperty) map.getPropertyByName("part");
		if (mapPart == null || !mapPart.getValue())
			gotKey = false;
	}

	private void doCollision() {
		if (!paused) {
			MapObject[] collisionObjects;
			collisionObjects = pollCollision();

			if (collisionObjects.length > 0)
				isJumping = false;

			validateKey(collisionObjects);
		}
	}

	public void tick(long delta) {
		if (!paused) {
			// Get input
			pollInput();
			doCollision();

			// Do gravity
			pollGravity();
			doCollision();

			// Don't go outside borders
			pollBorder();
		}
	}

	private boolean paused = false;

	public void pause() {
		paused = !paused;
	}

	private void validateKey(MapObject[] collisionObjects) {
		for (int i = 0; i < collisionObjects.length; i++) {
			if (collisionObjects[i].getType().equals("key")) {
				gotKey = true;
			} else if (collisionObjects[i].getType().equals("lock")) {
				StringProperty mapTarget = (StringProperty) collisionObjects[i].getPropertyByName("target");

				if (mapTarget != null && gotKey) {
					game.loadMap(new File("res/levels/" + mapTarget.getValue()));
				}
			} else if (collisionObjects[i].getType().equals("portal")) {
				StringProperty mapTarget = (StringProperty) collisionObjects[i].getPropertyByName("target");

				if (mapTarget != null) {
					String[] targetSplit = mapTarget.getValue().split("/");
					game.loadMap(new File("res/levels/" + targetSplit[0]));

					String[] posSplit = targetSplit[1].split(",");
					position.x = Float.valueOf(posSplit[0]) * scale;
					position.y = Float.valueOf(posSplit[1]) * scale;
				}
			}
		}

	}

	public void draw(Graphics g, long delta) {
		g.drawImage(image, (int) position.x, (int) position.y, null);
	}

	private void pollBorder() {
		if (position.x > map.getSize().getWidth() * map.getTileSize().getWidth() - map.getTileSize().getWidth())
			position.x = (float) (map.getSize().getWidth() * map.getTileSize().getWidth() - map.getTileSize()
					.getWidth());
		if (position.x < 0)
			position.x = 0;
	}

	private void pollInput() {
		oldPosition = new Vector2(position.x, position.y);

		KeyboardState keybState = Keyboard.getState();

		if (keybState.isKeyDown(Keys.D)) {
			// Face right
			facingDirection = Direction.RIGHT;

			// Move right
			position.x += movementSpeed;

		}
		if (keybState.isKeyDown(Keys.A)) {
			// Face right
			facingDirection = Direction.LEFT;

			// Move left
			position.x -= movementSpeed;
		}

		isMoving = keybState.isKeyDown(Keys.A) || keybState.isKeyDown(Keys.D);

		if (keybState.isKeyDown(Keys.SPACE) && !oldKeybState.isKeyDown(Keys.SPACE)) {
			if (!isJumping) {
				isJumping = true;
				lastJump = System.nanoTime();
				(new Thread() {
					@Override
					public void run() {
						float oldY = position.y;
						while (position.y > oldY - scale) {
							position.y--;
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
							}
						}
					}
				}).start();
			} else {
				if (!isDoubleJumping && lastJump < System.nanoTime() - 500 * 1000000) {
					isDoubleJumping = true;
					(new Thread() {
						@Override
						public void run() {
							float oldY = position.y;
							while (position.y >= oldY - scale) {
								position.y--;
								try {
									Thread.sleep(5);
								} catch (InterruptedException e) {
								}
							}
						}
					}).start();
				}
			}
		}

		oldKeybState = Keyboard.getState();
	}

	private void pollGravity() {
		if (!isJumping) {
			oldPosition = new Vector2(position.x, position.y);
			position.y += movementSpeed / 2;
		}
	}

	private MapObject[] pollCollision() {
		// Create a rectangle for collision
		final Rectangle playerRectangle = new Rectangle((int) position.x, (int) position.y, scale, scale);

		// Get all layers
		final Layer[] layers = map.getLayers();

		// Create an list
		final List<MapObject> collisionObjects = new ArrayList<MapObject>();
		for (int j = 0; j < layers.length; j++) {
			Layer layer = layers[j];

			if (layer instanceof ObjectGroup) {
				final MapObject[] objects = ((ObjectGroup) layer).getObjects();
				for (int i = 0; i < objects.length; i++) {
					MapObject object = objects[i];

					BooleanProperty collisionProperty = (BooleanProperty) object.getPropertyByName("collision");

					if (object instanceof RectangleObject) {
						if (((RectangleObject) object).getRectangle().intersects(playerRectangle)) {
							if (collisionProperty != null && collisionProperty.getValue()) {
								position = new Vector2(oldPosition.x, oldPosition.y);
								isJumping = false;
								isDoubleJumping = false;
							}
							collisionObjects.add(object);
						}
					}

					if (object instanceof EllipseObject) {
						if (((EllipseObject) object).getEllipse().intersects(playerRectangle)) {
							if (collisionProperty != null && collisionProperty.getValue()) {
								position = new Vector2(oldPosition.x, oldPosition.y);
								isJumping = false;
								isDoubleJumping = false;
							}
							collisionObjects.add(object);
						}
					}

					if (object instanceof PolygonObject) {
						if (((PolygonObject) object).getPolygon().intersects(playerRectangle)) {
							if (collisionProperty != null && collisionProperty.getValue()) {
								position = new Vector2(oldPosition.x, oldPosition.y);
								isJumping = false;
								isDoubleJumping = false;
							}
							collisionObjects.add(object);
						}
					}
					if (object instanceof TileObject) {
						Rectangle objectRectangle = new Rectangle(object.getX(), object.getY(), scale, scale);
						if (objectRectangle.intersects(playerRectangle)) {
							if (collisionProperty != null && collisionProperty.getValue()) {
								position = new Vector2(oldPosition.x, oldPosition.y);
								isJumping = false;
								isDoubleJumping = false;
							}
							collisionObjects.add(object);
						}
					}
				}
			}
		}

		return collisionObjects.toArray(new MapObject[collisionObjects.size()]);
	}

	/**
	 * Gets the player's position.
	 */
	public Vector2 getPosition() {
		return position;
	}

	/**
	 * Sets the player's position.
	 * 
	 * @param position
	 *            The position.
	 */
	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public boolean hasKey() {
		return gotKey;
	}
};
