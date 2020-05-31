package GUI.Schedule;

import java.time.*;
import java.time.format.DateTimeFormatter;

//import static Database.Actions.Appointment.FORMAT;

public class AppointmentObject {
    private int appointmentId;
    private String type;
    private String start;
    private String end;
    private String userName;
    private String customer;
    private ZoneId timeZone = ZoneId.systemDefault();
    public static DateTimeFormatter DISPLAYFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentObject(int appointmentId, String type, LocalDateTime start, LocalDateTime end, String userName, String customer) {
        this.appointmentId = appointmentId;
        this.type = type;
        this.userName = userName;
        this.customer = customer;

        OffsetDateTime startOffset = start.atOffset(ZoneOffset.UTC);
        OffsetDateTime endOffset = end.atOffset(ZoneOffset.UTC);

        //this.start = startOffset.atZoneSameInstant(timeZone).format(FORMAT);
        //this.end = endOffset.atZoneSameInstant(timeZone).format(FORMAT);
        this.start = startOffset.atZoneSameInstant(timeZone).format(DISPLAYFORMAT);
        this.end = endOffset.atZoneSameInstant(timeZone).format(DISPLAYFORMAT);

    }

    public int getAppointmentId() {
        return this.appointmentId;
    }

    public String getType() {
        return this.type;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getCustomer() {
        return this.customer;
    }
}
