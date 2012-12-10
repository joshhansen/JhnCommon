package jhn.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class DocLabelsFileWriter implements AutoCloseable {
	private final int labelCount;
	private int docLabels = 0;
	private PrintStream w;
	
	public DocLabelsFileWriter(String outputFilename) throws FileNotFoundException {
		this(outputFilename, 10);
	}
	
	public DocLabelsFileWriter(String outputFilename, int labelCount) throws FileNotFoundException {
		this.labelCount = labelCount;
		w = new PrintStream(new FileOutputStream(outputFilename));
		w.println("#docnum,source");
		for(int i = 1; i <= labelCount; i++) {
			w.print(",label");
			w.print(i);
		}
		w.println();
//		topic1label,topic2label,topic3label,topic4label,topic5label,topic6label,topic7label,topic8label,topic9label,topic10label");
	}
	
	public void startDocument(int docNum, String docSource) {
		w.print(docNum);
		w.print(',');
		w.print(docSource);
	}
	
	public void label(String label) {
		docLabels++;
		w.append(",\"").append(label).append('"');
	}
	
	public void endDocument() {
		if(docLabels != labelCount) {
			throw new IllegalStateException("Specified " + docLabels + " labels; expected " + labelCount);
		}
		w.println();
		docLabels = 0;
	}

	@Override
	public void close() throws Exception {
		w.close();
	}
//	try(PrintStream w = new PrintStream(new FileOutputStream(outputFilename))) {
//		w.println("#docnum,source,topic1label,topic2label,topic3label,topic4label,topic5label,topic6label,topic7label,topic8label,topic9label,topic10label");
//		
//		int topicNum;
//		int globalTopicNum;
//		String label;
//		w.println("#docnum,source,topic1label,topic2label,topic3label,topic4label,topic5label,topic6label,topic7label,topic8label,topic9label,topic10label");
//		@SuppressWarnings("unchecked")
//		Int2ObjectMap.Entry<Counter<Integer,Integer>>[] entries = docTopicCounts.int2ObjectEntrySet().toArray(new Int2ObjectMap.Entry[0]);
//		Arrays.sort(entries, cmp);
//		
//		for(Int2ObjectMap.Entry<Counter<Integer,Integer>> entry : entries) {
//			w.print(entry.getIntKey());
//			w.print(',');
//			w.print(docFilenames.get(entry.getIntKey()));
//			
//			for(Int2IntMap.Entry count : ((IntIntCounter)entry.getValue()).fastTopN(topNlabels)) {
//				topicNum = count.getIntKey();
//				globalTopicNum = topicMapping.objectAtI(topicNum);
//				label = labels.lookupObject(globalTopicNum).toString();
//				
//				w.print(",\"");
//				w.print(label);
//				w.print("\"");
//			}
//			w.println();
//		}
//	}//end try
}
