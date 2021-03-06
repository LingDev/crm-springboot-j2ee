package com.linkdoan.backend.repository;

import com.linkdoan.backend.dto.YearClassDTO;
import com.linkdoan.backend.model.YearClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface YearClassRepository extends JpaRepository<YearClass, String> {

    @Query(value =
            "SELECT new com.linkdoan.backend.dto.YearClassDTO(yearClass.classId, yearClass.className, yearClass.totalMember, " +
                    "yearClass.startYear, yearClass.endYear, yearClass.courseNumber , yearClass.educationProgramLevel, " +
                    "yearClass.educationProgramType, yearClass.teacherId, employee.fullName, yearClass.currentTerm, yearClass.departmentId, " +
                    "department.departmentName)  " +
                    "FROM YearClass  yearClass INNER JOIN Department department ON yearClass.departmentId = department.departmentId " +
                    "LEFT JOIN Employee employee ON yearClass.teacherId = employee.employeeId"
    )
    List<YearClassDTO> getAll();

    @Query(value =
            "SELECT new com.linkdoan.backend.dto.YearClassDTO(yearClass.classId, yearClass.className, yearClass.totalMember, " +
                    "yearClass.startYear, yearClass.endYear, yearClass.courseNumber , yearClass.educationProgramLevel, " +
                    "yearClass.educationProgramType, yearClass.teacherId, employee.fullName, yearClass.currentTerm, yearClass.departmentId, " +
                    "department.departmentName)  " +
                    "FROM YearClass  yearClass INNER JOIN Department department ON yearClass.departmentId = department.departmentId " +
                    "LEFT JOIN Employee employee ON yearClass.teacherId = employee.employeeId " +
                    "WHERE yearClass.classId = :yearClassId "
    )
    YearClassDTO getById(@Param("yearClassId") String yearClassId);

    Optional<YearClass> findFirstByTeacherId(String teacherId);

}
