package kakashicoin;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BlockChain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

	public static int difficulty = 4;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());

		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinbase = new Wallet();

		genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		genesisTransaction.generateSignature(coinbase.getPrivateKey());
		genesisTransaction.setTransactionId("0");
		genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getRecipient(),
				genesisTransaction.getValue(), genesisTransaction.getTransactionId()));
		// its important to store out first transaction in the UTXOs list.
		UTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		// testing
		Block block1 = new Block(genesis.getHash());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), 20f));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		isChainValid();
	}

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = StringUtil.getDifficultyString(difficulty);
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
		tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);

			// compare registered hash and calculated hash
			if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
				System.out.println("#Current Hashes not equal.");
				return false;
			}

			// compare previous hash and registered previous hash
			if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
				System.out.println("#Previous Hashes not equal.");
				return false;
			}

			// check if hash is solved
			if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
				System.out.println("#This blck hasn't been mined");
				return false;
			}

			// loop through blockchains transactions
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);

				if (!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are not equal to oututs on Transaction(" + t + ")");
					return false;
				}

				for (TransactionInput input : currentTransaction.getInputs()) {
					tempOutput = tempUTXOs.get(input.getTransactionOutputId());

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}

					if (input.getUTXO().getValue() != tempOutput.getValue()) {
						System.out.println("#Referenced input on Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.getTransactionOutputId());
				}

				for (TransactionOutput output : currentTransaction.getOutputs()) {
					tempUTXOs.put(output.getId(), output);
				}

				if (currentTransaction.getOutputs().get(0).getRecipient() != currentTransaction.getRecipient()) {
					System.out.println("#Transaction(" + t + ") output recipient is not who ti should be");
					return false;
				}

				if (currentTransaction.getOutputs().get(1).getRecipient() != currentTransaction.getSender()) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
			}
		}
		System.out.println("Blockchain is valid");
		return true;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}
