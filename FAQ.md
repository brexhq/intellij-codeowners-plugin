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