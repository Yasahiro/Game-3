package uk.co.marshmallow_zombies.Game3;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public interface IDrawable {

	BufferedImage getImage();

	void setImage(BufferedImage v);

	void draw(Graphics g, long delta);

};
