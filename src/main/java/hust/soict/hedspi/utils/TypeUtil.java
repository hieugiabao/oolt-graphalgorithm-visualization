package hust.soict.hedspi.utils;

public class TypeUtil {

  @SuppressWarnings("unchecked")
  public static <T> T uncheckedCast(Object o) {
    return (T) o;
  }
}
