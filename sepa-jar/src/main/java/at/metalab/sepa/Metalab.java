package at.metalab.sepa;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

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

	public static Reader getCollectionCsv() throws UnsupportedEncodingException {
		return getUTF8Reader("data/collection.txt");
	}

	public static Reader getCollectionSepaCsv()
			throws UnsupportedEncodingException {
		return getUTF8Reader("data/collection_sepa.txt");
	}

	public static Reader getStuzzaReturnCsv()
			throws UnsupportedEncodingException {
		return getUTF8Reader("data/stuzza_return.txt");
	}

	private static Reader getUTF8Reader(String resourceName)
			throws UnsupportedEncodingException {
		Reader reader = new InputStreamReader(Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(resourceName),
				"UTF-8");

		return reader;
	}

}
