package danchain;

import java.util.Date;

public class Block {

	private String hash;
	private String previousHash;
	private String data;
	private long timeStamp;

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
		String calculatedHash = StringUtil.applySha256(previousHash + Long.toString(timeStamp) + data);
		return calculatedHash;
	}
}
