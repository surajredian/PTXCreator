package com.kpit.ptxcreator.catalog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kpit.ptxcreator.PTXCreatorDTO;
import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;
import com.kpit.ptxcreator.signature.ISignature;
import com.kpit.ptxcreator.signature.SignatureFactory;

class CatalogCreatorImpl implements ICatalogCreator {

	private ISignature signOtx = null;
	private ISignature signOdx = null;
	private ISignature signBin = null;
	private final Document doc;
	private final Transformer transformer;
	private Element filesOtx;
	private Element filesOdx;
	private Element filesBin;

	private final PTXCreatorDTO ptxDto;

	CatalogCreatorImpl(PTXCreatorDTO ptxDto, PrivateKey prvKey) throws PTXCreatorException {
		try {
			this.ptxDto = ptxDto;
			if (null != prvKey) {
				signOtx = SignatureFactory.createInstance(ptxDto, prvKey);
				signOdx = SignatureFactory.createInstance(ptxDto, prvKey);
				signBin = SignatureFactory.createInstance(ptxDto, prvKey);
			}
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			initDocument(null != prvKey);
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError
				| ParserConfigurationException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Failed during Instatiating Catalog");
		}
	}

	private void initDocument(boolean encrypt) throws PTXCreatorException {
		final String HASH_ALGORITHM = "HASH-ALGORITHM";
		final String ALGORITHM = "ALGORITHM";
		final String FILES = "FILES";
		final String CATEGORY = "CATEGORY";
		final String SIGNATURE = "SIGNATURE";

		String productVersion = ptxDto.getProductVersion();
		String productName = ptxDto.getProductName();
		String productCompany = ptxDto.getCompany();

		Element root = doc.createElement("CATALOG");
		doc.appendChild(root);

		root.setAttribute("xmlns", "http://iso.org/OTX/1.0.0/Auxiliaries/Container");
		root.setAttribute("xmlns:ptxXhtml", "http://iso.org/OTX/1.0.0/Auxiliaries/ContainerXhtml");
		root.setAttribute("xsi:schemaLocation", "http://iso.org/OTX/1.0.0/Auxiliaries/Container schema.xsd");
		root.setAttribute("CONTAINER-VERSION", productVersion);
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

		Element rootShortName = doc.createElement("SHORT-NAME");
		rootShortName.setTextContent(productName);
		root.appendChild(rootShortName);

		Element companyDatum = doc.createElement("COMPANY-DATAS");
		root.appendChild(companyDatum);

		Element companyData = doc.createElement("COMPANY-DATA");
		companyDatum.appendChild(companyData);

		Element elementId = doc.createElement("ELEMENT-ID");
		companyData.appendChild(elementId);

		Element shortName = doc.createElement("SHORT-NAME");
		shortName.setTextContent(productCompany);
		elementId.appendChild(shortName);

		// Again from root
		Element ablocks = doc.createElement("ABLOCKS");
		root.appendChild(ablocks);
		Element ablock = doc.createElement("ABLOCK");
		ablocks.appendChild(ablock);

		// Add OTX/OTX Files
		Element categoryOtx = doc.createElement(CATEGORY);
		categoryOtx.setTextContent("OTX-DATA");
		ablock.appendChild(categoryOtx);
		filesOtx = doc.createElement(FILES);
		if (encrypt) {
			filesOtx.setAttribute(HASH_ALGORITHM, ptxDto.getHashAlgo().getAlgorithm());
		}
		ablock.appendChild(filesOtx);

		// Add ODX Files
		Element categoryOdx = doc.createElement(CATEGORY);
		categoryOdx.setTextContent("ODX-DATA");
		ablock.appendChild(categoryOdx);
		filesOdx = doc.createElement(FILES);
		if (encrypt) {
			filesOdx.setAttribute(HASH_ALGORITHM, ptxDto.getHashAlgo().getAlgorithm());
		}
		ablock.appendChild(filesOdx);

		// Add all apart from the above 2 sets of Files
		Element categoryBin = doc.createElement(CATEGORY);
		categoryBin.setTextContent("BINARY");
		ablock.appendChild(categoryBin);
		filesBin = doc.createElement(FILES);
		if (encrypt) {
			filesBin.setAttribute(HASH_ALGORITHM, ptxDto.getHashAlgo().getAlgorithm());
		}
		ablock.appendChild(filesBin);

		// Adds Files to corresponding Category
		// And creates Signature Digest for them
		addFilesToCatalog(ptxDto.getPackageBundleMap());

		// Signature can be generated only after adding Files to Catalog
		// Signature is dependent on order in which files are selected
		// And which Category List of Files

		if (encrypt) {
			Element signatureOtx = doc.createElement(SIGNATURE);
			signatureOtx.setAttribute(ALGORITHM, ptxDto.getSignAlgo().getAlgorithm());
			signatureOtx.setTextContent(signOtx.createSignature());
			filesOtx.appendChild(signatureOtx);

			Element signatureOdx = doc.createElement(SIGNATURE);
			signatureOdx.setAttribute(ALGORITHM, ptxDto.getSignAlgo().getAlgorithm());
			signatureOdx.setTextContent(signOdx.createSignature());
			filesOdx.appendChild(signatureOdx);

			Element signatureBin = doc.createElement(SIGNATURE);
			signatureBin.setAttribute(ALGORITHM, ptxDto.getSignAlgo().getAlgorithm());
			signatureBin.setTextContent(signBin.createSignature());
			filesBin.appendChild(signatureBin);
		}
	}

