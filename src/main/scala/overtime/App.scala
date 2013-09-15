package overtime

import javax.swing._
import net.miginfocom.swing.MigLayout
import java.awt.event.{ActionEvent, ActionListener}
import overtime.persist.{PersistenceManager, SaveData, JacksonWrapper}
import java.io.FileNotFoundException
import java.awt.{Dimension, Font}

/**
 * @author ${user.name}
 */
object App {

  val SETTINGS_PATH = "overtime_tracker_settings.json"

  private val frame = new JFrame("Overtime tracker")
  frame.setSize(300, 300)
  frame.setLocationRelativeTo(null)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  val textWorkedHours = new JTextField("8", 5)
  val lblWorkedHours = new JLabel("Hours worked")
  val btnOk = new JButton("Log worked hours")
  val lblTotalOvertimeHours = new JLabel()
  val lblUsedOvertime = new JLabel("Overtime hours spent")
  val txtUsedOvertime = new JTextField(5)
  val btnUseOvertime = new JButton("Use overtime hours")
  val lblLastUpdate = new JLabel()


  frame.setLayout(new MigLayout("wrap 2"))

  frame.add(lblLastUpdate, "span")
  val sep = new JSeparator(SwingConstants.HORIZONTAL)
  sep.setPreferredSize(new Dimension(frame.getWidth, 3))
  frame.add(sep, "span")

  frame.add(lblWorkedHours)
  frame.add(textWorkedHours)

  frame.add(btnOk, "span")

  frame.add(lblTotalOvertimeHours, "span")

  val sep2 = new JSeparator(SwingConstants.HORIZONTAL)
  sep2.setPreferredSize(new Dimension(frame.getWidth, 3))
  frame.add(sep2, "span")

  frame.add(lblUsedOvertime)
  frame.add(txtUsedOvertime)
  frame.add(btnUseOvertime)

  btnOk.addActionListener(new SaveAction)
  btnOk.setDefaultCapable(true)
  btnUseOvertime.addActionListener(new UseAction())

  updateOvertime(loadSavedData)

  UIManager.put("Label.font", UIManager.getFont("Label.font").deriveFont(Font.PLAIN))
  SwingUtilities.updateComponentTreeUI(frame)
  frame.getRootPane.setDefaultButton(btnOk)

  Thread.setDefaultUncaughtExceptionHandler(
    new Thread.UncaughtExceptionHandler() {
      def uncaughtException(t: Thread, e: Throwable) {
        JOptionPane.showMessageDialog(App.frame, "<html><b>" + e.getClass.getName + "</b><br>" + e.getMessage + "</html>", "Internal error", JOptionPane.ERROR_MESSAGE)
      }
    })

  def main(args: Array[String]) {
    frame.setVisible(true)
  }


  def getOvertime(worked: Int): Int = {
    if (worked < 0) throw new IllegalArgumentException("Worked hours must not be negative number")
    val overtime = worked - 8
    overtime
  }

  private def loadSavedData: SaveData = {
    try {
      return JacksonWrapper.deserialize[SaveData](PersistenceManager.readFile(SETTINGS_PATH))
    } catch {
      case e: FileNotFoundException =>
        return new SaveData()
    }
  }

  def updateOvertime(saveData: SaveData) {
    lblLastUpdate.setText("Last update: " + saveData.formatLastDate)
    val highlight = if (saveData.totalOvertimeHours < 0) "<font color=red>" else "<font>"
    lblTotalOvertimeHours.setText("<html><b>Overtime hours: </b>" + highlight + saveData.totalOvertimeHours.toString + "</font></html>")
  }

  class SaveAction extends ActionListener {
    def actionPerformed(e: ActionEvent) {
      val saveData = loadSavedData

      saveData.lastDate = System.currentTimeMillis()
      saveData.totalOvertimeHours = saveData.totalOvertimeHours + getOvertime(textWorkedHours.getText.toInt)

      PersistenceManager.writeToFile(text = JacksonWrapper.serialize(saveData), path = SETTINGS_PATH)

      updateOvertime(saveData)
      JOptionPane.showMessageDialog(frame, "Updated - total overtime hour = " + saveData.totalOvertimeHours)
    }
  }

  class UseAction extends ActionListener {
    def actionPerformed(e: ActionEvent) {

      val saveData = loadSavedData

      saveData.lastDate = System.currentTimeMillis()
      saveData.totalOvertimeHours = saveData.totalOvertimeHours - txtUsedOvertime.getText.toInt

      PersistenceManager.writeToFile(text = JacksonWrapper.serialize(saveData), path = SETTINGS_PATH)

      updateOvertime(saveData)
      JOptionPane.showMessageDialog(frame, "Updated - total overtime hour = " + saveData.totalOvertimeHours)
    }
  }

}
