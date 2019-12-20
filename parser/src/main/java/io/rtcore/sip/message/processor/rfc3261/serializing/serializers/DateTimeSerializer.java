/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZonedDateTime;

import com.google.common.base.Joiner;

/**
 * 
 *
 */
public class DateTimeSerializer extends AbstractRfcSerializer<ZonedDateTime> {

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */

  @Override
  public String serialize(final ZonedDateTime obj) {
    return Joiner.on(" ")
      .join(
        this.toString(obj.getDayOfWeek()) + ",",
        obj.getDayOfMonth(),
        this.toString(obj.getMonth()),
        obj.getYear(),
        String.format("%2d:%2d:%2d", obj.getHour(), obj.getMinute(), obj.getSecond()),
        "GMT");
  }

  private String toString(final Month month) {
    switch (month) {
      case APRIL:
        return "Apr";
      case AUGUST:
        return "Aug";
      case DECEMBER:
        return "Deb";
      case FEBRUARY:
        return "Feb";
      case JANUARY:
        return "Jan";
      case JULY:
        return "Jul";
      case JUNE:
        return "Jun";
      case MARCH:
        return "Mar";
      case MAY:
        return "May";
      case NOVEMBER:
        return "Nov";
      case OCTOBER:
        return "Oct";
      case SEPTEMBER:
        return "Sep";
      default:
        throw new RuntimeException("New Month invented?");

    }
  }

  private String toString(final DayOfWeek dayOfWeek) {
    switch (dayOfWeek) {
      case FRIDAY:
        return "Fri";
      case MONDAY:
        return "Mon";
      case SATURDAY:
        return "Sat";
      case SUNDAY:
        return "Sun";
      case THURSDAY:
        return "Thu";
      case TUESDAY:
        return "Tue";
      case WEDNESDAY:
        return "Wed";
      default:
        throw new RuntimeException("NEw day appears ot have been invented");
    }
  }

}
