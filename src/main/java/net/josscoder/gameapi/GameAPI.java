package net.josscoder.gameapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GameAPI {

  public static final String BRANCH =
    "https://api.github.com/repos/Josscoder/GameAPI/commits/thehivemc";
  public static final Properties GIT_INFO = getGitInfo();
  public static final String VERSION = getVersion();

  private static Properties getGitInfo() {
    InputStream gitFileStream =
      GameAPI.class.getClassLoader().getResourceAsStream("git.properties");
    if (gitFileStream == null) {
      log.debug("Unable to find git.properties");
      return null;
    }
    Properties properties = new Properties();
    try {
      properties.load(gitFileStream);
    } catch (IOException e) {
      log.debug("Unable to load git.properties", e);
      return null;
    }
    return properties;
  }

  public static String getBranch() {
    String branch;
    if (
      GIT_INFO == null || (branch = GIT_INFO.getProperty("git.branch")) == null
    ) {
      return "null";
    }
    return branch;
  }

  public static String getVersion() {
    StringBuilder version = new StringBuilder();
    version.append("git-");
    String commitId;
    if (
      GIT_INFO == null ||
      (commitId = GIT_INFO.getProperty("git.commit.id.abbrev")) == null
    ) {
      return version.append("null").toString();
    }
    return version.append(commitId).toString();
  }
}
