// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangEnumDeclaration extends PsiElement {

  @NotNull
  List<SlangEnumMember> getEnumMemberList();

  @NotNull
  SlangTypeName getTypeName();

  @Nullable
  PsiElement getEnumClass();

}
