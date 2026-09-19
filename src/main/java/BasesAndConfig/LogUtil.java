package BasesAndConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Wraps log4j2 so callers just say LogUtil.info("...") instead of grabbing their
// own Logger instance every time. The [3] below looks weird but it's just walking
// the stack up past getStackTrace/logger()/info() to find whoever actually called us.
public class LogUtil {

    private LogUtil() {
    }

    private static Logger logger() {
        return LogManager.getLogger(Thread.currentThread().getStackTrace()[3].getClassName());
    }

    public static void info(String message) {
        logger().info(message);
    }

    public static void warn(String message) {
        logger().warn(message);
    }

    public static void error(String message) {
        logger().error(message);
    }

    public static void debug(String message) {
        logger().debug(message);
    }
}
