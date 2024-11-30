// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangPrimaryExpression extends PsiElement {

  @Nullable
  SlangExpression getExpression();

  @Nullable
  SlangLiteral getLiteral();

  @Nullable
  SlangPrimaryExpressionVariable getPrimaryExpressionVariable();

}
