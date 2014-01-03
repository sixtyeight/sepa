package at.metalab.sepa.service;

import at.metalab.sepa.SepaException;
import at.metalab.sepa.bo.BlzKonto;
import at.metalab.sepa.bo.IbanKonto;

public interface IKontoConverter {
	IbanKonto convert(BlzKonto blzKonto) throws SepaException;
}
