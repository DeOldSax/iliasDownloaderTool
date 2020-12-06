Ilias Downloader Tool
===================

Das Ilias Downloader Tool ist unter anderem als Hilfe gedacht, um Dateien aus dem
E-Learning Portal [**ilias**](https://www.ilias.de/)
einfacher zu verwalten und herunterzuladen. Das klicken durch die Ordner im Browser, kann durch langsame oder fehlende Internetverbindung sehr mühsam werden. Bei steigender Anzahl von Ordnern und Kursen im Ilias verliert man leicht den Überblick welche Dokumente man schon lokal vorliegen hat und welche nicht.
<br>
Aktuell unterstütze Portale und weitere Infos findest du auf der [**Website**](https://iliasdownloadertool.de).

Application
===================

![Image of Application](https://iliasdownloadertool.de/static/img/screenshot.png)


How to build your own Ilias Downloader Tool
================

1. Install [Maven v3.3.9](https://maven.apache.org/download.cgi)
2. Install [Java JDK v1.8.0_102](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
3. Build with ```mvn clean install``` 
4. Find your iliasdownloadertool in ```release/```

Build with SDKMAN!
------------------
0. (Only with Windows):

    1. Install WSL, Cygwin or another Linux environment.
    2. Install a X Server for Windows like [VcXsrv](https://sourceforge.net/projects/vcxsrv/)
    3. Start it and export the Display variable to the emulated X Server in your Linux bash.
    Also export a GL pass-through, because the X Server can only handle GL 1.2: 
    ```shell
      echo "export Display=:0" >> ~/.bashrc
      echo "export LIBGL_ALWAYS_INDIRECT=1" >> ~/.bashrc
    ```   
1. Install [SDKMAN!](https://sdkman.io/install):
    ```shell
      curl -s "https://get.sdkman.io" | bash
      source "$HOME/.sdkman/bin/sdkman-init.sh"
      sdk version
    ```
2. Install Java 8 with JavaFX and Maven 3.3.9:
    ```shell
   sdk install java 8.0.232.fx-zulu
   sdk install maven 3.3.9
    ```
   With `sdk list java` or `sdk list maven`, you can find the other available versions.
3. Create a`JAVA_HOME` variable:
    ```shell
   echo JAVA_HOME="$HOME/.sdkman/candidates/java/current" >> ~/.bashrc
   source ~/.bashrc
    ```
4. Clone this repo and change your university in the `pom.xml` at line 12 and in `src/main/java/control/IliasManager.java` at line 14++.
5. Build it with `mvn clean install`.
6. Start it with `java -jar ./release/IliasDownloaderTool-ube-v1.2.2/Ilias-Downloader-Tool-ube-v1.2.2.jar`.



Contribute
================
Feel free to contribute!!!