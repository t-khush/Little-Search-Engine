//Finished (I think and I hope -- 4/14/20)
package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		HashMap<String, Occurrence> kw = new HashMap<String, Occurrence>();
		Scanner input = new Scanner(new File(docFile));
		while(input.hasNext()){
			String s = getKeyword(input.next());
			if(s==null) continue; //we're not adding null words
			if(!kw.containsKey(s)){
				Occurrence occurence = new Occurrence(docFile, 1);
				kw.put(s, occurence);
			}
			else{
				kw.get(s).frequency++; //+=1 before
			}
		}
		return kw;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for(String key:kws.keySet()){
			ArrayList<Occurrence> alist = keywordsIndex.containsKey(key) ? keywordsIndex.get(key) : new ArrayList<Occurrence>();
			Occurrence o = kws.get(key);
			if(keywordsIndex.containsKey(key)){
				alist.add(o);
				insertLastOccurrence(alist);
			}
			else{
				alist.add(o);
				keywordsIndex.put(key, alist);
			}
		}

	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/

		word = word.toLowerCase();
		word = word.trim();
		//delete trailing punctutation
		int lastChar = -1;
		for(int i =0; i<word.length(); i++){
			if(Character.isLetter(word.charAt(i))){
				lastChar = i;
			}
		}
		word = word.substring(0, lastChar+1);

		for(int i =0; i<word.length(); i++){
			if(!Character.isLetter(word.charAt(i))){
				return null;
			}
		}
		if(noiseWords.contains(word)) return null;
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		Occurrence last = occs.get(occs.size()-1);

		 int left = 0, right = occs.size()-2;
		 while(left<=right){
		 	int mid = (right+left)/2;
		 	midpoints.add(mid);
		 	if(occs.get(mid).frequency==last.frequency){
		 		occs.add(mid, occs.get(occs.size()-1));
		 		occs.remove(occs.size()-1);
		 		return midpoints;
			}

			 right = occs.get(mid).frequency<last.frequency ? mid-1 : right;
			 left = occs.get(mid).frequency>last.frequency ? mid+1 : left;

		 }
		 occs.add(left, occs.get(occs.size()-1));
		 occs.remove(occs.size()-1);
		return midpoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		ArrayList<String> results = new ArrayList<String>();

		if(kw1==null && kw2==null) 	return new ArrayList<String>();

		if(keywordsIndex.isEmpty()) return new ArrayList<String>();


		//legalize strings
		kw1 = kw1.toLowerCase().trim();
		kw2 = kw2.toLowerCase().trim();

		if(keywordsIndex.get(kw1)!=null && keywordsIndex.get(kw2)==null){
			for(Occurrence o :keywordsIndex.get(kw1)){
				results.add(o.document);
			}
			//method to delete commonalities ==> don't need.
			//j gonna keep this in case.
			while(results.size()>5){
				results.remove(results.size()-1);
			}
			return results;
		}

		else if(keywordsIndex.get(kw2)!=null && keywordsIndex.get(kw1)==null){
			for(Occurrence o :keywordsIndex.get(kw2)){
				results.add(o.document);
			}
			//method to delete commonalities ==> don't need.
			//j gonna keep this in case.
			while(results.size()>5){
				results.remove(results.size()-1);
			}
			return results;
		}

		else if(keywordsIndex.get(kw1)!=null && keywordsIndex.get(kw2)!=null){
			//Step 1: Create the array list of OCCURENCES for kw1
			//Step 2: Create array list of occurances for kw2
			//while results < 5, compare the beginning of kw1 and kw2 array list's frequency, add to results, and delete it from there
			ArrayList<Occurrence> forkw1 = new ArrayList<Occurrence>();
			for(Occurrence o :keywordsIndex.get(kw1)){
				forkw1.add(new Occurrence(o.document, o.frequency));
			}
			ArrayList<Occurrence> forkw2 = new ArrayList<Occurrence>();
			for(Occurrence o :keywordsIndex.get(kw2)){
				forkw2.add(new Occurrence(o.document, o.frequency));
			}

			while(forkw1.size()>0 && forkw2.size()>0){
				if(forkw1.get(0).frequency>=forkw2.get(0).frequency){
					results.add(forkw1.get(0).document);
					forkw1.remove(0);
				}
				else{
					results.add(forkw2.get(0).document);
					forkw2.remove(0);
				}
			}
			//if one of the lsits ran out, add the other shits tothe results list
			if(forkw1.size()==0 && forkw2.size()!=0){
				for(Occurrence o:forkw2){
					results.add(o.document);
				}
			}
			if(forkw2.size()==0 && forkw1.size()!=0){
				for(Occurrence o:forkw1){
					results.add(o.document);
				}
			}
			//delete same
			results = deleteSame(results);
			ArrayList<String> temp = results;
			results = new ArrayList<String>();
			//top 5
			for(int i =0; i<temp.size(); i++){
				if(results.size()==5) break;
				results.add(temp.get(i));
			}
		}
		else{
			//idk in what world imma be here but this is base case ig
			return new ArrayList<String>();
		}

		return results;
	
	}
	private static ArrayList<String> deleteSame(ArrayList<String> arr){
		ArrayList<String> temp = new ArrayList<String>();
		for(String s: arr){
			if(!temp.contains(s)){
				temp.add(s);
			}
		}
		return temp;
	}
}
