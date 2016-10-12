package tw.com.softleader.ethweb.tx.service;

import org.ethereum.core.CallTransaction;
import org.ethereum.crypto.ECKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ethercamp.harmony.keystore.FileSystemKeystore;

import tw.com.softleader.ethweb.eth.EthereumAdapter;
import tw.com.softleader.ethweb.eth.model.TxPackage;

@Service
public class TxService {

  @Autowired
  private Environment env;
  
  /**
   * 發送交易(執行function)
   * @param address 交易目的地地址
   * @param function 要呼叫的函式
   * @param args 函式所需的參數
   */
  public void addCallTx(String address, CallTransaction.Function function, Object... args) {
    FileSystemKeystore keystore = new FileSystemKeystore(env.getProperty("eth.keystoreDir"));
    ECKey ecKey = keystore.loadStoredKey(env.getProperty("eth.keystore.address"), env.getProperty("eth.keystore.password"));
    TxPackage tx = new TxPackage(address, ecKey, function, 0.7, 0L, args);
    EthereumAdapter.txs.push(tx);
  }
  
  /**
   * 發送一般交易
   * @param address 交易目的地地址
   * @param wei 要傳送的eth(單位wei)
   */
  public void addTx(String address, long wei) {
    FileSystemKeystore keystore = new FileSystemKeystore(env.getProperty("eth.keystoreDir"));
    ECKey ecKey = keystore.loadStoredKey(env.getProperty("eth.keystore.address"), env.getProperty("eth.keystore.password"));
    TxPackage tx = new TxPackage(address, ecKey, 0.7, wei);
    EthereumAdapter.txs.push(tx);
  }

}
