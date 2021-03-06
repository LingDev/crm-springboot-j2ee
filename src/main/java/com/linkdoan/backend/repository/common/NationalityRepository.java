package com.linkdoan.backend.repository.common;

import com.linkdoan.backend.model.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Integer> {

}
