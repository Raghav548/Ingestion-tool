package com.ingestion.service;

import com.ingestion.dto.ClickHouseRequestDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;


@Service
public class ClickHouseService {

    public boolean testConnection(String jdbcUrl, String user, String jwtToken) throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, user, jwtToken);
        return connection != null;
    }

    public List<String> fetchTables(String jdbcUrl, String user, String jwtToken) throws SQLException {
        List<String> tables = new ArrayList<>();
        Connection connection = DriverManager.getConnection(jdbcUrl, user, jwtToken);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SHOW TABLES");

        while (resultSet.next()) {
            tables.add(resultSet.getString(1));
        }

        resultSet.close();
        statement.close();
        connection.close();

        return tables;
    }

    public void exportToFlatFile(ClickHouseRequestDTO requestDTO) throws SQLException {
    String jdbcUrl = requestDTO.getJdbcUrl();
    String user = requestDTO.getUser();
    String jwtToken = requestDTO.getJwtToken();
    List<String> selectedColumns = requestDTO.getSelectedColumns();
    String tableName = requestDTO.getTableName();
    String filePath = requestDTO.getFilePath();

    Connection connection = DriverManager.getConnection(jdbcUrl, user, jwtToken);
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery("SELECT " + String.join(",", selectedColumns) + " FROM " + tableName);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        // Write column headers first
        writer.write(String.join(",", selectedColumns));
        writer.newLine();

        // Write rows of data
        while (resultSet.next()) {
            for (int i = 1; i <= selectedColumns.size(); i++) {
                writer.write(resultSet.getString(i));
                if (i < selectedColumns.size()) writer.write(",");
            }
            writer.newLine();
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to write CSV file: " + filePath, e);
    }

    resultSet.close();
    statement.close();
    connection.close();
}



    // Import data from a flat file to ClickHouse
   public void importFlatFileToClickHouse(MultipartFile file, String jdbcUrl, String user, String jwtToken, String tableName) throws SQLException {
    String insertQuery = "INSERT INTO " + tableName + " FORMAT CSV";

    try (Connection connection = DriverManager.getConnection(jdbcUrl, user, jwtToken);
         Statement statement = connection.createStatement();
         InputStream inputStream = file.getInputStream()) {

        byte[] csvBytes = inputStream.readAllBytes();
        String csvData = new String(csvBytes);

        String[] lines = csvData.split("\n");

        // ✅ Skip the first line (header row)
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] values = line.split(",");
            StringBuilder valueBuilder = new StringBuilder("(");
            for (int j = 0; j < values.length; j++) {
                valueBuilder.append("'").append(values[j].trim()).append("'");
                if (j < values.length - 1) valueBuilder.append(",");
            }
            valueBuilder.append(")");

            String insertRowQuery = "INSERT INTO " + tableName + " VALUES " + valueBuilder;
            statement.execute(insertRowQuery);
        }

    } catch (IOException e) {
        throw new RuntimeException("Failed to read flat file", e);
    }
}


    public List<String> fetchColumns(String jdbcUrl, String user, String jwtToken, String tableName) throws SQLException {
    List<String> columns = new ArrayList<>();

    String query = "DESCRIBE TABLE " + tableName;

    try (Connection connection = DriverManager.getConnection(jdbcUrl, user, jwtToken);
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            columns.add(rs.getString("name"));
        }
    }

    return columns;
}




}
