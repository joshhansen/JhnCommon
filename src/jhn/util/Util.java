package jhn.util;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;

public final class Util {
	private Util(){}
	
	public static InputStream smartInputStream(final String filename) throws IOException {
		if(filename.endsWith(".bz2")) {
			InputStream is = new FileInputStream(filename);
			is.read();
			is.read();
			return new CBZip2InputStream(is);
		} else {
			return new FileInputStream(filename);
		}
	}
	
	public static OutputStream smartOutputStream(final String filename) throws IOException {
		return smartOutputStream(filename, false);
	}
	
	public static OutputStream smartOutputStream(final String filename, final boolean buffered) throws IOException {
		OutputStream os = new FileOutputStream(filename);
		
		if(buffered) os = new BufferedOutputStream(os);
		
		if(filename.endsWith(".bz2")) {
			os = new CBZip2OutputStream(os);
		} else if(filename.endsWith(".gz")) {
			os = new GZIPOutputStream(os);
		}
		return os;
	}
	
	public static Reader smartReader(final String filename) throws IOException {
		return new InputStreamReader(smartInputStream(filename));
	}
	
	public static void serialize(final Object obj, final String outputFilename) {
		try {
			OutputStream os = smartOutputStream(outputFilename);
			ObjectOutputStream out = new ObjectOutputStream(os);
			out.writeObject(obj);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static Object deserialize(final String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}
	
	/** From Mallet 2.0.7 */
	public static Set<String> stopwords() {
		final Set<String> stopwords = new HashSet<String>();
		for(String stopword : Util.stopwords) stopwords.add(stopword);
		return stopwords;
	}
	private static final String[] stopwords = { "a", "able", "about", "above",
			"according", "accordingly", "across", "actually", "after",
			"afterwards", "again", "against", "all", "allow", "allows",
			"almost", "alone", "along", "already", "also", "although",
			"always", "am", "among", "amongst", "an", "and", "another", "any",
			"anybody", "anyhow", "anyone", "anything", "anyway", "anyways",
			"anywhere", "apart", "appear", "appreciate", "appropriate", "are",
			"around", "as", "aside", "ask", "asking", "associated", "at",
			"available", "away", "awfully", "b", "be", "became", "because",
			"become", "becomes", "becoming", "been", "before", "beforehand",
			"behind", "being", "believe", "below", "beside", "besides", "best",
			"better", "between", "beyond", "both", "brief", "but", "by", "c",
			"came", "can", "cannot", "cant", "cause", "causes", "certain",
			"certainly", "changes", "clearly", "co", "com", "come", "comes",
			"concerning", "consequently", "consider", "considering", "contain",
			"containing", "contains", "corresponding", "could", "course",
			"currently", "d", "definitely", "described", "despite", "did",
			"different", "do", "does", "doing", "done", "down", "downwards",
			"during", "e", "each", "edu", "eg", "eight", "either", "else",
			"elsewhere", "enough", "entirely", "especially", "et", "etc",
			"even", "ever", "every", "everybody", "everyone", "everything",
			"everywhere", "ex", "exactly", "example", "except", "f", "far",
			"few", "fifth", "first", "five", "followed", "following",
			"follows", "for", "former", "formerly", "forth", "four", "from",
			"further", "furthermore", "g", "get", "gets", "getting", "given",
			"gives", "go", "goes", "going", "gone", "got", "gotten",
			"greetings", "h", "had", "happens", "hardly", "has", "have",
			"having", "he", "hello", "help", "hence", "her", "here",
			"hereafter", "hereby", "herein", "hereupon", "hers", "herself",
			"hi", "him", "himself", "his", "hither", "hopefully", "how",
			"howbeit", "however", "i", "ie", "if", "ignored", "immediate",
			"in", "inasmuch", "inc", "indeed", "indicate", "indicated",
			"indicates", "inner", "insofar", "instead", "into", "inward", "is",
			"it", "its", "itself", "j", "just", "k", "keep", "keeps", "kept",
			"know", "knows", "known", "l", "last", "lately", "later", "latter",
			"latterly", "least", "less", "lest", "let", "like", "liked",
			"likely", "little", "look", "looking", "looks", "ltd", "m",
			"mainly", "many", "may", "maybe", "me", "mean", "meanwhile",
			"merely", "might", "more", "moreover", "most", "mostly", "much",
			"must", "my", "myself", "n", "name", "namely", "nd", "near",
			"nearly", "necessary", "need", "needs", "neither", "never",
			"nevertheless", "new", "next", "nine", "no", "nobody", "non",
			"none", "noone", "nor", "normally", "not", "nothing", "novel",
			"now", "nowhere", "o", "obviously", "of", "off", "often", "oh",
			"ok", "okay", "old", "on", "once", "one", "ones", "only", "onto",
			"or", "other", "others", "otherwise", "ought", "our", "ours",
			"ourselves", "out", "outside", "over", "overall", "own", "p",
			"particular", "particularly", "per", "perhaps", "placed", "please",
			"plus", "possible", "presumably", "probably", "provides", "q",
			"que", "quite", "qv", "r", "rather", "rd", "re", "really",
			"reasonably", "regarding", "regardless", "regards", "relatively",
			"respectively", "right", "s", "said", "same", "saw", "say",
			"saying", "says", "second", "secondly", "see", "seeing", "seem",
			"seemed", "seeming", "seems", "seen", "self", "selves", "sensible",
			"sent", "serious", "seriously", "seven", "several", "shall", "she",
			"should", "since", "six", "so", "some", "somebody", "somehow",
			"someone", "something", "sometime", "sometimes", "somewhat",
			"somewhere", "soon", "sorry", "specified", "specify", "specifying",
			"still", "sub", "such", "sup", "sure", "t", "take", "taken",
			"tell", "tends", "th", "than", "thank", "thanks", "thanx", "that",
			"thats", "the", "their", "theirs", "them", "themselves", "then",
			"thence", "there", "thereafter", "thereby", "therefore", "therein",
			"theres", "thereupon", "these", "they", "think", "third", "this",
			"thorough", "thoroughly", "those", "though", "three", "through",
			"throughout", "thru", "thus", "to", "together", "too", "took",
			"toward", "towards", "tried", "tries", "truly", "try", "trying",
			"twice", "two", "u", "un", "under", "unfortunately", "unless",
			"unlikely", "until", "unto", "up", "upon", "us", "use", "used",
			"useful", "uses", "using", "usually", "uucp", "v", "value",
			"various", "very", "via", "viz", "vs", "w", "want", "wants", "was",
			"way", "we", "welcome", "well", "went", "were", "what", "whatever",
			"when", "whence", "whenever", "where", "whereafter", "whereas",
			"whereby", "wherein", "whereupon", "wherever", "whether", "which",
			"while", "whither", "who", "whoever", "whole", "whom", "whose",
			"why", "will", "willing", "wish", "with", "within", "without",
			"wonder", "would", "would", "x", "y", "yes", "yet", "you", "your",
			"yours", "yourself", "yourselves", "z", "zero",
			// stop words for paper abstracts
			// "abstract",
			// "paper",
			// "presents",
			// "discuss",
			// "discusses",
			// "conclude",
			// "concludes",
			// "based",
			// "approach"
	};
	
	private static Integer charCount = 0;
	private static final int CHAROUT_NEWLINE_INTERVAL = 120;
	public static void charout(char c) {
		System.out.print(c);
		synchronized(charCount) {
			charCount++;
			if(charCount % CHAROUT_NEWLINE_INTERVAL == 0) System.out.println();
		}
	}
}
