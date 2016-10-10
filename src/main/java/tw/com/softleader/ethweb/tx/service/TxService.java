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
  
  public void addCallTx(String address, CallTransaction.Function function, Object... args) {
    FileSystemKeystore keystore = new FileSystemKeystore(env.getProperty("eth.keystoreDir"));
    ECKey ecKey = keystore.loadStoredKey(env.getProperty("eth.keystore.address"), env.getProperty("eth.keystore.password"));
    TxPackage tx = new TxPackage(address, ecKey, function, 0.7, 0L, args);
    EthereumAdapter.txs.push(tx);
  }
  
  public void addTx(String address, long wei) {
    FileSystemKeystore keystore = new FileSystemKeystore(env.getProperty("eth.keystoreDir"));
    ECKey ecKey = keystore.loadStoredKey(env.getProperty("eth.keystore.address"), env.getProperty("eth.keystore.password"));
    TxPackage tx = new TxPackage(address, ecKey, 0.7, wei);
    EthereumAdapter.txs.push(tx);
  }
  
//  FileSystemKeystore keystore = new FileSystemKeystore(env.getProperty("eth.keystoreDir"));
//  ECKey ecKey = keystore.loadStoredKey(env.getProperty("eth.keystore.address"), env.getProperty("eth.keystore.password"));
//  
//  String toAddress = "3B584F3d1E4F4462B684bEE0f7Fb96D03b807C0F";
//  CallTransaction.Function function = CallTransaction.Function.fromSignature("deposit", "bytes32");
//  TxPackage tx = new TxPackage(toAddress, ecKey, function, 0.7, 0L, message);
//
//  EthereumAdapter.txs.add(tx);

}
