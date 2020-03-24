package com.kpit.ptxcreator;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.PrivateKey;

import com.kpit.ptxcreator.PTXCreatorDTO.ComplianceType;
import com.kpit.ptxcreator.catalog.CatalogFactory;
import com.kpit.ptxcreator.catalog.ICatalogCreator;
import com.kpit.ptxcreator.exception.PTXCreatorException;
import com.kpit.ptxcreator.keygen.IKeyGen;
import com.kpit.ptxcreator.keygen.KeyGeneratorFactory;
import com.kpit.ptxcreator.unzip.IUnZip;
import com.kpit.ptxcreator.unzip.UnZipFactory;
import com.kpit.ptxcreator.zip.IZip;
import com.kpit.ptxcreator.zip.ZipFactory;

class PTXCreatorImpl implements IPTXCreator {

	private static final String CATALOG_FILE = "index.xml";
	private IZip zip = null;
	private ICatalogCreator catalog = null;
	private IUnZip unZip = null;

	@Override
	public KeyPair generatePTX(PTXCreatorDTO ptxCreatorDTO) throws PTXCreatorException {
		ptxCreatorDTO.validate();

		zip = ZipFactory.createInstance(ptxCreatorDTO);
		zip.includeExtensionList(ptxCreatorDTO.getIncludeExts());
		zip.excludeExtensionList(ptxCreatorDTO.getExcludeExts());
		zip.setPassword(ptxCreatorDTO.getPassword());
		zip.createPTX(ptxCreatorDTO.getPtxFile(), ptxCreatorDTO.getPackageBundleMap());
		KeyPair keyPair = null;
		PrivateKey prvKey = null;
		if (ptxCreatorDTO.getCompliance() == ComplianceType.ASAM) {
			IKeyGen keyGen = KeyGeneratorFactory.createInstance(ptxCreatorDTO);
			if (null != ptxCreatorDTO.getPrivateKey()) {
				prvKey = keyGen.createPrivateKey(ptxCreatorDTO.getPrivateKey());
			} else {
				keyPair = keyGen.keyPairGen();
				prvKey = keyPair.getPrivate();
			}
		}
		catalog = CatalogFactory.createInstance(ptxCreatorDTO, prvKey);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(catalog.createOutputByteStream().toByteArray());
		zip.setPassword(null);
		zip.addStreamToPTX(ptxCreatorDTO.getPtxFile(), byteIn, CATALOG_FILE, null);
		return keyPair;
	}

	@Override
	public void extractPTX(PTXExtractorDTO ptxExtractDTO) throws PTXCreatorException {
		ptxExtractDTO.validate();
		
		unZip = UnZipFactory.createInstance(ptxExtractDTO);
		unZip.setPassword(ptxExtractDTO.getPassword());
		unZip.includeExtensionList(ptxExtractDTO.getIncludeExts());
		unZip.excludeExtensionList(ptxExtractDTO.getExcludeExts());
		unZip.extractPTX(ptxExtractDTO.getTargetPTXFile(), ptxExtractDTO.getLocationToExtract(), ptxExtractDTO.getTargetProjectName());
	}
}
