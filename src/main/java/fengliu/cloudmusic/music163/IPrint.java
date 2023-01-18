package fengliu.cloudmusic.music163;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface IPrint {

    /**
     * 向游戏聊天框发送信息
     * @param source Fabric 命令源
     */
    void printToChatHud(FabricClientCommandSource source);
}
