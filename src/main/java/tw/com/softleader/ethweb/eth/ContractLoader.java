package tw.com.softleader.ethweb.eth;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.ethereum.core.CallTransaction;
import org.ethereum.core.CallTransaction.Param;
import org.spongycastle.util.encoders.Hex;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 此程式用來載入合約的界面，並且執行一切與合約介面有關的轉換
 */
@Slf4j
public class ContractLoader {
  
  /** 本次Demo用的天氣合約 */
  public final CallTransaction.Contract weatherPolicy;
  /** 借用來轉換的Jackson ObjectMapper */
  private final static ObjectMapper mapper = new ObjectMapper();
  
  /** 初始化 */
  public ContractLoader() {
    CallTransaction.Contract contract = null;
    try {
      contract = new CallTransaction.Contract(
          new String(
              Files.readAllBytes(
                  Paths.get(
                      EthereumAdapter.class.getClassLoader().getResource("contract/RainfallPolicy.json").toURI()
                  )
              )
          )
      );
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    } finally {
      weatherPolicy = contract;
    }
  }
  
  /**
   * 將invocation的內容轉換為Pojo
   * @param invocation 透過合約介面讀取logInfo所產生
   * @param clazz Pojo的Class
   * @return Pojo
   */
  public <T> T invocationToPojo(CallTransaction.Invocation invocation, Class<T> clazz) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    
    Map<String, Object> map = new HashMap<>();
    for (int i = 0; i < invocation.function.inputs.length; i++) {
      final Param param = invocation.function.inputs[i];
      final Object arg = invocation.args[i];
      final Object value = parse(param, arg);
      log.debug("parsing ethtereum event: param.name= {}, value={}", param.name, value);
      map.put(param.name, value);
    }
    
    return mapper.convertValue(map, clazz);
  }
  
  /**
   * 將invocation的內容轉換為單純的String
   * @param invocation 透過合約介面讀取logInfo所產生
   */
  public String invocationToString(CallTransaction.Invocation invocation) {
    return "[" + "contract=" + invocation.contract +
            (invocation.function.type + "= ") + invocation.function + ", args=" + parseArgsToString(invocation) + ']';
  }
  
  private String parseArgsToString(CallTransaction.Invocation invocation) {
    StringJoiner sj = new StringJoiner(",");
    for (int i = 0; i < invocation.function.inputs.length; i++) {
      final Param param = invocation.function.inputs[i];
      final Object arg = invocation.args[i];
      
      sj.add(parse(param, arg).toString());
    }
    
    return sj.toString();
  }

  private Object parse(final Param param, final Object arg) {
    if ("address".equals(param.getType())) {
      return new String(Hex.encode((byte[])arg));
    } else if ("bytes32".equals(param.getType())) {
      return (byte[]) arg;
    } else if ("byte".equals(param.getType())) {
      // FIXME still mismatch
      return new Byte(String.valueOf(arg));
    } else if ("bool".equals(param.getType())) {
      return new Boolean(String.valueOf(arg));
    } else if (param.getType().startsWith("uint")) {
      return new BigDecimal(String.valueOf(arg));
    } else if (param.getType().startsWith("int")) {
      return new BigDecimal(String.valueOf(arg));
    } else {
      return String.valueOf(arg);
    }
  }
  
}
