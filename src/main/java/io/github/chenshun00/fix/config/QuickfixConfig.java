package io.github.chenshun00.fix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import quickfix.*;

import java.io.InputStream;

/**
 * @author chenshun00@gmail.com
 * @since 2024/2/28 19:35
 */
@Configuration
public class QuickfixConfig {

    @Bean
    public MessageStoreFactory messageStoreFactory(SessionSettings settings) throws ConfigError {
        return new FileStoreFactory(this.settings());
    }

    @Bean
    public LogFactory logFactory(SessionSettings settings) {
        return new CustomSLF4JLogFactory(settings);
    }

    @Bean
    public SessionSettings settings() throws ConfigError {
        final InputStream resourceAsStream = this.getClass().getResourceAsStream("/acceptor.properties");
        Assert.notNull(resourceAsStream, "获取不到resource");
        return new SessionSettings(resourceAsStream);
    }
}
