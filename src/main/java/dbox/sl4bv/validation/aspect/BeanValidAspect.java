package dbox.sl4bv.validation.aspect;

import dbox.sl4bv.validation.annotation.BeanValid;
import dbox.sl4bv.validation.support.ValidatorUtil;
import dbox.sl4bv.validation.type.MsgThrowMode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 基于@Aspect注解的Bean Validator切面
 * 
 * @author chenck 2016年11月16日
 */
@Component
@Aspect
public class BeanValidAspect implements Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanValidAspect.class);

    private int order = -100;// order值越大，优先级越小

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * 切入点<br>
     * @within：使用“@within(注解类型)”匹配所以持有指定注解类型内的方法；注解类型也必须是全限定类型名；<br>
     * @annotation：使用“@annotation(注解类型)”匹配当前执行方法持有指定注解的方法；注解类型也必须是全限定类型名；<br>
     */
    @Pointcut("@within(dbox.sl4bv.validation.annotation.BeanValid)||@annotation(dbox.sl4bv.validation.annotation.BeanValid)")
    public void validPointcut() {
        LOGGER.debug("[BeanValid]Definition bean validator point cut.");
    }

    /**
     * 前置通知<br>
     * JoinPoint：提供访问当前被通知方法的目标对象、代理对象、方法参数等数据<br>
     * ProceedingJoinPoint：用于环绕通知，使用proceed()方法来执行目标方法<br>
     * 
     * @return
     * @throws Throwable
     */
    @Around(value = "validPointcut()")
    public Object valid(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取切点的函数信息
        Method method = null;
        try {
            MethodSignature ms = (MethodSignature) joinPoint.getSignature();
            method = ms.getMethod();
        } catch (Exception e) {
            LOGGER.error("MethodSignature类型转换错误", e);
            throw e;
        }

        // 获取方法上的BeanValid注解,如果没有则直接返回
        // 不应该出现走到此处但方法上没有注解
        BeanValid beanValid = method.getAnnotation(BeanValid.class);

        // 方法上找到不注解则从类上查找注解
        if (beanValid == null)
            beanValid = method.getDeclaringClass().getAnnotation(BeanValid.class);

        if (beanValid == null)
            return null;

        // 每一个方法的参数包含一个注解数组
        Annotation[][] paramAnnotations = method.getParameterAnnotations();// 方法参数注解
        if(null == paramAnnotations || paramAnnotations.length == 0){
            return joinPoint.proceed();// 调用原函数
        }
        
        Class<?>[] paramTypes = method.getParameterTypes();// 方法参数类型
        Object[] args = joinPoint.getArgs();// 方法参数值
        int i = 0;
        for (Annotation[] annotations : paramAnnotations) {
            Class<?> paramType = paramTypes[i];
            Object arg = args[i];
            for (Annotation annotation : annotations) {
                // 注：JSR和Hibernate validator的校验只能对Object的属性进行校验，不能对单个的参数进行校验，
                // 对加了@Valid注解的方法参数进行Bean校验
                if(annotation instanceof Valid){
                    // 判断是否java原始数据类型
                    if (isNativeType(arg)) {
                        LOGGER.debug("{}(...)第{}个参数Java原始数据类型{},不执行校验处理", method.getName(), i, paramType.getName());
                        continue;
                    }
                    // 校验并检查违反约束信息
                    String errorMsg = ValidatorUtil.validAndDealMsg(arg, beanValid.groups(), beanValid.msgThrowMode());
                    
                    if(!StringUtils.isEmpty(errorMsg)){
                        LOGGER.info("Bean validation failed: ", errorMsg);
                        // 不抛出异常消息（忽略异常消息）
                        if (beanValid.msgThrowMode() == MsgThrowMode.NOT_THROW) {
                            continue;
                        }
                        //throw new IllegalArgumentException(errorMsg);
                    }
                }
                // 扩展：对基本数据类型进行验证
                // 思路：扩展Spring本身的MethodValidationPostProcessor处理器进行基本数据类型验证
                // 处理：将Spring本身的@Validated修改为自定义的@BeanValid即可，其他代码均无需修改
            }
            i++;
        }

        // 调用原函数
        return joinPoint.proceed();
    }
    
    /**
     * 检查参数是否为原始数据类型
     * @param arg
     * @return
     */
    public boolean isNativeType(Object arg){
        if (arg instanceof Number || arg instanceof CharSequence || arg instanceof Character
                || arg instanceof Boolean || arg instanceof Date || arg instanceof Collection
                || arg instanceof Map || arg.getClass().isArray()) {
            return true;
        }
        return false;
    }
}
