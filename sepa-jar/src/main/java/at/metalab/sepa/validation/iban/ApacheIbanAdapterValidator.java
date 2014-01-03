package at.metalab.sepa.validation.iban;

import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;

public class ApacheIbanAdapterValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new ApacheIbanAdapterValidator();
	
	private ApacheIbanAdapterValidator() {
	}
	
	public void validate(Member member) throws SepaException {
		if (!IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(member.getAccount()
				.getIbanKonto().getIban())) {
			throw new SepaException("apache iban validation failed");
		}
	}
}
