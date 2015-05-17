/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.sf.redi.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import net.sf.redi.Utils;

import java.util.ResourceBundle;

public class I18NLogger {
    private static final LogFactory _factory = new LogFactoryImpl();
    private Log _log;
    private ResourceBundle _bundle;

    public I18NLogger(String name) {
        this(name, Utils.getResourceBundle());
    }

    public I18NLogger(String name, ResourceBundle resourceBundle) {
        _log = _factory.getInstance(name);
        _bundle = resourceBundle;
    }

    public boolean isDebugEnabled() {
        return _log.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return _log.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return _log.isFatalEnabled();
    }

    public boolean isInfoEnabled() {
        return _log.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return _log.isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        return _log.isWarnEnabled();
    }

    public void trace(String message, Object... args) {
        _log.trace(Utils.format(_bundle, message, args));
    }

    public void trace(String message, Throwable t, Object... args) {
        _log.trace(Utils.format(_bundle, message, args), t);
    }

    public void debug(String message, Object... args) {
        _log.debug(Utils.format(_bundle, message, args));
    }

    public void debug(String message, Throwable t, Object... args) {
        _log.debug(Utils.format(_bundle, message, args), t);
    }

    public void info(String message, Object... args) {
        _log.info(Utils.format(_bundle, message, args));
    }

    public void info(String message, Throwable t, Object... args) {
        _log.info(Utils.format(_bundle, message, args), t);
    }

    public void warn(String message, Object... args) {
        _log.warn(Utils.format(_bundle, message, args));
    }

    public void warn(String message, Throwable t, Object... args) {
        _log.warn(Utils.format(_bundle, message, args), t);
    }

    public void error(String message, Object... args) {
        _log.error(Utils.format(_bundle, message, args));
    }

    public void error(String message, Throwable t, Object... args) {
        _log.error(Utils.format(_bundle, message, args), t);
    }

    public void fatal(String message, Object... args) {
        _log.fatal(Utils.format(_bundle, message, args));
    }

    public void fatal(String message, Throwable t, Object... args) {
        _log.fatal(Utils.format(_bundle, message, args), t);
    }
}