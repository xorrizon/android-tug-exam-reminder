package at.tugraz.examreminder.core;

public class CourseContainer {
	private static CourseContainer instance = null;

	private CourseContainer(){}

	public synchronized CourseContainer instance() {
		if(instance == null)
			instance = new CourseContainer();
		return instance;
	}

}
