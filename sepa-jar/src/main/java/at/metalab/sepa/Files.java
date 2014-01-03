package at.metalab.sepa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public abstract class Files {

	public final static Files METALAB_PRODUCTION = new FilesystemFiles(
			new File(System.getProperty("user.home"), "metalab_sepa"));

	public final static Files METALAB_TESTDATA = ResourceFiles.INSTANCE;

	public Reader getCollectionCsv() throws UnsupportedEncodingException,
			FileNotFoundException {
		return getUTF8Reader("collection.txt");
	}

	public Reader getCollectionSepaCsv() throws UnsupportedEncodingException,
			FileNotFoundException {
		return getUTF8Reader("collection_sepa.txt");
	}

	public Reader getStuzzaReturnCsv() throws UnsupportedEncodingException,
			FileNotFoundException {
		return getUTF8Reader("stuzza_return.txt");
	}

	public Reader getNotReallyLikeMosCsv() throws UnsupportedEncodingException,
			FileNotFoundException {
		return getUTF8Reader("bank_data.csv");
	}

	protected abstract Reader getUTF8Reader(String resourceName)
			throws FileNotFoundException, UnsupportedEncodingException;

	private static class ResourceFiles extends Files {

		public static final Files INSTANCE = new ResourceFiles();

		private ResourceFiles() {
		}

		protected Reader getUTF8Reader(String resourceName)
				throws FileNotFoundException, UnsupportedEncodingException {
			Reader reader = new InputStreamReader(
					Thread.currentThread()
							.getContextClassLoader()
							.getResourceAsStream(
									String.format("data/%s", resourceName)),
					"UTF-8");

			return reader;
		}
	}

	public static class FilesystemFiles extends Files {

		private File inputDir;

		public FilesystemFiles(File inputDir) {
			this.inputDir = inputDir;
		}

		protected Reader getUTF8Reader(String fileName)
				throws UnsupportedEncodingException, FileNotFoundException {
			return new InputStreamReader(new FileInputStream(new File(inputDir,
					fileName)), "UTF-8");
		}
	}

}