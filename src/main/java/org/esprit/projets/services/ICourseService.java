package org.esprit.projets.services;


import org.esprit.projets.entity.Course;

import java.util.List;

public interface ICourseService {
    void addCourse(Course var1);

    void deleteCourse(int var1);

    void updateCourse(Course var1);

    List<Course> getAllCourses();
}
