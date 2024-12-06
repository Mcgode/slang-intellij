// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangAttribute extends PsiElement {

  @NotNull
  SlangAttributeIdentifier getAttributeIdentifier();

  @NotNull
  List<SlangAttributeParameter> getAttributeParameterList();

}
