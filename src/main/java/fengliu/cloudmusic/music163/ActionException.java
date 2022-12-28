package fengliu.cloudmusic.music163;

import net.minecraft.text.Text;

public class ActionException extends RuntimeException {

    public ActionException(String msg){
        super(msg);
    }

    public ActionException(Text text){
        super(text.getString());
    }
}
