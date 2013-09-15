package overtime.persist

import scala.io.Source
import java.io._
import javax.swing.JOptionPane

/**
 * @author Igo
 */
object PersistenceManager {
  def writeToFile(path: String, text: String) {
    if (!new File(path).exists()) {
      if (!new File(path).createNewFile()) {
        JOptionPane.showMessageDialog(null, "Could not create file for settings: " + path, "Error", JOptionPane.ERROR_MESSAGE)
        throw new IllegalStateException("Could not create file for settings: " + path)
      }
    }
    val writer: BufferedWriter = new BufferedWriter(new FileWriter(path))
    writer.write(text)
    writer.flush()
    writer.close()
  }

  def readFile(file: String): String = {
    Source.fromFile(file).getLines().mkString("\n")
  }
}
