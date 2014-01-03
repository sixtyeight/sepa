package at.metalab.sepa.validation.iban;

import nl.garvelink.iban.IBAN;
import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;

public class GarvelinkIbanAdapterValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new GarvelinkIbanAdapterValidator();
	
	private GarvelinkIbanAdapterValidator() {
	}
	
	public void validate(Member member) throws SepaException {
		try {
			IBAN.parse(member.getAccount().getIbanKonto().getIban());
		} catch (Exception exception) {
			throw new SepaException("Garvelink iban validation failed",
					exception);
		}
	}

}
