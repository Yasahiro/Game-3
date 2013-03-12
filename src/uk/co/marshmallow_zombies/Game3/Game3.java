package uk.co.marshmallow_zombies.Game3;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import uk.co.marshmallow_zombies.rj2dgl.framework.Game;
import uk.co.marshmallow_zombies.rj2dgl.framework.Keyboard;
import uk.co.marshmallow_zombies.rj2dgl.framework.Keys;
import uk.co.marshmallow_zombies.tmxloader.Layer;
import uk.co.marshmallow_zombies.tmxloader.Map;
import uk.co.marshmallow_zombies.tmxloader.MapObject;
import uk.co.marshmallow_zombies.tmxloader.ObjectGroup;
import uk.co.marshmallow_zombies.tmxloader.Tile;
import uk.co.marshmallow_zombies.tmxloader.TileLayer;

public class Game3 extends Game {

	private Map map;
	// private int offset;
	private Player player;

	public Game3() {

	}

	@Override
	protected void init() {
		map = Map.load(new File("res/levels/level1"));
		player = new Player(map);

		super.init();
		screen.setSize(25 * map.getTileWidth(), 15 * map.getTileHeight());
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
		player.pollInput();
		player.pollPosition();

		if (Keyboard.isKeyDown(Keys.ESCAPE))
			exit();

		super.tick(delta);
	}

	@Override
	protected void render(Graphics g, long delta) {
		screen.clear(map.getBackgroundColor());

		drawMap(g, 0, 0);

		player.draw(g);

		super.render(g, delta);
	}

	private void drawMap(Graphics g, int offsetX, int offsetY) {
		Graphics2D g2d = (Graphics2D) g;

		Layer[] layers = map.getLayers();

		for (int j = 0; j < layers.length; j++) {
			Layer layer = layers[j];

			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, layer.getOpacity()));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if (layer instanceof TileLayer) {
				Tile[] tiles = ((TileLayer) layer).getTiles();
				int w = tiles.length;

				for (int i = 0; i < w; i++) {
					if (tiles[i].equals(Tile.EMPTY))
						continue;

					int x = i % map.getWidth() * map.getTileWidth() + offsetX;
					int y = i / map.getWidth() * map.getTileHeight() + offsetY;
					BufferedImage image = tiles[i].getImage();

					g2d.drawImage(image, x, y, null);
				}
			} else if (layer instanceof ObjectGroup) {
				MapObject[] objects = ((ObjectGroup) layer).getObjects();
				int w = objects.length;

				for (int i = 0; i < w; i++) {
					MapObject object = objects[i];

					if (object.hasGID()) {
						int x = object.getX();
						int y = object.getY() - map.getTileHeight();

						BufferedImage image = object.getImage();

						g2d.drawImage(image, x, y, null);
					}
				}
			}
		}
	}
};
