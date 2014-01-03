package at.metalab.sepa.testfoo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import at.metalab.sepa.Files;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.csv.MOS;

public class CreateMOSLegacyCsvMain {

	public static void main(String[] args) throws IOException {
		Files files = Files.METALAB_TESTDATA;

		List<Member> members = MOS.readLegacy(files.getCollectionCsv());

		MOS.writeLegacy(new PrintWriter(System.out), members,
				"Mitgliedsbeitrag 2013/12");
	}

}
