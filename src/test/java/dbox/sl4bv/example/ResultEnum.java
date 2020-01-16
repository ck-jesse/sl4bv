package dbox.sl4bv.example;

import dbox.sl4bv.validation.support.IBeanValid;

public enum ResultEnum implements IBeanValid {

    SUCC("succ"), FAIL("fail"),;

    private String value;

    private ResultEnum(String value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return value;
    }
}
