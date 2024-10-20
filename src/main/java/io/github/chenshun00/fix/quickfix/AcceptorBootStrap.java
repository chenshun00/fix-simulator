package io.github.chenshun00.fix.quickfix;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import quickfix.*;

/**
 * @author chenshun00@gmail.com
 * @since 2024/2/28 19:30
 */
@Service
public class AcceptorBootStrap implements InitializingBean {

    private final ThreadedSocketAcceptor threadedSocketAcceptor;

    public AcceptorBootStrap(AcceptorApplication acceptorApplication,
                             MessageStoreFactory messageStoreFactory,
                             SessionSettings settings,
                             LogFactory logFactory) throws ConfigError {
        MessageFactory messageFactory = new DefaultMessageFactory();
        threadedSocketAcceptor = new ThreadedSocketAcceptor(acceptorApplication, messageStoreFactory, settings,
                logFactory, messageFactory, 10000);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        threadedSocketAcceptor.start();
    }

    @PreDestroy
    public void destroy(){
        threadedSocketAcceptor.stop();
    }
}
