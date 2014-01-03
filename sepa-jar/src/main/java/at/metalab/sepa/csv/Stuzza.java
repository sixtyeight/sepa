package at.metalab.sepa.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import at.metalab.sepa.Metalab;
import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;
import at.metalab.sepa.bo.Member;

/**
 * http://stuzza.at/11096_DE
 * 
 * @author m68k
 * 
 */
public class Stuzza {

	private final static String TYPE_REQUEST = "REQS";
	private final static String TYPE_RESPONSE = "RTRN";

	private static class ResultCode {
		// Stuzza definierte Codes die das Ergebnis pro Zeile (Konto) betreffen

		/*
		 * CMPT complete, also in Ordnung IBAN/BIC anbei AGNX agent not existing
		 * d.h. der IBANService hat die BLZ nicht registriert und weiß nicht,
		 * welche Bank für die Beantwortung zuständig wäre NDAV no data
		 * available d.h. der IBANService hat die BLZ zwar registriert, aber zu
		 * dieser BLZ können keine Informationen geliefert werden ACNX account
		 * not existing d.h. die Bank führt ein Konto mit dieser Nummer nicht
		 * ACCL account closed d.h. die Bank hat das Konto bereits geschlossen
		 * OERR other error d.h. bei der Ermittlung trat ein nicht anderweitig
		 * zuordenbaren Fehler auf
		 */

		private ResultCode() {
		}

		public final static String COMPLETE = "CMPT";
		public final static String AGENT_NOT_EXISTING = "AGNX";
		public final static String NO_DATA_AVAILABLE = "NDAV";
		public final static String ACCOUNT_NOT_EXISTING = "ACNX";
		public final static String ACCOUNT_CLOSED = "ACCL";
		public final static String OTHER_ERROR = "OERR";
	}

	public static class StuzzaResponse {
		private final Map<BlzKonto, IbanKonto> complete = new HashMap<BlzKonto, IbanKonto>();
		private final Set<BlzKonto> agentNotExisting = new HashSet<BlzKonto>();
		private final Set<BlzKonto> noDataAvailable = new HashSet<BlzKonto>();
		private final Set<BlzKonto> accountNotExisting = new HashSet<BlzKonto>();
		private final Set<BlzKonto> accountClosed = new HashSet<BlzKonto>();
		private final Set<BlzKonto> otherError = new HashSet<BlzKonto>();

		public Map<BlzKonto, IbanKonto> getComplete() {
			return complete;
		}

		public Set<BlzKonto> getAgentNotExisting() {
			return agentNotExisting;
		}

		public Set<BlzKonto> getNoDataAvailable() {
			return noDataAvailable;
		}

		public Set<BlzKonto> getAccountClosed() {
			return accountClosed;
		}

		public Set<BlzKonto> getAccountNotExisting() {
			return accountNotExisting;
		}

		public Set<BlzKonto> getOtherError() {
			return otherError;
		}

		public boolean isKnown(BlzKonto blzKonto) {
			return getComplete().containsKey(blzKonto)
					|| getAccountNotExisting().contains(blzKonto)
					|| getNoDataAvailable().contains(blzKonto)
					|| getAgentNotExisting().contains(blzKonto)
					|| getAccountClosed().contains(blzKonto)
					|| getOtherError().contains(blzKonto);
		}
	}

	public static void writeRequest(Writer out, Collection<Member> members)
			throws IOException {
		writeHeader(TYPE_REQUEST, out, Metalab.INSTANCE.getBlzKonto());

		for (Member member : members) {
			writeMember(out, member);
		}

		out.flush();
	}

	public static void writeResponse(Writer out, Collection<Member> members)
			throws IOException {
		writeHeader(TYPE_RESPONSE, out, Metalab.INSTANCE.getBlzKonto());

		for (Member member : members) {
			writeMapping(out, member);
		}

		out.flush();
	}

	public static StuzzaResponse readResponse(Reader in) throws IOException,
			SepaException {
		StuzzaResponse stuzzaResponse = new StuzzaResponse();

		BufferedReader reader = new BufferedReader(in);

		String line = null;
		boolean first = true;

		while ((line = reader.readLine()) != null) {
			Scanner scanner = new Scanner(line);
			scanner.useDelimiter(";");

			if (first) { // header row
				first = false;
				String type = scanner.next();
				if (!TYPE_RESPONSE.equals(type)) {
					throw new SepaException(
							"response file not well formed. wrong type.");
				}
			} else { // data row
				long blz = scanner.nextLong(); // mandatory
				long kontonummer = scanner.nextLong(); // mandatory

				String bic = scanner.next(); // optional
				String iban = scanner.next(); // optional
				scanner.next(); // skip institut, optional
				String resultCode = scanner.next();// mandatory

				BlzKonto blzKonto = new BlzKonto(blz, kontonummer);

				if (ResultCode.COMPLETE.endsWith(resultCode)) {
					IbanKonto ibanKonto = new IbanKonto(bic, iban);
					stuzzaResponse.getComplete().put(blzKonto, ibanKonto);
				} else if (ResultCode.ACCOUNT_CLOSED.equals(resultCode)) {
					stuzzaResponse.getAccountClosed().add(blzKonto);
				} else if (ResultCode.ACCOUNT_NOT_EXISTING.equals(resultCode)) {
					stuzzaResponse.getAccountNotExisting().add(blzKonto);
				} else if (ResultCode.AGENT_NOT_EXISTING.equals(resultCode)) {
					stuzzaResponse.getAgentNotExisting().add(blzKonto);
				} else if (ResultCode.NO_DATA_AVAILABLE.equals(resultCode)) {
					stuzzaResponse.getNoDataAvailable().add(blzKonto);
				} else if (ResultCode.OTHER_ERROR.equals(resultCode)) {
					stuzzaResponse.getOtherError().add(blzKonto);
				} else {
					throw new SepaException(
							"response file not well formed. unknown result code");
				}
			}
		}

		return stuzzaResponse;
	}

	private static void writeMember(Writer out, Member member)
			throws IOException {
		out.write(String.format("%d;%d;;;;\n", member.getAccount()
				.getBlzKonto().getBlz(), member.getAccount().getBlzKonto()
				.getKontonummer()));
	}

	private static void writeMapping(Writer out, Member member)
			throws IOException {
		out.write(String.format("%d;%d;%s;%s;;%s\n", member.getAccount()
				.getBlzKonto().getBlz(), member.getAccount().getBlzKonto()
				.getKontonummer(), member.getAccount().getIbanKonto().getBic(),
				member.getAccount().getIbanKonto().getIban(),
				ResultCode.COMPLETE));
	}

	private static void writeHeader(String type, Writer out, BlzKonto blzKonto)
			throws IOException {
		out.write(String.format("%s;%05d%011d;USRT;2014-01-01;0:00:00;1-1\n",
				type, blzKonto.getBlz(), blzKonto.getKontonummer()));
	}
}