	@Override
	public ByteArrayOutputStream createOutputByteStream() throws PTXCreatorException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		StreamResult consoleResult = new StreamResult(byteOut);
		DOMSource source = new DOMSource(doc);
		try {
			transformer.transform(source, consoleResult);
		} catch (TransformerException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e, "Failed to transform catalog to xml");
		}
		return byteOut;
	}

	private void addFilesToCatalog(Map<String, List<File>> packageBundleMap) throws PTXCreatorException {
		for (Entry<String, List<File>> pair : packageBundleMap.entrySet()) {
			String packagePath = (null == pair.getKey()) ? "" : pair.getKey();
			for (File file : pair.getValue()) {
				addDirFileToCatalogRecursive(packagePath, file);
			}
		}
	}

	private void addDirFileToCatalogRecursive(String packagePath, File fileToBundle) throws PTXCreatorException {
		String relativePath = packagePath + File.separator + fileToBundle.getName();
		if (fileToBundle.isDirectory()) {
			File files[] = fileToBundle.listFiles();
			for (int i = 0; i < files.length; i++) {
				addDirFileToCatalogRecursive(relativePath, files[i]);
			}
		} else {
			addFileToCatalog(relativePath, fileToBundle);
		}
	}

	private void addFileToCatalog(String relativePath, File file) throws PTXCreatorException {
		final String FILE = "FILE";
		final String HASH = "HASH";

		// Include Exclude specific extensions
		String split[] = file.getName().split("\\.");
		String ext = split[split.length - 1];
		if (null != ptxDto.getIncludeExts() && ptxDto.getIncludeExts().size() > 0) {
			if (!ptxDto.getIncludeExts().contains(ext))
				return;
		} else if (null != ptxDto.getExcludeExts() && ptxDto.getExcludeExts().size() > 0) {
			if (ptxDto.getExcludeExts().contains(ext))
				return;
		}

		// Standardizing the path displayed in catalog/digest construction
		String stdRelativePath = relativePath.replace("\\", "/");
		stdRelativePath = (stdRelativePath.charAt(0) == '/') ? stdRelativePath.substring(1) : stdRelativePath;

		String fileName = file.getName().toLowerCase();
		Element fileEle = doc.createElement(FILE);
		fileEle.setTextContent(stdRelativePath);
		FileInputStream fis;

		ISignature sign = null;
		if (fileName.endsWith(".otx") || fileName.endsWith(".otxt")) {
			sign = signOtx;
			filesOtx.appendChild(fileEle);
		} else if (fileName.endsWith(".odx")) {
			sign = signOdx;
			filesOdx.appendChild(fileEle);
		} else {
			sign = signBin;
			filesBin.appendChild(fileEle);
		}

		try {
			if (null != sign) {
				fis = new FileInputStream(file);
				String fileHash = sign.getHashForStream(fis);
				fileEle.setAttribute(HASH, fileHash);
				sign.addContentToSignatureDigest(
						new ByteArrayInputStream((fileHash + ":" + stdRelativePath + ";").getBytes()));
			}
		} catch (IOException e) {
			throw new PTXCreatorException(ErrorCode.INTERNAL_ERROR, e,
					"Failed Hashing/Signature Digest for File:" + file.getAbsolutePath());
		}
	}
}