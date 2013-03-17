package uk.co.marshmallow_zombies.Game3.level;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import uk.co.marshmallow_zombies.Game3.IDrawable;
import uk.co.marshmallow_zombies.Game3.IUpdateable;
import uk.co.marshmallow_zombies.Game3.ImageTools;
import uk.co.marshmallow_zombies.libtiledload.framework.BooleanProperty;
import uk.co.marshmallow_zombies.libtiledload.framework.Layer;
import uk.co.marshmallow_zombies.libtiledload.framework.Map;
import uk.co.marshmallow_zombies.libtiledload.framework.MapObject;
import uk.co.marshmallow_zombies.libtiledload.framework.ObjectGroup;
import uk.co.marshmallow_zombies.libtiledload.framework.RectangleObject;
import uk.co.marshmallow_zombies.libtiledload.framework.Tile;
import uk.co.marshmallow_zombies.libtiledload.framework.TileLayer;
import uk.co.marshmallow_zombies.rj2dgl.framework.Vector2;
import uk.co.marshmallow_zombies.rj2dgl.physics.Rigidbody;
import uk.co.marshmallow_zombies.rj2dgl.physics.World;

/**
 * Represents a level.
 * 
 * @author Oliver Davenport
 */
public class Level extends World implements IDrawable, IUpdateable {

	private Map map;
	private List<IDrawable> drawables = new ArrayList<IDrawable>();

	public Level() {
		this.setGravity(1f);
	}

	/**
	 * Gets the level's map.
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Sets the level's map.
	 * 
	 * @param v
	 *            The {@code Map} to set.
	 */
	public void setMap(Map v) {
		// Set the class map
		this.map = v;

		// Remove all rigidbodies
		clearRigidbodies();

		// Get all layers and iterate through them
		Layer[] layers = map.getLayers();
		for (int i = 0; i < layers.length; i++) {
			// Get the current layer
			Layer layer = layers[i];

			// Is it an object layer?
			if (layer instanceof ObjectGroup) {
				// Get all objects and iterate through them
				MapObject[] objects = ((ObjectGroup) layer).getObjects();
				for (int o = 0; o < objects.length; o++) {
					// Get the current object
					MapObject object = objects[o];

					// Get the boolean "collision" property
					BooleanProperty collisionProperty = (BooleanProperty) object.getPropertyByName("collision");

					if (collisionProperty != null && collisionProperty.getValue()) {
						// Is it a rectangle object?
						if (object instanceof RectangleObject) {
							// Get the object rectangle
							Rectangle rectangle = ((RectangleObject) object).getRectangle();

							// Get rectangle data
							int x = (int) rectangle.getX();
							int y = (int) rectangle.getY();
							int w = (int) rectangle.getWidth();
							int h = (int) rectangle.getHeight();

							// Create a new rigidbody
							Rigidbody rigidbody = new Rigidbody();
							rigidbody.setPosition(new Vector2(x, y));
							rigidbody.setSize(new Vector2(w, h));

							addRigidbody(rigidbody);
						}
					}
				}
			}
		}
	}

	@Override
	public BufferedImage getImage() {
		return null;
	}

	@Override
	public void setImage(BufferedImage image) {
	}

	@Override
	public void tick(long delta) {
	}

	@Override
	public void draw(Graphics g, long delta) {
		// Draw the map
		drawMap(g);
	}

	/**
	 * Draws the map.
	 */
	private void drawMap(Graphics g) {
		// Get all the layers and iterate through them
		Layer[] layers = map.getLayers();
		for (int i = 0; i < layers.length; i++) {
			// Get the current layer
			Layer layer = layers[i];

			if (layer instanceof TileLayer)
				// Draw the tile layer
				drawTileLayer(g, (TileLayer) layer);
		}
	}

	/**
	 * Draws a tile layer.
	 * 
	 * @param layer
	 *            The {@code TileLayer} to draw.
	 */
	private void drawTileLayer(Graphics g, TileLayer layer) {
		// Get all the tiles and
		Tile[] tiles = layer.getTiles();

		// Get the size of the layer
		int w = tiles.length;

		// Iterate through the tiles
		for (int i = 0; i < w; i++) {
			// Get the current tile
			Tile tile = tiles[i];

			if (tile.equals(Tile.EMPTY))
				// Skip if it's an empty tile
				continue;

			int layerWidth = (int) layer.getSize().getWidth();
			int width = (int) map.getTileSize().getWidth();
			int height = (int) map.getTileSize().getHeight();

			// Get coordinates based on index
			int x = i % layerWidth * width;
			int y = i / layerWidth * height;

			// Get tile image
			BufferedImage image = ImageTools.imageFromByteData(tile.getData());
			g.drawImage(image, x, y, null);
		}
	}

	/**
	 * Adds a drawable to the level.
	 * 
	 * @param drawable
	 *            The {@code IDrawable} to add.
	 */
	public void addDrawable(IDrawable drawable) {
		// Add drawable
		drawables.add(drawable);
	}

	/**
	 * Draw all drawable objects.
	 * 
	 * @param g
	 *            The {@code Graphics} object to draw on.
	 * @param delta
	 *            The time since the last update.
	 */
	public void drawDrawables(Graphics g, long delta) {
		// Iterate through the drawables
		for (int i = 0; i < drawables.size(); i++) {
			drawables.get(i).draw(g, delta);
		}
	}

};
