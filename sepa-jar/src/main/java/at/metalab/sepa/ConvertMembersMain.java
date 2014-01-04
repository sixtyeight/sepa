package at.metalab.sepa;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import at.metalab.sepa.MemberConverter.ConversionResult;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.csv.MOS;
import at.metalab.sepa.csv.NotReallyLikeMOS;
import at.metalab.sepa.service.BankCodeDotNetBankService;
import at.metalab.sepa.service.IBankService;
import at.metalab.sepa.service.IKontoConverter;
import at.metalab.sepa.service.SparkasseKontoConverter;

public class ConvertMembersMain {

	public static void main(String[] args) throws Exception {
		Files files = Files.METALAB_PRODUCTION;

		// read the member data from the MOS collection csv flatfile
		System.out.println("loading member data ...");
		List<Member> members = // MOS.readLegacy(files.getCollectionCsv());
		NotReallyLikeMOS.read(files.getNotReallyLikeMosCsv());
		System.out.println("done\n");

		// read the stuzza provided mappings for bic / iban lookup
		IKontoConverter kontoConverter = null;

		// kontoConverter = new StuzzaKontoConverter(Stuzza.readResponse(files
		// .getStuzzaReturnCsv()));

		kontoConverter = new SparkasseKontoConverter();

		// use the BankCode.net homepage to screen scrape the name of the banks
		IBankService bankService = new BankCodeDotNetBankService();

		// setup the converter accordingly
		MemberConverter memberConverter = new MemberConverter(kontoConverter,
				bankService);

		// convert the read members
		System.out.println("converting the members ...");
		ConversionResult conversionResult = memberConverter.convert(members);
		System.out.println("done\n");

		// display the result
		System.out.println("showing the result ...");
		conversionResult.printSummary(new PrintWriter(System.out));
		System.out.println("done\n");

		// print the sepa mos file
		List<Member> convertedMembers = new LinkedList<Member>();
		convertedMembers.addAll(conversionResult.selectOkWithWarnings());
		convertedMembers.addAll(conversionResult.selectOkWithoutWarnings());

		MOS.writeSepa(new PrintWriter(System.out), convertedMembers,
				"Mitgliedsbeitrag 2013/12", Metalab.INSTANCE.getCreditorId());

		System.out.println();

		MOSSql.writeUpdate(convertedMembers);
	}

}
