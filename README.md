# Guereza 🐒

Guereza is a small example of distributed application design. It uses the following patterns:
- DDD
- AOP
- EventBus
- Event Sourcing
- CQRS

Moreover, the project implements the following features:
- Netty Server and Client
- Custom Injection Framework
- Custom Event Sourcing Framework

## Build

Building and running is easy with `gradle`:

```
$ gradle wrapper
$ ./gradlew build
$ java -jar build/libs/guereza-1.0-SNAPSHOT-all.jar
```

## Usage

There is 4 available modules:
- server: aggregate and dispatch all events
- store: store events and let reducers act on them
- crawler and indexer: request URLs and index the content

You need to start **one** `server` and `store`. Then, you can start as many other modules as you want.
