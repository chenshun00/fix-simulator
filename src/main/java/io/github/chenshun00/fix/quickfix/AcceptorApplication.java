package io.github.chenshun00.fix.quickfix;

import io.github.chenshun00.fix.config.AutoConfig;
import io.github.chenshun00.fix.quickfix.holder.SessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import quickfix.*;
import quickfix.field.*;

import java.util.Objects;
import java.util.UUID;

/**
 * @author chenshun00@gmail.com
 * @since 2024/2/28 19:24
 */
@Slf4j
@Service
public class AcceptorApplication extends MessageCracker implements Application {

    private MessageFactory messageFactory = new DefaultMessageFactory();

    @Autowired
    private AutoConfig autoConfig;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onCreate(SessionID sessionID) {
        log.info("[onCreate][{}]", sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        final Session session = Session.lookupSession(sessionID);
        log.info("[onLogon][{}][{}]", sessionID, session);
        if (session == null) {
            log.error("[session is null]");
            return;
        }
        SessionHolder.put(sessionID, session);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        SessionHolder.remove(sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {

    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {

    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {

    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        this.crack(message, sessionID);
    }

    @Override
    protected void onMessage(Message message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        final String msgType = message.getHeader().getString(MsgType.FIELD);

        if (Objects.equals(msgType, MsgType.ORDER_SINGLE)) {
            if (autoConfig.isSingle() && message.isSetField(OrderQty.FIELD)) {
                final String handlerBean = autoConfig.getHandlerBean(message.getDouble(OrderQty.FIELD));
                if (handlerBean == null) {
                    return;
                }
                final Object handler = applicationContext.getBean(handlerBean);
                FixHandler fixHandler = (FixHandler) handler;
                fixHandler.handleMessage(message, sessionID);
            }
        } else if (Objects.equals(msgType, MsgType.ORDER_CANCEL_REPLACE_REQUEST)) {
            Session.lookupSession(sessionID).send(this.buildReplacedMessage(message, sessionID));
        } else if (Objects.equals(msgType, MsgType.ORDER_CANCEL_REQUEST)) {

        }
    }

    protected Message buildReplacedMessage(Message message, SessionID sessionID) throws FieldNotFound {
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

        res.setField(new OrderID(UUID.randomUUID().toString().replaceAll("-", "")));
        res.setField(new ExecID(UUID.randomUUID().toString().replaceAll("-", "")));
        res.setField(new ExecTransType(ExecTransType.NEW));
        res.setField(new ExecType(ExecType.REPLACED));
        res.setField(new OrdStatus(OrdStatus.NEW));

        res.setField(new LastPx(0));
        res.setField(new LastShares(0));
        res.setField(new LeavesQty(message.getDouble(OrderQty.FIELD)));
        res.setField(new CumQty(0));
        res.setField(new AvgPx(0));
        return res;
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

        res.setField(new OrderID(UUID.randomUUID().toString().replaceAll("-", "")));
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

    protected Message buildNewMessage(Message message, SessionID sessionID) throws FieldNotFound {
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

        res.setField(new OrderID(UUID.randomUUID().toString().replaceAll("-", "")));
        res.setField(new ExecID(UUID.randomUUID().toString().replaceAll("-", "")));
        res.setField(new ExecTransType(ExecTransType.NEW));
        res.setField(new ExecType(ExecType.NEW));
        res.setField(new OrdStatus(OrdStatus.NEW));

        res.setField(new LastPx(0));
        res.setField(new LastShares(0));
        res.setField(new LeavesQty(message.getDouble(OrderQty.FIELD)));
        res.setField(new CumQty(0));
        res.setField(new AvgPx(0));

        return res;
    }

    private void getAndSet(Message source, Message des, int field) throws FieldNotFound {
        if (source.isSetField(field)) {
            des.setString(field, source.getString(field));
        }
    }

    private void getAndSetChar(Message source, Message des, int field) throws FieldNotFound {
        if (source.isSetField(field)) {
            des.setChar(field, source.getChar(field));
        }
    }

    private void getAndSetDouble(Message source, Message des, int field) throws FieldNotFound {
        if (source.isSetField(field)) {
            des.setDouble(field, source.getDouble(field));
        }
    }

}
