package nl.siegmann.epublib.viewer;

import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;


public class ViewerUtil {
	
	private static Logger log = Logger.getLogger(ViewerUtil.class);

	/**
	 * Creates a button with the given icon. The icon will be loaded from the classpath.
	 * If loading the icon is unsuccessful it will use the defaultLabel.
	 * 
	 * @param iconName
	 * @param backupLabel
	 * @return a button with the given icon.
	 */
	// package
	static JButton createButton(String iconName, String backupLabel) {
		JButton result = null;
		ImageIcon icon = createImageIcon(iconName);
		if (icon == null) {
			result = new JButton(backupLabel);
		} else {
			result = new JButton(icon);
		}
		return result;
	}

	
	static ImageIcon createImageIcon(String iconName) {
		ImageIcon result = null;
		String fullIconPath = "/viewer/icons/" + iconName + ".png";
		try {
			Image image = ImageIO.read(ViewerUtil.class.getResourceAsStream(fullIconPath));
			result = new ImageIcon(image);
		} catch(Exception e) {
			log.severe("Icon \'" + fullIconPath + "\' not found");
		}
		return result;
	}
}
