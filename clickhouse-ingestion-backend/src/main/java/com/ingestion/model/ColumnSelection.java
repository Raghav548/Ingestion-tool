package com.ingestion.model;

import lombok.Data;
import java.util.List;

@Data
public class ColumnSelection {
    private String tableName;
    private List<String> columns;
    private String filePath; // for upload case
}