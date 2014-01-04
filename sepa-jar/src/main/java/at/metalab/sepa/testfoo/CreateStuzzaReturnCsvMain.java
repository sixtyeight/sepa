package at.metalab.sepa.testfoo;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import at.metalab.sepa.Files;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.csv.MOS;
import at.metalab.sepa.csv.Stuzza;

public class CreateStuzzaReturnCsvMain {

	public static void main(String[] args) throws Exception {
		Files files = Files.METALAB_TESTDATA;

		List<Member> members = MOS.readSepa(files.getCollectionSepaCsv());
		Collections.sort(members, Member.BY_BLZ_KONTO);

		System.out.println();

		Stuzza.writeResponse(new PrintWriter(System.out), members);
	}

}
