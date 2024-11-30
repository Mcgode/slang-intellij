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

public class SlangLiteralImpl extends ASTWrapperPsiElement implements SlangLiteral {

  public SlangLiteralImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SlangVisitor visitor) {
    visitor.visitLiteral(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SlangVisitor) accept((SlangVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getBoolLiteral() {
    return findChildByType(BOOL_LITERAL);
  }

  @Override
  @Nullable
  public PsiElement getDoubleLiteral() {
    return findChildByType(DOUBLE_LITERAL);
  }

  @Override
  @Nullable
  public PsiElement getFloatLiteral() {
    return findChildByType(FLOAT_LITERAL);
  }

  @Override
  @Nullable
  public PsiElement getIntLiteral() {
    return findChildByType(INT_LITERAL);
  }

  @Override
  @Nullable
  public PsiElement getUintLiteral() {
    return findChildByType(UINT_LITERAL);
  }

}
