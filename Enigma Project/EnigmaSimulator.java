/*
 * File: EnigmaSimulator.java
 * --------------------------
 * This is the file for an enigma simulator which uses the console program
 * to enter the rotor order, the rotor setting, and the plaintext. It then
 * uses the EnigmaModel class to do all the calculations and then shows the 
 * user their completed ciphertext.
 */

import acm.program.*;

public class EnigmaSimulator extends ConsoleProgram {

	private EnigmaModel enigma = new EnigmaModel(); //enigma instance variable

	public void run() {
		setOrder();
		setSetting();
		String plaintext = readLine("Enter a plaintext line: ");
		println("The encoded ciphertext: " + enigma.encrypt(plaintext));
	}
	
	/*Method: setOrder()*/
	/*
	 * accesses the EnigmaModel class and sets the order of the rotor if
	 * given a correct ordering, if not it will continue to ask the user
	 * for a new rotor order
	 */
	private void setOrder() {
		int rotorOrder = readInt("Enter rotor order: ");
		while(!enigma.setRotorOrder(rotorOrder)) {
			println("illegal rotor order");
			rotorOrder = readInt("Enter rotor order: ");
		}
	}
	
	/*Method: setSetting()*/
	/*
	 * accesses the EnigmaModel class and sets the setting of the rotors
	 * if given a correct setting, if not it will continue to ask the user
	 * for a new rotor setting.
	 */
	private void setSetting() {
		String rotorSetting = readLine("Enter rotor setting: ");
		while(!enigma.setRotorSetting(rotorSetting)) {
			println("illegal rotor setting");
			rotorSetting = readLine("Enter rotor setting: ");
		}
	}

}
