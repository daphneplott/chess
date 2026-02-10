# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Design

[![Sequence Diagram]]
(https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAHZM9qBACu2AMQALADMABwATACcIDD+yPYAFmA6CD6GAEoo9kiqFnJIEGiYiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAEQDlGjAALYo43XjMOMANCu46gDu0ByLy2srKLPASAj7KwC+mMK1MJWs7FyUDRNTUDPzF4fjm6o7UD2SxW63Gx1O52B42ubE43FgD1uogaUCyOTAlAAFJlsrlKJkAI5pXIAShuNVE9yqsnkShU6ga9hQYAAqoNMe9PigyTTFMo1KoqUYdHUAGJITgwNmUXkwHSWGCcuZiHSo4AAaylgxgWyQYASisGXJgwAQao4CpQAA90RpeXSBfdERSVA1pVBeeSRConVVbi8YAozShgBaOhr0ABRK0qbAEQpeu5lB4lcwNQJOYIjCbzdTAJmLFaRqDeeqG6bKk3B0MK+Tq9DQsycTD2-nqX3Vb0oBpoHwIBCJykPVv01R1EBqjHujmDXk87QO9sPYx1BQcDhamXaQc+4cLttjichjEKHz6zHAM8JOct-ejoUrtcb0-6z1I3cPWHPMs49H4tR9lgX7wh2-plm8RrKssDQHKCl76h0ED1mg0ErFciaUB2qYYA04ROE42aTJBXwwDBIIrPBCSIchqEHNc6AcKYXi+AE0DsEysSinAkbSHACgwAAMhA2RFNhzDOtQAYtO03R9AY6gFGg2ZKvM6x-ACHDXGBQrAQGEEVl8UKgupuzfDCTwgRJVDIjACDCRKmJCSJhLEmAZLvoYe60gejLMtOKncre3n3suIripK7qyvK5YfMqpiqiGmoAOreA4MAmYCQV8iFVk2ZF24eaBNQBiyMxXtASAAF4oBuxalhhCIpsgaYwBmACMhG5qo+YLDBdXQA0PhlfqFXVXs9HNlli6Csmfout2m4etuO6edSd4CuOk4oC+CQXleN4jo6oWruugb7QV80drpZZORKmSqIBmDXUVkngURBkLKRaE-JR1ENl9dENVhzU4TAeEEWM72xSRZE-Vef0oQD6EMUx3h+P4XgoOgsTxEkmPY05vhYGJQpgQ0jTSJGAmRh0kY9L08mqIpIy-Uh6CmNpn4WQGlGjfkhQNKzyFPdzCJzV2DR2fYROOcJRMuWobkrUKh0MjATJgDte0IWzaDzsFR1VCuMAAOLMmdr7aHKCpC+zmAJRqUrDQko01VNB4vdZroW9eF1dp7JXO67tUltAQOzfAINgOmTgdZDXU9YW4z9WWQ281AVU1Y2KPuzl4tewtO1vpdXnZRtHAoNwJ5XtrvvyPrZdLkbIrSJXzKGEXfsF1dosNITZ73Y9z0R2TMCjFpxWNVUYm4fh2YTYxnhowEqIbv42ASpqAnoqbyoaCTVlSSbNP0-Yyos-Duvh1U12C+VGeVfzaB3zrwvD-nNnILkJvKo56I--MBWpJlal2mr5TWNdbZ61zobYUDQzbME7vIa2JpL7CwdpqUq6dM6MVVjNXK3skHABAYfMsWD744JgCncOTVSjR1arHTqApE59VDqnIOD8s4LxgUuAhhdzryBAWtA2asv5gAAWoTEDdpoPhFKbc2poEDqz3jw-BH9vbb1yI0CRvIugkJvr3TR4jlSDwQEBUWAdwIrDPrmQsjQJg2JQAASWkIWNq4RgiBFBFsBIeoUDui5CMFYKRQDqgCVBJYPxHEADllSRMuDALoE9XrAzobPCGoxrF7zsQ45ULi3EeK8SsHxfjwnzCCeMEJIAwnEV6uMKJypYnzHiYk7hS8WL+A4G4SITgUBOFiJGYIcBuIADZ4BbV3vMYoUdSaT3Jm0Top9z7OwRtmGJyprjX0eHCHmFDH7xmfqg1+6BCLrMMt9cyOyxadgLg0I8cgUASMxHALaEigFK0KqAny6tmRaygdIj2x15GIIEcAFBUD7ZqkwRwnBqjPY2SIfom5gdsFjSoWwmh08o4xzjjmZhBZWGlkGjCsa2dmx4PhYQ0FQiZDrTVvcjETyzmBQpUCp8yj5hGGWp8rmVy6gvOPI8kxAEzEiyuZYuouT5j5IaO4zxySky0JauDU5eTXF1DlYENpzF0aWErnZLYOMkCJDAHq-sEBDUACkIASkmYYfwVT1TTLobM168yWSyV6I4i+xylJjGwAgYAeqoBwAgHZKA6xHEuM2ZzAxfKjkuwfk-F+VFdbZg2IG4Nobw37CSiwJxtNegACEBIKDgAAaQadK9VMBNUwEud+V1tyYAACsbVoCedaiUbyUBEkVu5EuwjG5jh+RA88-y4VAoQT7KKNs0HswwU7VFbtWV8IaIinl+cUV7Jqui+qnMlWg3akwvMBKixsOJcu8aTZcF0rUTchF1LPlDrAaOplaqySLuzdAGAdZ2arvUQtHRXchykPXdWC0MBwzIT3WHA9WK0kMNxeMBOZ7k4XqrOaWsEYULcOfbSkRI6WTYC0Iy3+Aag2UG-RGqN0gAUhWbn3CASFFTYA5YYaKLiRhwASOxQwFGs1hp-ZAdWSAaBoHSpm2gSLb7EdI0KwBIrzHipHnM8emLI6IZVWMbVy8MZQCDUak1XhDOIBDLAYA2AA2ECfs68wTapKU2prTemxgGqVFvmPBt8JKgeXHNwPAUiVqVDwQ0CuVd-GDFUEFvBlRjatwi1WJR7wNAeRC3esLbcyO5hi3e2RDQEvtyS+x1Lg6CPDsyxFna0X6OwPi1lwwiifZQd1qV-2EdPNmbwKY5TjbVNuq8xpmeYM546ZvZgIAA)

