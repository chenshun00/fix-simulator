package io.github.chenshun00.fix.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenshun00@gmail.com
 * @since 2024/10/20 10:11
 */
@ConfigurationProperties(prefix = "enable.auto")
@Component
@Data
@Slf4j
public class AutoConfig implements InitializingBean {

    private boolean single;
    private boolean replace;
    private boolean cancel;
    private Map<Set<Double>, String> map = new HashMap<>();
    private Map<String, String> temp;

    public String getHandlerBean(Double quantity) {
        for (Map.Entry<Set<Double>, String> entry : map.entrySet()) {
            if (entry.getKey().contains(quantity)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (temp == null || temp.isEmpty()) {
            log.info("[config is empty]");
            return;
        }
        for (Map.Entry<String, String> entry : temp.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            map.put(Arrays.stream(value.split(",")).map(Double::parseDouble).collect(Collectors.toSet()), key);
        }
        log.info("map config:{}", map.toString());
    }
}
