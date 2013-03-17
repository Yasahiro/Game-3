package uk.co.marshmallow_zombies.Game3;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Image tools helper which focuses on converting images to and from byte array
 * data.
 * 
 * @author Oliver Davenport
 */
public class ImageTools {

	/**
	 * Converts byte array data into an image.
	 * 
	 * @param data
	 *            The byte array containing image data.
	 */
	public static BufferedImage imageFromByteData(byte[] data) {
		try {
			return ImageIO.read(new ByteArrayInputStream(data));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

};
