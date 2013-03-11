package uk.co.marshmallow_zombies.Game3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import uk.co.marshmallow_zombies.rj2dgl.framework.Game;
import uk.co.marshmallow_zombies.rj2dgl.tilesets.Layer;
import uk.co.marshmallow_zombies.rj2dgl.tilesets.Map;
import uk.co.marshmallow_zombies.rj2dgl.tilesets.Tile;
import uk.co.marshmallow_zombies.rj2dgl.tilesets.TileLayer;

public class Game3 extends Game {

	private Map map;

	public Game3() {

	}

	@Override
	protected void init() {
		map = Map.load("res/levels/level1");

		window.setTitle(String.format("%d x %d", map.getWidth(), map.getHeight()));

		super.init();
		screen.setSize(map.getWidth() * map.getTileWidth(), map.getHeight() * map.getTileHeight());
	}

	@Override
	protected void start() {
		super.start();
	}

	@Override
	protected void stop() {
		super.stop();
	}

	@Override
	protected void tick(long delta) {
		// TODO update logic

		super.tick(delta);
	}

	@Override
	protected void render(Graphics g, long delta) {
		screen.clear(map.getBackgroundColor());

		drawMap(g);

		super.render(g, delta);
	}

	private void drawMap(Graphics g) {
		Layer[] layers = map.getLayers();

		for (int j = 0; j < layers.length; j++) {
			Layer layer = layers[j];

			if (layer instanceof TileLayer) {
				Tile[] tiles = ((TileLayer) layer).getTiles();
				int w = tiles.length;

				for (int i = 0; i < w; i++) {
					if (tiles[i].equals(Tile.EMPTY))
						continue;

					int x = i % map.getWidth() * map.getTileWidth();
					int y = i / map.getWidth() * map.getTileHeight();
					BufferedImage image = tiles[i].getImage();
					File file = new File("/home/oliver/Pictures/test/l" + j + "_t" + i);
					try {
						if (!file.exists())
							ImageIO.write(image, "png", file);
					} catch (IOException e) {
					}

					g.drawImage(image, x, y, null);

					g.setFont(new Font("Arial", Font.PLAIN, 10));
					g.setColor(Color.BLUE);
					g.drawString(String.format("%d", j + 1), x, y + 16);
				}
				System.out.printf("Layer %d\n", j + 1);
			}
		}
	}
};
