package io.github.chenshun00.fix.quickfix.handler.single;

import io.github.chenshun00.fix.quickfix.FixHandler;
import org.springframework.stereotype.Component;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.*;

import java.util.UUID;

/**
 * @author chenshun00@gmail.com
 * @since 2024/8/18 11:51
 */
@Component
public class Rejected extends FixHandler {

    //直接回复一个已报, 一个拒绝
    @Override
    public void handleMessage(Message src, SessionID sessionID) throws FieldNotFound {
        this.handleNew(src, sessionID);
        this.handleReject(src, sessionID);
    }

    public void handleReject(Message src, SessionID sessionID) throws FieldNotFound {
        final Message res = messageFactory.create(src.getHeader().getString(BeginString.FIELD), MsgType.EXECUTION_REPORT);
        res.getHeader().setField(new SenderCompID(sessionID.getSenderCompID()));
        res.getHeader().setField(new TargetCompID(sessionID.getTargetCompID()));
        //设置body, 先看哪些字段是必须的
        getAndSet(src, res, Account.FIELD);
        getAndSet(src, res, ClOrdID.FIELD);
        getAndSet(src, res, OrigClOrdID.FIELD);
        getAndSet(src, res, Symbol.FIELD);
        getAndSetChar(src, res, Side.FIELD);
        getAndSetChar(src, res, OrdType.FIELD);
        getAndSet(src, res, OrderQty.FIELD);
        getAndSetDouble(src, res, Price.FIELD);
        getAndSetDouble(src, res, StopPx.FIELD);

        res.setField(new OrderID(this.getOrderId(src, sessionID)));
        res.setField(new ExecID(UUID.randomUUID().toString().replaceAll("-", "")));
        res.setField(new ExecTransType(ExecTransType.NEW));
        res.setField(new ExecType(ExecType.REJECTED));
        res.setField(new OrdStatus(OrdStatus.REJECTED));

        res.setField(new LastPx(0));
        res.setField(new LastShares(0));
        res.setField(new LeavesQty(0));
        res.setField(new CumQty(0));
        res.setField(new AvgPx(0));

        Session.lookupSession(sessionID).send(res);
    }
}
