package at.tugraz.examreminder.crawler;

import java.util.List;

import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;

public interface Crawler {
	public List<Course> getCourseList(String searchTerm);
	public List<Exam> getExams(Course course);
}
