package at.tugraz.examreminder.crawler;

import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;

import java.util.*;

public class SimpleMockCrawler implements Crawler {

    public SimpleMockCrawler() {


    }

    @Override
    public List<Course> getCourseList(String searchTerm) {
        return createCourses();
    }

    @Override
    public SortedSet<Exam> getExams(Course course) {
        return createExams();
    }

    public static List<Course> createCourses() {
        List<Course> courses = new ArrayList<Course>();

        Course course = new Course();
        course.name = "THE COURSE #1";
        course.exams = new TreeSet<Exam>(createExams());
        course.lecturer = "Our leader";
        course.number = "course.101";
        course.term = "SS";
        course.type = "VO";
        courses.add(course);

        Course course2 = new Course();
        course2.name = "THE COURSE #2";
        course2.exams = new TreeSet<Exam>(createExams());
        course2.lecturer = "Our leader";
        course2.number = "course.102";
        course2.term = "SS";
        course2.type = "VO";
        courses.add(course2);

        return courses;
    }

    public static SortedSet<Exam> createExams(){
        SortedSet<Exam> exams = new TreeSet<Exam>();
        Calendar calender = new GregorianCalendar();
        Exam exam = new Exam();
        exam.lecturer = "Mr. Professor";
        exam.examinar = "Mr. Aufsicht";
        exam.place = "Der Höhrsaal";
        calender.set(2014,1,1,12,0);
        exam.from = calender.getTime();
        calender.set(2014,1,1,14,0);
        exam.to = calender.getTime();
        exams.add(exam.clone());

        calender.set(2014,2,1,12,0);
        exam.from = calender.getTime();
        calender.set(2014,2,1,14,0);
        exam.to = calender.getTime();
        exams.add(exam.clone());

        return exams;
    }
}
