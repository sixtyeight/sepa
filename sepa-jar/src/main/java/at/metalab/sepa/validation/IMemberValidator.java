package at.metalab.sepa.validation;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;

public interface IMemberValidator {
	void validate(Member member) throws SepaException;
}
