package com.revelvol.progressservice.repository;

import com.revelvol.progressservice.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRepository extends JpaRepository<Progress,Long> {

}
