package com.ingestion.service;

import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {

    public int writeCsv(ResultSet resultSet, List<String> selectedColumns, String fileName) throws IOException, SQLException {
        FileWriter writer = new FileWriter(fileName);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(selectedColumns.toArray(new String[0])));

        int count = 0;
        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (String col : selectedColumns) {
                row.add(resultSet.getString(col));
            }
            csvPrinter.printRecord(row);
            count++;
        }

        csvPrinter.flush();
        return count;
    }

    public int importCsvToClickHouse(String filePath, String tableName, Connection conn) throws SQLException, IOException {
        try (CSVParser parser = new CSVParser(new FileReader(filePath), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            List<String> headers = parser.getHeaderNames();

            String query = "INSERT INTO " + tableName + " (" + String.join(",", headers) + ") VALUES (" +
                    headers.stream().map(h -> "?").collect(Collectors.joining(",")) + ")";
            PreparedStatement stmt = conn.prepareStatement(query);

            int count = 0;
            for (CSVRecord record : parser) {
                for (int i = 0; i < headers.size(); i++) {
                    stmt.setString(i + 1, record.get(i));
                }
                stmt.addBatch();
                count++;
            }

            stmt.executeBatch();
            return count;
        }
    }
}