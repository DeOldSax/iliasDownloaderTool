package utils;

import junit.framework.Assert;

import org.junit.Test;

public class IliasCourseFormatterTest {

	@Test
	public void testFormatting() {
		String courseName = "[938493] TestCourse eins 5 [info7]";
		String formattedCourseName = IliasCourseFormatter.formatCourseName(courseName);
		Assert.assertEquals("TestCourse eins 5 [info7]", formattedCourseName);
	}

	@Test
	public void testFormatting1() {
		String courseName = " [938493] TestCourse";
		String formattedCourseName = IliasCourseFormatter.formatCourseName(courseName);
		Assert.assertEquals("TestCourse", formattedCourseName);
	}

	@Test
	public void testFormatting2() {
		String courseName = "TestCourse [infotext5]";
		String formattedCourseName = IliasCourseFormatter.formatCourseName(courseName);
		Assert.assertEquals(courseName, formattedCourseName);
	}
}
