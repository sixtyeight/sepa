package at.metalab.sepa.validation.sanity;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;

public class OwnerContainsNameValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new OwnerContainsNameValidator();

	private OwnerContainsNameValidator() {
	}

	public void validate(Member member) throws SepaException {
		String owner = member.getAccount().getOwner().toLowerCase();

		String fname = member.getFirstname().toLowerCase();
		String lname = member.getLastname().toLowerCase();

		boolean failed = false;
		StringBuilder warnings = new StringBuilder();

		if (!owner.contains(fname)) {
			failed = true;
			missing(warnings, "firstname", fname, owner);
		}

		if (!owner.contains(lname)) {
			failed = true;
			missing(warnings, "lastname", lname, owner);
		}

		if (failed) {
			throw new SepaException(warnings.toString());
		}
	}

	private void missing(StringBuilder sb, String part, String contains,
			String string) {
		if (sb.length() != 0) {
			sb.append(", ");
		}
		sb.append(String.format("%s '%s' not found in bank account owner '%s'",
				part, contains, string));
	}

}
