[![Build Status](https://ci.cloudnetservice.eu/buildStatus/icon?job=CloudNetService/CloudNet/master)](https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet/master)
[![star this repo](http://githubbadges.com/star.svg?user=CloudNetService&repo=CloudNet)](https://github.com/CloudNetService/CloudNet)
[![fork this repo](http://githubbadges.com/fork.svg?user=CloudNetService&repo=CloudNet)](https://github.com/CloudNetService/CloudNet/fork)
[![GitHub license](https://img.shields.io/github/license/CloudNetService/CloudNet.svg)](https://github.com/CloudNetService/CloudNet/blob/master/LICENSE)

[![DepShield Badge](https://depshield.sonatype.org/badges/CloudNetService/CloudNet/depshield.svg)](https://depshield.github.io)
[![GitHub issues](https://img.shields.io/github/issues/CloudNetService/CloudNet.svg)](https://github.com/CloudNetService/CloudNet/issues)
[![GitHub contributors](https://img.shields.io/github/contributors/CloudNetService/CloudNet.svg)](https://github.com/CloudNetService/CloudNet/graphs/contributors)
[![Github All Releases](https://img.shields.io/github/downloads/CloudNetService/CloudNet/total.svg)](https://github.com/CloudNetService/CloudNet/releases)
[![GitHub release](https://img.shields.io/github/release/CloudNetService/CloudNet.svg)](https://github.com/CloudNetService/CloudNet/releases)


# CloudNet | The Cloud Network Environment Technology 2
![Image of CloudNet](https://cdn.discordapp.com/attachments/325383142464552972/354670548292206594/CloudNet.png)


This is the CloudNet project, which has already taken a lot of time and effort for over 1 year. 
Here you can find the most recent commits to the project. 
I am absolutely not satisfied by the code and find it horribly awful.

CloudNet is a cloud computing service for Minecraft networks. With a wide range of functionality, it offers both control over Minecraft Servers (Craftbukkit / Spigot / Glowstone) and BungeeCord Proxys.

CloudNet manages server and helps networks to expand through new technologies to help the administration of a Minecraft Network. Another aspect of CloudNet are the unlimited development possibilities because you are able to build your own modules and extensive programming interfaces.
CloudNet supports a wide range of needs and future opportunities, such as a Round Robin DNS management with the CloudFlare company service and the multi-proxy functionality for networks with more than 500 players, the project is important to enable high performance and stabilization for a Minecraft network.

For general information about CloudNet go to [spigotmc.org](https://www.spigotmc.org/resources/cloudnet-the-cloud-network-environment-technology.42059/). 

### Requirements

 * Java 8
 * Linux/Windows server with a minimum of 2GB DDR3 Memory and 2 vCores
 
 **The use of Linux containers (LXC) or OpenVZ containers (OVZ) is discouraged. There are many issues with their stability.**  
Use of KVM virtualization or dedicated servers is recommended.

 ### Support
 
  * Spigot-Support » 1.8.8 - 1.15
    * PaperSpigot, TacoSpigot, Spigot
  * BungeeCord-Support » 1.8.8 - 1.15
    * BungeeCord, HexaCord, Waterfall, TraverTine
    
### Discord

* [Discord Invite](https://discord.cloudnetservice.eu/)
 
### Developers
If you would like to contribute to this repository, feel free to fork the repo and then create a pull request to our current development branch. 
  
Maven:
```xml

    <repositories>
      <repository>
        <id>cloudnet-releases</id>
        <name>CloudNet Release Repository</name>
        <url>https://repo.cloudnetservice.eu/repository/releases/</url>
      </repository>
    </repositories>

    <dependencies>
        <!-- Spigot/BungeeCord -->
        <dependency>
            <groupId>eu.cloudnetservice.cloudnet.v2</groupId>
            <artifactId>cloudnet-api-bridge</artifactId>
            <version>2.2.0</version>
            <scope>provided</scope>
        </dependency>
         <!-- CloudNet Core -->
        <dependency>
            <groupId>eu.cloudnetservice.cloudnet.v2</groupId>
            <artifactId>cloudnet-master</artifactId>
            <version>2.2.0</version>
            <scope>provided</scope>
        </dependency>
     </dependencies>

```
