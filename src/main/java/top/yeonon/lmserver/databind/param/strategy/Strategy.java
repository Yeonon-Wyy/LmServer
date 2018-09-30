package top.yeonon.lmserver.databind.param.strategy;

import top.yeonon.lmserver.http.LmRequest;

/**
 * @Author yeonon
 * @date 2018/8/9 0009 17:45
 **/
public interface Strategy {

    Object execute(LmRequest request, String paramName);
}
