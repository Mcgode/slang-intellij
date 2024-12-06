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

public class SlangStructDeclarationImpl extends ASTWrapperPsiElement implements SlangStructDeclaration {

  public SlangStructDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SlangVisitor visitor) {
    visitor.visitStructDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SlangVisitor) accept((SlangVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SlangAttribute> getAttributeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SlangAttribute.class);
  }

  @Override
  @NotNull
  public List<SlangDeclarationModifier> getDeclarationModifierList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SlangDeclarationModifier.class);
  }

  @Override
  @NotNull
  public List<SlangEmptyDeclaration> getEmptyDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SlangEmptyDeclaration.class);
  }

  @Override
  @NotNull
  public List<SlangStructDeclaration> getStructDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SlangStructDeclaration.class);
  }

  @Override
  @Nullable
  public SlangStructName getStructName() {
    return findChildByClass(SlangStructName.class);
  }

}
