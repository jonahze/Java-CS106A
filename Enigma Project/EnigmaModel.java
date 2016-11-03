/*
 * File: EnigmaModel.java
 * ----------------------
 * This file is a class the models the german enigma used in WWII. It
 * has 5 possible rotors that can be used three at a time in any order
 * as a chain of substitution ciphers, along with a reflector panel. Also
 * every time a key is pressed the first rotor rotates, and then every
 * 26 times that rotates the second one does and so on, further changing 
 * the cipher for each letter
 */

public class EnigmaModel {

	/**
	 * Creates a new object that models the operation of an Enigma machine.
	 * By default, the rotor order is initialized to 123, which indicates
	 * that stock rotors 1, 2, and 3 are used for the slow, medium, and
	 * fast rotor positions, respectively.  The rotor setting is initialized
	 * to "AAA".
	 */

	public EnigmaModel() {
		rotorOrder = 123;
		rotorSetting = "AAA";
	}

	/**
	 * Sets the rotor order for the Enigma machine.  The rotor order is
	 * specified as a three-digit integer giving the numbers of the stock
	 * rotors to use.  For example, calling setRotorOrder(513) uses stock
	 * rotor 5 as the slow rotor, stock rotor 1 as the medium rotor, and
	 * stock rotor 3 as the fast rotor.  This method returns true if the
	 * argument specifies a legal rotor order (three digits in the range
	 * 1 to 5 with no duplication) and false otherwise.
	 *
	 * @param order A three-digit integer specifying the rotor order
	 * @return A Boolean value indicating whether the rotor order is legal
	 */

	public boolean setRotorOrder(int order) {
		rotorOrder = order;
		if (rotorOrderIsLegal()) {
			return true;
		} else return false;   
	}

	/**
	 * Establishes the rotor setting for the Enigma machine.  A legal rotor
	 * setting must be a string of three uppercase letters.  This method
	 * returns true if the argument is a legal setting and false otherwise.
	 *
	 * @param str The rotor settings
	 * @return A Boolean value indicating whether the rotor setting is legal
	 */

	public boolean setRotorSetting(String setting) {
		setting = setting.toUpperCase();
		if (rotorSettingIsLegal(setting)) {
			rotorSetting = setting;
			return true;
		} else return false;
	}

	/**
	 * Gets the current rotor setting for the Enigma machine.
	 *
	 * @return The current rotor setting
	 */

	public String getRotorSetting() {
		return rotorSetting; 
	}

	/**
	 * Encrypts a string by passing each letter through the various rotors
	 * of the Enigma machine.  All letters in the string are converted to
	 * uppercase, and the rotors of the Enigma machine are advanced before
	 * translating the letter.  If a character in the plaintext string is
	 * not a letter, the rotors do not advance and the character is simply
	 * copied to the output unchanged.
	 *
	 * @param plaintext The input plaintext string
	 * @return The output ciphertext string
	 */

	public String encrypt(String plaintext) {
		String result = "";
		initRotors();
		plaintext = plaintext.toUpperCase();
		for(int i = 0; i < plaintext.length(); i++) {
			char ch = plaintext.charAt(i);
			if (Character.isLetter(ch)) {
				rotorTurning();
				ch = getEncryptedLetter(ch);
			}
			result += ch;
		}
		return result;
	}

	/* Private methods */

	/*Method: rotorOrderIsLegal()*/
	/*
	 * splits the rotor order integer into three different integers which
	 * correspond to each rotor, the fast, medium, and slow. It then checks
	 * to see if it is a legal combination of rotors by checking if there
	 * are any repeats and if they are all in between 1 and 5. It returns
	 * true if the conditions are met, false otherwise.
	 */
	private boolean rotorOrderIsLegal() {
		int order = rotorOrder;
		fastInt = order % 10;
		order /= 10;
		medInt = order % 10;
		order /= 10;
		slowInt = order;
		if (fastInt == medInt || fastInt == slowInt || medInt == slowInt){
			return false;
		}
		if (fastInt <= 5 && fastInt >= 1 && medInt <= 5 && medInt >= 1 && slowInt <= 5 && slowInt >= 1){
			return true;
		} else return false;
	}

	/*Method: rotorSettingIsLegal*/
	/*
	 * checks to see if the rotor setting given by the user is a legal 
	 * setting by making sure it only has 3 letters and returns true
	 * if the conditions are met, false otherwise. 
	 */
	private boolean rotorSettingIsLegal(String setting) {
		if(setting.length() != 3) {
			return false;
		}
		for (int i = 0; i < 3; i ++) {
			char ch = setting.charAt(i);
			if (!Character.isLetter(ch)) return false;
		}
		return true;
	}

