# CODEOWNERS IntelliJ plugin

![demo](demo.gif)

<!-- Plugin description -->

Adds a widget to your IDE's status bar showing which users or teams own the currently
opened file, as defined by your project's [CODEOWNERS file](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/creating-a-repository-on-github/about-code-owners). 
Multiple code owners are supported by displaying a list of owners when you click the widget, and clicking 
an owner will navigate to the line of the CODEOWNERS file which declares the currently open file's ownership.
<!-- Plugin description end -->

### Installing from the IntelliJ Marketplace

This plugin is available from the [IntelliJ plugin marketplace](https://plugins.jetbrains.com/plugin/16819-codeowners)! Just open the Plugins page in Preferences,
and search for "CodeOwners" in the Marketplace tab. 

### Installing from the Releases page

To install the JAR from the [releases](releases) page:

1. Download the [latest release](releases/latest)
2. Open IntelliJ
3. Open Preferences -> Plugins
4. Click the cog icon in the top right
5. Click "Install from disk"
6. Find and open the .JAR file you downloaded.

### Building and installing manually

To build and install this plugin yourself:

1. Clone this repo
2. Import the project into IntelliJ as a Gradle project
3. Run using the "Run Plugin" configuration.
4. If this succeeds, a new IntelliJ instance should open with the plugin installed, which you can close.
5. Check that a .jar file is present in `./build/libs`
6. Install manually as described above 

### Known issues

- May not work correctly for modules containing multiple source roots
- The test task currently fails, in part because we're formatting with ktlint but the test is set up to use Detekt
