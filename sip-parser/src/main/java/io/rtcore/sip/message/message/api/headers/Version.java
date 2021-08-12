/**
 * 
 */
package io.rtcore.sip.message.message.api.headers;

/**
 * 
 */
public final class Version {
  private final int majorVersion;
  private final int minorVersion;

  public Version(final int majorVersion, final int minorVersion) {
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
  }

  public int majorVersion() {
    return this.majorVersion;
  }

  public int minorVersion() {
    return this.minorVersion;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof Version)) return false;
    final Version other = (Version) o;
    if (this.majorVersion() != other.majorVersion()) return false;
    if (this.minorVersion() != other.minorVersion()) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + this.majorVersion();
    result = result * PRIME + this.minorVersion();
    return result;
  }

  @Override
  public String toString() {
    return "Version(majorVersion=" + this.majorVersion() + ", minorVersion=" + this.minorVersion() + ")";
  }
}