	/*Method: initRotors()*/
	/*
	 * sets the rotors up to their initial values by first getting the 
	 * integer for each rotor and using a method to match it with the 
	 * specific stock rotor. Then it uses the rotorSetting string to get
	 * the character value for each rotor and then advances the rotor the
	 * appropriate number of times for that character
	 */
	private void initRotors() {
		fastRotor = getKey(fastInt);
		mediumRotor = getKey(medInt);
		slowRotor = getKey(slowInt);
		for(int i = 0; i < (int)(rotorSetting.charAt(2) - 'A'); i++) {
			fastRotor = advanceRotor(fastRotor);
		}
		for(int i = 0; i < (int)(rotorSetting.charAt(1) - 'A'); i++) {
			mediumRotor = advanceRotor(mediumRotor);
		}
		for(int i = 0; i < (int)(rotorSetting.charAt(0) - 'A'); i++) {
			slowRotor = advanceRotor(slowRotor);
		}
	}

	/*Method: getKey()*/
	/*
	 * returns the correct stock rotor string by using the value of the 
	 * rotor integer in a switch statement.
	 */
	private String getKey(int i) {
		switch (i) {
		case 1: return STOCK_ROTOR_1;
		case 2: return STOCK_ROTOR_2;
		case 3: return STOCK_ROTOR_3;
		case 4: return STOCK_ROTOR_4;
		case 5: return STOCK_ROTOR_5;
		default: return null;
		}
	}

	/*Method: advanceRotor()*/
	/*
	 * returns a string of a rotor advanced forward by one turn by first
	 * taking the first letter off the front and adding it on the back 
	 * and then missing the value of each letter by one to make it the 
	 * letter before it in the alphabet
	 */
	private String advanceRotor(String rotor) {
		String firstResult = "";
		char last = rotor.charAt(0);
		for(int i = 1; i < rotor.length(); i ++) {
			char ch = rotor.charAt(i);
			firstResult += ch;
		}
		firstResult += last;
		String finalResult = "";
		for(int i = 0; i < firstResult.length(); i ++) {
			char ch = firstResult.charAt(i);
			if (ch == 'A') {
				finalResult += (char)('Z');
			} else finalResult += (char)(ch - 1);
		}
		return finalResult;
	}

	/*Method: setSpecificRotor*/
	/*
	 * takes the current string of rotor setting and changes one of the 
	 * settings to coincide with the specific rotor and character it 
	 * needs to set it to. It then returns the new setting. Useful for 
	 * keeping track of the overall setting
	 */
	private String setSpecificRotor(int rotor, char letter) {
		String result = "";
		for(int i = 0; i < 3; i++) {
			char ch = rotorSetting.charAt(i);
			if( i == rotor) {
				ch = letter;
			}
			result += ch;
		}
		return result;
	}

	/*Method: rotorTurning()*/
	/*
	 * goes through the turning of the rotors each time a letter is 
	 * pressed. It resets the rotorSetting string in order to keep track
	 * of the current rotor settings and it checks to see if the rotor 
	 * about to be turned is on Z so that it knows to reset that rotor to
	 * A and subsequently turn the next rotor.
	 */
	private void rotorTurning() {
		if(rotorSetting.charAt(2) == 'Z') {
			fastRotor = getKey(fastInt);
			rotorSetting = setSpecificRotor(2, 'A');
			if(rotorSetting.charAt(1) == 'Z') {
				mediumRotor = getKey(medInt);
				rotorSetting = setSpecificRotor(1, 'A');
				if(rotorSetting.charAt(0) == 'Z') {
					slowRotor = getKey(slowInt);
					rotorSetting = setSpecificRotor(0, 'A');
				} else {
					slowRotor = advanceRotor(slowRotor);
					rotorSetting = setSpecificRotor(0, (char)(rotorSetting.charAt(0) + 1));
				}
			} else {
				mediumRotor = advanceRotor(mediumRotor);
				rotorSetting = setSpecificRotor(1, (char)(rotorSetting.charAt(1) + 1));
			}
		} else {
			fastRotor = advanceRotor(fastRotor);
			rotorSetting = setSpecificRotor(2, (char)(rotorSetting.charAt(2) + 1));
		}
	}

