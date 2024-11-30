// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangDeclarationStatement extends PsiElement {

  @NotNull
  List<SlangArraySpecifier> getArraySpecifierList();

  @NotNull
  List<SlangInitializer> getInitializerList();

  @NotNull
  SlangSingleDeclaration getSingleDeclaration();

  @NotNull
  List<SlangVariableIdentifier> getVariableIdentifierList();

}
