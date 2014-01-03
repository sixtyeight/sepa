package at.metalab.iban.test;

import org.junit.Test;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Bank;
import at.metalab.sepa.service.BankCodeDotNetBankService;
import at.metalab.sepa.service.IBankService;

public class TestBankService {

	private IBankService bankService = new BankCodeDotNetBankService();
	
	@Test
	public void testMetalab() throws SepaException {
		Bank bank = bankService.getBank("RLNWATWWXXX");
		System.out.println(bank.getName());
	}
}
