package at.metalab.sepa.testfoo;

import java.util.Map;

import at.metalab.sepa.Files;
import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;
import at.metalab.sepa.csv.Stuzza;
import at.metalab.sepa.csv.Stuzza.StuzzaResponse;

public class ReadStuzzaReturnCsvMain {

	public static void main(String[] args) throws Exception {
		Files files = Files.METALAB_TESTDATA;

		StuzzaResponse stuzzaResponse = Stuzza.readResponse(files
				.getStuzzaReturnCsv());

		for (Map.Entry<BlzKonto, IbanKonto> entry : stuzzaResponse
				.getComplete().entrySet()) {
			BlzKonto blzKonto = entry.getKey();
			IbanKonto ibanKonto = entry.getValue();

			System.out.println(String.format("%d %11d -> %s %s",
					blzKonto.getBlz(), blzKonto.getKontonummer(),
					ibanKonto.getBic(), ibanKonto.getIban()));
		}

		System.out.println(String.format("found %d entries", stuzzaResponse
				.getComplete().size()));
	}

}
