package at.metalab.sepa.bo;

public class BlzKonto {
	private final long blz;

	private final long kontonummer;

	public BlzKonto(long blz, long kontonummer) {
		this.blz = blz;
		this.kontonummer = kontonummer;
	}

	public long getBlz() {
		return blz;
	}

	public long getKontonummer() {
		return kontonummer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (blz ^ (blz >>> 32));
		result = prime * result + (int) (kontonummer ^ (kontonummer >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlzKonto other = (BlzKonto) obj;
		if (blz != other.blz)
			return false;
		if (kontonummer != other.kontonummer)
			return false;
		return true;
	}
}
