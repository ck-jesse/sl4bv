package dbox.sl4bv.example;

import org.hibernate.validator.constraints.NotBlank;

public class CascadeInput {

    @NotBlank(message = "cascade 不能为空")
    private String cascade;

    public String getCascade() {
        return cascade;
    }

    public void setCascade(String cascade) {
        this.cascade = cascade;
    }

}
