package io.github.chenshun00.fix.dao.mapper;

import io.github.chenshun00.fix.dao.domain.Entrust;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chenshun00@gmail.com
 * @since 2024/10/20 12:55
 */
public interface EntrustMapper {

    int save(@Param("q") Entrust entrust);

    List<Entrust> query();

}
