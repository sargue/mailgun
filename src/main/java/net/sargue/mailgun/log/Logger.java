package net.sargue.mailgun.log;

/**
 * The logger abstraction.
 * <p>
 * This logger provides abstraction over the three logger APIs supported optionally.
 * These are (in order of preference):
 * <ul>
 * <li>slf4j</li>
 * <li>log4j</li>
 * <li>jav.util.logging</li>
 * </ul>
 * This {@link #getLogger(Class)} tries to instantiate any of the above loggers,
 * catching potential {@link NoClassDefFoundError}'s in case any logger API
 * cannot be found on the classpath.
 * <p>
 * <strong>Parameterized logging</strong>
 * <p>
 * The logging methods of this class use parameterized logging for improved performance and
 * code readability. The idea come from the
 * <a href="https://www.slf4j.org/faq.html#logging_performance">SLF4J project</a>.
 * The idea is to use placeholders to build the log message and throw the values you want
 * on the placeholders as arguments. An example:
 * <pre>
 *     log.info("User {} logged successfully", user.getName());
 * </pre>
 * <p>
 * <strong>Copyright notes</strong>
 * <p>
 * Parts of this class and related classes, and the overall idea, are from the
 * <a href="https://www.jooq.org/">jOOQ</a> project. The original code is authored by
 * Lukas Eder (lead developer of jOOQ). All bugs and mistakes are surely introduced
 * by my own changes so I am the one to blame.
 *
 * @author Sergi Baila
 * @author Lukas Eder
 */
public class Logger implements Log {
    /**
     * The parameterized placeholder.
     */
    private static final String PLACEHOLDER                    = "{}";

    /**
     * The SLF4j Logger instance, if available.
     */
    private org.slf4j.Logger                  slf4j;

    /**
     * The log4j Logger instance, if available.
     */
    private org.apache.logging.log4j.Logger   log4j;

    /**
     * The JDK Logger instance, if available.
     */
    private java.util.logging.Logger           util;

    /**
     * Whether calls to {@link #trace(String)} are possible.
     */
    private boolean                            supportsTrace   = true;

    /**
     * Whether calls to {@link #debug(String)} are possible.
     */
    private boolean                            supportsDebug   = true;

    /**
     * Whether calls to {@link #info(String)} are possible.
     */
    private boolean                            supportsInfo    = true;

    /**
     * Get a logger wrapper for a class.
     */
    public static Logger getLogger(Class<?> clazz) {
        Logger result = new Logger();

        // Prioritise slf4j
        try {
            result.slf4j = org.slf4j.LoggerFactory.getLogger(clazz);
        }

        // If that's not on the classpath, try log4j instead
        catch (Exception e1) {
            try {
                result.log4j = org.apache.logging.log4j.LogManager.getLogger(clazz);
            }

            // If that's not on the classpath either, ignore most of logging
            catch (Exception e2) {
                result.util = java.util.logging.Logger.getLogger(clazz.getName());
            }
        }

        try {
            result.isInfoEnabled();
        }
        catch (Exception e) {
            result.supportsInfo = false;
        }

        try {
            result.isDebugEnabled();
        }
        catch (Exception e) {
            result.supportsDebug = false;
        }

        try {
            result.isTraceEnabled();
        }
        catch (Exception e) {
            result.supportsTrace = false;
        }

        return result;
    }

    @Override
    public boolean isTraceEnabled() {
        if (!supportsTrace)
            return false;
        else if (slf4j != null)
            return slf4j.isTraceEnabled();
        else if (log4j != null)
            return log4j.isTraceEnabled();
        else
            return util.isLoggable(java.util.logging.Level.FINER);
    }

