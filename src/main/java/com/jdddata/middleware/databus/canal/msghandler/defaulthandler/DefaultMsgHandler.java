package com.jdddata.middleware.databus.canal.msghandler.defaulthandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.jdddata.middleware.databus.canal.Annotation.CanalMsgProcessService;
import com.jdddata.middleware.databus.canal.api.ICanalBuildMsg;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.canal.entity.CanalRocketMsg;
import com.jdddata.middleware.databus.canal.msghandler.defaulthandler.entity.CanalRocketmqDbColumn;
import com.jdddata.middleware.databus.canal.msghandler.defaulthandler.entity.CanalRocketmqEntry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.SystemUtils;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CanalMsgProcessService
public class DefaultMsgHandler implements ICanalBuildMsg {

  private static final Logger logger = LoggerFactory.getLogger(DefaultMsgHandler.class);
  /**
   * 分隔符
   */
  private static final String SEP = SystemUtils.LINE_SEPARATOR;
  private static DefaultMsgHandler instance;
  private static String contextFormat = null;
  private static String rowFormat = null;
  private static String transactionFormat = null;
  private static String topicPrefix = null;
  private CanalContext context;
  private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public DefaultMsgHandler(CanalContext context) {
    this.context = context;
  }

  public static DefaultMsgHandler instance(CanalContext context) {
    if (null == instance) {
      instance = new DefaultMsgHandler(context);
    }
    return instance;


  }

