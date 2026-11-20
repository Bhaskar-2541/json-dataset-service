package com.project.service;

import com.project.dto.InsertRecordResponse;
import com.project.entity.DatasetRecord;
import com.project.exception.DatasetNotFoundException;
import com.project.exception.InvalidQueryException;
import com.project.repository.DatasetRecordRepository;
import com.project.strategy.QueryCriteria;
import com.project.strategy.QueryOperationStrategy;
import com.project.strategy.QueryStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class DatasetServiceImpl implements DatasetService {

    private final DatasetRecordRepository repository;
    private final QueryStrategyFactory strategyFactory;

    public DatasetServiceImpl(
            DatasetRecordRepository repository,
            QueryStrategyFactory strategyFactory) {
        this.repository = repository;
        this.strategyFactory = strategyFactory;
    }

    @Override
    @Transactional
    public InsertRecordResponse insertRecord(String datasetName, Map< String, Object > payload) {
        validateDatasetName(datasetName);

        if (payload == null || payload.isEmpty()) {
            throw new InvalidQueryException("Request body cannot be empty. Provide a valid JSON object.");
        }

        DatasetRecord record = new DatasetRecord(datasetName.trim(), payload);

        DatasetRecord saved = repository.save(record);

        return new InsertRecordResponse(
            "Record added successfully",
            saved.getDatasetName(),
            saved.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Object queryDataset(String datasetName, QueryCriteria criteria) {
        validateDatasetName(datasetName);

        QueryOperationStrategy strategy = strategyFactory.getStrategy(criteria);

        List< DatasetRecord > entities = repository.findByDatasetNameOrderByIdAsc(datasetName.trim());
        if (entities.isEmpty()) {
            throw new DatasetNotFoundException(datasetName.trim());
        }

        List< Map< String, Object > > records = entities.stream()
            .map(DatasetRecord::getRecordData)
            .toList();

        return strategy.execute(records, criteria);
    }

    private void validateDatasetName(String datasetName) {
        if (datasetName == null || datasetName.isBlank()) {
            throw new InvalidQueryException("Dataset name must not be blank.");
        }
    }
}