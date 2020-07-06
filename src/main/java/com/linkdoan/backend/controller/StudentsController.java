package com.linkdoan.backend.controller;

import com.linkdoan.backend.dto.StudentDTO;
import com.linkdoan.backend.model.JwtRequest;
import com.linkdoan.backend.model.Student;
import com.linkdoan.backend.repository.StudentRepository;
import com.linkdoan.backend.service.impl.StudentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController


public class StudentsController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentServiceImpl studentService;

    @RequestMapping(value = "/student/getListStudent", method = RequestMethod.GET)
    public ResponseEntity<?> getListStudent(@Valid @ModelAttribute StudentDTO studentDTO  ) throws Exception { //@RequestBody JwtRequest authenticationRequest

        Pageable pageable = PageRequest.of(studentDTO.getPage(), studentDTO.getPageSize());
        Example<Student> searchTerm = Example.of(new Student());
        return new ResponseEntity<>( studentService.getListStudent( pageable),HttpStatus.OK);
    }

    @RequestMapping(value = "/student/insert", method = RequestMethod.POST)
    public ResponseEntity<?> insertStudent(@Valid @ModelAttribute StudentDTO studentDTO  ) throws Exception { //@RequestBody JwtRequest authenticationRequest

        return new ResponseEntity<>( studentService.insertStudent(studentDTO),HttpStatus.OK);
    }
}
