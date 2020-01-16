package dbox.sl4bv.validation.support.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.hibernate.validator.constraints.Length;

import dbox.sl4bv.validation.support.constraints.ContainValidator;

/**
 * 包含约束注解（验证参数的合法性）<br>
 * 用于判断值是否在指定的值里面<br>
 * 注：目前仅支持原始数据类型<br>
 * @author chenck 2016年11月17日
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { ContainValidator.class })
public @interface Contains {

    /**
     * 来源数据类型，枚举，字典，字符串，数值等
     */
    Type type() default Type.CHAR_SEQUENCE;
    
    /**
     * 枚举列表
     */
    Class<?>[] enums() default {};
    
    /**
     * 字典key,缓存或DB中获取来源数据进行验证<br>
     */
    String dictKey() default "";
    
    /**
     * 字典列表[字符串]
     */
    String[] charDicts() default {};
    
    /**
     * 字典列表[数值]
     */
    int[] numberDicts() default {};
    
    String message() default "Parameter is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 来源数据类型
     * @author chenck 2016年11月17日
     */
    public static enum Type {
        /**
         * 枚举类型：表示从枚举类中获取所有值与参数值进行合法性验证
         */
        ENUM, 
        /**
         * 字典类型：表示从缓存或DB中获取字典数据进行合法性验证
         */
        DICT,
        /**
         * 字符串类型：表示直接通过@Contain的dictList属性中获取来源数据进行合法性验证
         */
        CHAR_SEQUENCE, 
        /**
         * 数值类型：表示直接通过@Contain的dictList属性中获取来源数据进行合法性验证
         */
        NUMBER,
    }
    
    
    /**
     * Defines several {@code @Length} annotations on the same element.
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        Length[] value();
    }
    
}
