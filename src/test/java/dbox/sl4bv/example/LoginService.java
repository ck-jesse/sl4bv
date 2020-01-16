package dbox.sl4bv.example;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import dbox.sl4bv.validation.annotation.BeanValid;
import dbox.sl4bv.validation.type.MsgThrowMode;

@Service
//@BeanValid
@BeanValid(msgThrowMode = MsgThrowMode.NOT_THROW)
public class LoginService {

    public void login(@Valid int age) {
        System.out.println("age=" + age);
    }

    // 方法属性校验
    public void login(
            @NotEmpty(message = "姓名不能为空") @Size(min = 10, max = 300, message = "姓名大小必须在指定的范围内") String name, String addr) {
        System.out.println("name=" + name);
    }

    public void login(@Valid int age, @Valid String name) {
        System.out.println("age=" + age + ",name=" + name);
    }

    public void login(@Valid LoginInput input, String name) {
        System.out.println("input.name=" + input.getName());
    }
}
