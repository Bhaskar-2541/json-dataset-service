package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.DatasetRecord;

import java.util.List;

public interface DatasetRecordRepository extends JpaRepository<DatasetRecord, Long> {

    List<DatasetRecord> findByDatasetNameOrderByIdAsc(String datasetName);
}