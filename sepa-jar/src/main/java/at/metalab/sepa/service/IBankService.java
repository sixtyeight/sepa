package at.metalab.sepa.service;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Bank;

public interface IBankService {
	Bank getBank(String bic) throws SepaException;
}