    @Override
    public void trace(String msg) {
        if (slf4j != null)
            slf4j.trace(msg);
        else if (log4j != null)
            log4j.trace(msg);
        else
            util.finer(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        if (!isTraceEnabled()) return;

        if (slf4j != null)
            slf4j.trace(format, arg);
        else if (log4j != null)
            log4j.trace(format(format, arg));
        else
            util.finer(format(format, arg));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (slf4j != null)
            slf4j.trace(format, arg1, arg2);
        else if (log4j != null)
            log4j.trace(format(format, arg1, arg2));
        else
            util.finer(format(format, arg1, arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (!isTraceEnabled()) return;

        if (slf4j != null)
            slf4j.trace(format, (Object[]) arguments);
        else if (log4j != null)
            log4j.trace(format(format, (Object[]) arguments));
        else
            util.finer(format(format, (Object[]) arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (!isTraceEnabled()) return;

        if (slf4j != null)
            slf4j.trace(msg, t);
        else if (log4j != null)
            log4j.trace(msg, t);
        else
            util.log(java.util.logging.Level.FINER, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        if (!supportsDebug)
            return false;
        else if (slf4j != null)
            return slf4j.isDebugEnabled();
        else if (log4j != null)
            return log4j.isDebugEnabled();
        else
            return util.isLoggable(java.util.logging.Level.FINE);
    }

    @Override
    public void debug(String msg) {
        if (slf4j != null)
            slf4j.debug(msg);
        else if (log4j != null)
            log4j.debug(msg);
        else
            util.fine(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        if (!isDebugEnabled()) return;

        if (slf4j != null)
            slf4j.debug(format, arg);
        else if (log4j != null)
            log4j.debug(format(format, arg));
        else
            util.fine(format(format, arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (slf4j != null)
            slf4j.debug(format, arg1, arg2);
        else if (log4j != null)
            log4j.debug(format(format, arg1, arg2));
        else
            util.fine(format(format, arg1, arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (!isDebugEnabled()) return;

        if (slf4j != null)
            slf4j.debug(format, (Object[]) arguments);
        else if (log4j != null)
            log4j.debug(format(format, (Object[]) arguments));
        else
            util.fine(format(format, (Object[]) arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (!isDebugEnabled()) return;

        if (slf4j != null)
            slf4j.debug(msg, t);
        else if (log4j != null)
            log4j.debug(msg, t);
        else
            util.log(java.util.logging.Level.FINE, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        if (!supportsInfo)
            return false;
        else if (slf4j != null)
            return slf4j.isInfoEnabled();
        else if (log4j != null)
            return log4j.isInfoEnabled();
        else
            return util.isLoggable(java.util.logging.Level.INFO);
    }

    @Override
    public void info(String msg) {
        if (slf4j != null)
            slf4j.info(msg);
        else if (log4j != null)
            log4j.info(msg);
        else
            util.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        if (!isInfoEnabled()) return;

        if (slf4j != null)
            slf4j.info(format, arg);
        else if (log4j != null)
            log4j.info(format(format, arg));
        else
            util.info(format(format, arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (slf4j != null)
            slf4j.info(format, arg1, arg2);
        else if (log4j != null)
            log4j.info(format(format, arg1, arg2));
        else
            util.info(format(format, arg1, arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        if (!isInfoEnabled()) return;

        if (slf4j != null)
            slf4j.info(format, (Object[]) arguments);
        else if (log4j != null)
            log4j.info(format(format, (Object[]) arguments));
        else
            util.info(format(format, (Object[]) arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        if (!isInfoEnabled()) return;

        if (slf4j != null)
            slf4j.info(msg, t);
        else if (log4j != null)
            log4j.info(msg, t);
        else
            util.log(java.util.logging.Level.INFO, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        if (slf4j != null)
            return slf4j.isWarnEnabled();
        else if (log4j != null)
            return log4j.isWarnEnabled();
        else
            return util.isLoggable(java.util.logging.Level.WARNING);
    }

    @Override
    public void warn(String msg) {
        if (slf4j != null)
            slf4j.warn(msg);
        else if (log4j != null)
            log4j.warn(msg);
        else
            util.warning(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        if (!isWarnEnabled()) return;

        if (slf4j != null)
            slf4j.warn(format, arg);
        else if (log4j != null)
            log4j.warn(format(format, arg));
        else
            util.warning(format(format, arg));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (slf4j != null)
            slf4j.warn(format, arg1, arg2);
        else if (log4j != null)
            log4j.warn(format(format, arg1, arg2));
        else
            util.warning(format(format, arg1, arg2));
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (!isWarnEnabled()) return;

        if (slf4j != null)
            slf4j.warn(format, (Object[]) arguments);
        else if (log4j != null)
            log4j.warn(format(format, (Object[]) arguments));
        else
            util.warning(format(format, (Object[]) arguments));
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (!isWarnEnabled()) return;

        if (slf4j != null)
            slf4j.warn(msg, t);
        else if (log4j != null)
            log4j.warn(msg, t);
        else
            util.log(java.util.logging.Level.WARNING, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        if (slf4j != null)
            return slf4j.isErrorEnabled();
        else if (log4j != null)
            return log4j.isErrorEnabled();
        else
            return util.isLoggable(java.util.logging.Level.SEVERE);
    }

    @Override
    public void error(String msg) {
        if (slf4j != null)
            slf4j.error(msg);
        else if (log4j != null)
            log4j.error(msg);
        else
            util.severe(msg);
    }

    @Override
    public void error(String format, Object arg) {
        if (!isErrorEnabled()) return;

        if (slf4j != null)
            slf4j.error(format, arg);
        else if (log4j != null)
            log4j.error(format(format, arg));
        else
            util.severe(format(format, arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (slf4j != null)
            slf4j.error(format, arg1, arg2);
        else if (log4j != null)
            log4j.error(format(format, arg1, arg2));
        else
            util.severe(format(format, arg1, arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        if (!isErrorEnabled()) return;

        if (slf4j != null)
            slf4j.error(format, (Object[]) arguments);
        else if (log4j != null)
            log4j.error(format(format, (Object[]) arguments));
        else
            util.severe(format(format, (Object[]) arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        if (!isErrorEnabled()) return;

        if (slf4j != null)
            slf4j.error(msg, t);
        else if (log4j != null)
            log4j.error(msg, t);
        else
            util.log(java.util.logging.Level.SEVERE, msg, t);
    }

    private String format(String format, Object... arguments) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int j;
        for (Object argument : arguments) {
            j = format.indexOf(PLACEHOLDER, i);
            sb.append(format, i, j);
            sb.append(argument.toString());
            i = j + 2;
        }
        sb.append(format, i, format.length());
        return sb.toString();
    }
}
