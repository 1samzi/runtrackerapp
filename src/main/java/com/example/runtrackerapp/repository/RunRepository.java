package com.example.runtrackerapp.repository;

import com.example.runtrackerapp.model.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RunRepository extends JpaRepository<Run, Long>, JpaSpecificationExecutor<Run> {
}
