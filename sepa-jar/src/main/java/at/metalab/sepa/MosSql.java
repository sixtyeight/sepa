package at.metalab.sepa;

import java.util.Collection;

import at.metalab.sepa.bo.Member;

public class MosSql {

	public static void writeUpdate(Collection<Member> members) {
		for (Member member : members) {
			writeUpdate(member);
		}
	}

	public static void writeUpdate(Member member) {
		int id = Integer.valueOf(member.getFirstname().substring(
				member.getFirstname().indexOf("-") + 1));

		System.out
				.println(String
						.format("update members_paymentinfo set bank_account_iban = '%s', bank_account_bic = '%s' where id = %d;",
								member.getAccount().getIbanKonto().getIban(),
								member.getAccount().getIbanKonto().getBic(),
								id));
	}
}
