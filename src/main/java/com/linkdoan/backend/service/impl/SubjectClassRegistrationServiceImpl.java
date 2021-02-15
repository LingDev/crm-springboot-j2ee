package com.linkdoan.backend.service.impl;

import com.linkdoan.backend.dto.SubjectClassRegistrationDTO;
import com.linkdoan.backend.model.*;
import com.linkdoan.backend.repository.*;
import com.linkdoan.backend.service.SubjectClassRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
        (
                propagation = Propagation.REQUIRED,
                readOnly = false,
                rollbackFor = Throwable.class
        )
public class SubjectClassRegistrationServiceImpl implements SubjectClassRegistrationService {

    @Autowired
    SubjectRegistrationRepository subjectRegistrationRepository;

    @Autowired
    SubjectClassRegistrationRepository subjectClassRegistrationRepository;

    @Qualifier("studentRepository")
    @Autowired
    StudentRepository studentRepository;

    @Qualifier("subjectRepository")
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SubjectClassRepository subjectClassRepository;

    @Autowired
    TermRepository termRepository;

    //admin role
    @Override
    public boolean subjectClassSubmitForNewStudent(String termId) {
        List<SubjectRegistration> subjectRegistrationList = subjectRegistrationRepository.findAllByTermIdAndAutoSubmit(termId, 1);
        LocalDateTime timeNow = LocalDateTime.now();
        for (SubjectRegistration subjectRegistration : subjectRegistrationList) {
            List<SubjectClass> subjectClassList = subjectClassRepository.findAllByTermIdAndSubjectId(termId, subjectRegistration.getSubjectId());
            boolean submitted = false;
            for (SubjectClass subjectClass : subjectClassList) {
                if (!submitted) {
                    if (subjectClass.getCurrentOfSubmittingNumber() < subjectClass.getNumberOfSeats()) {
                        SubjectClassRegistration subjectClassRegistration = new SubjectClassRegistration();
                        subjectClassRegistration.setSubmittedDate(timeNow);
                        subjectClassRegistration.setTermId(termId);
                        subjectClassRegistration.setSubjectClassId(subjectClass.getSubjectClassId());
                        subjectClassRegistration.setStudentId(subjectRegistration.getStudentId());
                        subjectClassRegistration.setAutoSubmit(1);
                        subjectClassRegistration.setProgressSubmitted(new Long(21));
                        subjectClassRegistration.setStatus(1);
                        subjectClassRegistrationRepository.save(subjectClassRegistration);
                        subjectClass.setCurrentOfSubmittingNumber(subjectClass.getCurrentOfSubmittingNumber() + 1);
                        subjectClassRepository.save(subjectClass);
                        submitted = true;
                    }
                }
            }
            if (!submitted)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm được lớp phù hợp cho sinh viên: " + subjectRegistration.getStudentId() + "- " + subjectRegistration.getSubjectId());
        }
        return false;
    }

    /*
        Student Role
     */

    @Override
    public Map<String, Object> getListSubmitted(String studentId, String termId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        Map<String, Object> rs = new HashMap<>();
        int count  = 0 ;
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            rs.put("student", student);
            List<Map<String, Object>> subjectClassList = new ArrayList<>();
            String[] labels = {"id", "autoSubmit", "studentId", "subjectClassId", "submittedDate",
                    "termId", "status", "progressSubmitted", "subjectName", "eachSubject", "subjectId", "roomId", "duration", "dayOfWeek", "hourOfDay"};
            List<Object[]> subjectClassObjectArrayList = subjectClassRegistrationRepository.getListSubmittedByStudentIdAndTermId(studentId, termId);
            for (Object[] subjectClassObjectArray : subjectClassObjectArrayList) {
                Map<String, Object> subjectClassObject = new HashMap<>();
                for (int i = 0; i < labels.length; i++) {
                    subjectClassObject.put(labels[i], subjectClassObjectArray[i]);
                }
                subjectClassList.add(subjectClassObject);
                count += (Integer)subjectClassObjectArray[9];
            }
            rs.put("listSubjectClass", subjectClassList);
            rs.put("totalSubjectClass", count);
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Lỗi xác thực!!!");

        return rs;
    }

    @Override
    public SubjectClassRegistration submit(String studentId, SubjectClassRegistrationDTO subjectClassRegistrationDTO) {
        //check student exist
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            Optional<Term> termOptional = termRepository.findById(subjectClassRegistrationDTO.getTermId());
            if (termOptional.isPresent()) {
                Term term = termOptional.get();
                LocalDateTime ldt = LocalDateTime.now();
                boolean isBefore = ldt.isBefore(term.getSubjectCLassSubmittingEndDate());
                if (!isBefore)
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Đã kết thúc đăng ký học phần");
                Optional<SubjectClass> subjectClassOptional = subjectClassRepository.findById(subjectClassRegistrationDTO.getSubjectClassId());
                if (subjectClassOptional.isPresent()) {
                    SubjectClass subjectClass = subjectClassOptional.get();
                    if (subjectClass.getCurrentOfSubmittingNumber() >= subjectClass.getNumberOfSeats())
                        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Lớp học phần đã đầy");
                    SubjectClassRegistration subjectClassRegistration = new SubjectClassRegistration();
                    subjectClassRegistration.setStatus(1);
                    subjectClassRegistration.setProgressSubmitted(21L);
                    subjectClassRegistration.setSubmittedDate(ldt);
                    subjectClassRegistration.setTermId(term.getId());
                    subjectClassRegistration.setAutoSubmit(0);
                    subjectClassRegistration.setSubjectClassId(subjectClassRegistrationDTO.getSubjectClassId());
                    subjectClassRegistration.setStudentId(studentId);
                    Optional<SubjectClassRegistration> subjectClassRegistrationOptional =
                            subjectClassRegistrationRepository.findFirstBySubjectClassIdAndStudentIdAndTermId(subjectClassRegistrationDTO.getSubjectClassId(),studentId, term.getId());
                    if(subjectClassRegistrationOptional.isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Đã dăng ký");
                    subjectClassRegistrationRepository.save(subjectClassRegistration);
                    subjectClass.setCurrentOfSubmittingNumber(subjectClass.getCurrentOfSubmittingNumber() + 1);
                    subjectClassRepository.save(subjectClass);
                    return subjectClassRegistration;
                } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học phần này!!!");
            } else throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Kỳ học không chính xác!!1");
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Lỗi xác thực!!!");

    }

    @Override
    public boolean delete(String studentId, String subjectClassId, String termId) {
        Optional<SubjectClassRegistration> subjectClassRegistrationOptional = subjectClassRegistrationRepository.findFirstBySubjectClassIdAndStudentIdAndTermId(subjectClassId,studentId,termId);
        if(subjectClassRegistrationOptional.isPresent()){
            SubjectClassRegistration subjectClassRegistration = subjectClassRegistrationOptional.get();
            Optional<SubjectClass> subjectClassOptional = subjectClassRepository.findById(subjectClassId);
            SubjectClass subjectClass = subjectClassOptional.get();
            subjectClass.setCurrentOfSubmittingNumber(subjectClass.getCurrentOfSubmittingNumber() - 1);
            subjectClassRegistrationRepository.delete(subjectClassRegistration);
            subjectClassRepository.save(subjectClass);
            return true;
        }else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chưa đăng ký!!!");
    }

}
