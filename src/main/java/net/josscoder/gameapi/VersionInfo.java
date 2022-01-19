package net.josscoder.gameapi;

import lombok.Getter;

@Getter
public class VersionInfo {

  public static final boolean DEFAULT_DEBUG = true;

  private final String snapshot = "1.1-SNAPSHOT";
  private final String author = "Josscoder";

  private final String branchName;
  private final String commitId;

  public VersionInfo(String branchName, String commitId) {
    this.branchName = branchName;
    this.commitId = commitId;
  }

  public static VersionInfo unknown() {
    return new VersionInfo("unknown", "unknown");
  }
}
