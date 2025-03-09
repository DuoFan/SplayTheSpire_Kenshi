package game.duofan.kenshi.initialize;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.megacrit.cardcrawl.rewards.RewardSave;
import game.duofan.kenshi.reward.JianShuYaoLingReward;
import game.duofan.kenshi.reward.RewardTypeEnum;

public class RewardRegister implements PostInitializeSubscriber {

    @Override
    public void receivePostInitialize() {
        BaseMod.registerCustomReward(
                RewardTypeEnum.JIAN_SHU_YAO_LING_REWARD,
                (rewardSave) -> {
                    return new JianShuYaoLingReward();
                },
                (customReward) -> {
                    return new RewardSave(customReward.type.toString(), null);
                });
    }
}
