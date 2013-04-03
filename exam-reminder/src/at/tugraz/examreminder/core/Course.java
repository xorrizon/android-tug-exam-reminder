package at.tugraz.examreminder.core;

import java.util.*;

public class Course {
	public String name;
	public String number;
	public String term;
	public String type;
	public String lecturer;
    public SortedSet<Exam> exams = new TreeSet<Exam>();
}
