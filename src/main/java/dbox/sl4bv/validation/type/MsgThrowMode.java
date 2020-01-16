package dbox.sl4bv.validation.type;

/**
 * 验证消息throw模式
 * 
 * @author chenck 2016年11月17日
 */
public interface MsgThrowMode {

    /**
     * 不throw,拼接后返回所有验证消息
     */
    public static final int NOT_THROW = 0;
    
    /**
     * throw所有
     */
    public static final int THROW_ALL = 1;
    
    /**
     * throw第一条
     */
    public static final int THROW_ONE = 2;
}
