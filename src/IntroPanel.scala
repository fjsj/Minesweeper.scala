import javax.swing.JPanel
import javax.imageio.ImageIO
import java.io.File
import java.awt.Graphics

class IntroPanel extends JPanel {

	private val serialVersionUID = 1L 
	private val image = ImageIO.read(new File("img/start2.png")); 

	val imgWidth = image.getWidth()
	val imgHeight = image.getHeight()
	
	override def paintComponent(g: Graphics) {
		g.drawImage(image, 0, 0, null);
	}

}