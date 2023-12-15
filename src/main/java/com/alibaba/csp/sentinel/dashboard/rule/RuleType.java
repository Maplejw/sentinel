package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.DynamicRuleApolloProvider;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.DynamicRuleApolloPublisher;
import com.alibaba.csp.sentinel.dashboard.util.ApplicationContextHolder;
import com.alibaba.fastjson.JSON;
import java.util.List;

public enum RuleType {

    FLOW_RULE("-flow-rules", FlowRuleEntity.class),
    SYSTEM_RULE("-systems-rules", SystemRuleEntity.class),
    DEGRADE_RULE("-degrade-rules", DegradeRuleEntity.class),
    GATEWAY_RULE("-flow-rules",GatewayFlowRuleEntity.class);


    private String key;
    private Class<? extends RuleEntity> clz;

    private static DynamicRuleApolloProvider dynamicRuleApolloProvider =
            (DynamicRuleApolloProvider) ApplicationContextHolder.getBean(DynamicRuleApolloProvider.class);
    private static DynamicRuleApolloPublisher dynamicRuleApolloPublisher =
            (DynamicRuleApolloPublisher) ApplicationContextHolder.getBean(DynamicRuleApolloPublisher.class);

    RuleType(String key,Class<? extends RuleEntity> clz){
        this.key = key;
        this.clz = clz;
    }

    public <T> List<T> getRules(String appName) throws Exception {
        String apolloKey = getApolloKey(appName);
        String rules = dynamicRuleApolloProvider.getRules(appName,apolloKey);
        List<T> list = (List<T>) JSON.parseArray(rules,this.clz);

        return list;
    }

    public <T> void publish(String appName, List<T> rules) throws Exception {
        String apolloKey = getApolloKey(appName);
        dynamicRuleApolloPublisher.publish(appName,JSON.toJSONString(rules),apolloKey);
    }

    private String getApolloKey(String appName){
        return appName + key;
    }

}
