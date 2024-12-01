// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangMemberDeclaration extends PsiElement {

  @NotNull
  SlangFullType getFullType();

  @Nullable
  SlangSemantic getSemantic();

  @NotNull
  SlangVariableIdentifier getVariableIdentifier();

}
