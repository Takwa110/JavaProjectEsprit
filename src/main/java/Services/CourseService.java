//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package Services;

import Entite.Course;
import Utils.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseService implements ICourseService {
    private final Connection connection = DataSource.getConnection();

    public CourseService() {
    }

    public void addCourse(Course course) {
        String query = "INSERT INTO course (id, title, description, pdfPath) VALUES (NULL, ?, ?, ?)";

        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, course.getTitle());
            pstmt.setString(2, course.getDescription());
            pstmt.setString(3, course.getPdfPath());
            int result = pstmt.executeUpdate();
            System.out.println(result + " course added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteCourse(int courseId) {
        String query = "DELETE FROM course WHERE id = ?";

        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            int result = pstmt.executeUpdate();
            System.out.println(result + " course deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateCourse(Course updatedCourse) {
        String query = "UPDATE course SET title = ?, description= ?, pdfPath = ? WHERE id = ?";

        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
            pstmt.setString(1, updatedCourse.getTitle());
            pstmt.setString(2, updatedCourse.getDescription());
            pstmt.setString(3, updatedCourse.getPdfPath());
            pstmt.setInt(4, updatedCourse.getId());
            int result = pstmt.executeUpdate();
            System.out.println(result + " course updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList();
        String query = "SELECT * FROM course";

        try (
                Statement stmt = this.connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
        ) {
            while(rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String pdfPath = rs.getString("pdfPath");
                courses.add(new Course(id, title, description, pdfPath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }
}
