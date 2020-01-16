package dbox.sl4bv.example;

import dbox.sl4bv.validation.support.IBeanValid;

public enum SexEnum implements IBeanValid {
    MAN(0), FEMALE(1),;

    private int value;

    private SexEnum(int value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return value;
    }

}
