package com.project.controller;

import com.project.dto.InsertRecordResponse;
import com.project.service.DatasetService;
import com.project.strategy.QueryCriteria;
import com.project.strategy.SortOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping("/{datasetName}/record")
    public ResponseEntity< InsertRecordResponse > insertRecord(
            @PathVariable String datasetName,
            @RequestBody Map< String, Object > payload) {

        InsertRecordResponse response = datasetService.insertRecord(datasetName, payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{datasetName}/query")
    public ResponseEntity< Object > queryDataset(
            @PathVariable String datasetName,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        QueryCriteria criteria = new QueryCriteria(groupBy, sortBy, SortOrder.from(order));
        Object result = datasetService.queryDataset(datasetName, criteria);
        return ResponseEntity.ok(result);
    }
}