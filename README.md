[![Build Status](https://travis-ci.org/SpaiR/byond-message-client.svg?branch=master)](https://travis-ci.org/SpaiR/byond-message-client) 
[![Coverage Status](https://coveralls.io/repos/github/SpaiR/byond-message-client/badge.svg?branch=master)](https://coveralls.io/github/SpaiR/byond-message-client?branch=master)
[![Javadocs](https://www.javadoc.io/badge/io.github.spair/byond-message-client.svg)](https://www.javadoc.io/doc/io.github.spair/byond-message-client)
[![License](http://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/MIT)

# BYOND Message Client
## About 
Small library for Java, which provide simple way to send messages and receive responses from [BYOND](http://www.byond.com/) server.

## Installation
[![Maven Central](https://img.shields.io/maven-central/v/io.github.spair/byond-message-client.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.github.spair/byond-message-client)
[![JCenter](https://img.shields.io/bintray/v/spair/io.github.spair/byond-message-client.svg?label=jcenter)](https://bintray.com/spair/io.github.spair/byond-message-client/_latestVersion)

Library is deployed and provided with Maven Central and JCenter repositories.
#### pom.xml:
```
<dependency>
    <groupId>io.github.spair</groupId>
    <artifactId>byond-message-client</artifactId>
    <version>1.2.1</version>
</dependency>
```
#### build.gradle:
```
compile 'io.github.spair:byond-message-client:1.2.1'
```

## Usage
There are three main classes to use:
- ByondClient
- ByondMessage (with ServerAddress class)
- ByondResponse

### Example:
```
ByondMessage message = new ByondMessage("bagil.game.tgstation13.org", 2337, "ping");
ByondResponse response = ByondClient.getInstance().sendMessage(message);
```
If you print response object you could see something like that: 
```
ByondResponse(response=56.0, responseType=FLOAT_NUMBER)
```
Response data is an Object class, so manual class cast is needed.

#### Additional info:
* `ByondClient` object if fully singleton class with lazy initialization.
* On BYOND side message should be handled in `World/Topic()` method. Look [BYOND Ref](http://www.byond.com/docs/ref/info.html#/world/proc/Topic) for more info.
* If you want just to send message to BYOND and you don't care about response use `sendCommand()` method instead of `sendMessage()` or set expected response type in ByondMessage as `ResponseType.NONE`.
* To control response type from BYOND set `ResponseType.FLOAT_NUMBER` or `ResponseType.STRING` in `ByondMessage` instance.
If actual response type is different exception `UnexpectedResponseException` will be thrown.

Also there are some exceptions I'll recommend to handle due to significant reasons.
1) __HostUnavailableException__ It will be thrown if host you try to send message is currently offline. Restart moment, for example.
2) __UnexpectedResponseException__ Exception will be thrown while two reasons: `World/Topic()` doesn't return any response on your message; moment between when server already restarted, but World didn't initialized. Little chance, but you can got in this situation.

Read more in [JavaDoc](https://www.javadoc.io/doc/io.github.spair/byond-message-client).
