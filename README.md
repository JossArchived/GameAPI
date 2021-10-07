# GameAPI [![Build](https://github.com/Josscoder/GameAPI/actions/workflows/build.yml/badge.svg)](https://github.com/Josscoder/GameAPI/actions/workflows/build.yml) [![](https://jitpack.io/v/Josscoder/GameAPI.svg)](https://jitpack.io/#Josscoder/GameAPI)


> A simple API to create games based on game states

### Create a new game

create a class and extend it to Game!

```java
import jossc.game.Game;

public class SkyWars extends Game {

  @Override
  public void init() {
    super.init();

    System.out.println("Hello world!");
  }

  @Override
  public void close() {
    super.close();

    System.out.println("Bye bye!");
  }
}
```

### How does this work?

First, create your statuses!. If you want a state where you can spend seconds to finish as a parameter, use TimeState, extend your class to it. If you want a custom State, use Game State, still extend your class to it. Example:

```java
import jossc.game.state.TimeState;

public class ExampleStateOne extends TimeState {

  public ExampleStateOne(PluginBase plugin, int secondsToEnd) {
    super(plugin, secondsToEnd);
  }

  @Override
  protected void onEnd() {
    System.out.println("Bye!");
  }

  @Override
  protected void onStart() {
    System.out.println("Hello players!");
  }

  @Override
  public void onUpdate() {
    System.out.println("Hi, I'm a little loop, it only lasted " + getDuration() + " seconds longer!");
  }
}
```

```java
import jossc.game.state.GameState;

public class ExampleStateTwo extends GameState {

  public ExampleStateTwo(PluginBase plugin) {
    super(plugin);
  }

  @NotNull
  @Override
  public Duration getDuration() {
    return Duration.ZERO; 
  }

  @Override
  protected void onEnd() {

  }

  @Override
  protected void onStart() {

  }

  @Override
  public void onUpdate() {

  }

  @Override
  public boolean isReadyToEnd() {
    return playersSize() >= 12; // If this condition is met, it will continue with the next state (worth the redundancy)
  }
}
```

```java
import jossc.game.Game;
import jossc.game.state.ScheduledStateSeries;

public class SkyWars extends Game {

  @Override
  public void init() {
    super.init();

    ScheduledStateSeries mainState = new ScheduledStateSeries();
    mainState.add(new ExampleStateOne(this, 10)); // This means that this state, after starting, only lasts 10 seconds.
    mainState.add(new ExampleStateTwo(this));
    mainState.start();
  }
}
```

Any class that extends GameState can add events and they will be listened to, GameState is registered as an event listener class.

### Methods of the Game class

```java
import jossc.game.Game;
import jossc.game.command.MyPositionCommand;
import jossc.game.state.ScheduledStateSeries;

public class SkyWars extends Game {

  @Override
  public void init() {
    super.init();

    ScheduledStateSeries mainState = new ScheduledStateSeries();
    //TODO: Here you add your states
    mainState.start();
    

    // You have to pass the mainState variable as a parameter,
    // which returns the series of states that you registered
    // This so that the command system knows how to interact
    registerDefaultCommands(mainState);

    // Among the default commands that you are going to register are:
    // skipState, This is used to skip to the next state, example, if you are in the "Preparing Game" state you want to skip, it will continue to the next state in the order as you registered them.
    // stopStates, This is used for debugging, this prohibits the system from continuing with the states and staying frozen in the current state.
    // continueStates, If the states are paused, this will continue, remove the system freeze.
    // To execute all these commands, the user must have the permission "minigame.admin.permission"
    
    // This method can register any command that extends to the command class of nukkit
    registerCommand(new MyPositionCommand());
    
    //The MyPosition command, as its name indicates,
    // reveals your position in a message,
    // this command has already been created in this API, if you want to register it,
    // execute the previous command
    
    
    // This method allows you to register several commands at the same time, all separated by the symbol ","
    registerCommands(new MyPositionCommand(), ...other command);
    
    // This method, as its name implies, deletes all the commands registered so far.
    unregisterAllCommands();
    
    
    // and last but not least, the methods that listeners register
    registerListener(a listener here);
    
    registerListeners(multiple listeners separated by ",");
  }
}
```

### An example of how to work with this will be uploaded soon, it will go here.

### Thank you very much @Minikloon

- Base library [here](https://github.com/Minikloon/FSMgasm)
- Twitter of Minikloon [here](https://twitter.com/Minikloon)
- How to do this for Spigot [here](https://www.spigotmc.org/threads/organizing-your-minigame-code-using-fsmgasm.235786/)
