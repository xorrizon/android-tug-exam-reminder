package at.tugraz.examreminder;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class at.tugraz.examreminder.MyActivityTest \
 * at.tugraz.examreminder.tests/android.test.InstrumentationTestRunner
 */
public class MyActivityTest extends ActivityInstrumentationTestCase2<MyActivity> {

	public MyActivityTest() {
		super("at.tugraz.examreminder", MyActivity.class);
	}

}
