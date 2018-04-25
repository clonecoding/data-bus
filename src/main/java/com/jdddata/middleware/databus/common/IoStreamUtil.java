package com.jdddata.middleware.databus.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoStreamUtil {
    private static Logger log = LoggerFactory.getLogger(IoStreamUtil.class);

    public static void closeStream(InputStream is) {

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.debug("", e);
            }
        }
    }

    public static void closeStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                log.debug("", e);
            }
        }
    }
}
