/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dbox.sl4bv.validation.support;

import java.lang.annotation.Annotation;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import dbox.sl4bv.validation.annotation.BeanValid;

/**
 * // 扩展：对基本数据类型进行验证<br>
 * // 思路：扩展Spring本身的MethodValidationPostProcessor处理器进行基本数据类型验证<br>
 * // 处理：将Spring本身的@Validated修改为自定义的@BeanValid即可，其他代码均无需修改<br>
 * // 使用：在类上添加@BeanValid即可
 * @author chenck 2016年11月17日
 */
@Component
@SuppressWarnings("serial")
public class MethodValidationPostProcessor extends AbstractAdvisingBeanPostProcessor implements InitializingBean {

    // 将Spring本身的@Validated修改为自定义的@BeanValid
    private Class<? extends Annotation> validatedAnnotationType = BeanValid.class;

    private Validator validator;


    /**
     * Set the 'validated' annotation type.
     * The default validated annotation type is the {@link Validated} annotation.
     * <p>This setter property exists so that developers can provide their own
     * (non-Spring-specific) annotation type to indicate that a class is supposed
     * to be validated in the sense of applying method validation.
     * @param validatedAnnotationType the desired annotation type
     */
    public void setValidatedAnnotationType(Class<? extends Annotation> validatedAnnotationType) {
        Assert.notNull(validatedAnnotationType, "'validatedAnnotationType' must not be null");
        this.validatedAnnotationType = validatedAnnotationType;
    }

    /**
     * Set the JSR-303 Validator to delegate to for validating methods.
     * <p>Default is the default ValidatorFactory's default Validator.
     */
    public void setValidator(Validator validator) {
        if (validator instanceof LocalValidatorFactoryBean) {
            this.validator = ((LocalValidatorFactoryBean) validator).getValidator();
        }
        else {
            this.validator = validator;
        }
    }

    /**
     * Set the JSR-303 ValidatorFactory to delegate to for validating methods,
     * using its default Validator.
     * <p>Default is the default ValidatorFactory's default Validator.
     * @see javax.validation.ValidatorFactory#getValidator()
     */
    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validator = validatorFactory.getValidator();
    }


    @Override
    public void afterPropertiesSet() {
        Pointcut pointcut = new AnnotationMatchingPointcut(this.validatedAnnotationType, true);
        this.advisor = new DefaultPointcutAdvisor(pointcut, createMethodValidationAdvice(this.validator));
    }

    /**
     * Create AOP advice for method validation purposes, to be applied
     * with a pointcut for the specified 'validated' annotation.
     * @param validator the JSR-303 Validator to delegate to
     * @return the interceptor to use (typically, but not necessarily,
     * a {@link MethodValidationInterceptor} or subclass thereof)
     * @since 4.2
     */
    protected Advice createMethodValidationAdvice(Validator validator) {
        return (validator != null ? new MethodValidationInterceptor(validator) : new MethodValidationInterceptor());
    }

}
