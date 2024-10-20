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
public class PartiallyFilled extends FixHandler {

    //回复1个已报, 一个部成
    @Override
    public void handleMessage(Message src, SessionID sessionID) throws FieldNotFound {
        this.handleNew(src, sessionID);
        this.buildPartiallyFilledMessage(src, sessionID);
    }

    public void buildPartiallyFilledMessage(Message src, SessionID sessionID) throws FieldNotFound {
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
        res.setField(new ExecType(ExecType.PARTIAL_FILL));
        res.setField(new OrdStatus(OrdStatus.PARTIALLY_FILLED));

        res.setField(new LastPx(src.getDouble(Price.FIELD)));
        res.setField(new LastShares(src.getDouble(OrderQty.FIELD) / 3));
        res.setField(new LeavesQty(src.getDouble(OrderQty.FIELD) - src.getDouble(OrderQty.FIELD) / 3));
        res.setField(new CumQty(src.getDouble(OrderQty.FIELD) / 3));
        res.setField(new AvgPx(src.getDouble(Price.FIELD)));

        Session.lookupSession(sessionID).send(res);
    }
}
