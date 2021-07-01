package gg.archipelago.aprandomizer.common.Utils;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class QueuedTitle {

    private final int ticks;
    private final List<ServerPlayerEntity> players;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    private final ITextComponent subTitle;
    private final ITextComponent title;
    private ITextComponent chatMessage = null;

    public QueuedTitle(List<ServerPlayerEntity> players, int fadeIn, int stay, int fadeOut, ITextComponent subTitle, ITextComponent title) {
        this.players = players;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.subTitle = subTitle;
        this.title = title;
        this.ticks = fadeIn + stay + fadeOut + 20;
    }

    public QueuedTitle(List<ServerPlayerEntity> players, int fadeIn, int stay, int fadeOut, ITextComponent subTitle, ITextComponent title,ITextComponent chatMessage) {
        this(players,fadeIn,stay,fadeOut,subTitle,title);
        this.chatMessage = chatMessage;

    }


    public void sendTitle() {
        APRandomizer.getServer().execute(() -> {
            TitleUtils.setTimes(players, fadeIn, stay, fadeOut);
            TitleUtils.showTitle(players, subTitle, STitlePacket.Type.SUBTITLE);
            TitleUtils.showTitle(players, title, STitlePacket.Type.TITLE);
            if(chatMessage != null) {
                Utils.sendMessageToAll(chatMessage);
            }
        });
    }

    public int getTicks() {
        return ticks;
    }
}
