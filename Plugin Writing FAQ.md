# How to...

Random things we learned about writing IntelliJ plugins 

## Listen to editor events

Add classes to plugin.xml ([docs](https://plugins.jetbrains.com/docs/intellij/plugin-listeners.html#defining-application-level-listeners))

```xml
<idea-plugin>
    <applicationListeners>
      <listener class="myPlugin.MyListenerClass" topic="BaseListenerInterface"/>
    </applicationListeners>
</idea-plugin>
``` 

Alternatively, subscribe in code. This can be useful if you e.g. want a widget instance to listen to
some events, instead of separate listener/widget instances which communicate.

```kotlin
fun init() {
    project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: MutableList<out VFileEvent>) {
                    super.after(events)
                    /* ... */
                }
            }
    )
}
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

## Gotchas

- `EditorBasedWidget` is supposed to keep track of the currently selected & open editor/file,
    but sometimes it seems to just 'lose' its link to the editor!
    - If you're using it to subscribe to file events, just use the files in the event instead
- Working with files/projects is not as simple as you might imagine. There are...
    - Projects
    - ... which can have multiple modules
    - ... which can be inside and depend on other modules
    - ... all of which can have multiple source roots!
    - So finding "the top level" of a project is not as simple as it might seem.
    
