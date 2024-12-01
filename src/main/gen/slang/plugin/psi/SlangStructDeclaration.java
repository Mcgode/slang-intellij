// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangStructDeclaration extends PsiElement {

  @NotNull
  List<SlangMemberDeclaration> getMemberDeclarationList();

  @Nullable
  SlangTypeName getTypeName();

}
