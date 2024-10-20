package io.github.chenshun00.fix.config;

import io.github.chenshun00.fix.quickfix.CustomSLF4JLog;
import quickfix.LocationAwareLogFactory;
import quickfix.Log;
import quickfix.SessionID;
import quickfix.SessionSettings;

/**
 * @author chenshun00@gmail.com
 * @since 2024/2/28 23:00
 */
public class CustomSLF4JLogFactory implements LocationAwareLogFactory {

    /**
     * Log category for events.
     */
    public final static String SETTING_EVENT_CATEGORY = "SLF4JLogEventCategory";

    /**
     * Log category for error events.
     */
    public final static String SETTING_ERROR_EVENT_CATEGORY = "SLF4JLogErrorEventCategory";

    /**
     * Log category for incoming messages.
     */
    public final static String SETTING_INMSG_CATEGORY = "SLF4JLogIncomingMessageCategory";

    /**
     * Log category for outgoing messages.
     */
    public final static String SETTING_OUTMSG_CATEGORY = "SLF4JLogOutgoingMessageCategory";

    /**
     * Flag for prepending session ID to log output
     */
    public final static String SETTING_PREPEND_SESSION_ID = "SLF4JLogPrependSessionID";

    /**
     * Controls logging of heartbeats (Y or N)
     */
    public final static String SETTING_LOG_HEARTBEATS = "SLF4JLogHeartbeats";

    private final SessionSettings settings;

    public CustomSLF4JLogFactory(SessionSettings settings) {
        this.settings = settings;
    }

    public Log create(SessionID sessionID) {
        // it's actually code in AbstractLog that makes the final code to Log4J and not SLF4JLog itself
        // so send the AbstractLog here
        return create(sessionID, CustomSLF4JLogFactory.class.getName());
    }

    /**
     * This supports use of this log in a CompositeLogFactory.
     */
    public Log create(SessionID sessionID, String callerFQCN) {
        String eventCategory = null;
        String errorEventCategory = null;
        String incomingMsgCategory = null;
        String outgoingMsgCategory = null;
        boolean prependSessionID = true;
        boolean logHeartbeats = true;
        try {
            if (settings.isSetting(sessionID, SETTING_EVENT_CATEGORY)) {
                eventCategory = settings.getString(sessionID, SETTING_EVENT_CATEGORY);
            }
            if (settings.isSetting(sessionID, SETTING_ERROR_EVENT_CATEGORY)) {
                errorEventCategory = settings.getString(sessionID, SETTING_ERROR_EVENT_CATEGORY);
            }
            if (settings.isSetting(sessionID, SETTING_INMSG_CATEGORY)) {
                incomingMsgCategory = settings.getString(sessionID, SETTING_INMSG_CATEGORY);
            }
            if (settings.isSetting(sessionID, SETTING_OUTMSG_CATEGORY)) {
                outgoingMsgCategory = settings.getString(sessionID, SETTING_OUTMSG_CATEGORY);
            }
            if (settings.isSetting(sessionID, SETTING_PREPEND_SESSION_ID)) {
                prependSessionID = settings.getBool(sessionID, SETTING_PREPEND_SESSION_ID);
            }
            if (settings.isSetting(sessionID, SETTING_LOG_HEARTBEATS)) {
                logHeartbeats = settings.getBool(sessionID, SETTING_LOG_HEARTBEATS);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new CustomSLF4JLog(sessionID, eventCategory, errorEventCategory, incomingMsgCategory, outgoingMsgCategory,
                prependSessionID, logHeartbeats, callerFQCN);
    }

}
