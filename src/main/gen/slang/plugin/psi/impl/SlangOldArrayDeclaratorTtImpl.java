// This is a generated file. Not intended for manual editing.
package slang.plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static slang.plugin.psi.SlangOldTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import slang.plugin.psi.*;

public class SlangOldArrayDeclaratorTtImpl extends ASTWrapperPsiElement implements SlangOldArrayDeclaratorTt {

  public SlangOldArrayDeclaratorTtImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SlangOldVisitor visitor) {
    visitor.visitArrayDeclaratorTt(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SlangOldVisitor) accept((SlangOldVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SlangOldArrayDeclaratorTt getArrayDeclaratorTt() {
    return findChildByClass(SlangOldArrayDeclaratorTt.class);
  }

  @Override
  @Nullable
  public SlangOldNameDeclaratorTt getNameDeclaratorTt() {
    return findChildByClass(SlangOldNameDeclaratorTt.class);
  }

}
