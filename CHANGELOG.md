# CODEOWNERS plugin Changelog

## [0.4.0]

### Fixed

- Don't show any text in the status bar if selected file is null

## [0.3.0]

### Added

- More extensive description
- A custom icon
- Shortened status bar text to "Owner(s):"
- Support all Github-supported CODEOWNER file locations

### Fixed

- Code owner display is now updated on file rename
- Owners are now found correctly when modules are inside other modules
- Status bar is hidden when a project has no CODEOWNERS files

## [0.2.0]

### Fixed

- Projects with multiple modules and CODEOWNERS per module
- Projects where the project base dir is not the same as the module dir

## [0.1.0]

### Added
- Initial version