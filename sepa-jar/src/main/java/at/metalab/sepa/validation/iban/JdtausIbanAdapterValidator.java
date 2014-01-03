package at.metalab.sepa.validation.iban;

import org.jdtaus.iso13616.IBAN;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;

public class JdtausIbanAdapterValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new JdtausIbanAdapterValidator();
	
	private JdtausIbanAdapterValidator() {
	}
	
	public void validate(Member member) throws SepaException {
		try {
			IBAN.parse(member.getAccount().getIbanKonto().getIban());
		} catch (Exception exception) {
			throw new SepaException("Jdtaus iban validation failed", exception);
		}
	}

}
