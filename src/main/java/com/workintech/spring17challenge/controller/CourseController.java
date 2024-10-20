package com.workintech.spring17challenge.controller;

import com.workintech.spring17challenge.exceptions.ApiException;
import com.workintech.spring17challenge.model.Course;
import com.workintech.spring17challenge.model.CourseGpa;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private Map<Integer, Course> courses;
    private CourseGpa lowCourseGpa;
    private CourseGpa mediumCourseGpa;
    private CourseGpa highCourseGpa;

    @Autowired
    public CourseController(@Qualifier("lowCourseGpa") CourseGpa lowCourseGpa,
                            @Qualifier("mediumCourseGpa") CourseGpa mediumCourseGpa,
                            @Qualifier("highCourseGpa") CourseGpa highCourseGpa) {
        this.lowCourseGpa = lowCourseGpa;
        this.mediumCourseGpa = mediumCourseGpa;
        this.highCourseGpa = highCourseGpa;
    }

    @PostConstruct
    public void init() {
        courses = new HashMap<>();
    }

    @GetMapping("")
    public List<Course> getAllCourses() {
        return courses.values().stream().toList();
    }

    @GetMapping("/{name}")
    public Course getCourseByName(@PathVariable String name) {

        //for each map'e uygulamaıyor,o yüzden önce List'e çeviriyoruz:
        List<Course> courseList = courses.values().stream().toList();

        for (Course course : courseList) {
            if (course.getName().equals(name)) {
                return course;
            }
        }
        throw new ApiException("Course cannot be found", HttpStatus.NOT_FOUND);
    }


    @PostMapping("")
    public ResponseEntity<Map<Course, Integer>> addCourse(@RequestBody Course course) {

        Map<Course, Integer> courseIntegerMap = new HashMap<>();
        Integer totalGpa = calculateTotalGpa(course);

        //credit null check:
        if(course.getCredit() == null){
            throw new ApiException("Course credit cannot be null", HttpStatus.BAD_REQUEST);
        }

        //name null check:
        if (course.getName() == null) {
            throw new ApiException("Name field must be filled", HttpStatus.BAD_REQUEST);
        }

        courses.put(course.getId(), course);
        courseIntegerMap.put(course, totalGpa);
        return new ResponseEntity<>(courseIntegerMap, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<Course, Integer>> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        Map<Course, Integer> result = new HashMap<>();
        Integer totalGpa = calculateTotalGpa(course);

        //name null check:
        if (course.getName() == null) {
            throw new ApiException("Name field must be filled", HttpStatus.BAD_REQUEST);
        }

        courses.put(id, course);
        result.put(course, totalGpa);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public Course deleteCourse(@PathVariable Integer id) {
        return courses.remove(id);
    }


    //POST ve PUT Api'lerinde totalGpa'yı hesaplayacak metod:
    public int calculateTotalGpa(Course course) {

        //credit value check:
        if (course.getCredit() < 0 || course.getCredit() > 4) {
            throw new ApiException("Course credit cannot be smaller than 0, greater than 4", HttpStatus.BAD_REQUEST);
        }

        int totalGpa = 0;

        // GPA (Grade Point Average) hesaplaması, dersin kredisi (credit)
        // ve dersin not katsayısına (coefficient) bağlı olarak yapılacak.
        // formül -> totalGpa = Grade’s Coefficient × Credit × Gpa Value
        if (course.getCredit() <= 2) {
            totalGpa = course.getGrade().getCoefficient() * course.getCredit() * lowCourseGpa.getGpa();
        } else if (course.getCredit() == 3) {
            totalGpa = course.getGrade().getCoefficient() * course.getCredit() * mediumCourseGpa.getGpa();
        } else if (course.getCredit() == 4) {
            totalGpa = course.getGrade().getCoefficient() * course.getCredit() * highCourseGpa.getGpa();
        }
        return totalGpa;
    }
}
