package game.duofan.kenshi.patch;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import javassist.CannotCompileException;
import javassist.CodeConverter;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.convert.Transformer;

@SpirePatch2(clz = DrawCardAction.class, method = "update")
public class DrawCardActionPatch {
    @SpireInsertPatch(rloc = 89 - 68, localvars = {"deckSize", "discardSize"})
    public static void InstertPatch(int deckSize, int discardSize) {
        System.out.println("---------------deckSize,-------------discardSize");
        System.out.println(deckSize);
        System.out.println(discardSize);
    }
}
