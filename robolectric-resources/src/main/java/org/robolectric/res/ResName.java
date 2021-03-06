package org.robolectric.res;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResName {
  private static final Pattern FQN_PATTERN = Pattern.compile("^([^:]*):([^/]+)/(.+)$");
  private static final int NAMESPACE = 1;
  private static final int TYPE = 2;
  private static final int NAME = 3;

  public final @NotNull String packageName;
  public final @NotNull String type;
  public final @NotNull String name;

  public final int hashCode;

  public ResName(@NotNull String packageName, @NotNull String type, @NotNull String name) {
    this.packageName = packageName;
    this.type = type;
    this.name = name.indexOf('.') != -1 ? name.replace('.', '_').trim() : name.trim();

    hashCode = computeHashCode();
  }

  public ResName(@NotNull String fullyQualifiedName) {
    Matcher matcher = FQN_PATTERN.matcher(fullyQualifiedName.trim());
    if (!matcher.find()) {
      throw new IllegalStateException("\"" + fullyQualifiedName + "\" is not fully qualified");
    }
    packageName = matcher.group(NAMESPACE);
    type = matcher.group(TYPE);
    String nameStr = matcher.group(NAME);
    name = nameStr.indexOf('.') != -1 ? nameStr.replace('.', '_') : nameStr;

    hashCode = computeHashCode();
    if (packageName.equals("xmlns")) throw new IllegalStateException("\"" + fullyQualifiedName + "\" unexpected");
  }

  /**
   * Returns null if the resource could not be qualified.
   */
  public static String qualifyResourceName(@NotNull String possiblyQualifiedResourceName, String defaultPackageName, String defaultType) {
    ResName resName = qualifyResName(possiblyQualifiedResourceName, defaultPackageName, defaultType);
    return resName != null ? resName.getFullyQualifiedName() : null;
  }

  public static ResName qualifyResName(@NotNull String possiblyQualifiedResourceName, ResName defaults) {
    return qualifyResName(possiblyQualifiedResourceName, defaults.packageName, defaults.type);
  }

  public static ResName qualifyResName(@NotNull String possiblyQualifiedResourceName, String defaultPackageName, String defaultType) {
    int indexOfColon = possiblyQualifiedResourceName.indexOf(':');
    int indexOfSlash = possiblyQualifiedResourceName.indexOf('/');
    String type = null;
    String packageName = null;
    String name = possiblyQualifiedResourceName;
    if (indexOfColon > indexOfSlash) {
      if (indexOfSlash > 0) {
        type = possiblyQualifiedResourceName.substring(0, indexOfSlash);
      }
      packageName = possiblyQualifiedResourceName.substring(indexOfSlash + 1, indexOfColon);
      name =  possiblyQualifiedResourceName.substring(indexOfColon + 1);
    } else if (indexOfSlash > indexOfColon) {
      if (indexOfColon > 0) {
        packageName = possiblyQualifiedResourceName.substring(0, indexOfColon);
      }
      type = possiblyQualifiedResourceName.substring(indexOfColon + 1, indexOfSlash);
      name = possiblyQualifiedResourceName.substring(indexOfSlash + 1);
    }

    if ((type == null && defaultType == null) || packageName == null && defaultPackageName == null) {
      return null;
    }

    return new ResName(packageName == null ? defaultPackageName : packageName,
        type == null ? defaultType : type,
        name);
  }

  public static Integer getResourceId(ResourceIndex resourceIndex, String possiblyQualifiedResourceName, String contextPackageName) {
    if (possiblyQualifiedResourceName == null) {
      return null;
    }

    if (possiblyQualifiedResourceName.equals("@null")) {
      return null;
    }

    // Was not able to fully qualify the resource name
    String fullyQualifiedResourceName = qualifyResourceName(possiblyQualifiedResourceName, contextPackageName, null);
    if (fullyQualifiedResourceName == null) {
      return null;
    }

    fullyQualifiedResourceName = fullyQualifiedResourceName.replaceAll("[@+]", "");
    Integer resourceId = resourceIndex.getResourceId(new ResName(fullyQualifiedResourceName));
    // todo warn if resourceId is null
    return resourceId;
  }

  public static ResName qualifyFromFilePath(@NotNull final String packageName, @NotNull final String filePath) {
    final FileFsFile filePathFile = new FileFsFile(new File(filePath));
    final String type = filePathFile.getParent().getName().split("-")[0];
    final String name = filePathFile.getBaseName();

    return new ResName(packageName, type, name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ResName resName = (ResName) o;

    if (hashCode() != resName.hashCode()) return false;

    if (!packageName.equals(resName.packageName)) return false;
    if (!type.equals(resName.type)) return false;
    if (!name.equals(resName.name)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    return "ResName{" + getFullyQualifiedName() + "}";
  }

  public String getFullyQualifiedName() {
    return packageName + ":" + type + "/" + name;
  }

  public String getNamespaceUri() {
    return "http://schemas.android.com/apk/res/" + packageName;
  }

  public ResName withPackageName(String packageName) {
    if (packageName.equals(this.packageName)) return this;
    return new ResName(packageName, type, name);
  }

  public void mustBe(String expectedType) {
    if (!type.equals(expectedType)) {
      throw new RuntimeException("expected " + getFullyQualifiedName() + " to be a " + expectedType + ", is a " + type);
    }
  }

  private int computeHashCode() {
    int result = packageName.hashCode();
    result = 31 * result + type.hashCode();
    result = 31 * result + name.hashCode();
    return result;
  }
}
