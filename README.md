# OneGaming-NetworkManager

### Description
This project contains several APIs for communication over netty packets, running plugins, 
connecting to the OneGaming database or interacting with the JVM console.  
It should be used as a base application for all standalone projects in OneGaming.  
> I.e. the mcone-cloud runs on networkmanager to benefit from the built in netty features 
> to communicate easily with mcserver and the cloud dashboard.

### Build and Run

Use `mvn install` to compile a runnable jar file.  
Please utilize the database credentials JVM options like here:  
`java -Xms512M -Xmx1024M -DHost=$DB_HOST -DPort=$DB_PORT -DUsername=$DB_USERNAME -DPassword=$DB_PASSWORD -jar onegaming-networkmanager-host-api.jar`

### Coding conventions
Please familiarize yourself with the [Oracle java code conventions](https://www.oracle.com/technetwork/java/codeconventions-150003.pdf).
All source code in this repository must be formatted as described there.

### Versioning Conventions
We *dont* utilize Semantic Versioning here, as it my not be necessary to increase the version on every commit.
Therefore we added the **Bugfix** Versioning. The rest partly depends on [Semantic Versioning](https://semver.org).

* The Version syntax is of 3 numbers seperated by dots and a `-SNAPSHOT` behind them (i.e. `0.0.1-SNAPSHOT`).
* If the plugin is still in creation process use `0.` at the beginning. Otherwise the first number must be greater than 0. 
* You can increase the version of your project if there are some small or big changes. 
Please change the maven project version in all `pom.xml` files of your repository. 
*(You can use `Ctrl+Shift+F` in IntelliJ with the file filter `pom.xml` to replace all old versions with the new one)*
* [**Bugfix**] If you just changed one or two classes in course of a small bugfix 
and you only changed a few lines of code, you dont necessarily have to increase the version.
* [**Patch**] If your project does not have an API or you just changed some code that dont modified the API, 
then increase only the last number (i.e. from `1.0.0-SNAPSHOT` to `1.0.1-SNAPSHOT`).
* [**Minor**] If the API code was changed or a bigger amount of code was changed, increase the second number
(i.e. from `1.0.1-SNAPSHOT` to `1.1.0-SNAPSHOT`)
* [**Major**] If the API code was changed and a breaking major new feature was added, increase the second number
(i.e. from `1.1.0-SNAPSHOT` to `2.0.0-SNAPSHOT`)