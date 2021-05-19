# CODEOWNERS IntelliJ plugin

![demo](demo.gif)

<!-- Plugin description -->

The CodeOwners plugin adds a widget to your IDE's status bar which shows which users or teams own the currently opened
file, as defined by your project's [CODEOWNERS file](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/creating-a-repository-on-github/about-code-owners). 

If the file has multiple owners, the widget will show the first owner and indicate that there are more, and you can see
the full list by clicking the widget

When you click on an owner, the plugin navigates you to the line in the CODEOWNERS file which declares the open file's
ownership.

<!-- Plugin description end -->


### Building and installing manually

To build and install this plugin yourself instead of installing from the Plugins marketplace:

1. Clone this repo
2. Import the project into IntelliJ as a Gradle project
3. Run using the "Run Plugin" configuration.
4. If this succeeds, a new IntelliJ instance should open with the plugin installed, which you can close.
5. Check that a .jar file is present in `./build/libs`
6. In your IntelliJ editor, open Plugins, click the cog icon, click install from disk, and select the .jar file.

### Known issues

- May not work correctly for modules containing multiple source roots
- Doesn't support all valid CODEOWNERS file locations
- Displays even when no modules have CODEOWNERS
- Module:CODEOWNERS must be 1:1
