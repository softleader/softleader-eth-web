package tw.com.softleader.ethweb.eth.model;

import org.ethereum.core.Block;
import org.spongycastle.util.encoders.Hex;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockInfo {
  
  private long cumulativeDifficulty;
  
  private long difficultyBI;
  
  private long gasUsed;
  
  private String hash;
  
  private String parentHash;
  
  private long lastKnownBlockNumber;
  
  private long number;
  
  private long txSize;
  
  private long timestamp;
  
  private long peerCnt;
  
  public BlockInfo(Block block, long peerCnt, long lastKnownBlockNumber) {
    this.cumulativeDifficulty = block.getCumulativeDifficulty().longValue();
    this.difficultyBI = block.getDifficultyBI().longValue();
    this.gasUsed = block.getGasUsed();
    this.hash = Hex.toHexString(block.getHash());
    this.parentHash = Hex.toHexString(block.getParentHash());
    this.lastKnownBlockNumber = lastKnownBlockNumber;
    this.number = block.getNumber();
    this.txSize = block.getTransactionsList().size();
    this.timestamp = block.getTimestamp();
    this.peerCnt = peerCnt;
  }
  
}
