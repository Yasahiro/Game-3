package uk.co.marshmallow_zombies.Game3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import uk.co.marshmallow_zombies.libtiledload.framework.Layer;
import uk.co.marshmallow_zombies.libtiledload.framework.Map;
import uk.co.marshmallow_zombies.libtiledload.framework.MapObject;
import uk.co.marshmallow_zombies.libtiledload.framework.ObjectGroup;
import uk.co.marshmallow_zombies.libtiledload.framework.RectangleObject;
import uk.co.marshmallow_zombies.libtiledload.framework.StringProperty;
import uk.co.marshmallow_zombies.libtiledload.framework.Tile;
import uk.co.marshmallow_zombies.libtiledload.framework.TileLayer;
import uk.co.marshmallow_zombies.libtiledload.framework.TileObject;
import uk.co.marshmallow_zombies.rj2dgl.framework.Game;
import uk.co.marshmallow_zombies.rj2dgl.framework.Keyboard;
import uk.co.marshmallow_zombies.rj2dgl.framework.KeyboardState;
import uk.co.marshmallow_zombies.rj2dgl.framework.Keys;
import uk.co.marshmallow_zombies.rj2dgl.framework.Vector2;

public class Game3Old extends Game {

	private Map map;
	// private int offset;
	private int level = 1;
	private BufferedImage keyImage = null;
	private BufferedImage lockImage = null;
	private BufferedImage unlockImage = null;
	private BufferedImage skyImage = null;

	// In-game objects
	private PlayerOld player;
	private Cloud[] clouds = new Cloud[5];

	public Game3Old() {
	}

	@Override
	protected void init() {
		player = new PlayerOld(this);

		loadMap(new File("res/levels/level" + level));
		BufferedImage cloudImage = null;
		try {
			cloudImage = ImageIO.read(new File("res/generic/cloud" + (new Random().nextInt(2) + 1) + ".png"));
		} catch (IOException e) {
		}

		for (int i = 0; i < clouds.length; i++) {
			// Initialise a new cloud
			clouds[i] = new Cloud(map);
			clouds[i].setImage(cloudImage);
		}

		try {
			keyImage = ImageIO.read(new File("res/items/key.png"));
			lockImage = ImageIO.read(new File("res/items/lock.png"));
			unlockImage = ImageIO.read(new File("res/items/unlock.png"));
			skyImage = ImageIO.read(new File("res/generic/sky.png"));
		} catch (IOException e) {
		}

		super.init();
		screen.setSize(25 * (int) map.getTileSize().getWidth(), 15 * (int) map.getTileSize().getHeight());
	}

	@Override
	protected void start() {
		super.start();
	}

	@Override
	protected void stop() {
		super.stop();

		System.exit(0);
	}

	@Override
	protected void tick(long delta) {
		KeyboardState keybState = Keyboard.getState();

		if (keybState.isKeyDown(Keys.ESCAPE))
			exit();

		if (keybState.isKeyDown(Keys.D1)) {
			level = 1;
			loadMap(new File("res/levels/level" + level));
		}
		if (keybState.isKeyDown(Keys.D2)) {
			level = 2;
			loadMap(new File("res/levels/level" + level));
		}
		if (keybState.isKeyDown(Keys.D3)) {
			level = 3;
			loadMap(new File("res/levels/level" + level));
		}

		for (int i = 0; i < clouds.length; i++) {
			// Tick clouds
			clouds[i].tick(delta);
		}

		// Tick player
		player.tick(delta);

		super.tick(delta);
	}

	@Override
	protected void render(Graphics g, long delta) {
		screen.clear(Color.BLACK);

		g.drawImage(skyImage, 0, 0, null);

		for (int i = 0; i < clouds.length; i++) {
			// Render clouds
			clouds[i].draw(g, delta);
		}

		drawMap(g);
		player.draw(g, delta);

		super.render(g, delta);
	}

	public void loadMap(File mapFile) {
		player.pause();
		
		map = Map.load(mapFile);

		StringProperty startProperty = (StringProperty) map.getPropertyByName("start");
		if (startProperty != null) {
			String startPoint = startProperty.getValue();

			String[] pointSplit = startPoint.split(",");

			int x = Integer.valueOf(pointSplit[0]);
			int y = Integer.valueOf(pointSplit[1]);

			player.setPosition(new Vector2(x * (int) map.getTileSize().getWidth(), y
					* (int) map.getTileSize().getHeight()));
		}

		player.setMap(map);
		
		player.pause();
	}

	private void drawMap(Graphics g) {
		// Get all layers
		Layer[] layers = map.getLayers();

		for (int j = 0; j < layers.length; j++) {
			Layer layer = layers[j];

			// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
			// layer.getOpacity()));
			// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			// RenderingHints.VALUE_ANTIALIAS_ON);

			if (layer instanceof TileLayer) {
				// We are working with a normal <layer>
				drawTileLayer(g, (TileLayer) layer);
			} else if (layer instanceof ObjectGroup) {
				// We are working with an <objectgroup>
				drawObjectLayer(g, (ObjectGroup) layer);
			}
		}
	}

	private void drawObjectLayer(Graphics g, ObjectGroup layer) {
		// Get the Graphics2D object
		Graphics2D g2d = (Graphics2D) g;

		// Get all objects
		MapObject[] objects = layer.getObjects();
		int w = objects.length;

		// Iterate through the objects
		for (int i = 0; i < w; i++) {
			MapObject object = objects[i];
			// We need to be working with just a TileObject
			if (object instanceof TileObject) {
				try {
					// Convert tile byte data to image
					BufferedImage image = ImageIO.read(new ByteArrayInputStream(((TileObject) object).getData()));

					// Draw the tile
					g2d.drawImage(image, object.getX(), object.getY() - (int) map.getTileSize().getHeight(), null);
				} catch (Exception e) {
				}
			}

			if (object instanceof RectangleObject) {
				if (object.getType().equals("key")) {
					// Draw the key, if the player hasn't picked it up
					if (!player.hasKey())
						g2d.drawImage(keyImage, object.getX(), object.getY(), null);
				}
				if (object.getType().equals("lock")) {
					// Draw the lock
					g2d.drawImage(player.hasKey() ? unlockImage : lockImage, object.getX(), object.getY(), null);
				}
			}
		}
	}

	private void drawTileLayer(Graphics g, TileLayer layer) {
		// Get the Graphics2D object
		Graphics2D g2d = (Graphics2D) g;

		// Get all tiles
		Tile[] tiles = layer.getTiles();
		int w = tiles.length;

		// Iterate through the tiles
		for (int i = 0; i < w; i++) {
			if (tiles[i].equals(Tile.EMPTY))
				// Skip if it's an empty tile
				continue;

			// Get tile size
			Dimension tileSize = map.getTileSize();
			int tileWidth = (int) tileSize.getWidth();
			int tileHeight = (int) tileSize.getHeight();

			// Get layer size
			Dimension layerSize = layer.getSize();
			int layerWidth = (int) layerSize.getWidth();

			// Get x-coordinate and y-coordinate from index
			int x = i % layerWidth * tileWidth;
			int y = i / layerWidth * tileHeight;

			try {
				// Convert tile byte data to image
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(tiles[i].getData()));

				// Draw the tile
				g2d.drawImage(image, x, y, null);
			} catch (Exception e) {
			}
		}
	}
};
