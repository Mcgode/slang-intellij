// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SlangExpressionAssignment extends PsiElement {

  @NotNull
  SlangAssignmentOperator getAssignmentOperator();

  @Nullable
  SlangExpressionNoAssignment getExpressionNoAssignment();

  @NotNull
  SlangUnaryExpression getUnaryExpression();

}
