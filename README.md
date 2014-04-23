PlayerNames
==========

PlayerNames is a Bukkit plugin (with an API) that keeps track of names that players joined the server with. 
It is recommended that this plugin be used only if other plugins depend on it. This plugin currently supports YAML 
and MySQL for storage, but it is highly recommended that this plugin use MySQL for performance reasons, especially 
when thousands of players are being tracked. 

Just to clarify, this would be useful with commands such as **/seen**, where the command sender would insert the 
last name their target player logged on to the server with. At that point in time, whether their target player logged
in with their new name (if they did change their name) or not, the database will keep record of the name belonging to 
them. The issue that arises is when another player changes their name to the name of the target player. This is the 
part where the command sender would receive info about the new player and get mislead. I don't believe there is much 
I can do on my end to prevent this issue.

Usage
==========
Each method should have sufficient documentation, if you feel like a method does not have enough documentation please
 create an issue or pm me (SupaHam) anywhere. The class that you should use is the PlayerNamesAPI class.

```java    
    // Use the following method to retrieve all names a player previously logged in with
    PlayerNamesAPI.getNames(UUID)
```
