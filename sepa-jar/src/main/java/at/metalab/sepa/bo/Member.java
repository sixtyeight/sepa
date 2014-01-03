package at.metalab.sepa.bo;

import java.math.BigDecimal;
import java.util.Comparator;

public class Member {
	private String firstname;
	private String lastname;

	private Account account = new Account();

	private BigDecimal fee;
	private String mandatsReferenz;

	public void setMandatsReferenz(String mandatsReferenz) {
		this.mandatsReferenz = mandatsReferenz;
	}
	
	public String getMandatsReferenz() {
		return mandatsReferenz;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}

	public Account getAccount() {
		return account;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getName() {
		return String.format("%s %s", getFirstname(), getLastname());
	}

	public final static Comparator<Member> BY_LASTNAME_FIRSTNAME = new Comparator<Member>() {
		public int compare(Member o1, Member o2) {
			int lname = o1.getLastname().compareTo(o2.getLastname());

			if (lname == 0) {
				return o1.getFirstname().compareTo(o2.getFirstname());
			}

			return lname;
		}
	};

	public final static Comparator<Member> BY_BLZ_KONTO = new Comparator<Member>() {
		public int compare(Member o1, Member o2) {
			int blz = Long.valueOf(o1.getAccount().getBlzKonto().getBlz())
					.compareTo(o2.getAccount().getBlzKonto().getBlz());
			if (blz == 0) {
				return Long.valueOf(
						o1.getAccount().getBlzKonto().getKontonummer())
						.compareTo(
								o2.getAccount().getBlzKonto().getKontonummer());
			}

			return blz;
		}
	};

}
