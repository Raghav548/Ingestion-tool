package com.ingestion.controller;

import com.ingestion.dto.ClickHouseRequestDTO;
import com.ingestion.service.ClickHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api")
public class IngestionController {

    @Autowired
    private ClickHouseService clickHouseService;

    @PostMapping("/test-connection")
public ResponseEntity<Map<String, Object>> testConnection(@RequestBody ClickHouseRequestDTO requestDTO) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("Received connection test request:");
        System.out.println("JDBC URL: " + requestDTO.getJdbcUrl());
        System.out.println("User: " + requestDTO.getUser());
        System.out.println("JWT Token: " + requestDTO.getJwtToken());

        boolean isConnected = clickHouseService.testConnection(
            requestDTO.getJdbcUrl(),
            requestDTO.getUser(),
            requestDTO.getJwtToken()
        );
        response.put("connectionSuccessful", isConnected);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        // Log full error
        e.printStackTrace();

        response.put("connectionSuccessful", false);
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


    @PostMapping("/fetch-tables")
    public List<String> fetchTables(@RequestBody ClickHouseRequestDTO requestDTO) {
        try {
            
            return clickHouseService.fetchTables(requestDTO.getJdbcUrl(), requestDTO.getUser(), requestDTO.getJwtToken());
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching tables: " + e.getMessage());
        }
    }

    @RequestMapping("/export-to-flatfile")
public String exportToFlatFile(@RequestBody ClickHouseRequestDTO requestDTO) {
    try {
        clickHouseService.exportToFlatFile(requestDTO);
        return "Data exported successfully to flat file";
    } catch (SQLException e) {
        return "Error exporting data: " + e.getMessage();
    }
}

    @PostMapping("/import-flatfile-to-clickhouse")
public String importFlatFileToClickHouse(@RequestParam("file") MultipartFile file,
                                          @RequestParam("jdbcUrl") String jdbcUrl,
                                          @RequestParam("user") String user,
                                          @RequestParam("jwtToken") String jwtToken,
                                          @RequestParam("tableName") String tableName) {
    try {
        clickHouseService.importFlatFileToClickHouse(file, jdbcUrl, user, jwtToken, tableName);
        return "Data imported successfully from flat file";
    } catch (SQLException e) {
        return "Error importing data: " + e.getMessage();
    }
}
@PostMapping("/preview")
public ResponseEntity<List<Map<String, String>>> previewCsv(
        @RequestParam("file") MultipartFile file,
        @RequestParam("selectedColumns") List<String> selectedColumns
) {
    List<Map<String, String>> previewData = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
        String headerLine = reader.readLine();
        if (headerLine == null) return ResponseEntity.badRequest().body(Collections.emptyList());

        String[] headers = headerLine.split(",");

        // Map header name to index
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            columnIndexMap.put(headers[i].trim(), i);
        }

        int count = 0;
        String line;
        while ((line = reader.readLine()) != null && count < 100) {
            String[] values = line.split(",");
            Map<String, String> row = new LinkedHashMap<>();
            for (String col : selectedColumns) {
                Integer index = columnIndexMap.get(col);
                if (index != null && index < values.length) {
                    row.put(col, values[index]);
                }
            }
            previewData.add(row);
            count++;
        }

        return ResponseEntity.ok(previewData);
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
}

}