	/*Method: getEncryptedLetter()*/
	/*
	 * returns the encrypted value of a letter by taking it and passing 
	 * it through each rotor, the reflector panel, and the each inverted
	 * rotor in reverse order, thereby returning the correctly ciphered 
	 * letter
	 */
	private char getEncryptedLetter(char ch) {
		ch = fastRotor.charAt(ch - 'A');
		ch = mediumRotor.charAt(ch - 'A');
		ch = slowRotor.charAt(ch - 'A');
		ch = REFLECTOR.charAt(ch - 'A');
		ch = invertRotor(slowRotor).charAt(ch - 'A');
		ch = invertRotor(mediumRotor).charAt(ch - 'A');
		ch = invertRotor(fastRotor).charAt(ch - 'A');
		return ch;
	}
	
	/*Method: invertRotor()*/
	/*
	 * returns the inverted key value of an entered rotor by going 
	 * through each letter, finding its position on the line, then making
	 * a new rotor with the distance added onto A for each character, 
	 * thereby inverted the string.
	 */
	private String invertRotor(String rotor) {
		String invertedRotor = "";
		for(int i = 0; i < 26; i++) {
			int distance = rotor.indexOf('A' + i);
			invertedRotor += (char)('A' + distance);
		}
		return invertedRotor;
	}

	/* Private instance variables */
	private int rotorOrder; //three digit rotor order integer
	private int fastInt; //the integer of the fast rotor
	private int medInt; //the integer of the medium rotor
	private int slowInt; //the integer of the slow rotor
	private String rotorSetting; //the string with the current setting for the three rotors
	private String fastRotor; //the string with the current key for the fast rotor
	private String mediumRotor; //the string with the current key for the medium rotor
	private String slowRotor; //the string with the current key for the slow rotor


	/* Private constants */

	/**
	 * The German Enigma machines were supplied with a stock of five rotors,
	 * although the required part of the assignment uses only the first three.
	 * Each rotor is represented by a string of 26 letters that shows how the
	 * letters in the alphabet are mapped to new letters as the current in the
	 * Enigma machine flows across the rotor from right to left.  For example,
	 * the STOCK_ROTOR_1 string ("EKMFLGDQVZNTOWYHXUSPAIBRCJ") indicates the
	 * following mapping when it is in its initial position:
	 *
	 *    A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
	 *    | | | | | | | | | | | | | | | | | | | | | | | | | |
	 *    E K M F L G D Q V Z N T O W Y H X U S P A I B R C J
	 *
	 * As the rotor advances, the permutation shifts by one position.  For
	 * example, after this rotor advances, the bottom line of this transformation
	 * is shifted one position to the left, with the E wrapping around to the
	 * other side, as follows:
	 *
	 *    A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
	 *    | | | | | | | | | | | | | | | | | | | | | | | | | |
	 *    K M F L G D Q V Z N T O W Y H X U S P A I B R C J E
	 *
	 * Whenever the rotor setting advances past Z, the next rotor advances
	 * one position.
	 */

	private static final String STOCK_ROTOR_1 = "EKMFLGDQVZNTOWYHXUSPAIBRCJ";
	private static final String STOCK_ROTOR_2 = "AJDKSIRUXBLHWTMCQGZNPYFVOE";
	private static final String STOCK_ROTOR_3 = "BDFHJLCPRTXVZNYEIWGAKMUSQO";
	private static final String STOCK_ROTOR_4 = "ESOVPZJAYQUIRHXLNFTGKDCMWB";
	private static final String STOCK_ROTOR_5 = "VZBRGITYUPSDNHLXAWMJQOFECK";

	/*
	 * The Enigma reflector is also a 26-character string that works just like
	 * the rotors except for the fact that it stays in one position and never
	 * advances.  The reflector setting of "IXUHFEZDAOMTKQJWNSRLCYPBVG"
	 * therefore means that a signal coming into the reflector on the wire
	 * shown at the top of the following translation table will go out again
	 * on the letter at the bottom:
	 *
	 *    A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
	 *    | | | | | | | | | | | | | | | | | | | | | | | | | |
	 *    I X U H F E Z D A O M T K Q J W N S R L C Y P B V G
	 *
	 * Note that the reflector is symmetric.  If A is transformed to I, then
	 * I is transformed to A.
	 */

	private static final String REFLECTOR = "IXUHFEZDAOMTKQJWNSRLCYPBVG";

}
