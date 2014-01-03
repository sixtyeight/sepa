package at.metalab.sepa.validation.sanity;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;

public class BankFoundValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new BankFoundValidator();

	private BankFoundValidator() {
	}

	public void validate(Member member) throws SepaException {
		if (member.getAccount().getBank() != null) {
			String name = member.getAccount().getBank().getName();

			if (name == null || name.trim().isEmpty()) {
				throw new SepaException("bank name not found");
			}
		} else {
			throw new SepaException("bank data missing");
		}
	}

}
