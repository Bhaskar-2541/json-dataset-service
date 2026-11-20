package com.project.exception;

public class DatasetNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public DatasetNotFoundException(String datasetName) {
        super("No records found for dataset: '" + datasetName + "'");
    }
}
