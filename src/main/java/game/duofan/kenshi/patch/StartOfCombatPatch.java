package game.duofan.kenshi.patch;

        import com.brashmonkey.spriter.Player;
        import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
        import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
        import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
        import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
        import com.megacrit.cardcrawl.characters.AbstractPlayer;
        import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
        import game.duofan.common.EventKey;
        import game.duofan.common.EventManager;
        import game.duofan.kenshi.KenShi;

@SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfCombatLogic")
public class StartOfCombatPatch {

    @SpirePrefixPatch
    public static void patch(AbstractPlayer __instance) {
        EventManager.getInstance().removeAll_NotPersist_Event();

        if(AbstractDungeon.player.chosenClass.equals(KenShi.CharacterEnum.CHARACTER_KENSHI)){
            KenShi kenShi = (KenShi) AbstractDungeon.player;
            kenShi.RegistAnimListener();
        }

        EventManager.getInstance().notifyEvent(EventKey.ON_BATTLE_START, __instance, null);
    }
}
