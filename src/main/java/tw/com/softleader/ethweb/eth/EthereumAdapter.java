package tw.com.softleader.ethweb.eth;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.ethereum.config.SystemProperties;
import org.ethereum.core.Block;
import org.ethereum.core.CallTransaction;
import org.ethereum.core.PendingState;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionExecutionSummary;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListener;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.net.eth.message.StatusMessage;
import org.ethereum.net.message.Message;
import org.ethereum.net.p2p.HelloMessage;
import org.ethereum.net.rlpx.Node;
import org.ethereum.net.server.Channel;
import org.ethereum.sync.SyncManager;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.LogInfo;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import tw.com.softleader.commons.collect.Sets;
import tw.com.softleader.ethweb.eth.model.BlockInfo;
import tw.com.softleader.ethweb.eth.model.TxPackage;

@Slf4j
public class EthereumAdapter implements Runnable {
  public static final Stack<TxPackage> txs = new Stack<>();
  public static final List<String> txLogs = new ArrayList<>();

  private static final Map<String, Consumer<LogInfo>> eventCallbacks = Maps.newHashMap();
  private static final Set<String> eventAddrSet = Sets.newHashSet();
  private static final ExecutorService eventExecutor = Executors.newSingleThreadExecutor();

  @Autowired
  protected Ethereum ethereum;

  @Autowired
  private SyncManager syncManager;

  @Autowired
  protected SystemProperties config;

  @Autowired
  private SimpMessagingTemplate msgSender;
  
  private volatile long txCount;
  private volatile long gasSpent;
  
  /**
   * The method is called after all EthereumJ instances are created
   */
  @PostConstruct
  private void springInit() {
    // adding the main EthereumJ callback to be notified on different kind of events
    ethereum.addListener(listener);

    log.info("Sample component created. Listening for ethereum events...");

    // starting lifecycle tracking method run()
    new Thread(this, "EthereumWorker").start();
  }

  /**
   * The method tracks step-by-step the instance lifecycle from node discovery till sync completion.
   * At the end the method onSyncDone() is called which might be overridden by a sample subclass to
   * start making other things with the Ethereum network
   */
  public void run() {
    try {
      log.info("Sample worker thread started.");

      if (config.peerDiscovery()) {
        waitForDiscovery();
      } else {
        log.info(
            "Peer discovery disabled. We should actively connect to another peers or wait for incoming connections");
      }

      waitForAvailablePeers();

      waitForSyncPeers();

      waitForFirstBlock();

      waitForSync();

      onSyncDone();

    } catch (Exception e) {
      log.error("Error occurred in Sample: ", e);
    }
  }

  /**
   * Is called when the whole blockchain sync is complete
   */
  public void onSyncDone() {
    log.info("Monitoring new blocks in real-time...");
  }

  protected List<Node> nodesDiscovered = new Vector<>();

  /**
   * Waits until any new nodes are discovered by the UDP discovery protocol
   */
  protected void waitForDiscovery() throws Exception {
    log.info("Waiting for nodes discovery...");

    int bootNodes = config.peerDiscoveryIPList().size() + 1; // +1: home node
    int cnt = 0;
    while (true) {
      Thread.sleep(cnt < 30 ? 300 : 5000);

      if (nodesDiscovered.size() > bootNodes) {
        log.info("[v] Discovery works, new nodes started being discovered.");
        return;
      }

      if (cnt >= 30)
        log.warn("Discovery keeps silence. Waiting more...");
      if (cnt > 50) {
        log.error("Looks like discovery failed, no nodes were found.\n"
            + "Please check your Firewall/NAT UDP protocol settings.\n"
            + "Your IP interface was detected as " + config.bindIp() + ", please check "
            + "if this interface is correct, otherwise set it manually via 'peer.discovery.bind.ip' option.");
        throw new RuntimeException("Discovery failed.");
      }
      cnt++;
    }
  }

  protected Map<Node, StatusMessage> ethNodes = new Hashtable<>();

  /**
   * Discovering nodes is only the first step. No we need to find among discovered nodes those ones
   * which are live, accepting inbound connections, and has compatible subprotocol versions
   */
  protected void waitForAvailablePeers() throws Exception {
    log.info("Waiting for available Eth capable nodes...");
    int cnt = 0;
    while (true) {
      Thread.sleep(cnt < 30 ? 1000 : 5000);

      if (ethNodes.size() > 0) {
        log.info("[v] Available Eth nodes found.");
        return;
      }

      if (cnt >= 30)
        log.info("No Eth nodes found so far. Keep searching...");
      if (cnt > 60) {
        log.error("No eth capable nodes found. Logs need to be investigated.");
        // throw new RuntimeException("Eth nodes failed.");
      }
      cnt++;
    }
  }

  protected List<Node> syncPeers = new Vector<>();
  protected AtomicInteger syncPeerCnt = new AtomicInteger(0);

  /**
   * When live nodes found SyncManager should select from them the most suitable and add them as
   * peers for syncing the blocks
   */
  protected void waitForSyncPeers() throws Exception {
    log.info("Searching for peers to sync with...");
    int cnt = 0;
    while (true) {
      Thread.sleep(cnt < 30 ? 1000 : 5000);

      if (syncPeers.size() > 0) {
        log.info("[v] At least one sync peer found.");
        return;
      }

      if (cnt >= 30)
        log.info("No sync peers found so far. Keep searching...");
      if (cnt > 60) {
        log.error("No sync peers found. Logs need to be investigated.");
        // throw new RuntimeException("Sync peers failed.");
      }
      cnt++;
    }
  }

  protected Block bestBlock = null;

