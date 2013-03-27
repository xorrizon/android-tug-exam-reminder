package at.tugraz.examreminder.core;

public class CourseContainer {
	private static CourseContainer instance = null;

	private CourseContainer(){}

	public static synchronized CourseContainer instance() {
		if(instance == null)
			instance = new CourseContainer();
		return instance;
	}

}
