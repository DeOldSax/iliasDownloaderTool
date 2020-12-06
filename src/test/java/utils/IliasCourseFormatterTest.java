package utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IliasCourseFormatterTest {

    @Test
    public void testFormatting() {
        String courseName = "[938493] TestCourse eins 5 [info7]";
        String formattedCourseName = IliasCourseFormatter.formatCourseName(courseName);
        assertEquals("TestCourse eins 5 [info7]", formattedCourseName);
    }

    @Test
    public void testFormatting1() {
        String courseName = " [938493] TestCourse";
        String formattedCourseName = IliasCourseFormatter.formatCourseName(courseName);
        assertEquals("TestCourse", formattedCourseName);
    }

    @Test
    public void testFormatting2() {
        String courseName = "TestCourse [infotext5]";
        String formattedCourseName = IliasCourseFormatter.formatCourseName(courseName);
        assertEquals(courseName, formattedCourseName);
    }
}
