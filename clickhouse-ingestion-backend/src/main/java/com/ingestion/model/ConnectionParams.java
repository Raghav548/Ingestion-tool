package com.ingestion.model;

import lombok.Data;

@Data
public class ConnectionParams {
    private String host;
    private int port;
    private String database;
    private String user;
    private String jwtToken;
}