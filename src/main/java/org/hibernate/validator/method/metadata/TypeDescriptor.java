package org.hibernate.validator.method.metadata;

import java.util.Set;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ElementDescriptor;

@Deprecated
public abstract interface TypeDescriptor extends ElementDescriptor {
    public abstract boolean isTypeConstrained();

    public abstract Set<MethodDescriptor> getConstrainedMethods();

    public abstract MethodDescriptor getConstraintsForMethod(String paramString,
            Class<?>... paramVarArgs);

    public abstract BeanDescriptor getBeanDescriptor();
}
