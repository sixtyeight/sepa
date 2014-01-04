package at.metalab.sepa.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;
import at.metalab.sepa.bo.Member;

public class MOS {

	public static void writeLegacy(Writer out, Collection<Member> members,
			String verwendungszweck) throws IOException {
		for (Member member : members) {
			writeMember(out, member, verwendungszweck);
			out.write("\n");
		}

		out.flush();
	}

	public static void writeSepa(Writer out, Collection<Member> members,
			String verwendungszweck, String creditorId) throws IOException {
		for (Member member : members) {
			writeMember(out, member, verwendungszweck);
			writeSepa(out, member, creditorId);
			out.write("\n");
		}

		out.flush();
	}

	private static void writeMember(Writer out, Member member,
			String verwendungszweck) throws IOException {
		// Django;Müller;32000;9564568;Django Müller;20;Mitgliedsbeitrag
		// 2014/01;
		out.write(String.format("%s;%s;%d;%d;%s;%s;%s;", member.getFirstname(),
				member.getLastname(), member.getAccount().getBlzKonto()
						.getKontonummer(), member.getAccount().getBlzKonto()
						.getBlz(), member.getAccount().getOwner(), member
						.getFee().toPlainString(), verwendungszweck));
	}

	private static void writeSepa(Writer out, Member member, String creditorId)
			throws IOException {
		out.write(String.format("%s;%s;%s", member.getAccount().getIbanKonto()
				.getIban(), member.getAccount().getIbanKonto().getBic(),
				member.getMandatsReferenz()));
	}

	public static List<Member> readLegacy(Reader in) throws IOException {
		return read(false, in);
	}

	public static List<Member> readSepa(Reader in) throws IOException {
		return read(true, in);
	}

	private static List<Member> read(boolean sepa, Reader in)
			throws IOException {
		int countFailed = 0;
		int countSkipped = 0;
		String csvRow = null;

		BufferedReader reader = new BufferedReader(in);
		List<Member> members = new LinkedList<Member>();

		while ((csvRow = reader.readLine()) != null) {
			try {
				if (csvRow.startsWith("#")) {
					countSkipped++;
					continue;
				}
				members.add(buildMember(sepa, csvRow));
			} catch (Exception exception) {
				System.out
						.println(String.format("could not parse: %s", csvRow));

				exception.printStackTrace();
				countFailed++;
			}
		}

		System.out.println(String.format("%d lines successfully parsed",
				members.size()));
		System.out.println(String.format("%d lines failed to parse",
				countFailed));
		System.out.println(String.format("%d lines have been skipped",
				countSkipped));

		return members;
	}

	private static Member buildMember(boolean sepa, String csvRow) {
		Member member = new Member();

		StringTokenizer stringTokenizer = new StringTokenizer(csvRow, ";");
		member.setFirstname(stringTokenizer.nextToken().trim());
		member.setLastname(stringTokenizer.nextToken().trim());

		String kontonummer = stringTokenizer.nextToken().replace(" ", "");
		String blz = stringTokenizer.nextToken().trim();

		member.getAccount().setOwner(stringTokenizer.nextToken().trim());

		member.setFee(new BigDecimal(stringTokenizer.nextToken().trim())
				.setScale(0));

		member.getAccount().setBlzKonto(
				new BlzKonto(Long.valueOf(blz), Long.valueOf(kontonummer)));

		stringTokenizer.nextToken(); // skip verwendungszweck column

		if (sepa) {
			String iban = stringTokenizer.nextToken();
			String bic = stringTokenizer.nextToken();
			member.setMandatsReferenz(stringTokenizer.nextToken());

			IbanKonto ibanKonto = new IbanKonto(bic, iban);
			member.getAccount().setIbanKonto(ibanKonto);
		}

		return member;
	}
}
