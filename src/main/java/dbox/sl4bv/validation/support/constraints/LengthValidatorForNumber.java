package dbox.sl4bv.validation.support.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import dbox.sl4bv.validation.support.annotation.BVLength;

/**
 * 扩展Hibernate @Length 支持支持Number和CharSequence类型长度校验<br>
 * @author chenck 2016年12月6日
 */
public class LengthValidatorForNumber implements ConstraintValidator<BVLength, Number> {

    private int min;
    private int max;

    @Override
    public void initialize(BVLength parameters) {
        min = parameters.min();
        max = parameters.max();
        validateParameters();
    }

    @Override
    public boolean isValid(Number value,
            ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        int length = String.valueOf(value).length();
        return length >= min && length <= max;
    }

    private void validateParameters() {
        if (min < 0) {
            throw new IllegalArgumentException("The min parameter cannot be negative.");
        }
        if (max < 0) {
            throw new IllegalArgumentException("The max parameter cannot be negative.");
        }
        if (max < min) {
            throw new IllegalArgumentException("The length cannot be negative.");
        }
    }
}
