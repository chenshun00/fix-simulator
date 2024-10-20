package io.github.chenshun00.fix.dao.domain;

import lombok.Data;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;

import java.time.LocalDateTime;

/**
 * @author chenshun00@gmail.com
 * @since 2024/10/20 10:16
 */
@Data
public class Entrust {
    private long id;
    private String senderCompId;
    private String targetCompId;
    private String msgType;
    private String symbol;
    private String text;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static Entrust buildEntrust(Message message) throws FieldNotFound {
        Entrust entrust = new Entrust();
        entrust.setSenderCompId(message.getHeader().getString(SenderCompID.FIELD));
        entrust.setTargetCompId(message.getHeader().getString(TargetCompID.FIELD));
        entrust.setMsgType(message.getHeader().getString(MsgType.FIELD));
        entrust.setSymbol(message.getString(Symbol.FIELD));
        entrust.setText(message.toString());
        entrust.setCreateTime(LocalDateTime.now());
        entrust.setUpdateTime(LocalDateTime.now());
        return entrust;
    }
}
