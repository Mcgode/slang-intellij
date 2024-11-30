// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class SlangVisitor extends PsiElementVisitor {

  public void visitDeclaration(@NotNull SlangDeclaration o) {
    visitPsiElement(o);
  }

  public void visitDeclarationStatement(@NotNull SlangDeclarationStatement o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull SlangExpression o) {
    visitPsiElement(o);
  }

  public void visitExpressionStatement(@NotNull SlangExpressionStatement o) {
    visitPsiElement(o);
  }

  public void visitStatement(@NotNull SlangStatement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
