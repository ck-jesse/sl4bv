package dbox.sl4bv.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Payload;
import javax.validation.groups.Default;

import dbox.sl4bv.validation.type.MsgThrowMode;

/**
 * Bean验证
 * 
 * @author chenck 2016年11月16日
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BeanValid {

    /**
     * 验证消息throw模式,默认throw抛出第一条
     */
    public int msgThrowMode() default MsgThrowMode.THROW_ONE;

    /**
     * Bean validator注解的组别验证约束,默认Default.class
     */
    public Class<?>[] groups() default {Default.class};
    
    /**
     * Bean validator注解的payload验证约束
     */
    public Class<? extends Payload>[] payload() default { };
}
