package at.tugraz.examreminder.crawler;

import java.util.List;
import java.util.SortedSet;

import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;

public interface Crawler {
	public SortedSet<Course> getCourseList(String searchTerm);
	public List<Exam> getExams(Course course);
}
