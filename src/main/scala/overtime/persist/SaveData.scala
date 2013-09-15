package overtime.persist

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * @author Igo
 */
class SaveData extends Serializable {

  var lastDate: Long = 0
  var totalOvertimeHours: Int = 0


  def formatLastDate: String = new DateTime(lastDate).toString("dd. MM. yyyy - EEEE")
}