  /**
   * Waits until blocks import started
   */
  protected void waitForFirstBlock() throws Exception {
    Block currentBest = ethereum.getBlockchain().getBestBlock();
    log.info("Current BEST block: " + currentBest.getShortDescr());
    log.info("Waiting for blocks start importing (may take a while)...");
    int cnt = 0;
    while (true) {
      Thread.sleep(cnt < 300 ? 1000 : 60000);

      if (bestBlock != null && bestBlock.getNumber() > currentBest.getNumber()) {
        log.info("[v] Blocks import started.");
        return;
      }

      if (cnt >= 300)
        log.info("Still no blocks. Be patient...");
      if (cnt > 330) {
        log.error(
            "No blocks imported during a long period. Must be a problem, logs need to be investigated.");
        // throw new RuntimeException("Block import failed.");
      }
      cnt++;
    }
  }

  boolean synced = false;
  boolean syncComplete = false;

  /**
   * Waits until the whole blockchain sync is complete
   */
  private void waitForSync() throws Exception {
    log.info(
        "Waiting for the whole blockchain sync (will take up to several hours for the whole chain)...");
    while (true) {
      Thread.sleep(10000);

      if (synced) {
        log.info("[v] Sync complete! The best block: " + bestBlock.getShortDescr());
        syncComplete = true;
        return;
      }

//      BlockInfo msgBlock = new BlockInfo(bestBlock, syncPeerCnt.get(), syncManager.getLastKnownBlockNumber());
//      msgBlock.setTxSize(txCount);
//      msgBlock.setGasUsed(gasSpent);
//      msgSender.convertAndSend("/topic/onblock", msgBlock);
      log.info("Blockchain syncing. peers: " + syncPeerCnt.get() + ". Last imported block: "
          + bestBlock.getShortDescr() + " (Total: txs: " + txCount + ", gas: " + (gasSpent / 1000)
          + "k)");
      txCount = 0;
      gasSpent = 0;
    }
  }
  
  public void watchEvent(String address, Consumer<LogInfo> consumer) {
    eventCallbacks.put(address.toUpperCase(), consumer);
    eventAddrSet.addAll(eventCallbacks.keySet());
  }

  /**
   * The main EthereumJ callback.
   */
  EthereumListener listener = new EthereumListenerAdapter() {
    @Override
    public void onSyncDone() {
      synced = true;
    }

    @Override
    public void onNodeDiscovered(Node node) {
      if (nodesDiscovered.size() < 1000) {
        nodesDiscovered.add(node);
      }
    }

    @Override
    public void onEthStatusUpdated(Channel channel, StatusMessage statusMessage) {
      ethNodes.put(channel.getNode(), statusMessage);
    }

    @Override
    public void onPeerAddedToSyncPool(Channel peer) {
      syncPeers.add(peer.getNode());
      syncPeerCnt.addAndGet(1);
    }

    @Override
    public void onBlock(Block block, List<TransactionReceipt> receipts) {
      msgSender.convertAndSend("/topic/onblock", new BlockInfo(block, syncPeerCnt.get(), syncManager.getLastKnownBlockNumber()));
      bestBlock = block;
      txCount += receipts.size();
      for (TransactionReceipt receipt : receipts) {
        gasSpent += ByteUtil.byteArrayToLong(receipt.getGasUsed());
      }
      if (syncComplete) {
        log.info("New block. peers: " + syncPeerCnt.get() + " block: " + block.getShortDescr());
        // logger.info("PendingStateTransactions: " +
        // ethereum.getPendingStateTransactions().size());

        while(!txs.isEmpty()) {
          TxPackage txp = txs.pop();
          final long nonce = ethereum.getRepository().getNonce(txp.getEcKey().getAddress()).longValue();
          final long gasLimit = Long.parseLong(Hex.toHexString(ethereum.getBlockchain().getBestBlock().getGasLimit()), 16);
          final long gas = (long) (gasLimit * txp.getGasRatio());
          
          final Transaction tx;
          if (txp.getFunction() == null) {
            tx = CallTransaction.createRawTransaction(nonce, ethereum.getGasPrice(), gas, txp.getToAddress(), txp.getValue(), null);
          } else {
            tx = CallTransaction.createCallTransaction(nonce, ethereum.getGasPrice(), gas, txp.getToAddress(), txp.getValue(), txp.getFunction(), txp.getArgs());
          }
          
          tx.sign(txp.getEcKey());
          log.info("Sending Transaction: nonce:{}, value:{}, Gas:{}, GasPrice:{}, GasLimit{}", nonce, txp.getValue(), gas, ethereum.getGasPrice(), gasLimit);
          ethereum.submitTransaction(tx);
        }

      }
    }

    @Override
    public void onRecvMessage(Channel channel, Message message) {}

    @Override
    public void onSendMessage(Channel channel, Message message) {}

    @Override
    public void onPeerDisconnect(String host, long port) {
      syncPeerCnt.addAndGet(-1);
    }

    @Override
    public void onPendingTransactionsReceived(List<Transaction> transactions) {}

    @Override
    public void onPendingStateChanged(PendingState pendingState) {}

    @Override
    public void onHandShakePeer(Channel channel, HelloMessage helloMessage) {}

    @Override
    public void onNoConnections() {}

    @Override
    public void onVMTraceCreated(String transactionHash, String trace) {}

    @Override
    public void onTransactionExecuted(TransactionExecutionSummary summary) {
      summary.getLogs().stream()
          .filter(l -> eventAddrSet.stream().anyMatch(e -> e.equalsIgnoreCase(Hex.toHexString(l.getAddress()))))
          .forEach(l -> {
            eventExecutor.submit(() -> eventCallbacks.get(Hex.toHexString(l.getAddress()).toUpperCase()).accept(l));
          });
    }
  };

}
