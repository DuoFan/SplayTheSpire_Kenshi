package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.AddAudioSubscriber;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import game.duofan.common.AudioKey;

public class AudioRegister implements AddAudioSubscriber {
    @Override
    public void receiveAddAudio () {
        BaseMod.addAudio(AudioKey.SWORD_SOUND, "audio/SwordSound.mp3");
        BaseMod.addAudio(AudioKey.QIN_SHEN_ZHUI_SOUND, "audio/QinShenZhui.mp3");
        BaseMod.addAudio(AudioKey.BAI_LU_YOU_SOUND, "audio/BaiLuYou.mp3");
    }
}
