package dbox.sl4bv.example;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import dbox.sl4bv.validation.support.ValidatorUtil;
import dbox.sl4bv.validation.support.annotation.BVLength;

public class LengthInput {

    @BVLength(min = 1, max = 6, message = "姓名必须为1-6", charsetName = "utf-8")
    private String name;

    @BVLength(min = 1, max = 6, message = "地址必须为1-6")
    private String addr;

    // 级联验证
    @Valid
    @NotNull(message = "集合不能为空")
    private List<CascadeInput> cascadeInputList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public List<CascadeInput> getCascadeInputList() {
        return cascadeInputList;
    }

    public void setCascadeInputList(List<CascadeInput> cascadeInputList) {
        this.cascadeInputList = cascadeInputList;
    }

    public static void main(String[] args) {
        LengthInput input = new LengthInput();
        input.setName("ssdfws");
        input.setAddr("fefii分");
        CascadeInput cascadeInput = new CascadeInput();
        cascadeInput.setCascade("ddd");
        CascadeInput cascadeInput1 = new CascadeInput();
        List<CascadeInput> cascadeInputList = new ArrayList<CascadeInput>();
        cascadeInputList.add(cascadeInput);
        cascadeInputList.add(cascadeInput1);
        input.setCascadeInputList(cascadeInputList);
        
        String errorMsg = ValidatorUtil.validAndDealMsg(input);
        System.out.println(errorMsg);
    }

}
