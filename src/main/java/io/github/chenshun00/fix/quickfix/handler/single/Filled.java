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
public class Filled extends FixHandler {

    //回复2个部成,一个全成
    @Override
    public void handleMessage(Message src, SessionID sessionID) throws FieldNotFound {
        this.handleNew(src, sessionID);
        final double orderQty = src.getDouble(OrderQty.FIELD);

        double cumQty = 0;
        double lastShare = orderQty / 3;
        for (int i = 0; i < 3; i++) {
            cumQty += orderQty / 3;
            if (i == 2) {
                this.buildFilled(src, sessionID, orderQty, lastShare, new ExecType(ExecType.FILL), new OrdStatus(OrdStatus.FILLED));
            } else {
                this.buildFilled(src, sessionID, cumQty, lastShare, new ExecType(ExecType.PARTIAL_FILL), new OrdStatus(OrdStatus.PARTIALLY_FILLED));
            }
        }
    }

    public void buildFilled(Message src, SessionID sessionID, double cumQty, double lastShare, ExecType execType, OrdStatus ordStatus) throws FieldNotFound {
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
        res.setField(execType);
        res.setField(ordStatus);

        res.setField(new LastPx(src.getDouble(Price.FIELD)));
        res.setField(new LastShares(lastShare));
        res.setField(new LeavesQty(src.getDouble(OrderQty.FIELD) - cumQty));
        res.setField(new CumQty(cumQty));
        res.setField(new AvgPx(src.getDouble(Price.FIELD)));

        Session.lookupSession(sessionID).send(res);
    }
}
