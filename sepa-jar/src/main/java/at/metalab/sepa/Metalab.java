package at.metalab.sepa;

import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;

public class Metalab {
	private Metalab() {
	}

	public final static Metalab INSTANCE = new Metalab();

	private final IbanKonto ibanKonto = new IbanKonto("RLNWATWWXXX",
			"AT653200000009564568");

	private final BlzKonto blzKonto = new BlzKonto(32000, 9564568);

	private volatile int mandatSequence = 0;

	public synchronized String createNewMandat() {
		mandatSequence++;
		return String.format("METALAB-MANDAT-ID-%05d", mandatSequence);
	}

	public String getCreditorId() {
		return "METALAB-CREDITOR-ID";
	}

	public BlzKonto getBlzKonto() {
		return blzKonto;
	}

	public IbanKonto getIbanKonto() {
		return ibanKonto;
	}

}
