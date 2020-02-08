package kakashicoin;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

	private String transactionId;
	private PublicKey sender;
	private PublicKey recipient;
	private float value;
	private byte[] signature;

	private ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	private ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0;

	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}

	private String calculateHash() {
		sequence++;
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value) + sequence);
	}
	
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}
	
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
		
	}
	
	public boolean processTransaction() {
		if(verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
		
		// gather transaction inputs (Make sure the are unspent):
		for(TransactionInput i : inputs) {
			i.setUTXO(BlockChain.UTXOs.get(i.getTransactionOutputId()));
		}
		
		// check if transaction is valid:
		if(getInputsValue() < BlockChain.minimumTransaction) {
			System.out.println("#Transaction Inputs to small: " + getInputsValue());
			return false;
		}
		
		// generate transaction outputs:
		// get value of inputs then left over change:
		float leftOver = getInputsValue() - value;
		transactionId = calculateHash();
		outputs.add(new TransactionOutput(recipient, value, transactionId));
		outputs.add(new TransactionOutput(sender, leftOver, transactionId));
		
		// add outputs to unspent list
		for(TransactionOutput o : outputs) {
			BlockChain.UTXOs.put(o.getId(), o);
		}
		
		// remove transaction inputs from UTXO lists as spent:
		for(TransactionInput i : inputs) {
			if(i.getUTXO() == null) continue;
			BlockChain.UTXOs.remove(i.getUTXO().getId());
		}
		
		return true;
	}
	
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.getUTXO() == null) continue;
			total += i.getUTXO().getValue();
		}
		return total;
	}
	
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.getValue();
		}
		return total;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public PublicKey getSender() {
		return sender;
	}

	public PublicKey getRecipient() {
		return recipient;
	}

	public float getValue() {
		return value;
	}

	public byte[] getSignature() {
		return signature;
	}

	public ArrayList<TransactionInput> getInputs() {
		return inputs;
	}

	public ArrayList<TransactionOutput> getOutputs() {
		return outputs;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
