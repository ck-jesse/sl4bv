package dbox.sl4bv.example;

// 父类属性校验
public class ImplicitInput extends LoginInput {

    /**
     * 
     */
    private static final long serialVersionUID = -7231284722028811352L;
    
    private String name;
    private String ownerName;

    // 隐式验证
    public String getName() {
        return name;
    }
    
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

}
