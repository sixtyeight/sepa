package at.metalab.sepa.testfoo;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import at.metalab.sepa.Metalab;
import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.Bank;
import at.metalab.sepa.bo.IbanKonto;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.csv.MOS;
import at.metalab.sepa.csv.Stuzza;
import at.metalab.sepa.service.BankCodeDotNetBankService;
import at.metalab.sepa.service.IBankService;
import at.metalab.sepa.service.IKontoConverter;
import at.metalab.sepa.service.StuzzaKontoConverter;
import at.metalab.sepa.validation.IMemberValidator;
import at.metalab.sepa.validation.iban.ApacheIbanAdapterValidator;
import at.metalab.sepa.validation.iban.GarvelinkIbanAdapterValidator;
import at.metalab.sepa.validation.iban.JbankingIbanAdapterValidator;
import at.metalab.sepa.validation.iban.JdtausIbanAdapterValidator;
import at.metalab.sepa.validation.sanity.BankFoundValidator;
import at.metalab.sepa.validation.sanity.BicIsAustrianValidator;
import at.metalab.sepa.validation.sanity.IbanIsAustrianValidator;
import at.metalab.sepa.validation.sanity.IbanMatchesBlzKontoValidator;
import at.metalab.sepa.validation.sanity.OwnerContainsNameValidator;

public class ConvertAccountMain {

	public static void main(String[] args) throws Exception {
		IBankService bankService = new BankCodeDotNetBankService();

		IKontoConverter kontoConverter = null;
		// kontoConverter = new SparkasseKontoConverter();
		kontoConverter = new StuzzaKontoConverter(Stuzza.readResponse(Metalab
				.getStuzzaReturnCsv()));

		List<Member> members = MOS.readLegacy(Metalab.getCollectionCsv());

		System.out.println();

		List<Member> convertedMembers = new LinkedList<Member>();

		for (Member member : members) {
			try {
				IbanKonto ibanKonto = kontoConverter.convert(member
						.getAccount().getBlzKonto());
				member.getAccount().setIbanKonto(ibanKonto);

				Bank bank = bankService.getBank(ibanKonto.getBic());
				member.getAccount().setBank(bank);

				member.setMandatsReferenz(Metalab.INSTANCE.createNewMandat());

				System.out.println(String.format("ok: %s", member.getName()));

				convertedMembers.add(member);
			} catch (SepaException sepaException) {
				System.out.println(String.format("failed: %s (%s)",
						member.getName(), sepaException.getMessage()));
			}
		}

		System.out.println(String.format("%d members successfully converted",
				convertedMembers.size()));
		System.out.println(String.format("%s members failed to convert",
				members.size() - convertedMembers.size()));
		System.out.println();

		for (Member member : convertedMembers) {
			validate(member);
		}

		MOS.writeSepa(new PrintWriter(System.out), convertedMembers,
				"Mitgliedsbeitrag 2013/12", Metalab.INSTANCE.getCreditorId());
	}

	private static List<IMemberValidator> validators = new ArrayList<IMemberValidator>();

	static {
		validators.add(IbanIsAustrianValidator.INSTANCE);
		validators.add(BicIsAustrianValidator.INSTANCE);
		validators.add(OwnerContainsNameValidator.INSTANCE);
		validators.add(IbanMatchesBlzKontoValidator.INSTANCE);
		validators.add(BankFoundValidator.INSTANCE);

		validators.add(ApacheIbanAdapterValidator.INSTANCE);
		validators.add(GarvelinkIbanAdapterValidator.INSTANCE);
		validators.add(JbankingIbanAdapterValidator.INSTANCE);
		validators.add(JdtausIbanAdapterValidator.INSTANCE);
	}

	private static void validate(Member member) {
		for (IMemberValidator memberValidator : validators) {
			try {
				memberValidator.validate(member);
			} catch (SepaException sepaException) {
				System.err.println(String.format("validation failed: %s (%s)",
						sepaException.getMessage(), memberValidator.getClass()
								.getCanonicalName()));
			}
		}
	}
}