  @Override
  public CanalRocketMsg buildMsg(Message message) {

    CanalRocketMsg canalRocketMsg = new CanalRocketMsg();

    List<CanalEntry.Entry> entrys = message.getEntries();

    for (CanalEntry.Entry entry : entrys) {
      //定义一个可序列化的推送给 rocketmq 的对象
      CanalRocketmqEntry canalRocketmqEntry = new CanalRocketmqEntry();
      long executeTime = entry.getHeader().getExecuteTime();
      // 变更数据的执行时间
      canalRocketmqEntry.setExecuteTime(executeTime);
      // 数据类型
      canalRocketmqEntry.setEntryType(entry.getEntryType().getNumber());
      // binlog 文件名
      canalRocketmqEntry.setLogFileName(entry.getHeader().getLogfileName());
      // binlog offset
      canalRocketmqEntry.setLogFileOffset(entry.getHeader().getLogfileOffset());

      long delayTime = new Date().getTime() - executeTime;

      if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
          || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
        if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN) {
          CanalEntry.TransactionBegin begin;
          try {
            begin = CanalEntry.TransactionBegin.parseFrom(entry.getStoreValue());
          } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
          }
          // 打印事务头信息，执行的线程id，事务耗时
          logger.info(transactionFormat,
              entry.getHeader().getLogfileName(),
              entry.getHeader().getLogfileOffset(),
              entry.getHeader().getExecuteTime(),
              delayTime);
          logger.info(" BEGIN ----> Thread id: {}", begin.getThreadId());
        } else {
          CanalEntry.TransactionEnd end;
          try {
            end = CanalEntry.TransactionEnd.parseFrom(entry.getStoreValue());
          } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
          }
          // 打印事务提交信息，事务id
          logger.info("----------------\n END ----> transaction id: {}" + transactionFormat,
              end.getTransactionId(),
              entry.getHeader().getLogfileName(),
              String.valueOf(entry.getHeader().getLogfileOffset()),
              String.valueOf(entry.getHeader().getExecuteTime()), String.valueOf(delayTime));
        }
        continue;
      }

      if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
        CanalEntry.RowChange rowChange = null;
        try {
          rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (Exception e) {
          logger.error("parse event has an error , data:" + entry.toString(), e);
        }

        CanalEntry.EventType eventType = rowChange.getEventType();
        // ddl dml create index .....
        canalRocketmqEntry.setEventType(rowChange.getEventType().getNumber());
        // schema name 默认是数据库名
        canalRocketmqEntry.setSchemaName(entry.getHeader().getSchemaName());
        // table name
        canalRocketmqEntry.setTableName(entry.getHeader().getTableName());
        logger.info(rowFormat,
            entry.getHeader().getLogfileName(),
            entry.getHeader().getLogfileOffset(), entry.getHeader().getSchemaName(),
            entry.getHeader().getTableName(), eventType,
            entry.getHeader().getExecuteTime(), delayTime);

        if (eventType == CanalEntry.EventType.QUERY || rowChange.getIsDdl()) {
          logger.info(" sql ----> " + rowChange.getSql() + SEP);
          continue;
        }

        StringBuilder columnBuilder = new StringBuilder("\n");
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
          List<CanalRocketmqDbColumn> dbColumns = printColumns(eventType, rowData, columnBuilder);
          canalRocketmqEntry.addData(dbColumns);
        }
        logger.info(columnBuilder.toString());
        //current 数据库名字
        String dbName = entry.getHeader().getSchemaName();
        //current 表名
        String tableName = entry.getHeader().getTableName();
        //current position
        Long binlogPosition = entry.getHeader().getLogfileOffset();

        byte[] msgBtyes = JSON.toJSONBytes(canalRocketmqEntry);

        canalRocketMsg.setArg(dbName + "_" + tableName);
        canalRocketMsg.setSelector(new SelectMessageQueueByHash());
        canalRocketMsg.setTopic(context.getTopicPrefix() + dbName.toUpperCase());
        org.apache.rocketmq.common.message.Message value = new org.apache.rocketmq.common.message.Message(
            canalRocketMsg.getTopic(), (String) canalRocketMsg.getArg(), "POS" + binlogPosition,
            msgBtyes);
        canalRocketMsg.setMsg(value);
      }
    }
    return canalRocketMsg;
  }

  /**
   * print column
   *
   * @param builder StringBuilder
   */
  private List<CanalRocketmqDbColumn> printColumns(CanalEntry.EventType eventType,
      CanalEntry.RowData rowData, StringBuilder builder) {
    List<CanalRocketmqDbColumn> dbColumns = new ArrayList<>();
    if (eventType == CanalEntry.EventType.DELETE) {
      for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
        dbColumns.add(printColumn(CanalEntry.EventType.DELETE, column, builder));
      }
    } else if (eventType == CanalEntry.EventType.INSERT) {
      for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
        dbColumns.add(printColumn(CanalEntry.EventType.INSERT, column, builder));
      }
    } else if (eventType == CanalEntry.EventType.UPDATE) {
      for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
        CanalRocketmqDbColumn dbColumn = printColumn(CanalEntry.EventType.UPDATE, column, builder);
        for (CanalEntry.Column before : rowData.getBeforeColumnsList()) {
          if (before.getName().equals(dbColumn.getName())) {
            dbColumn.setBeforeValue(before.getValue());
          }
        }
        dbColumns.add(dbColumn);
      }
    }
    return dbColumns;
  }

  private CanalRocketmqDbColumn printColumn(CanalEntry.EventType eventType,
      CanalEntry.Column column, StringBuilder builder) {

    builder.append(column.getName()).append(" : ").append(column.getValue());
    builder.append("    type=").append(column.getMysqlType());
    if (column.getUpdated()) {
      builder.append("    update=").append(column.getUpdated());
    }
    builder.append(SEP);

    CanalRocketmqDbColumn dbColumn = new CanalRocketmqDbColumn();
    dbColumn.setName(column.getName());
    if (eventType == CanalEntry.EventType.INSERT) {
      dbColumn.setBeforeValue(null);
      dbColumn.setAfterValue(column.getValue());
    }
    if (eventType == CanalEntry.EventType.DELETE) {
      dbColumn.setBeforeValue(column.getValue());
      dbColumn.setAfterValue(null);
    }
    if (eventType == CanalEntry.EventType.UPDATE) {
      dbColumn.setBeforeValue(null);
      dbColumn.setAfterValue(column.getValue());
    }
    dbColumn.setType(column.getMysqlType());
    dbColumn.setUpdated(column.getUpdated());
    return dbColumn;
  }
}