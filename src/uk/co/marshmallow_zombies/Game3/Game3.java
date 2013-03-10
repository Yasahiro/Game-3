package uk.co.marshmallow_zombies.Game3;

import java.awt.Color;
import java.awt.Graphics;

import uk.co.marshmallow_zombies.rj2dgl.framework.Game;
import uk.co.marshmallow_zombies.rj2dgl.tilesets.Map;

public class Game3 extends Game {

	public static final int GAME_WIDTH = 25 * 32;
	public static final int GAME_HEIGHT = 19 * 32;

	private Map map;

	public Game3() {

	}

	@Override
	protected void init() {
		System.out.println("init()");
		
		map = Map.load("res/levels/level1");

		super.init();
		screen.setSize(GAME_WIDTH, GAME_HEIGHT);
	}

	@Override
	protected void start() {
		System.out.println("start()");

		super.start();
	}

	@Override
	protected void stop() {
		System.out.println("stop()");

		super.stop();
	}

	@Override
	protected void tick(long delta) {
		// TODO update logic

		super.tick(delta);
	}

	@Override
	protected void render(Graphics g, long delta) {
		screen.clear(Color.BLUE);

		drawMap();

		super.render(g, delta);
	}

};
