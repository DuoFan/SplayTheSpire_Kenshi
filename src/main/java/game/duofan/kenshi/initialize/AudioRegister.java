package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.AddAudioSubscriber;
import game.duofan.common.AudioKey;

public class AudioRegister implements AddAudioSubscriber {
    @Override
    public void receiveAddAudio () {
        BaseMod.addAudio(AudioKey.SWORD_SOUND, "audio/SwordSound.mp3");
    }
}
