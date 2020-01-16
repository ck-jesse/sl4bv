package org.hibernate.validator.method.metadata;

import javax.validation.metadata.ElementDescriptor;

@Deprecated
public abstract interface ParameterDescriptor extends ElementDescriptor {
    public abstract boolean isCascaded();

    public abstract int getIndex();
}
