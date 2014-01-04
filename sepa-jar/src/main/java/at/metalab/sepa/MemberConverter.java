package at.metalab.sepa;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.metalab.sepa.bo.Bank;
import at.metalab.sepa.bo.IbanKonto;
import at.metalab.sepa.bo.Member;
import at.metalab.sepa.service.IBankService;
import at.metalab.sepa.service.IKontoConverter;
import at.metalab.sepa.validation.IMemberValidator;
import at.metalab.sepa.validation.iban.ApacheIbanAdapterValidator;
import at.metalab.sepa.validation.iban.GarvelinkIbanAdapterValidator;
import at.metalab.sepa.validation.iban.JbankingIbanAdapterValidator;
import at.metalab.sepa.validation.iban.JdtausIbanAdapterValidator;
import at.metalab.sepa.validation.sanity.BicIsAustrianValidator;
import at.metalab.sepa.validation.sanity.IbanIsAustrianValidator;
import at.metalab.sepa.validation.sanity.IbanMatchesBlzKontoValidator;
import at.metalab.sepa.validation.sanity.OwnerContainsNameValidator;

public class MemberConverter {

	private IKontoConverter kontoConverter;
	private IBankService bankService;

	private final List<IMemberValidator> validators = new ArrayList<IMemberValidator>();

	public MemberConverter(IKontoConverter kontoConverter,
			IBankService bankService) {
		this.kontoConverter = kontoConverter;
		this.bankService = bankService;

		validators.add(IbanIsAustrianValidator.INSTANCE);
		validators.add(BicIsAustrianValidator.INSTANCE);
		validators.add(OwnerContainsNameValidator.INSTANCE);
		validators.add(IbanMatchesBlzKontoValidator.INSTANCE);

		validators.add(ApacheIbanAdapterValidator.INSTANCE);
		validators.add(GarvelinkIbanAdapterValidator.INSTANCE);
		validators.add(JbankingIbanAdapterValidator.INSTANCE);
		validators.add(JdtausIbanAdapterValidator.INSTANCE);
	}

	public static class ConversionResult {

		private Map<Member, MemberResult> result = new HashMap<Member, MemberConverter.MemberResult>();

		public Map<Member, MemberResult> getResults() {
			return result;
		}

		public List<Member> selectOkWithoutWarnings() {
			List<Member> selection = new LinkedList<Member>();

			for (Map.Entry<Member, MemberResult> entry : result.entrySet()) {
				if (!entry.getValue().hasFailed()
						&& !entry.getValue().hasWarnings()) {
					selection.add(entry.getKey());
				}
			}

			return selection;
		}

		public List<Member> selectOkWithWarnings() {
			List<Member> selection = new LinkedList<Member>();

			for (Map.Entry<Member, MemberResult> entry : result.entrySet()) {
				if (!entry.getValue().hasFailed()
						&& entry.getValue().hasWarnings()) {
					selection.add(entry.getKey());
				}
			}

			return selection;
		}

		public List<Member> selectFailed() {
			List<Member> selection = new LinkedList<Member>();

			for (Map.Entry<Member, MemberResult> entry : result.entrySet()) {
				if (entry.getValue().hasFailed()) {
					selection.add(entry.getKey());
				}
			}

			return selection;
		}

		public void printSummary(PrintWriter out) {
			int countOkWithoutWarnings = selectOkWithoutWarnings().size();
			int countOkWithWarnings = selectOkWithWarnings().size();
			int countFailed = selectFailed().size();

			int total = countOkWithoutWarnings + countOkWithWarnings
					+ countFailed;

			out.println("-- summary --");
			out.println(String.format("processed %d members in total", total));

			out.println(String.format(
					"successfully converted %d members (without warnings)",
					countOkWithoutWarnings));

			out.println(String.format(
					"successfully converted %d members (with warnings)",
					countOkWithWarnings));

			out.println(String.format("failed to convert %d members",
					countFailed));

			out.println();
			out.println("-- details --");

			if (countFailed > 0) {
				out.println("the following errors have occured:");
				for (Member member : selectFailed()) {
					out.println(String.format("%s %s:", member.getFirstname(),
							member.getLastname()));
					printDetail(member, out);
					out.println();
				}
			}

			if (countOkWithWarnings > 0) {
				out.println("the following warnings have occured:");
				for (Member member : selectOkWithWarnings()) {
					out.println(String.format("%s %s:", member.getFirstname(),
							member.getLastname()));
					printDetail(member, out);
					out.println();
				}
			}

			out.flush();
		}

		public void printDetail(Member member, PrintWriter out) {
			getResults().get(member).printDetail(out);

			out.flush();
		}

	}

	public static class MemberResult {

		private List<SepaException> warnings = new LinkedList<SepaException>();
		private List<SepaException> errors = new LinkedList<SepaException>();

		private MemberResult() {
		}

		public List<SepaException> getErrors() {
			return errors;
		}

		public List<SepaException> getWarnings() {
			return warnings;
		}

		public boolean hasFailed() {
			return getErrors().size() > 0;
		}

		public boolean hasWarnings() {
			return getWarnings().size() > 0;
		}

		public void printDetail(PrintWriter out) {
			if (hasFailed()) {
				out.println(String.format("has %d error(s)", errors.size()));
				for (SepaException sepaException : errors) {
					out.println(String.format("error: %s",
							sepaException.getMessage()));
				}
			} else if (hasWarnings()) {
				out.println(String.format("has %d warning(s)", warnings.size()));
				for (SepaException sepaException : warnings) {
					out.println(String.format("warning: %s",
							sepaException.getMessage()));
				}
			} else {
				out.println("no errors or warnings");
			}
		}
	}

	public ConversionResult convert(Collection<Member> members) {
		ConversionResult conversionResult = new ConversionResult();

		for (Member member : members) {
			conversionResult.getResults().put(member, convert(member));
		}

		return conversionResult;
	}

	public MemberResult convert(Member member) {
		MemberResult conversionResult = new MemberResult();

		try {
			IbanKonto ibanKonto = kontoConverter.convert(member.getAccount()
					.getBlzKonto());
			member.getAccount().setIbanKonto(ibanKonto);

			try {
				Bank bank = bankService.getBank(ibanKonto.getBic());
				member.getAccount().setBank(bank);
			} catch (SepaException sepaException) {
				conversionResult.getWarnings().add(sepaException);
			}

			member.setMandatsReferenz(Metalab.INSTANCE.createNewMandat());

			validate(member, conversionResult);
		} catch (SepaException sepaException) {
			conversionResult.getErrors().add(sepaException);
		}

		return conversionResult;
	}

	public ConversionResult validate(Collection<Member> members) {
		ConversionResult conversionResult = new ConversionResult();

		for (Member member : members) {
			MemberResult memberResult = new MemberResult();

			validate(member, memberResult);

			conversionResult.getResults().put(member, memberResult);
		}

		return conversionResult;
	}

	private void validate(Member member, MemberResult conversionResult) {
		for (IMemberValidator memberValidator : validators) {
			try {
				memberValidator.validate(member);
			} catch (SepaException sepaException) {
				conversionResult.getWarnings().add(sepaException);
			}
		}
	}
}
