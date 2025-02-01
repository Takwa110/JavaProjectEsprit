
package Controllers;

import Services.CourseService;
import java.util.Scanner;

public class DeleteCourseController {
    private final CourseService courseService = new CourseService();

    public DeleteCourseController() {
    }

    public void deleteCourse() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the course ID to delete:");

        int courseId;
        try {
            courseId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException var5) {
            System.out.println("Invalid course ID. Please enter a valid number.");
            return;
        }

        try {
            this.courseService.deleteCourse(courseId);
            System.out.println("Course deleted successfully.");
        } catch (Exception e) {
            System.out.println("Failed to delete course: " + e.getMessage());
        }

    }
}
