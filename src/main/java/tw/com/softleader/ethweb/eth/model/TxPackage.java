package tw.com.softleader.ethweb.eth.model;

import org.ethereum.core.CallTransaction;
import org.ethereum.crypto.ECKey;

public class TxPackage {
  
  private final String toAddress;
  
  private final ECKey ecKey;
  
  private final CallTransaction.Function function;
  
  private final long value;
  
  private final Object[] object;
  
  private final double gasRatio;

  public TxPackage(String toAddress, ECKey ecKey, CallTransaction.Function function, double gasRatio, long value, Object... object) {
    super();
    this.toAddress = toAddress;
    this.ecKey = ecKey;
    this.function = function;
    this.gasRatio = gasRatio;
    this.value = value;
    this.object = object;
  }

  public TxPackage(String toAddress, ECKey ecKey, double gasRatio, long value) {
    super();
    this.toAddress = toAddress;
    this.ecKey = ecKey;
    this.function = null;
    this.gasRatio = gasRatio;
    this.value = value;
    this.object = null;
  }

  public String getToAddress() {
    return toAddress;
  }

  public ECKey getEcKey() {
    return ecKey;
  }

  public CallTransaction.Function getFunction() {
    return function;
  }

  public double getGasRatio() {
    return gasRatio;
  }

  public long getValue() {
    return value;
  }

  public Object[] getArgs() {
    return object;
  }
  
}
