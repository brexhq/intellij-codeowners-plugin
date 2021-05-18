# How to...

## Listen to editor events

Add classes to plugin.xml ([docs](https://plugins.jetbrains.com/docs/intellij/plugin-listeners.html#defining-application-level-listeners))

```xml
<idea-plugin>
    <applicationListeners>
      <listener class="myPlugin.MyListenerClass" topic="BaseListenerInterface"/>
    </applicationListeners>
</idea-plugin>
``` 

### Listeners

| Listen to | Listener |
|-|-|
| Project open | com.intellij.openapi.project.ProjectManagerListener |
| File open | org.brex.plugins.codeowners.services.FileService |

## Add status bar widgets

Status bar widgets require:

1. A Wdiget Factory class which extends from `StatusBarWidgetFactory`
2. A Widget class which extends from `StatusBarWidget` (or a subclass e.g. `EditorBasedWidget`)
3. An entry in the plugin configuration which hooks the factory into the status bar Extension Point: 

```xml
<idea-plugin>
    <applicationListeners>
      <statusBarWidgetFactory implementation="org.brex.plugins.codeowners.widget.CodeOwnersWidgetFactory"/>
    </applicationListeners>
</idea-plugin>
``` 

**Note**: The name of the XML element (`statusBarWidgetFactory`) and the name of the interface `StatusBarWidgetFactory`
_must match_. They are the name of the Extension Point.  