package uk.co.marshmallow_zombies.Game3;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;

import uk.co.marshmallow_zombies.Game3.level.Level;
import uk.co.marshmallow_zombies.libtiledload.framework.Map;
import uk.co.marshmallow_zombies.rj2dgl.framework.Game;

public class Game3 extends Game {

	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 480;

	private Level level;
	private Player player;

	@Override
	protected void init() {
		// Get first map
		File mapFile = new File("res/levels/level1");
		Map map = Map.load(mapFile);

		// Set the map to the level
		level = new Level();
		level.setMap(map);

		// Load a new player
		player = new Player(level);

		// Add the player to the level as a drawable, and as a rigidbody
		level.addDrawable(player);
		level.addRigidbody(player);

		super.init();
		screen.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
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
		// Update player logic
		player.tick(delta);

		super.tick(delta);
	}

	@Override
	protected void render(Graphics g, long delta) {
		screen.clear(Color.BLACK);

		// Draw the level and its drawables
		level.draw(g, delta);
		level.drawDrawables(g, delta);

		super.render(g, delta);
	}

};
