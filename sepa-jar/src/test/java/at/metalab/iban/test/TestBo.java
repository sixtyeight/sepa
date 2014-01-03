package at.metalab.iban.test;

import org.junit.Assert;
import org.junit.Test;

import at.metalab.sepa.bo.IbanKonto;

public class TestBo {
	@Test
	public void testEquals() {
		IbanKonto ibanKonto1 = new IbanKonto("1234", "567");
		IbanKonto ibanKonto2 = new IbanKonto("1234", "567");

		Assert.assertEquals(ibanKonto1, ibanKonto2);
	}
	
	@Test
	public void testGetBic() {
		IbanKonto ibanKonto = new IbanKonto("1234", "567");
		Assert.assertEquals("1234", ibanKonto.getBic());
	}

	@Test
	public void testGetIban() {
		IbanKonto ibanKonto = new IbanKonto("1234", "567");
		Assert.assertEquals("567", ibanKonto.getIban());
	}
}
