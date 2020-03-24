package com.kpit.ptxcreator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.kpit.ptxcreator.exception.ErrorCode;
import com.kpit.ptxcreator.exception.PTXCreatorException;
import com.kpit.ptxcreator.signature.HashAlgorithm;
import com.kpit.ptxcreator.signature.SignAlgorithm;

public class PTXCreatorDTO {

	public static enum ComplianceType {
		KPIT, ASAM;
	}

	private String ptxFile;
	private Map<String, List<File>> packageBundleMap = new HashMap<>();
	private String productName;
	private String productVersion;
	private String company;
	private ComplianceType compliance = ComplianceType.KPIT;
	private String password = null;
	private List<String> includeExts = null;
	private List<String> excludeExts = null;
	private HashAlgorithm hashAlgo = HashAlgorithm.SHA256;
	private SignAlgorithm signAlgo = SignAlgorithm.SHA1withRSA;
	private byte[] privateKey = null;

	/**
	 * Adds a source folder (Project Directory) and a list of Packages(or Files)
	 * under it. The package/file List mentioned should be relative paths from
	 * the source/project directory and the same will be maintained in the PTX
	 * created. These will be all bundled together in the PTX
	 * 
	 * @param rootProjectDirectory
	 *            - source/project Directory to be looked up for the packages
	 * @param packages
	 *            - List of packages which needs to be bundled up
	 * 
	 * @return - returns internally maintained Package Bundling structure
	 */
	public void addProjectDirectoryAndPackages(String rootProjectDirectory, List<String> packages)
			throws PTXCreatorException {
		File rootProject = new File(rootProjectDirectory);
		if (!rootProject.exists()) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Root Project Directory Missing");
		}
		for (Iterator<String> iterator = packages.iterator(); iterator.hasNext();) {
			String strPackage = iterator.next();
			File packagePath = new File(rootProject, strPackage);
			if (!packagePath.exists()) {
				throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR,
						"Package Path missing: " + packagePath.getAbsolutePath());
			}
			String rootPackage = packagePath.getParent().replace(rootProject.getAbsolutePath(), "");
			//Checking for greater than 1 as the first will be always a path separator
			rootPackage = (rootPackage.length() > 1) ? rootPackage.substring(1) : null;
			List<File> packagesToBundle = packageBundleMap.get(rootPackage);
			if (null == packagesToBundle) {
				packagesToBundle = new ArrayList<>();
				packageBundleMap.put(rootPackage, packagesToBundle);
			}
			packagesToBundle.add(packagePath);
		}
	}

	/**
	 * Fetches the internally maintained Package Bundling structure
	 * 
	 * @return - returns internally maintained Package Bundling structure
	 * 
	 */
	public Map<String, List<File>> getPackageBundleMap() {
		return packageBundleMap;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public ComplianceType getCompliance() {
		return compliance;
	}

	/**
	 * Sets which compliance standard that needs to be followed
	 * 
	 * @param compliance-
	 *            ComplianceType (KPIT, ASAM)
	 * 
	 * @return - returns internally maintained Package Bundling structure
	 */
	public void setCompliance(ComplianceType compliance) {
		this.compliance = compliance;
	}

	public String getPtxFile() {
		return ptxFile;
	}

	public void setPtxFile(String ptxFile) {
		this.ptxFile = ptxFile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getIncludeExts() {
		return includeExts;
	}

	/**
	 * Sets the List of file extensions to be included while packaging 
	 * Include list takes priority over exclude list 
	 * If both are set then exclude list will be ignored
	 * 
	 * @param includeExts
	 *            - list of files to be included
	 */
	public void setIncludeExts(List<String> includeExts) {
		this.includeExts = includeExts;
	}

	public List<String> getExcludeExts() {
		return excludeExts;
	}

	/**
	 * Sets the List of file extensions to be excluded while packaging 
	 * Include list takes priority over exclude list 
	 * If both are set then exclude list will be ignored
	 * 
	 * @param includeExts
	 *            - list of files to be included
	 */
	public void setExcludeExts(List<String> excludeExts) {
		this.excludeExts = excludeExts;
	}

	public HashAlgorithm getHashAlgo() {
		return hashAlgo;
	}

	/**
	 * Sets the Algorithm for generating file Hashes File Hash 
	 * Information will appear in Catalog
	 * It will appear in Hex format in the file
	 * 
	 * @param hashAlgo
	 *            - Algorithm to be followed for Hashing
	 */
	public void setHashAlgo(HashAlgorithm hashAlgo) {
		this.hashAlgo = hashAlgo;
	}

	public SignAlgorithm getSignAlgo() {
		return signAlgo;
	}

	/**
	 * Sets the Algorithm for generating signature 
	 * Signature for the PTX will appear in Catalog 
	 * It will appear in Base64 format in the file
	 * 
	 * @param signAlgo
	 *            - Algorithm to be followed for signing
	 */
	public void setSignAlgo(SignAlgorithm signAlgo) {
		this.signAlgo = signAlgo;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

	/**
	 * Validates if the PTXCreatorDTO object is constructed properly
	 * This is implicitly invoked by PTX Creator Implementations
	 * 
	 * @throws PTXCreatorException
	 */
	void validate() throws PTXCreatorException {
		if (isEmpty(company)) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Company cannot be empty");
		} else if (isEmpty(productName)) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Product Name cannot be empty");
		} else if (isEmpty(productVersion)) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Product Version cannot be empty");
		} else if (isEmpty(ptxFile) || !ptxFile.endsWith(".ptx")) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR,
					"PTX file to be created cannot be empty and shoud end with extension .ptx");
		} else if (null == packageBundleMap || packageBundleMap.size() == 0) {
			throw new PTXCreatorException(ErrorCode.VALIDATION_ERROR, "Provide valid packageBundleMap for Packaging");
		}
	}

	private boolean isEmpty(String value) {
		if (null == value || value.isEmpty()) {
			return true;
		}
		return false;
	}
}
