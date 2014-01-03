package at.metalab.iban.test;

import org.junit.Assert;
import org.junit.Test;

import at.metalab.sepa.Metalab;
import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;
import at.metalab.sepa.service.IKontoConverter;
import at.metalab.sepa.service.SparkasseKontoConverter;

public class TestSparkasseKontoConverter {

	private IKontoConverter sparkasse = new SparkasseKontoConverter();

	@Test
	public void testMetalab() {
		BlzKonto blzKonto = Metalab.INSTANCE.getBlzKonto();
		IbanKonto ibanKonto = Metalab.INSTANCE.getIbanKonto();
		check(sparkasse, blzKonto, ibanKonto);
	}

	@Test
	public void testBspSparkasse() {
		// Beispiel Sparkasse
		// https://www.s-bausparkasse.at/portal/?page=calc.ibanbic#
		BlzKonto blzKonto = new BlzKonto(24012, 333333333);
		IbanKonto ibanKonto = new IbanKonto("BAOSATWWXXX",
				"AT352401200333333333");
		check(sparkasse, blzKonto, ibanKonto);
	}

	@Test
	public void testBspRaiffeisen() {
		// Beispiel Raiffeisen
		BlzKonto blzKonto = new BlzKonto(34000, 62679);
		IbanKonto ibanKonto = new IbanKonto("RZOOAT2LXXX",
				"AT843400000000062679");
		check(sparkasse, blzKonto, ibanKonto);
	}

	private void check(IKontoConverter ibanConverter, BlzKonto blzKonto,
			IbanKonto ibanKonto) {
		try {
			IbanKonto ibanKontoResult = ibanConverter.convert(blzKonto);
			Assert.assertEquals(ibanKonto, ibanKontoResult);
		} catch (SepaException sepaException) {
			Assert.fail(sepaException.getMessage());
		}
	}
}
