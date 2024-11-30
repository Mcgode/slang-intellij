// This is a generated file. Not intended for manual editing.
package slang.plugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static slang.plugin.psi.SlangTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import slang.plugin.psi.*;

public class SlangPrimaryExpressionImpl extends ASTWrapperPsiElement implements SlangPrimaryExpression {

  public SlangPrimaryExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SlangVisitor visitor) {
    visitor.visitPrimaryExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SlangVisitor) accept((SlangVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SlangExpression getExpression() {
    return findChildByClass(SlangExpression.class);
  }

  @Override
  @Nullable
  public SlangLiteral getLiteral() {
    return findChildByClass(SlangLiteral.class);
  }

  @Override
  @Nullable
  public SlangPrimaryExpressionVariable getPrimaryExpressionVariable() {
    return findChildByClass(SlangPrimaryExpressionVariable.class);
  }

}
