package danchain;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class BlockChain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();

	public static void main(String[] args) {

		blockchain.add(new Block("Hi i'm the first block", "0"));
		blockchain.add(new Block("Hi i'm the second block", blockchain.get(blockchain.size() - 1).getHash()));
		blockchain.add(new Block("Hey i'm the third block", blockchain.get(blockchain.size() - 1).getHash()));

		String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJSON);
	}
}
