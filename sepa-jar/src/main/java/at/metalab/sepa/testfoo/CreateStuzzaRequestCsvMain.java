package at.metalab.sepa.testfoo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import at.metalab.sepa.Metalab;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.csv.MOS;
import at.metalab.sepa.csv.Stuzza;

public class CreateStuzzaRequestCsvMain {

	public static void main(String[] args) throws IOException {
		List<Member> members = MOS.readLegacy(Metalab.getCollectionCsv());
		Collections.sort(members, Member.BY_BLZ_KONTO);

		System.out.println();

		Stuzza.writeRequest(new PrintWriter(System.out), members);
	}

}
