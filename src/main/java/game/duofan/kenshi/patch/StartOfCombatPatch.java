package game.duofan.kenshi.patch;

        import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
        import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
        import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
        import com.megacrit.cardcrawl.characters.AbstractPlayer;
        import game.duofan.common.EventKey;
        import game.duofan.common.EventManager;

@SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfCombatLogic")
public class StartOfCombatPatch {

    @SpirePostfixPatch
    public static void patch(AbstractPlayer __instance) {
        EventManager.getInstance().removeAll_NotPersist_Event();
        EventManager.getInstance().notifyEvent(EventKey.ON_BATTLE_START, __instance, null);
    }
}
