package kakashicoin;

import java.security.Security;
import java.util.ArrayList;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.gson.GsonBuilder;

public class BlockChain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 5;
	public static Wallet walletA;
	public static Wallet walletB;

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		
		walletA = new Wallet();
		walletB = new Wallet();
		
		System.out.println("Private and public keys:");
		System.out.println(StringUtil.getStringFromKey(walletA.getPrivateKey()));
		System.out.println(StringUtil.getStringFromKey(walletA.getPublicKey()));
		
		Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
		transaction.generateSignature(walletA.getPrivateKey());

		System.out.println("Is signature verified");
		System.out.println(transaction.verifySignature());
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
