package com.ingestion.controller;

import com.ingestion.model.ConnectionParams;
import com.ingestion.service.ClickHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.Collections;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clickhouse")
public class ClickHouseController {

    @Autowired
    private ClickHouseService clickHouseService;

    @PostMapping("/tables")
    public ResponseEntity<?> getTables(@RequestBody Map<String, String> request) {
        try {
            String jdbcUrl = request.get("jdbcUrl"); // full JDBC string expected
            String user = request.get("user");
            String jwtToken = request.get("jwtToken");

            List<String> tables = clickHouseService.fetchTables(jdbcUrl, user, jwtToken);
            return ResponseEntity.ok(tables);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Error fetching tables: " + e.getMessage());
        }
    }

   @GetMapping("/fetch-columns")
public ResponseEntity<List<String>> fetchColumns(
        @RequestParam String jdbcUrl,
        @RequestParam String user,
        @RequestParam String jwtToken,
        @RequestParam String tableName) {

    try {
        List<String> columnNames = clickHouseService.fetchColumns(jdbcUrl, user, jwtToken, tableName);
        return ResponseEntity.ok(columnNames);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
}


}