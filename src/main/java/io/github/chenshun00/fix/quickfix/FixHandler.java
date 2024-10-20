package io.github.chenshun00.fix.quickfix;

import quickfix.*;
import quickfix.field.*;

import java.util.UUID;

/**
 * @author chenshun00@gmail.com
 * @since 2024/8/18 11:46
 */
public abstract class FixHandler {

    protected MessageFactory messageFactory = new DefaultMessageFactory();

    public abstract void handleMessage(Message src, SessionID sessionID) throws FieldNotFound;

    protected void getAndSet(Message source, Message des, int field) throws FieldNotFound {
        if (source.isSetField(field)) {
            des.setString(field, source.getString(field));
        }
    }

    protected void getAndSetChar(Message source, Message des, int field) throws FieldNotFound {
        if (source.isSetField(field)) {
            des.setChar(field, source.getChar(field));
        }
    }

    protected void getAndSetDouble(Message source, Message des, int field) throws FieldNotFound {
        if (source.isSetField(field)) {
            des.setDouble(field, source.getDouble(field));
        }
    }

    protected void handleNew(Message src, SessionID sessionID) throws FieldNotFound {
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
        res.setField(new ExecType(ExecType.NEW));
        res.setField(new OrdStatus(OrdStatus.NEW));

        res.setField(new LastPx(0));
        res.setField(new LastShares(0));
        res.setField(new LeavesQty(src.getDouble(OrderQty.FIELD)));
        res.setField(new CumQty(0));
        res.setField(new AvgPx(0));

        Session.lookupSession(sessionID).send(res);
    }

    protected Message buildFilledMessage(Message message, SessionID sessionID) throws FieldNotFound {
        final Message res = messageFactory.create(message.getHeader().getString(BeginString.FIELD), MsgType.EXECUTION_REPORT);
        res.getHeader().setField(new SenderCompID(sessionID.getSenderCompID()));
        res.getHeader().setField(new TargetCompID(sessionID.getTargetCompID()));
        //设置body, 先看哪些字段是必须的
        getAndSet(message, res, Account.FIELD);
        getAndSet(message, res, ClOrdID.FIELD);
        getAndSet(message, res, OrigClOrdID.FIELD);
        getAndSet(message, res, Symbol.FIELD);
        getAndSetChar(message, res, Side.FIELD);
        getAndSetChar(message, res, OrdType.FIELD);
        getAndSet(message, res, OrderQty.FIELD);
        getAndSetDouble(message, res, Price.FIELD);
        getAndSetDouble(message, res, StopPx.FIELD);

        res.setField(new OrderID(this.getOrderId(message, sessionID)));
        res.setField(new ExecID(UUID.randomUUID().toString().replaceAll("-", "")));
        res.setField(new ExecTransType(ExecTransType.NEW));
        res.setField(new ExecType(ExecType.FILL));
        res.setField(new OrdStatus(OrdStatus.FILLED));

        final double price = message.getDouble(Price.FIELD);
        res.setField(new LastPx(price));
        res.setField(new LastShares(message.getDouble(OrderQty.FIELD)));
        res.setField(new LeavesQty(0));
        res.setField(new CumQty(message.getDouble(OrderQty.FIELD)));
        res.setField(new AvgPx(price));
        return res;
    }

    protected String getOrderId(Message src, SessionID sessionID) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
