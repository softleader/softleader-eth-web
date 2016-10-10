package com.ethercamp.harmony.keystore;

import org.ethereum.crypto.ECKey;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import tw.com.softleader.ethweb.demo.GenericTest;

public class FileSystemKeystoreTest extends GenericTest {

  @Autowired
  private Environment env;

  @Test
  public void test() {
    FileSystemKeystore keystore = new FileSystemKeystore(env.getProperty("eth.keystoreDir"));
    ECKey ecKey = keystore.loadStoredKey(env.getProperty("eth.keystore.address"), env.getProperty("eth.keystore.password"));
    System.out.println(ecKey.getAddress());
  }

}
