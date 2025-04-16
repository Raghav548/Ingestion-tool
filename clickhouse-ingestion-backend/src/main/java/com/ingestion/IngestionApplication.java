package com.ingestion;

import com.ingestion.service.ClickHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IngestionApplication implements CommandLineRunner {

    @Autowired
    private ClickHouseService clickHouseService;

    public static void main(String[] args) {
        SpringApplication.run(IngestionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Example parameters (replace with your actual parameters)
        // String jdbcUrl = "jdbc:clickhouse://eeruzfz6kd.asia-southeast1.gcp.clickhouse.cloud:8443/default?ssl=true";
        // String user = "default";
        // String jwtToken = "W0iVj_dNh7dcr";

        // // Calling the testConnection method with the correct parameters
        // boolean connectionSuccessful = clickHouseService.testConnection(jdbcUrl, user, jwtToken);
        // if (connectionSuccessful) {
        //     System.out.println("Connection successful");
        // } else {
        //     System.out.println("Connection failed");
        // }
    }
}
