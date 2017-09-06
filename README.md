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

Library is deployed and provided with Maven Central and JCenter repositories, so to use it appropriate dependencies should be added to pom.xml if you use Maven or build.gradle in case of Gradle. Separate .jar archive also can be used, but why you should do it?
#### pom.xml:
```
<dependency>
    <groupId>io.github.spair</groupId>
    <artifactId>byond-message-client</artifactId>
    <version>1.1</version>
</dependency>
```
#### build.gradle:
```
dependencies {
    compile 'io.github.spair:byond-message-client:1.1'
}
```

## Usage
There are three main classes to use:
- ByondClient
- ByondMessage (with ServerAddress class)
- ByondResponse

### Example:
```
ByondClient client = new ByondClient();  // or ByondClient.create()
ByondMessage message = new ByondMessage(new ServerAddress("bagil.game.tgstation13.org", 2337), "?ping");

ByondResponse response = client.sendMessage(message);
```
If you print response object you could see something like that: 
```
ByondResponse(responseData=56.0, responseType=FLOAT_NUMBER)
```
Response data is an Object class, so manual casting into Float or String is needed.

#### Additional info:
* `ByondClient` object can be used with singleton pattern. (Actually, I would recommend that way.)
* On BYOND side message should be handled in `World/Topic()` method. Look for [BYOND Ref](http://www.byond.com/docs/ref/info.html#/world/proc/Topic) to see more.
* If you want just to send message to BYOND and you don't care about response use `sendCommand()` method instead of `sendMessage()` or set expected response type in ByondMessage as `ResponseType.NONE`.
* To control response type from BYOND set `ResponseType.FLOAT_NUMBER` or `ResponseType.STRING` in `ByondMessage` instance.
If actual response type is different exception `UnexpectedResponseTypeException` will be thrown.

Also there are some exceptions I recommend to handle due to significant reasons.
1) __HostUnavailableException__ It will be thrown if host you try to send message is currently offline. Restart moment, for example.
2) __EmptyResponseException__ Exception will be thrown while two reasons: `World/Topic()` doesn't return any response on your message; moment between when server already restarted, but World didn't initialized. Little chance, but you can got in this situation.

Read more in [JavaDoc](https://www.javadoc.io/doc/io.github.spair/byond-message-client).
