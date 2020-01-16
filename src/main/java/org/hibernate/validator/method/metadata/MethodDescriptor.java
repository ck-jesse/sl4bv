package org.hibernate.validator.method.metadata;

import java.util.List;
import javax.validation.metadata.ElementDescriptor;

@Deprecated
public abstract interface MethodDescriptor extends ElementDescriptor {
    public abstract String getMethodName();

    public abstract List<ParameterDescriptor> getParameterDescriptors();

    public abstract boolean isCascaded();
}
