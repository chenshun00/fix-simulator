package io.github.chenshun00.fix.quickfix.handler.single;

import io.github.chenshun00.fix.quickfix.FixHandler;
import org.springframework.stereotype.Component;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;

import java.util.List;

/**
 * @author chenshun00@gmail.com
 * @since 2024/8/18 11:44
 */
@Component
public class New extends FixHandler {

    //回复一个已报
    @Override
    public void handleMessage(Message src, SessionID sessionID) throws FieldNotFound {
        this.handleNew(src, sessionID);
    }
}
