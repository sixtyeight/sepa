package at.metalab.sepa.service;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;

public class SparkasseKontoConverter extends AbstractKontoConverter {

	public IbanKonto convert(BlzKonto blzKonto) throws SepaException {
		String iban = calculateIban(blzKonto);
		String bic = getBic(blzKonto);

		return new IbanKonto(bic, iban);
	}

	private synchronized String getBic(BlzKonto blzKonto) throws SepaException {
		try {
			String uri = createRequest(blzKonto);

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpUriRequest request = new HttpPost(uri);
			HttpResponse response = httpClient.execute(request);

			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), stringWriter);

			/*
			 * listofbic==<span class='amount strong'>BAOSATWWXXX</span><br
			 * />&&&iban==
			 */

			String s = stringWriter.toString().replace(
					"listofbic==<span class='amount strong'>", "");
			s = s.substring(0, s.indexOf("<"));
			
			return s;
		} catch (Exception exception) {
			throw new SepaException(exception);
		}
	}

	private String createRequest(BlzKonto blzKonto) {
		StringBuilder uri = new StringBuilder();

		uri.append("https://www.s-bausparkasse.at/portal/if_ajax.asp?");
		uri.append("&account="); // not needed
		uri.append("&bank=").append(blzKonto.getBlz());
		uri.append("&alt_iban=");
		uri.append("&country=AT");
		uri.append("&berechnungsdaten=");
		uri.append("&autocal=");
		uri.append("&mode=calc.ibanbic.listofbic");
		uri.append("&cuid=");
		uri.append("&currentpageid=87");
		uri.append("&getresult=");
		uri.append("&iban=");
		uri.append("&rechnername=IBAN%2FBIC-Rechner");

		return uri.toString();
	}
}
