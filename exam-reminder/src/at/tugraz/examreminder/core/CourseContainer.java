package at.tugraz.examreminder.core;

import java.util.*;

public class CourseContainer extends Observable {
	private static CourseContainer instance = null;
    private List<Course> courses = new ArrayList<Course>();

	private CourseContainer(){}

	public static synchronized CourseContainer instance() {
		if(instance == null)
			instance = new CourseContainer();
		return instance;
	}

    public Course get(int location) {
        return courses.get(location);
    }

    public boolean contains(Course course){
        return courses.contains(course);
    }

    public void add(Course course) {
        courses.add(course);
        sort();
        setChanged();
    }

    public void remove(Course course) {
        courses.remove(course);
        setChanged();
    }

    public void clear() {
        courses.clear();
        setChanged();
    }

    public boolean isEmpty() {
        return courses.isEmpty();
    }

    public int size() {
        return courses.size();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    public List<Course> getCourseList() {
        return courses;
    }

    public void addAll(List<Course> courses) {
        this.courses.addAll(courses);
        sort();
    }

    private void sort() {
        Collections.sort(courses);
    }
}
