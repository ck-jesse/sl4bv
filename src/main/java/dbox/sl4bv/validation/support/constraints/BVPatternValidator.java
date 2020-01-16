package dbox.sl4bv.validation.support.constraints;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import dbox.sl4bv.validation.support.annotation.BVPattern;

/**
 * 扩展@Pattern，允许为空和为null
 * 
 * @author chenck
 * @date 2017年7月19日 下午5:31:51
 */
public class BVPatternValidator implements ConstraintValidator<BVPattern, CharSequence> {

    private java.util.regex.Pattern pattern;

    @Override
    public void initialize(BVPattern parameters) {
        BVPattern.Flag[] flags = parameters.flags();
        int intFlag = 0;
        for (BVPattern.Flag flag : flags) {
            intFlag = intFlag | flag.getValue();
        }

        try {
            pattern = java.util.regex.Pattern.compile(parameters.regexp(), intFlag);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("@BVPattern.regexp() Invalid regular expression.");
        }
    }

    @Override
    public boolean isValid(CharSequence value,
            ConstraintValidatorContext constraintValidatorContext) {
        // 允许为空和为null
        if (value == null || "".equals(value)) {
            return true;
        }
        Matcher m = pattern.matcher(value);
        return m.matches();
    }
}
