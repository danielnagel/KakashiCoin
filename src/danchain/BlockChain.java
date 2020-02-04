package danchain;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class BlockChain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 5;

	public static void main(String[] args) {

		blockchain.add(new Block("Hi i'm the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		blockchain.get(blockchain.size() - 1).mineBlock(difficulty);
		
		blockchain.add(new Block("Hi i'm the second block", blockchain.get(blockchain.size() - 1).getHash()));
		System.out.println("Trying to Mine block 2... ");
		blockchain.get(blockchain.size() - 1).mineBlock(difficulty);
		
		blockchain.add(new Block("Hey i'm the third block", blockchain.get(blockchain.size() - 1).getHash()));
		System.out.println("Trying to Mine block 3... ");
		blockchain.get(blockchain.size() - 1).mineBlock(difficulty);
		
		System.out.println("\nBlockchain is Valid: " + isChainValid());

		String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJSON);
	}
	
	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;

		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			
			// compare registered hash and calculated hash
			if(!currentBlock.getHash().equals(currentBlock.calculateHash())) {
				System.out.println("Current Hashes not equal.");
				return false;
			}
			
			// compare previous hash and registered previous hash
			if(!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
				System.out.println("Previous Hashes not equal.");
				return false;
			}
		}
		return true;
	}
}
