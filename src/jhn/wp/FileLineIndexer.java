package jhn.wp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;

public class FileLineIndexer {
	public static void index(String srcFilename, String destFilename) throws IOException {
		RandomAccessFile r = new RandomAccessFile(srcFilename, "r");
		ObjectOutputStream w = new ObjectOutputStream(new FileOutputStream(destFilename));
		
		w.writeLong(0L);//First line starts at byte 0
		
		final long length = r.length();
		
		while(r.getFilePointer() < length) {
			r.readLine();
			w.writeLong(r.getFilePointer());
		}
		
		r.close();
		w.close();
		
	}
}
