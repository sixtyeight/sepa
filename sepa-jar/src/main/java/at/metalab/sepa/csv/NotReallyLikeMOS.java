package at.metalab.sepa.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.Member;

public class NotReallyLikeMOS {

	public static List<Member> read(Reader in) throws IOException {
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
				members.add(buildMember(csvRow));
			} catch (Exception exception) {
				System.out.println(String.format(
						"could not parse: \"%s\" (%s) ", csvRow,
						exception.getMessage()));
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

	private static String kacke(Long id) {
		return String.format("kacke-%d", id);
	}

	private static Member buildMember(String csvRow) {
		Member member = new Member();

		StringTokenizer stringTokenizer = new StringTokenizer(csvRow, ";");

		Long id = Long.valueOf(stringTokenizer.nextToken());
		String kontonummer = stringTokenizer.nextToken().replace(" ", "");
		String blz = stringTokenizer.nextToken().replace(" ", "");

		member.setFirstname(kacke(id));
		member.setLastname(kacke(id));

		member.getAccount().setOwner(kacke(id));

		member.setFee(BigDecimal.ZERO.setScale(0));

		member.getAccount().setBlzKonto(
				new BlzKonto(Long.valueOf(blz), Long.valueOf(kontonummer)));

		return member;
	}

}
