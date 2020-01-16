package org.hibernate.validator.method;

import java.lang.reflect.Method;
import java.util.Set;

import org.hibernate.validator.method.metadata.TypeDescriptor;

/**
 * 基于Hibernate Validator 4.3的Method包中的功能实现基本数据类型校验
 * @author chenck 2016年11月17日
 */
@Deprecated
public abstract interface MethodValidator {
    public abstract <T> Set<MethodConstraintViolation<T>> validateParameter(T paramT,
            Method paramMethod, Object paramObject, int paramInt, Class<?>... paramVarArgs);

    public abstract <T> Set<MethodConstraintViolation<T>> validateAllParameters(T paramT,
            Method paramMethod, Object[] paramArrayOfObject, Class<?>... paramVarArgs);

    public abstract <T> Set<MethodConstraintViolation<T>> validateReturnValue(T paramT,
            Method paramMethod, Object paramObject, Class<?>... paramVarArgs);

    public abstract TypeDescriptor getConstraintsForType(Class<?> paramClass);
}
