import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

abstract class ATile {
	public Character storeC;

	Character letter;

	public ATile(Character letter) {
		this.letter = letter;
		this.storeC = ' ';
	}

	boolean correct(Character c) {
		return this.letter.equals(c);
	}

	abstract Color color(String state);

	public void update() {

	}

	public void check(Character c) {

	}
	
	public int bonusPoints() {
		return 0;
	}
	
	public int exp() {
		return 0;
	}

}

class BaseTile extends ATile {

	BaseTile(Character letter) {
		super(letter);
	}

	Color color(String state) {
		if (state.equals("green")) {
			return Color.green;
		} else if (state.equals("yellow")) {
			return Color.yellow;
		} else {
			return Color.gray;
		}
	}
	
	public int exp() {
		return 5;
	}

}

class InverseTile extends ATile {

	Character letter;

	InverseTile(Character letter) {
		super(letter);
	}

	Color color(String state) {
		if (state.equals("green")) {
			return Color.gray;
		} else if (state.equals("yellow")) {
			return Color.yellow;
		} else {
			return Color.green;
		}
	}
	
	public int bonusPoints() {
		return 500;
	}
	
	public int exp() {
		return 100;
	}

}

class CountdownTile extends ATile {

	int timer;
	Random r = new Random();

	CountdownTile(Character letter) {
		super(letter);
		this.timer = this.r.nextInt(4);
	}

	boolean correct(Character c) {
		return this.letter.equals(c) && this.timer <= 0;
	}

	Color color(String state) {

		if (timer > 0) {
			return Color.orange;
		} else if (state.equals("green")) {
			return Color.green;
		} else if (state.equals("yellow")) {
			return Color.yellow;
		} else {
			return Color.gray;
		}
	}

	public void update() {
		timer = timer - 1;
	}
	
	public int bonusPoints() {
		return 100;
	}
	
	public int exp() {
		return 50;
	}

}

class VolaTile extends ATile {

	Random r = new Random();

	boolean triggered;
	boolean turnBlack;
	String targetLetter;

	VolaTile(Character letter) {
		super(letter);
		this.triggered = false;
		this.turnBlack = false;
		this.targetLetter = new Utils().alphabet.get(r.nextInt(new Utils().alphabet.size()));
	}

	boolean correct(Character c) {
		return this.letter.equals(c);
	}

	Color color(String state) {

		if (this.triggered && !this.turnBlack) {
			this.turnBlack = true;
			return Color.black;
		} else if (this.triggered && this.turnBlack) {
			return Color.white;
		} else if (state.equals("green")) {
			return Color.green;
		} else if (state.equals("yellow")) {
			return Color.yellow;
		} else {
			return Color.gray;
		}
	}

	public void check(Character c) {
		System.out.println(this.targetLetter);
		if (c.toString().equals(this.targetLetter)) {
			this.triggered = true;
		}
	}
	
	public int bonusPoints() {
		return 30;
	}
	
	public int exp() {
		return 25;
	}

}

class HeatTile extends ATile {

	String guessed;

	HeatTile(Character letter) {
		super(letter);
		this.guessed = "";
	}

	Color color(String state) {
		double distance = Math.abs(new Utils().alphabet.indexOf(this.letter.toString())
				- new Utils().alphabet.indexOf(this.guessed.toString()));

		if (state.equals("green")) {
			return Color.green;
		} else {
			return new Color(255 - (int) (distance / 26 * 255), 0, 0 + (int) (distance / 26 * 255));
		}

	}

	public void check(Character c) {
		this.guessed = c.toString();
	}
	
	public int bonusPoints() {
		return 100;
	}
	
	public int exp() {
		return 50;
	}

}

class FadeTile extends ATile {

	int fade;

	FadeTile(Character letter) {
		super(letter);
		this.fade = 0;
	}

	Color color(String state) {

		if (state.equals("green")) {
			return Color.green;
			//return new Color(55 + (200 / (4 - fade)), 255, 55 + (200 / (4 - fade)));
		} else if (state.equals("yellow")) {
			return new Color(255, 255, 55 + (200 / (4 - fade)));
		} else {
			return new Color(128 + (127 / (4 - fade)), 128 + (127 / (4 - fade)), 128 + (127 / (4 - fade)));
		}

	}

	public void update() {
		if (fade < 3) {
			fade += 1;
		}
	}
	
	public int bonusPoints() {
		return 50;
	}
	
	public int exp() {
		return 30;
	}

}

class AlternatorTile extends ATile {

	int turn;
	Random r = new Random();

	AlternatorTile(Character letter) {
		super(letter);
		this.turn = r.nextInt(4);
	}

	boolean correct(Character c) {
		return this.letter.equals(c);
	}

	Color color(String state) {
		if (state.equals("green")) {
			return Color.green;
		} else if (turn%2 != 0) {
			return Color.white;
		} else if (state.equals("yellow")) {
			return Color.yellow;
		} else {
			return Color.gray;
		}
	}

	public void update() {
		turn+=1;
	}
	
	public int bonusPoints() {
		return 100;
	}
	
	public int exp() {
		return 50;
	}

}

class LockTile extends ATile {

	int keyIndex;
	int currIndex;
	ArrayList<ArrayList<Pair<Character, Color>>> guesses;
	ArrayList<ATile> answer;
	String storeState;

	boolean locked;
	Random r = new Random();
	
	LockTile(Character letter, ArrayList<ArrayList<Pair<Character, Color>>> guesses, ArrayList<ATile> answer, int currIndex) {
		super(letter);
		this.storeState = "";
		this.locked = true;
		this.keyIndex = r.nextInt(5);
		this.currIndex = currIndex;
		this.guesses = guesses;
		this.answer = answer;
		
		
	}
	
	boolean correct(Character c) {
		while(this.answer.get(keyIndex).equals(this) || (this.answer.get(keyIndex) instanceof LockTile)) {
			this.keyIndex = r.nextInt(5);
		}
		return this.letter.equals(c);
	}

	public void update() {
		
		if(guesses.size() > 0) {
			if(answer.get(keyIndex).correct(guesses.get(guesses.size() - 1).get(keyIndex).v1)) {
				this.locked = false;
				guesses.get(guesses.size() - 1).get(currIndex).v1 = storeC;
				guesses.get(guesses.size() - 1).get(currIndex).v2 = this.color(storeState);
			}
		}
		
	}
	
	Color color(String state) {
		this.storeState = state;
		
		if(this.locked) {
			return Color.cyan;
		}else if (state.equals("green")) {
			return Color.green;
		} else if (state.equals("yellow")) {
			return Color.yellow;
		} else {
			return Color.gray;
		}
	}
	
	public int bonusPoints() {
		return 250;
	}

	public int exp() {
		return 150;
	}
	
}
