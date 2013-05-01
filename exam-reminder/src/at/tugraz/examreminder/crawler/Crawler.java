package at.tugraz.examreminder.crawler;

import java.util.List;
import java.util.SortedSet;

import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;

public interface Crawler {
	public List<Course> getCourseList(String searchTerm);
	public SortedSet<Exam> getExams(Course course);
}
