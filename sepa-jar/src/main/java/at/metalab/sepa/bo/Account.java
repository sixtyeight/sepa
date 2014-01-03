package at.metalab.sepa.bo;

public class Account {
	private Bank bank;
	private String owner;
	private IbanKonto ibanKonto;
	private BlzKonto blzKonto;

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public IbanKonto getIbanKonto() {
		return ibanKonto;
	}

	public void setIbanKonto(IbanKonto ibanKonto) {
		this.ibanKonto = ibanKonto;
	}

	public BlzKonto getBlzKonto() {
		return blzKonto;
	}

	public void setBlzKonto(BlzKonto blzKonto) {
		this.blzKonto = blzKonto;
	}

}
