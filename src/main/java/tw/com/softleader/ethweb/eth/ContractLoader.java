package tw.com.softleader.ethweb.eth;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

public class ContractLoader {
  
  public final CallTransaction.Contract contract01;
  private final static ObjectMapper mapper = new ObjectMapper();
  
  public ContractLoader() {
    CallTransaction.Contract contract = null;
    try {
      contract = new CallTransaction.Contract(
          new String(
              Files.readAllBytes(
                  Paths.get(
                      EthereumAdapter.class.getClassLoader().getResource("contract/contract01.json").toURI()
                  )
              )
          )
      );
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    } finally {
      contract01 = contract;
    }
  }
  
  public <T> T invocationToPojo(CallTransaction.Invocation invocation, Class<T> clazz) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    
    Map<String, Object> map = new HashMap<>();
    for (int i = 0; i < invocation.function.inputs.length; i++) {
      final Param param = invocation.function.inputs[i];
      final Object arg = invocation.args[i];
      final Object value = parse(param, arg);
      System.out.println(param.name + " = " + value);
      map.put(param.name, value);
    }
    
    return mapper.convertValue(map, clazz);
  }
  
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
      return new Byte(String.valueOf(arg));
    } else if ("bool".equals(param.getType())) {
      return new Boolean(String.valueOf(arg));
    } else if (param.getType().startsWith("uint")) {
      return new Integer(String.valueOf(arg));
    } else if (param.getType().startsWith("int")) {
      return new Integer(String.valueOf(arg));
    } else {
      return String.valueOf(arg);
    }
//    return CallTransaction.Type.getType(param.getType()).decode((byte[]) arg).toString();
//    if (typeName.contains("[")) return ArrayType.getType(typeName);
//    if ("bool".equals(typeName)) return new BoolType();
//    if (typeName.startsWith("int") || typeName.startsWith("uint")) return new IntType(typeName);
//    if ("address".equals(typeName)) return new AddressType();
//    if ("string".equals(typeName)) return new StringType();
//    if ("bytes".equals(typeName)) return new BytesType();
//    if (typeName.startsWith("bytes")) return new Bytes32Type(typeName);
//    throw new RuntimeException("Unknown type: " + typeName);
  }
  
}
