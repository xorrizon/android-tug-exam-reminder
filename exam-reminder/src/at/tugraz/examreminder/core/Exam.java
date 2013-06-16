package at.tugraz.examreminder.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Exam implements Comparable<Exam> {
    public static transient String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    public transient Course course;
	private GregorianCalendar from;
	private GregorianCalendar to;
	public String place;
	public String term;
	public String lecturer;
	public String examinar;
	public GregorianCalendar registerDeadline;
	public GregorianCalendar cancelDeadline;
	public int participants;
	public int participants_max;
	public GregorianCalendar updated_at;
    public long event_id;

    public Exam(Course course){
        this.course = course;
        setFrom(new GregorianCalendar());
        setTo(new GregorianCalendar());
        cancelDeadline = new GregorianCalendar();
        registerDeadline = new GregorianCalendar();
        updated_at = new GregorianCalendar();
        event_id = -1;
    }

    public String getFromFormated() {
        return new SimpleDateFormat(DATE_FORMAT).format(getFrom().getTime());
    }

    public String getToFormated() {
        return new SimpleDateFormat(DATE_FORMAT).format(getTo().getTime());
    }

    public String getRegisterDeadline() {
        return new SimpleDateFormat(DATE_FORMAT).format(registerDeadline.getTime());
    }

    public String getCancelDeadline() {
        return new SimpleDateFormat(DATE_FORMAT).format(cancelDeadline.getTime());
    }

    @Override
    public int compareTo(Exam exam) {
        return getFrom().compareTo(exam.getFrom());
    }


    @Override
    public Exam clone() {
        Exam exam = new Exam(course);
        exam.setFrom(getFrom());
        exam.setTo(getTo());
        exam.place = place;
        exam.term = term;
        exam.lecturer = lecturer;
        exam.examinar = examinar;
        exam.registerDeadline = registerDeadline;
        exam.cancelDeadline = cancelDeadline;
        exam.participants = participants;
        exam.participants_max = participants_max;
        exam.updated_at = updated_at;
        return exam;
    }

    public GregorianCalendar getFrom() {
        return from;
    }

    public void setFrom(GregorianCalendar from) {
        this.from = from;
        this.from.set(Calendar.SECOND, 0);
        this.from.set(Calendar.MILLISECOND, 0);
    }

    public GregorianCalendar getTo() {
        return to;
    }

    public void setTo(GregorianCalendar to) {
        this.to = to;
        this.to.set(Calendar.SECOND, 0);
        this.to.set(Calendar.MILLISECOND, 0);
    }
}
