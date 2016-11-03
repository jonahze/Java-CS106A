/*
 * File: LetterSubstitutionCipher.java
 * -----------------------------------
 * This program translates a line of text using a letter-substitution cipher.
 * It also checks to make sure the key you provide is a usable key for the
 * substitution cipher
 */

import acm.program.*;
import java.util.*;

public class LetterSubstitutionCipher extends ConsoleProgram {

	public void run() {
		println("Letter-substitution cipher.");
		String key = readLine("Enter 26-letter key: ");
		while (!keyIsLegal(key)) {
			println("That key is illegal");
			key = readLine("Enter 26-letter key: ");
		}
		String plaintext = readLine("Plaintext:  ");
		String ciphertext = encrypt(plaintext, key);
		println("Ciphertext: " + ciphertext);
	}

	/**
	 * Encrypts a string according to the key.  All letters in the string
	 * are converted to uppercase.  Any character that is not a letter is
	 * copied to the output unchanged.
	 *
	 * @param str The string to be encrypted
	 * @param key The encryption key
	 * @return The encrypted string
	 */

	private String encrypt(String str, String key) {
		String result = "";
		str = str.toUpperCase();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (Character.isLetter(ch)) {
				ch = key.charAt(ch - 'A');
			}
			result += ch;
		}
		return result;
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
