package com.jdddata.middleware.databus.common;

import java.io.File;

public class DataBusConstants {

  /**
   * separator
   */
  public static final String SEP = File.separator;

  /**
   * canal work path
   */
  public static final String WORK_PATH = SEP + "data" + SEP + "work" + SEP + "canal" + SEP;

  /**
   * pid file
   */
  public static final String PID_FILE = WORK_PATH + "canal.pid";
  public static final int BATCH_SIZE = 1;
}
