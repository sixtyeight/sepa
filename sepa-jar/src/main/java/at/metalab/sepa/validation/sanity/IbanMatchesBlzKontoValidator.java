package at.metalab.sepa.validation.sanity;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;

public class IbanMatchesBlzKontoValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new IbanMatchesBlzKontoValidator();

	private IbanMatchesBlzKontoValidator() {
	}

	public void validate(Member member) throws SepaException {
		BlzKonto blzKonto = member.getAccount().getBlzKonto();
		IbanKonto ibanKonto = member.getAccount().getIbanKonto();

		String expectedIban = String.format("AT??%05d%011d", blzKonto.getBlz(),
				blzKonto.getKontonummer());

		StringBuilder actualIban = new StringBuilder(ibanKonto.getIban());
		actualIban.setCharAt(2, '?');
		actualIban.setCharAt(3, '?');

		if (!expectedIban.equals(actualIban.toString())) {
			throw new SepaException(String.format(
					"iban does not correlate with blz/kontonummer: %s != %s",
					actualIban, expectedIban));
		}
	}
}
