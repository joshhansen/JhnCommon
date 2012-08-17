package jhn.util;

import java.io.File;
import java.io.FileFilter;

public class FileExtensionFilter implements FileFilter {
	private final String extension;
	
	public FileExtensionFilter(String extension) {
		this.extension = extension.charAt(0)=='.' ? extension.substring(1) : extension;
	}

	@Override
	public boolean accept(File pathname) {
		return pathname.getName().endsWith("."+extension);
	}

}
