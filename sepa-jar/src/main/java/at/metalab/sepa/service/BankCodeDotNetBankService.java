package at.metalab.sepa.service;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Bank;

public class BankCodeDotNetBankService implements IBankService {

	public Bank getBank(String bic) throws SepaException {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpUriRequest request = new HttpGet(String.format(
					"http://bank-code.net/swift-code/%s.html", bic));
			HttpResponse response = httpClient.execute(request);

			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), stringWriter);

			/*
			 * ... <div class="row-fluid row-fluid-text">
			 * <p><strong>RLNWATWWXXX</strong> Swift / BIC code for
			 * <strong>RAIFFEISENLANDESBANK NIEDEROESTERREICH WIEN AG</strong>
			 * bank located in <strong>VIENNA - AUSTRIA (AT)</strong></p> </div>
			 * ...
			 */

			String s = stringWriter.toString();

			String searchPattern = String
					.format("<p><strong>%s</strong> Swift / BIC code for <strong>",
							bic);

			if (s.indexOf(searchPattern) == -1) {
				throw new SepaException(String.format(
						"bank name not found for %s", bic));
			}

			s = s.substring(s.indexOf(searchPattern)); // cut everything before
														// the search pattern
			s = s.replace(searchPattern, ""); // replace the search pattern with
												// nothing

			s = s.substring(0, s.indexOf("<")); // keep everything before <

			Bank bank = new Bank();
			bank.setName(s);

			return bank;
		} catch (Exception exception) {
			throw new SepaException(String.format(
					"could not extract bank name for %s: %s", bic,
					exception.getMessage()));
		}
	}

}
