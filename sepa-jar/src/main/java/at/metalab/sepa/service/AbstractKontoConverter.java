package at.metalab.sepa.service;

import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;

public abstract class AbstractKontoConverter implements IKontoConverter {

	public abstract IbanKonto convert(BlzKonto blzKonto) throws SepaException;

	protected String calculateIban(BlzKonto blzKonto) throws SepaException {
		String input = String.format("AT00%05d%011d", blzKonto.getBlz(),
				blzKonto.getKontonummer());

		try {
			String checkdigit = IBANCheckDigit.IBAN_CHECK_DIGIT.calculate(input);
			String iban = String.format("AT%s%s", checkdigit,
					input.substring(4));

			if (iban.length() != 20) {
				throw new SepaException(
						"Austrian ibans have a mandatory length of 20 characters");
			}

			return iban;
		} catch (CheckDigitException checkDigitException) {
			throw new SepaException(checkDigitException);
		}
	}

}
