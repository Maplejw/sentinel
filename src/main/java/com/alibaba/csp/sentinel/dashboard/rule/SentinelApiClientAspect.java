package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Aspect
public class SentinelApiClientAspect {
    private static final Logger LOG = LoggerFactory.getLogger(SentinelApiClientAspect.class);

    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchFlowRuleOfMachine(..))")
    public void fetchFlowRulesPointcut() {
    }

    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchSystemRuleOfMachine(..))")
    public void fetchSystemRulesPointcut() {
    }


    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setSystemRuleOfMachine(..))")
    public void setSystemRulesPointcut() {
    }

    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setFlowRuleOfMachineAsync(..))")
    public void setFlowRulesPointcut() {
    }

    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchDegradeRuleOfMachine(..))")
    public void fetchDegradeRulesPointcut() {
    }

    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.setDegradeRuleOfMachine(..))")
    public void setDegradeRulesPointcut() {
    }

    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.fetchGatewayFlowRules(..))")
    public void fetchGatewayFlowRulesPointcut() {
    }

    @Pointcut("execution(public * com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient.modifyGatewayFlowRules(..))")
    public void setGatewayFlowRulesPointcut() {
    }

    /**
     * 获取网关流控规则
     */
    @Around("fetchGatewayFlowRulesPointcut()")
    public Object fetchGatewayFlowRulesPointcut(final ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return RuleType.GATEWAY_RULE.getRules(args[0] + "");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

    }

    /**
     * 设置熔断规则
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("setGatewayFlowRulesPointcut()")
    public Object setGatewayFlowRulesPointcut(final ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        RuleType.GATEWAY_RULE.publish(args[0]+"",(List<GatewayFlowRuleEntity>)args[3]);

        return true;
    }

    /**
     * 获取熔断规则
     */
    @Around("fetchDegradeRulesPointcut()")
    public Object fetchDegradeRules(final ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        return RuleType.DEGRADE_RULE.getRules(args[0] + "");
    }

    /**
     * 设置熔断规则
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("setDegradeRulesPointcut()")
    public Object setDegradeRules(final ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        RuleType.DEGRADE_RULE.publish(args[0]+"",(List<DegradeRuleEntity>)args[3]);

        return true;
    }

    /**
     * 拉取流控规则配置
     */
    @Around("fetchFlowRulesPointcut()")
    public Object fetchFlowRules(final ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        return RuleType.FLOW_RULE.getRules(args[0] + "");

    }

    @Around("setFlowRulesPointcut()")
    public Object setFlowRules(final ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        return CompletableFuture.runAsync(() -> {
            try {
                RuleType.FLOW_RULE.publish(args[0] + "",(List<FlowRuleEntity>)args[3]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 设置系统规则
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("setSystemRulesPointcut()")
    public Object setSystemRules(final ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        RuleType.SYSTEM_RULE.publish(args[0]+"",(List<SystemRuleEntity>)args[3]);

        return true;
    }

    /**
     * 拉取系统规则配置
     */
    @Around("fetchSystemRulesPointcut()")
    public Object fetchSystemRules(final ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        return RuleType.SYSTEM_RULE.getRules(args[0] + "");
    }
}
