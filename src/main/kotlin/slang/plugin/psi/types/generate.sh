#!/bin/bash

# Check if an argument is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <filename>"
    exit 1
fi

# Assign the input parameter to a variable
INTERFACE_FILENAME=Slang$1.kt
IMPLEMENTATION_FILENAME=impl/Slang$1Impl.kt


# Check if the file already exists
if [ -f "$INTERFACE_FILENAME" ]; then
    echo "File '$INTERFACE_FILENAME' already exists. Overwrite? (y/n)"
    read -r CONFIRM
    if [ "$CONFIRM" != "y" ]; then
        echo "Operation cancelled."
        exit 0
    fi
fi

{
  echo "package slang.plugin.psi.types";
  echo "";
  echo "import com.intellij.psi.PsiElement";
  echo "";
  echo "interface Slang$1: PsiElement {";
  echo "}";
} > "$INTERFACE_FILENAME"

{
  echo "package slang.plugin.psi.types.impl";
  echo "";
  echo "import com.intellij.extapi.psi.ASTWrapperPsiElement";
  echo "import com.intellij.lang.ASTNode";
  echo "import slang.plugin.psi.types.Slang$1";
  echo "";
  echo "class Slang$1Impl(node: ASTNode): ASTWrapperPsiElement(node), Slang$1 {";
  echo "}";
} > "$IMPLEMENTATION_FILENAME"

git add "$INTERFACE_FILENAME"
git add "$IMPLEMENTATION_FILENAME"

echo "File '$INTERFACE_FILENAME' has been created or updated."
echo "File '$IMPLEMENTATION_FILENAME' has been created or updated."