package at.tugraz.examreminder.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class CourseContainer extends Observable {
	private static CourseContainer instance = null;
    private List<Course> courses = new ArrayList<Course>();

	private CourseContainer(){}

	public static synchronized CourseContainer instance() {
		if(instance == null)
			instance = new CourseContainer();
		return instance;
	}

    public void add(Course course) {
        courses.add(course);
        setChanged();
    }

    public void remove(Course course) {
        courses.remove(course);
        setChanged();
    }

    public boolean isEmpty() {
        return courses.isEmpty();
    }

    public int size() {
        return courses.size();
    }

}
