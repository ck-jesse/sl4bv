package dbox.sl4bv.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dbox.sl4bv.validation.support.ValidatorUtil;

public class Main {

    private static ApplicationContext context;

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        LoginInput input = new LoginInput();
        input.setName("ohyesss");
        input.setAddr("地址");
        input.setEmail("ddddd");
        input.setBirthday(new Date(System.currentTimeMillis() + 1000 * 60));
        input.setStartDate("20160109101010");
        input.setEndDate("20160109101010s");
        input.setCascadeInput(new CascadeInput());
        input.setSex(1);
        input.setSex1(1);
        input.setSex2("02");
        input.setSex3(ResultEnum.SUCC.getValue().toString());
        input.setTime(7L);
        input.setAmount(new BigDecimal(10.2).setScale(2, RoundingMode.HALF_UP));
        input.setAge(3);
//         springAopTest(input);

        Class<?>[] groups = {  };
        beanValidatorTest(input, groups, 0);

    }

    public static void beanValidatorTest(Object arg, Class<?>[] groups, int msgThrowMode) {
        String errorMsg = ValidatorUtil.validAndDealMsg(arg, groups, msgThrowMode);
        System.out.println(errorMsg);
    }

    public static void springAopTest(LoginInput input) {
        context = new ClassPathXmlApplicationContext("META-INF/spring/sl4bv.xml");
        LoginService login = context.getBean(LoginService.class);
//         login.login(input, "name");
        login.login("name", "addr");
//         login.login(123, "name");
    }

}
