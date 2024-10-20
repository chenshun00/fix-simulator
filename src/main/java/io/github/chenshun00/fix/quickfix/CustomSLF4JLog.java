package io.github.chenshun00.fix.quickfix;

import org.slf4j.Logger;
import quickfix.*;

/**
 * @author chenshun00@gmail.com
 * @since 2024/2/28 22:54
 */
public class CustomSLF4JLog extends SLF4JLog {

    public CustomSLF4JLog(SessionID sessionID, String eventCategory, String errorEventCategory, String incomingMsgCategory, String outgoingMsgCategory, boolean prependSessionID, boolean logHeartbeats, String inCallerFQCN) {
        super(sessionID, eventCategory, errorEventCategory, incomingMsgCategory, outgoingMsgCategory, prependSessionID, logHeartbeats, inCallerFQCN);
    }

    @Override
    protected void log(Logger log, String text) {
        super.log(log, text.replace("\001", " | "));
    }
}

