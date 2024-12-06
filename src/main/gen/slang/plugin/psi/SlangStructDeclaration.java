// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangStructDeclaration extends PsiElement {

  @NotNull
  List<SlangAttribute> getAttributeList();

  @NotNull
  List<SlangDeclarationModifier> getDeclarationModifierList();

  @NotNull
  List<SlangEmptyDeclaration> getEmptyDeclarationList();

  @NotNull
  List<SlangStructDeclaration> getStructDeclarationList();

  @Nullable
  SlangStructName getStructName();

}
