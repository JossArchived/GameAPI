package net.josscoder.gameapi.configurator;

public interface IConfigurator {
  void initItems();
  void run();
  boolean isCompleted();
  void complete();
}
