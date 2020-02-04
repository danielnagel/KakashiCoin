package danchain;

import java.util.Date;

public class Block {

	private String hash;
	private String previousHash;
	private String data;
	private long timeStamp;
	private int nonce;

	public Block(String data, String previousHash) {
		this.data = data;
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
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
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
		// Create a string with difficulty * "0"
		String target = new String(new char[difficulty]).replace('\0', '0');
		while(!hash.substring(0, difficulty).contentEquals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}
}
