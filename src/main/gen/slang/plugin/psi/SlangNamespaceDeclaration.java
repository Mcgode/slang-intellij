// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangNamespaceDeclaration extends PsiElement {

  @NotNull
  List<SlangDeclaration> getDeclarationList();

  @NotNull
  List<SlangStatement> getStatementList();

  @Nullable
  SlangTypeName getTypeName();

}
