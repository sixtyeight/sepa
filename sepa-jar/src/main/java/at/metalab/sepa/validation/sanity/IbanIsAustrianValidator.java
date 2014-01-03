package at.metalab.sepa.validation.sanity;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;

public class IbanIsAustrianValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new IbanIsAustrianValidator();

	private IbanIsAustrianValidator() {
	}

	public void validate(Member member) throws SepaException {
		if(member.getAccount().getIbanKonto() != null) {
			if (!member.getAccount().getIbanKonto().getIban().startsWith("AT")) {
				throw new SepaException("not an austrian iban");
			}

			if (member.getAccount().getIbanKonto().getIban().length() != 20) {
				throw new SepaException("invalid length for an austrian iban");
			}
		}
	}

}
