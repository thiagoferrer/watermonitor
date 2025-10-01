package com.monitor.repository;

import com.monitor.model.Medicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {
}
