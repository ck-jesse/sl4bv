package org.hibernate.validator.method;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ValidationException;

@Deprecated
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MethodConstraintViolationException extends ValidationException {
    private static final long serialVersionUID = 5694703022614920634L;
    private final Set<MethodConstraintViolation<?>> constraintViolations;

    public MethodConstraintViolationException(
            Set<? extends MethodConstraintViolation<?>> constraintViolations) {
        super("The following constraint violations occurred: " + constraintViolations);
        this.constraintViolations = new HashSet(constraintViolations);
    }

    public MethodConstraintViolationException(String message,
            Set<? extends MethodConstraintViolation<?>> constraintViolations) {
        super(message);
        this.constraintViolations = new HashSet(constraintViolations);
    }

    public Set<MethodConstraintViolation<?>> getConstraintViolations() {
        return this.constraintViolations;
    }
}
