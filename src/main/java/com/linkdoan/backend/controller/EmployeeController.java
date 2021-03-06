package com.linkdoan.backend.controller;

import com.linkdoan.backend.dto.EmployeeDTO;
import com.linkdoan.backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = "/employee")
    public ResponseEntity<?> getAllEmployee(@RequestParam(name = "type", required = false) Long type) {
        return new ResponseEntity<>(employeeService.getAllEmployee(type), HttpStatus.OK);
    }

    @GetMapping(value = "/employee/{employeeId}")
    public ResponseEntity<?> getDetail(@PathVariable("employeeId") String employeeId) {
        return new ResponseEntity<>(employeeService.getEmployeeDetail(employeeId), HttpStatus.OK);
    }

    @PostMapping(value = "/employee")
    public ResponseEntity<?> create(@RequestBody EmployeeDTO employeeDTO) {
        return new ResponseEntity<>(employeeService.create(employeeDTO), HttpStatus.OK);
    }

    @PutMapping(value = "/employee/{employeeId}")
    public ResponseEntity<?> update(@PathVariable("employeeId") String employeeId,
                                    @RequestBody EmployeeDTO employeeDTO) {
        return new ResponseEntity<>(employeeService.updateEmployee(employeeId, employeeDTO), HttpStatus.OK);
    }

    @DeleteMapping(value = "/employee/{employeeId}")
    public ResponseEntity<?> delete(@PathVariable("employeeId") String employeeId) {
        return new ResponseEntity<>(employeeService.deleteEmployee(employeeId), HttpStatus.OK);
    }
}
