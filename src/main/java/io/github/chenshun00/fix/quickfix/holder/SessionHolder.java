package io.github.chenshun00.fix.quickfix.holder;

import quickfix.Session;
import quickfix.SessionID;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenshun00@gmail.com
 * @since 2024/2/28 19:25
 */
public class SessionHolder {
    private final static Map<SessionID, Session> SESSION_ID_SESSION_MAP = new HashMap<>();

    public static void put(SessionID sessionID, Session session) {
        SESSION_ID_SESSION_MAP.put(sessionID, session);
    }

    public static void remove(SessionID sessionID) {
        SESSION_ID_SESSION_MAP.remove(sessionID);
    }
}
