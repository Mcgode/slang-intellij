// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangSingleDeclaration extends PsiElement {

  @Nullable
  SlangArraySpecifier getArraySpecifier();

  @NotNull
  SlangFullType getFullType();

  @Nullable
  SlangInitializer getInitializer();

  @Nullable
  SlangVariableIdentifier getVariableIdentifier();

}
