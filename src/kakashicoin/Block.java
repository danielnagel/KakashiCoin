package kakashicoin;

import java.util.ArrayList;
import java.util.Date;

public class Block {

	private String hash;
	private String previousHash;
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private long timeStamp;
	private int nonce;

	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}

	public String getHash() {
		return this.hash;
	}

	public String getPreviousHash() {
		return this.previousHash;
	}

	public String calculateHash() {
		String calculatedHash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedHash;
	}
	
	/**
	 * In reality each miner will start iterating from a random point.
	 * Some miners may even try random numbers for nonce.
	 * Also it’s worth noting that at the harder difficulties solutions may require more than integer.MAX_VALUE,
	 * miners can then try changing the timestamp.
	 * 
	 * @param difficulty
	 */
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		// Create a string with difficulty * "0"
		String target = StringUtil.getDifficultyString(difficulty);
		while(!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}
	
	// Add transactions to this block
	public boolean addTransaction(Transaction transaction) {
		// process transaction and check if valid, unless block is genesis block
		if(transaction == null) return false;
		if(previousHash != "0") {
			if(!transaction.processTransaction()) {
				System.out.println("Transaction failed to proces. Discarded.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
}
