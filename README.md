PlayerNames
==========

PlayerNames is a Bukkit plugin (with an API) that keeps track of names that players joined the server with. 
It is recommended that this plugin be used only if other plugins depend on it. This plugin currently supports YAML 
and MySQL for storage, but it is highly recommended that this plugin use MySQL for performance reasons, especially 
when thousands of players are being tracked. 

Usage
==========
Each method should have sufficient documentation, if you feel like a method does not have enough documentation please
 create an issue or pm me (SupaHam) anywhere. The class that you should use is the PlayerNamesAPI class.

```java    
    // Use the following method to retrieve all names a player previously logged in with
    PlayerNamesAPI.getNames(UUID)
```
