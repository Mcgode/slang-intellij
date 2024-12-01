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

public class SlangNamespaceDeclarationImpl extends ASTWrapperPsiElement implements SlangNamespaceDeclaration {

  public SlangNamespaceDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SlangVisitor visitor) {
    visitor.visitNamespaceDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SlangVisitor) accept((SlangVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SlangDeclaration> getDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SlangDeclaration.class);
  }

  @Override
  @NotNull
  public List<SlangStatement> getStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SlangStatement.class);
  }

  @Override
  @Nullable
  public SlangTypeName getTypeName() {
    return findChildByClass(SlangTypeName.class);
  }

}
