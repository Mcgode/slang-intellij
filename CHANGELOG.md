<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# slang-intellij Changelog

## [Unreleased]

## [0.1.0] - 2024-12-23

### Added

- LSP support using [LSP4IJ](https://github.com/redhat-developer/lsp4ij)
  - Optional dependency. To use this feature, make sure to install the plugin.
- Code color settings page

### Changes

- Parsing for a lot more element types (almost done with non-preprocessors), including (but not limited to):
  - Statements
  - Expressions
  - Functions
  - Classes
  - Namespaces
  - And many more!
- Basic scope tracking in parser
- More elements now have specific code highlighting

## [0.0.2] - 2024-12-09

### Added

- Code signing capability
- Publishing token setup

### Changes

- Parsing for more element type: 
  - Declaration
  - Inheritance declaration
  - Attribute parsing
  - Postfix expression
- Test files now use .slang file extension
- Cleaned up README
- Removed Slang.bnf and associated generated classes

## [0.0.1] - 2024-12-08

First unstable iteration of the plugin

### Added

- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Work in progress slang file parsing
- Work in progress slang code highlighting

[Unreleased]: https://github.com/Mcgode/slang-intellij/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/Mcgode/slang-intellij/compare/v0.0.2...v0.1.0
[0.0.2]: https://github.com/Mcgode/slang-intellij/compare/v0.0.1...v0.0.2
[0.0.1]: https://github.com/Mcgode/slang-intellij/commits/v0.0.1
