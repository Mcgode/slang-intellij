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

public class SlangDeclarationImpl extends ASTWrapperPsiElement implements SlangDeclaration {

  public SlangDeclarationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SlangVisitor visitor) {
    visitor.visitDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SlangVisitor) accept((SlangVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SlangDeclarationStatement getDeclarationStatement() {
    return findChildByClass(SlangDeclarationStatement.class);
  }

  @Override
  @Nullable
  public SlangEnumDeclaration getEnumDeclaration() {
    return findChildByClass(SlangEnumDeclaration.class);
  }

  @Override
  @Nullable
  public SlangNamespaceDeclaration getNamespaceDeclaration() {
    return findChildByClass(SlangNamespaceDeclaration.class);
  }

  @Override
  @Nullable
  public SlangStructDeclaration getStructDeclaration() {
    return findChildByClass(SlangStructDeclaration.class);
  }

}
