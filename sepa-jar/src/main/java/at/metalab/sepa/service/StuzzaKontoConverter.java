package at.metalab.sepa.service;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;
import at.metalab.sepa.csv.Stuzza.StuzzaResponse;

public class StuzzaKontoConverter extends AbstractKontoConverter {

	private StuzzaResponse stuzzaResponse;

	public StuzzaKontoConverter(StuzzaResponse stuzzaResponse) {
		this.stuzzaResponse = stuzzaResponse;
	}

	public IbanKonto convert(BlzKonto blzKonto) throws SepaException {
		if (!stuzzaResponse.isKnown((blzKonto))) {
			throw new SepaException("konto not found in mapping");
		}

		if (stuzzaResponse.getAccountClosed().contains(blzKonto)) {
			throw new SepaException("[stuzza] account closed");
		}

		if (stuzzaResponse.getAccountNotExisting().contains(blzKonto)) {
			throw new SepaException("[stuzza] account not existing");
		}

		if (stuzzaResponse.getAgentNotExisting().contains(blzKonto)) {
			throw new SepaException("[stuzza] agent not existing");
		}

		if (stuzzaResponse.getNoDataAvailable().contains(blzKonto)) {
			throw new SepaException("[stuzza] no data available");
		}

		if (stuzzaResponse.getOtherError().contains(blzKonto)) {
			throw new SepaException("[stuzza] other error");
		}

		return stuzzaResponse.getComplete().get(blzKonto);
	}
}
