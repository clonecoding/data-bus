package com.jdddata.middleware.databus.canal.msghandler.defaulthandler.entity;

import java.util.ArrayList;
import java.util.List;

public class CanalRocketmqEntry {

  private long executeTime;
  private int entryType;
  private String logFileName;
  private long logFileOffset;
  private int eventType;
  private String schemaName;
  private String tableName;
  private List<List<CanalRocketmqDbColumn>> datas = new ArrayList<>();

  public List<List<CanalRocketmqDbColumn>> getDatas() {
    return datas;
  }

  public void addData(List<CanalRocketmqDbColumn> data) {
    this.datas.add(data);
  }

  public long getExecuteTime() {
    return executeTime;
  }

  public void setExecuteTime(long executeTime) {
    this.executeTime = executeTime;
  }

  public int getEntryType() {
    return entryType;
  }

  public void setEntryType(int entryType) {
    this.entryType = entryType;
  }

  public String getLogFileName() {
    return logFileName;
  }

  public void setLogFileName(String logFileName) {
    this.logFileName = logFileName;
  }

  public long getLogFileOffset() {
    return logFileOffset;
  }

  public void setLogFileOffset(long logFileOffset) {
    this.logFileOffset = logFileOffset;
  }

  public int getEventType() {
    return eventType;
  }

  public void setEventType(int eventType) {
    this.eventType = eventType;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
}
