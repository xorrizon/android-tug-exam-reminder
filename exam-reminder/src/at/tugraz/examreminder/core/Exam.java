package at.tugraz.examreminder.core;

import java.util.Date;

public class Exam implements Comparable<Exam> {
	public Date from;
	public Date to;
	public String place;
	public String term;
	public String lecturer;
	public String examinar;
	public Date registrationStart;
	public Date registrationEnd;
	public int participants;
	public int participants_max;
	public Date updated_at;

    public Exam(){
        from = new Date();
        to = new Date();
        registrationEnd = new Date();
        registrationStart = new Date();
        updated_at = new Date();
    }

    @Override
    public int compareTo(Exam exam) {
        return from.compareTo(exam.from);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Exam))
            return false;
        Exam e = (Exam)o;
        boolean equal = true;
        equal &= from.equals(e.from);
        equal &= to.equals(e.to);
        equal &= place.equals(e.place);
        equal &= term.equals(e.term);
        equal &= lecturer.equals(e.lecturer);
        equal &= examinar.equals(e.examinar);
        equal &= registrationStart.equals(e.registrationStart);
        equal &= registrationEnd.equals(e.registrationEnd);
        equal &= participants == e.participants;
        equal &= participants_max == e.participants_max;
        equal &= updated_at.equals(e.updated_at);
        return equal;
    }

    @Override
    public Exam clone() {
        Exam exam = new Exam();
        exam.from = from;
        exam.to = to;
        exam.place = place;
        exam.term = term;
        exam.lecturer = lecturer;
        exam.examinar = examinar;
        exam.registrationStart = registrationStart;
        exam.registrationEnd = registrationEnd;
        exam.participants = participants;
        exam.participants_max = participants_max;
        exam.updated_at = updated_at;
        return exam;
    }
}
