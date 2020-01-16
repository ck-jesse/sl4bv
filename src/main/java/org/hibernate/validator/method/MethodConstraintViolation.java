package org.hibernate.validator.method;

import java.lang.reflect.Method;

import javax.validation.ConstraintViolation;

@Deprecated
public abstract interface MethodConstraintViolation<T> extends ConstraintViolation<T> {
    public abstract Method getMethod();

    public abstract Integer getParameterIndex();

    public abstract String getParameterName();

    public abstract Kind getKind();

    @Deprecated
    public static enum Kind {
        PARAMETER, RETURN_VALUE;

        private Kind() {
        }
    }
}
