package at.metalab.sepa.validation.iban;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.validation.IMemberValidator;
import fr.marcwrobel.jbanking.iban.Iban;

public class JbankingIbanAdapterValidator implements IMemberValidator {

	public final static IMemberValidator INSTANCE = new JbankingIbanAdapterValidator();
	
	private JbankingIbanAdapterValidator() {
	}
	
	public void validate(Member member) throws SepaException {
		try {
			new Iban(member.getAccount().getIbanKonto().getIban());
		} catch (Exception exception) {
			throw new SepaException("Jbanking iban validation failed", exception);
		}
	}

}
