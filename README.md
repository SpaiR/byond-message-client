[![Build Status](https://travis-ci.org/SpaiR/byond-message-client.svg?branch=master)](https://travis-ci.org/SpaiR/byond-message-client) 
[![Coverage Status](https://coveralls.io/repos/github/SpaiR/byond-message-client/badge.svg?branch=master)](https://coveralls.io/github/SpaiR/byond-message-client?branch=master)
[![Javadocs](https://www.javadoc.io/badge/io.github.spair/byond-message-client.svg)](https://www.javadoc.io/doc/io.github.spair/byond-message-client)
[![License](http://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/MIT)

# BYOND Message Client
## About 
Small library for Java, which provide simple way to send messages and receive responses from BYOND game server.

## Installation
[![Maven Central](https://img.shields.io/maven-central/v/io.github.spair/byond-message-client.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.github.spair/byond-message-client)
[![JCenter](https://img.shields.io/bintray/v/spair/io.github.spair/byond-message-client.svg?label=jcenter)](https://bintray.com/spair/io.github.spair/byond-message-client/_latestVersion)

Library is deployed and provided with Maven Central and JCenter repositories, so to use it appropriate dependencies should be added to pom.xml if you use Maven or build.gradle in case of Gradle. Separate .jar archive also can be used, but why you should do it?
#### pom.xml:
```
<dependency>
    <groupId>io.github.spair</groupId>
    <artifactId>byond-message-client</artifactId>
    <version>1.0</version>
</dependency>
```
#### build.gradle:
```
dependencies {
    compile 'io.github.spair:byond-message-client:1.0'
}
```

## Usage
There are three main classes to use:

#### ByondClient
Sends message.
Methods:
```
public void sendCommand(ByondMessage);
public ByondResponse sendMessage(ByondMessage);
public ByondResponse sendMessage(ByondMessage, int);
```

#### ByondMessage
Wraps message and server address.
Fields:
```
private ServerAddress serverAddress;
private String message;
private ResponseType expectedResponse = ResponseType.ANY;
```
_ServerAddress_ has `String name` and `int port`.

#### ByondResponse
Wraps response data.
Fields:
```
private Object responseData;
private ResponseType responseType;
```

### Example:
```
ByondClient client = new ByondClient();  // or ByondClient.create()
ByondMessage message = new ByondMessage(new ServerAddress("bagil.game.tgstation13.org", 2337), "?ping");

/**
 * 500 - is read timeout. In that case it's unnecessary parameter, because 500 is default value.
 * So you could just: `client.sendMessage(message);`.
 * Read JavaDoc to get more info.
 */
ByondResponse response = client.sendMessage(message, 500);

// If you print response object you could see next:
// ByondResponse(responseData=56.0, responseType=FLOAT_NUMBER)
```

* `ByondClient` object can be used with singletone pattern. (Actually, I would recommend that way.)
* On BYOND side message should be handled in `World/Topic()` method, look for [BYOND Ref](http://www.byond.com/docs/ref/info.html#/world/proc/Topic) to see more.
* If you want just to send message to BYOND and you don't care about response use `sendCommand()` method instead of `sendMessage()` or set expected response type in ByondMessage as `ResponseType.NONE`.
* To control response type from BYOND set `ResponseType.FLOAT_NUMBER` or `ResponseType.STRING` in `ByondMessage` object.
If actual response type is different exception `UnexpectedResponseTypeException` will be thrown.

Also there are some exceptions I recommend to handle due significant reasons.
1) __HostUnavailableException__ It will be thrown if host you try to send message is currently offline. Restart moment, for example.
2) __EmptyResponseException__ Exception will be thrown while two reasons: `World/Topic()` doesn't return any response on your message; moment between when server already restarted, but World didn't initialized. Little chance, but you can got in this situation.

Read more in [JavaDoc](https://www.javadoc.io/doc/io.github.spair/byond-message-client).

___Important__: Lombok is used to generate boilerplate code as getters/setters/constructors and some of it didn't get into javadoc, so be sure, for ByondMessage and ByondResponse it's all exist._ 

