/*
 * File: InvertKey.java
 * --------------------
 * This program takes a 26 letter substitution cipher key and checks to
 * make sure it is a valid key then inverts the key and prints the 
 * inverted key for the user, then inverts the key again back to the 
 * original to prove it inverted the key correctly
 */

import java.util.Set;
import java.util.*;

import acm.program.*;

public class InvertKey extends ConsoleProgram {

	public void run() {
		String key = readLine("Enter 26-letter key: ");
		while (!keyIsLegal(key)) {
			println("That key is illegal");
			key = readLine("Enter 26-letter key: ");
		}
		println("key:          " + key);
		String invertedKey = invertKey(key);
		println("Inverted key: " + invertedKey);
		invertedKey = invertKey(invertedKey);
		println("Original key: " + invertedKey);
	}

	/**
	 * Inverts a key for a letter-substitution cipher, where a key is a
	 * 26-letter string that shows how each letter in the alphabet is
	 * translated into the encrypted message.  For example, if the key is
	 * "LZDRXPEAJYBQWFVIHCTGNOMKSU", that means that 'A' (the first letter
	 * in the alphabet) translates to 'L' (the first letter in the key),
	 * 'B' translates to 'Z', 'C' translates to 'D', and so on.  The inverse
	 * of a key is a 26-letter that translates in the opposite direction.
	 * As an example, the inverse of "LZDRXPEAJYBQWFVIHCTGNOMKSU" is
	 * "HKRCGNTQPIXAWUVFLDYSZOMEJB".
	 *
	 * @param key The original key
	 * @return The key that translates in the opposite direction
	 */

	/*Method: invertKey()*/
	/*
	 * takes a valid substitution cipher key and returns the inverted 
	 * version that can be used for deciphering a message. Does this by
	 * finding the distance on the string of an indexed letter then 
	 * adding that distance to A to find the new letter for the inverted 
	 * key
	 */
	private String invertKey(String key) {
		String invertedKey = "";
		key = key.toUpperCase();
		for(int i = 0; i < 26; i++) {
			int distance = key.indexOf('A' + i);
			invertedKey += (char)('A' + distance);
		}
		return invertedKey;
	}

	/*Method: keyIsLegal()*/
	/*
	 * checks to see if the key provided is legal and abides by the 
	 * conditions of having all the letters in the alphabet only once
	 * and returns true if it abides by the conditions and false if it 
	 * doesn't
	 */
	private boolean keyIsLegal(String key) {
		if (key.length() == 26 && stringHasAllLetters(key)) {
			return true;
		}
		return false;
	}
	
	/*Method: stringHasAllLetters()*/
	/*
	 * checks to see if a the string contains all the letters in the 
	 * alphabet only once by making a set with one of each letter then
	 * taking the letter away every time it comes up in the key and 
	 * checking at the end for any remaining letters. It then prints the
	 * letters so that the user can know which letters he has duplicated
	 * and which letters he is missing
	 */
	private boolean stringHasAllLetters(String key) {
		key = key.toUpperCase();
		Set<Character> alphabetLetters = new TreeSet();
		for(int i = 0; i < 26; i++) {
			char ch = (char)('A' + i);
			alphabetLetters.add(ch);
		}
		for(int j = 0; j < 26; j++) {
			char ch = key.charAt(j);
			if (alphabetLetters.contains(ch)) {
				alphabetLetters.remove(ch);
			} else println("duplicate letter " + ch);
		}
		if(alphabetLetters.size()==0) return true;
		else {
			println("missing letter(s): " + alphabetLetters);
			return false;
		}
	}
}
