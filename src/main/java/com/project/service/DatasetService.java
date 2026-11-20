package com.project.service;

import com.project.dto.InsertRecordResponse;
import com.project.strategy.QueryCriteria;

import java.util.Map;

public interface DatasetService {

    InsertRecordResponse insertRecord(String datasetName, Map< String, Object > payload);

    Object queryDataset(String datasetName, QueryCriteria criteria);
}