package tw.com.softleader.ethweb.utils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.springframework.validation.BindingResult;

import tw.com.softleader.commons.message.Message;

public class MvcUtils {
  
  private static Function<ConstraintViolation<?>, Message> violationToMessage = c -> new Message(c.getPropertyPath().toString(), c.getMessage());

  /**
   * 將檢核的結果轉為 Messages
   * @param validateResult
   * @return
   */
  public static <T> List<Message> toMessages(Set<ConstraintViolation<T>> validateResult) {
    return validateResult.stream().map(violationToMessage).collect(Collectors.toList());
  }

  /**
   * 將檢核的結果轉為 Messages
   * @param bindingResult
   * @return
   */
  public static List<Message> toMessages(BindingResult bindingResult) {
    return bindingResult.getFieldErrors().stream().map(Message::new).collect(Collectors.toList());
  }
  
  /**
   * 將 collection 轉為以 "," 區隔的String
   * @param collection
   * @return
   */
  public static String toListString(Collection<?> collection) {
    return toListString(collection, ",");
  }
  
  /**
   * 將 collection 轉為以 delimiter 區隔的String
   * @param collection
   * @param delimiter
   * @return
   */
  public static String toListString(Collection<?> collection, CharSequence delimiter) {
    if (collection != null && !collection.isEmpty()) {
      return collection.stream().map(Object::toString).collect(Collectors.joining(delimiter));
    } else {
      return null;
    }
  }
  
}