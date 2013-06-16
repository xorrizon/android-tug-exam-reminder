package at.tugraz.examreminder.core;

import java.util.*;

public class Course implements Comparable<Course> {
    public String id;
	public String name;
	public String number;
	public String term;
	public String type;
	public String lecturer;
  public SortedSet<Exam> exams = new TreeSet<Exam>();

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(!(o instanceof Course))
            return false;
        Course c = (Course)o;
        if(c == this)
            return true;
        return name.equals(c.name) && number.equals(c.number) && term.equals(c.term) && type.equals(c.type);
    }

    @Override
    public int compareTo(Course course) {
        return this.name.compareTo(course.name);

    }
}
