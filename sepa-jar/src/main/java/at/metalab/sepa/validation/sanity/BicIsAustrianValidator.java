package at.metalab.sepa.validation.sanity;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;

public class BicIsAustrianValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new BicIsAustrianValidator();

	private BicIsAustrianValidator() {
	}

	public void validate(Member member) throws SepaException {
		if (member.getAccount().getIbanKonto() != null) {
			if (!member.getAccount().getIbanKonto().getBic()
					.regionMatches(4, "AT", 0, 2)) {
				throw new SepaException("not an austrian bic");
			}
		}
	}

}
