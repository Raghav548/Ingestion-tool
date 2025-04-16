import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [statusMessage, setStatusMessage] = useState('');
  const [progress, setProgress] = useState(0);

  const [connectionStatus, setConnectionStatus] = useState('');
  const [jdbcUrl, setJdbcUrl] = useState('');
  const [user, setUser] = useState('');
  const [jwtToken, setJwtToken] = useState('');
  const [tables, setTables] = useState([]);
  const [showTableInput, setShowTableInput] = useState(false);
  const [tableNameForColumns, setTableNameForColumns] = useState('');
  const [columns, setColumns] = useState([]);
  const [selectedColumns, setSelectedColumns] = useState([]);
  const [file, setFile] = useState(null);
  const [exportFilePath, setExportFilePath] = useState('');
  const [previewData, setPreviewData] = useState([]);
  const [previewError, setPreviewError] = useState('');
  const [isPreviewLoading, setIsPreviewLoading] = useState(false);

  const testConnection = async () => {
    setStatusMessage('Connecting...');
    setProgress(10);
    try {
      const response = await axios.post('http://localhost:8081/api/test-connection', {
        jdbcUrl,
        user,
        jwtToken,
      });
  
      // Check actual value of connectionSuccessful in response
      if (response.data.connectionSuccessful) {
        setConnectionStatus('Connected');
        setStatusMessage('✅ Connected');
        setProgress(100);
      } else {
        setConnectionStatus('Failed');
        setStatusMessage('❌ Failed to connect: Invalid credentials or JDBC URL');
        setProgress(0);
      }
    } catch (error) {
      const backendMessage = error.response?.data?.error || error.message;
  setConnectionStatus('Error');
  setStatusMessage(`❌ Connection Error: ${backendMessage}`);
      alert(`Connection Error: ${error.message}`);
      setProgress(0);
      console.error('Connection error:', error);
    }
  };
  

  const fetchTables = async () => {
    setStatusMessage('Fetching Columns...');
    setProgress(30);
    try {
      const response = await axios.post('http://localhost:8081/api/fetch-tables', {
        jdbcUrl,
        user,
        jwtToken,
      });
      setTables(response.data);
      setProgress(100);

    } catch (error) {
      setProgress(0);
      console.error('Error fetching tables:', error);
    }
  };


  const handleFetchColumnsClick = () => {
    setShowTableInput(true);
  };

  const fetchColumns = async () => {
    try {
      const response = await axios.get('http://localhost:8081/api/clickhouse/fetch-columns', {
        params: {
          jdbcUrl,
          user,
          jwtToken,
          tableName: tableNameForColumns,
        },
      });
      setColumns(response.data);
    } catch (error) {
      console.error('Error fetching columns:', error);
    }
  };

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleColumnSelection = (column) => {
    if (selectedColumns.includes(column)) {
      setSelectedColumns(selectedColumns.filter((col) => col !== column));
    } else {
      setSelectedColumns([...selectedColumns, column]);
    }
  };

  const uploadData = async () => {
    setStatusMessage('Ingesting file...');
  setProgress(30);

    const formData = new FormData();
    formData.append('file', file);
    formData.append('jdbcUrl', jdbcUrl);
    formData.append('user', user);
    formData.append('jwtToken', jwtToken);
    formData.append('tableName', tableNameForColumns);
    formData.append('selectedColumns', selectedColumns);

    try {
      await axios.post('http://localhost:8081/api/import-flatfile-to-clickhouse', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      setStatusMessage('File uploaded successfully');
      setProgress(100);
      alert('File uploaded successfully');
    } catch (error) {
      setProgress(0);
      console.error('Error uploading file:', error);
    }
  };

  const exportData = async () => {
    setStatusMessage('Exporting...');
    setProgress(40);
    try {
      await axios.post('http://localhost:8081/api/export-to-flatfile', {
        jdbcUrl,
        user,
        jwtToken,
        tableName: tableNameForColumns,
        selectedColumns,
        filePath: exportFilePath,
      });
      setStatusMessage('Export completed');
      setProgress(100);
      alert('Data exported successfully');
    } catch (error) {
      setProgress(0);
      console.error('Error exporting data:', error);
    }
  };

  const previewCSV = async () => {
    if (!file || selectedColumns.length === 0) {
      setPreviewError('Please select a file and at least one column.');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    selectedColumns.forEach((col) => formData.append('selectedColumns', col));

    setIsPreviewLoading(true);
    setPreviewError('');
    try {
      const response = await axios.post('http://localhost:8081/api/preview', formData);
      setPreviewData(response.data);
    } catch (error) {
      setPreviewError('Failed to preview data.');
      console.error('Preview error:', error);
    } finally {
      setIsPreviewLoading(false);
    }
  };

  return (
    
    <div className="app-container">
      <div className="status-bar">
        <p>Status: {statusMessage}</p>
        <div className="progress-bar">
          <div className="progress" style={{ width: `${progress}%` }}></div>
        </div>
      </div>
      <div className="content">
        <h1>ClickHouse Data Ingestion Tool</h1>

        <div className="card">
          <input type="text" placeholder="JDBC URL" value={jdbcUrl} onChange={(e) => setJdbcUrl(e.target.value)} />
          <input type="text" placeholder="User" value={user} onChange={(e) => setUser(e.target.value)} />
          <input type="text" placeholder="JWT Token" value={jwtToken} onChange={(e) => setJwtToken(e.target.value)} />
          <button onClick={testConnection}>Test Connection</button>
          <p>{connectionStatus}</p>
        </div>

        <div className="card">
          <button onClick={fetchTables}>Fetch Tables</button>
          {tables.length > 0 && (
            <ul>
              {tables.map((table) => (
                <li key={table}>{table}</li>
              ))}
            </ul>
          )}
        </div>

        <div className="card">
          <button onClick={handleFetchColumnsClick}>Fetch Columns</button>
          {showTableInput && (
            <div>
              <input
                type="text"
                placeholder="Enter table name"
                value={tableNameForColumns}
                onChange={(e) => setTableNameForColumns(e.target.value)}
              />
              <button onClick={fetchColumns}>Get Columns</button>
            </div>
          )}

          
            <div>
              <h4>Select Columns</h4>
              <div className="checkbox-grid">
                {columns.map((col) => (
                  <label key={col}>
                    <input
                      type="checkbox"
                      checked={selectedColumns.includes(col)}
                      onChange={() => handleColumnSelection(col)}
                    />
                    {col}
                  </label>
                ))}
              </div>

              <div>
                <label>Upload CSV File</label>
                <input type="file" onChange={handleFileChange} />
                <button onClick={uploadData}>Upload to ClickHouse</button>
                <button onClick={previewCSV}>Preview Data</button>
              </div>

              <div>
                <label>Export to File Path</label>
                <input
                  type="text"
                  placeholder="Enter file path to export CSV"
                  value={exportFilePath}
                  onChange={(e) => setExportFilePath(e.target.value)}
                />
                <button onClick={exportData}>Export to Flat File</button>
              </div>

              {isPreviewLoading && <p>Loading preview...</p>}
              {previewError && <p className="error">{previewError}</p>}
              {previewData.length > 0 && (
                <div className="preview-table">
                  <h4>Preview (first 100 rows)</h4>
                  <table>
                    <thead>
                      <tr>
                        {Object.keys(previewData[0]).map((header) => (
                          <th key={header}>{header}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {previewData.map((row, idx) => (
                        <tr key={idx}>
                          {Object.values(row).map((val, i) => (
                            <td key={i}>{val}</td>
                          ))}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          
        </div>
      </div>
    </div>
  );
}

export default App;
