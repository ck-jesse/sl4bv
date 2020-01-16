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
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.annotation.Validated;

import dbox.sl4bv.validation.annotation.BeanValid;
import dbox.sl4bv.validation.type.MsgThrowMode;

/**
 * 基于Hibernate Validator 4.3的Method包中的功能实现基本数据类型校验
 * @author chenck 2016年11月17日
 */
public class MethodValidationInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodValidationInterceptor.class);
    
    private static Method forExecutablesMethod;

    private static Method validateParametersMethod;

    private static Method validateReturnValueMethod;

    static {
        try {
            forExecutablesMethod = Validator.class.getMethod("forExecutables");
            Class<?> executableValidatorClass = forExecutablesMethod.getReturnType();
            validateParametersMethod = executableValidatorClass.getMethod(
                    "validateParameters", Object.class, Method.class, Object[].class, Class[].class);
            validateReturnValueMethod = executableValidatorClass.getMethod(
                    "validateReturnValue", Object.class, Method.class, Object.class, Class[].class);
        }
        catch (Exception ex) {
            // Bean Validation 1.1 ExecutableValidator API not available
        }
    }


    private final Validator validator;


    /**
     * Create a new MethodValidationInterceptor using a default JSR-303 validator underneath.
     */
    public MethodValidationInterceptor() {
        this(forExecutablesMethod != null ? Validation.buildDefaultValidatorFactory() :
                HibernateValidatorDelegate.buildValidatorFactory());
    }

    /**
     * Create a new MethodValidationInterceptor using the given JSR-303 ValidatorFactory.
     * @param validatorFactory the JSR-303 ValidatorFactory to use
     */
    public MethodValidationInterceptor(ValidatorFactory validatorFactory) {
        this(validatorFactory.getValidator());
    }

    /**
     * Create a new MethodValidationInterceptor using the given JSR-303 Validator.
     * @param validator the JSR-303 Validator to use
     */
    public MethodValidationInterceptor(Validator validator) {
        this.validator = validator;
    }


    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?>[] groups = determineValidationGroups(invocation);

        if (forExecutablesMethod != null) {
            Method methodToValidate = invocation.getMethod();
            // start
            // 获取方法上的BeanValid注解,如果没有则直接返回
            // 不应该出现走到此处但方法上没有注解
            BeanValid beanValid = methodToValidate.getAnnotation(BeanValid.class);

            // 方法上找到不注解则从类上查找注解
            if (beanValid == null)
                beanValid = methodToValidate.getDeclaringClass().getAnnotation(BeanValid.class);
            
            // 此处用于过滤Method上标注@Valid注解的参数，不进行验证，因为此种注解的参数在BeanValidAspect中会进行验证
            Annotation[][] paramAnnotations = methodToValidate.getParameterAnnotations();// 方法参数注解
            Object[] arguments = invocation.getArguments();
            Object[] argumentsTemp = new Object[arguments.length];
            if(null != paramAnnotations && paramAnnotations.length > 0){
                int i = 0;
                label1 : for (Annotation[] annotations : paramAnnotations) {
                    for (Annotation annotation : annotations) {
                        if(annotation instanceof Valid){
                            argumentsTemp[i++] = null;
                            continue label1;
                        }
                    }
                    argumentsTemp[i] = arguments[i];
                    i++;
                }
            }
            // end

            // Standard Bean Validation 1.1 API
            Object execVal = ReflectionUtils.invokeMethod(forExecutablesMethod, this.validator);
            Set<ConstraintViolation<?>> result;
            try {
                result = (Set<ConstraintViolation<?>>) ReflectionUtils.invokeMethod(validateParametersMethod,
                        execVal, invocation.getThis(), methodToValidate, argumentsTemp, groups);
            }
            catch (IllegalArgumentException ex) {
                // Probably a generic type mismatch between interface and impl as reported in SPR-12237 / HV-1011
                // Let's try to find the bridged method on the implementation class...
                methodToValidate = BridgeMethodResolver.findBridgedMethod(
                        ClassUtils.getMostSpecificMethod(invocation.getMethod(), invocation.getThis().getClass()));
                result = (Set<ConstraintViolation<?>>) ReflectionUtils.invokeMethod(validateParametersMethod,
                        execVal, invocation.getThis(), methodToValidate, argumentsTemp, groups);
            }
            
            if (!result.isEmpty()) {
                // throw new ConstraintViolationException(result);
                String errorMsg = ValidatorUtil.dealErrorMsg2(result, MsgThrowMode.NOT_THROW);
                LOGGER.debug("Method validation failed: ", errorMsg);
                // 不抛出异常消息（忽略异常消息）
                if (beanValid == null || beanValid.msgThrowMode() != MsgThrowMode.NOT_THROW){
                    throw new IllegalArgumentException(errorMsg);
                }
            }

            Object returnValue = invocation.proceed();

            result = (Set<ConstraintViolation<?>>) ReflectionUtils.invokeMethod(validateReturnValueMethod,
                    execVal, invocation.getThis(), methodToValidate, returnValue, groups);
            if (!result.isEmpty()) {
                //throw new ConstraintViolationException(result);
                String errorMsg = ValidatorUtil.dealErrorMsg2(result, MsgThrowMode.NOT_THROW);
                LOGGER.debug("Method validation failed: ", errorMsg);
                // 不抛出异常消息（忽略异常消息）
                if (beanValid == null || beanValid.msgThrowMode() != MsgThrowMode.NOT_THROW){
                    throw new IllegalArgumentException(errorMsg);
                }
            }

            return returnValue;
        }

        else {
            // Hibernate Validator 4.3's native API
            return HibernateValidatorDelegate.invokeWithinValidation(invocation, this.validator, groups);
        }
    }

    /**
     * Determine the validation groups to validate against for the given method invocation.
     * <p>Default are the validation groups as specified in the {@link Validated} annotation
     * on the containing target class of the method.
     * @param invocation the current MethodInvocation
     * @return the applicable validation groups as a Class array
     */
    protected Class<?>[] determineValidationGroups(MethodInvocation invocation) {
        Validated validatedAnn = AnnotationUtils.findAnnotation(invocation.getMethod(), Validated.class);
        if (validatedAnn == null) {
            validatedAnn = AnnotationUtils.findAnnotation(invocation.getThis().getClass(), Validated.class);
        }
        return (validatedAnn != null ? validatedAnn.value() : new Class<?>[0]);
    }


    /**
     * Inner class to avoid a hard-coded Hibernate Validator 4.3 dependency.
     */
    private static class HibernateValidatorDelegate {

        public static ValidatorFactory buildValidatorFactory() {
            return Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory();
        }

        @SuppressWarnings("deprecation")
        public static Object invokeWithinValidation(MethodInvocation invocation, Validator validator, Class<?>[] groups)
                throws Throwable {

            org.hibernate.validator.method.MethodValidator methodValidator =
                    validator.unwrap(org.hibernate.validator.method.MethodValidator.class);
            Set<org.hibernate.validator.method.MethodConstraintViolation<Object>> result =
                    methodValidator.validateAllParameters(
                            invocation.getThis(), invocation.getMethod(), invocation.getArguments(), groups);
            if (!result.isEmpty()) {
                throw new org.hibernate.validator.method.MethodConstraintViolationException(result);
            }
            Object returnValue = invocation.proceed();
            result = methodValidator.validateReturnValue(
                    invocation.getThis(), invocation.getMethod(), returnValue, groups);
            if (!result.isEmpty()) {
                throw new org.hibernate.validator.method.MethodConstraintViolationException(result);
            }
            return returnValue;
        }
    }

}
