import java.awt.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import tester.Tester;

class Pair<A, B> {
	A v1;
	B v2;

	Pair(A v1, B v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	String format() {
		return "hi";
	}

}

public class GameGrid extends World {

	int optimal = 1000;
	int guessNum = 0;
	int optimalSet = 1000;
	int tempBonus = 1;
	int prestigeLevel;
	int maxturns;
	int exp;
	double multi;
	int expStore;
	int level;
	ArrayList<Integer> levels;
	int points;
	int pointStore;
	boolean wordFound;
	boolean critScored;
	ArrayList<AItem> inventory = new ArrayList<>();
	ArrayList<Character> answer = new ArrayList<>();
	ArrayList<Character> currGuess = new ArrayList<>();
	ArrayList<String> prestigeIcon = new ArrayList<>(Arrays.asList("", "I 🍏", "II 🍐", "III 🍊", "IV 🍓",
			"V 🥭", "VI 🥝", "VII 🍒", "VIII 🍋","IX 🍌",  "X 🍇", "XI 🍑", "XII 🍍", "XIII 🍈",
			"XIV 🍉", "XV 🧀", "XVI 🥞", "XVII 🌮", "XVIII 🍗", "XIX 🍕", "XX 🍔"));
	
	Random tr = new Random();
	
	ArrayList<ATile> tileAnswer = new ArrayList<>();
	ArrayList<ArrayList<Pair<Character, Color>>> guesses = new ArrayList<>();

	GameGrid() {
		this.prestigeLevel = 0;
		
		this.exp = 0;
		if(tr.nextInt(10) == 1) {
			this.maxturns = 7;
		}else {
			this.maxturns = 6;
		}
		
		this.multi = 1.0;
		this.expStore = 0;
		this.level = 0;
		this.levels = new ArrayList<>(Arrays.asList(50, 100, 150, 200, 500, 1000, 1500, 2000, 2500, 3000, 4000, 5000, 6000, 7000,
				8000, 9000, 10000, 12000, 14000, 16000, 18000, 20000, 25000, 30000, 35000, 40000, 50000, 60000, 70000,
				80000, 90000, 100000, 120000, 140000, 160000, 180000, 200000, 250000, 300000, 400000, 450000, 500000,
				600000, 700000, 800000, 900000, 1000000));
		this.critScored = false;
		this.wordFound = false;
		this.points = 0;
		this.pointStore = 0;
		Random r = new Random();
		String tempAnswer = new Utils().answers.get(r.nextInt(new Utils().answers.size()));
		for (int i = 0; i < tempAnswer.length(); i++) {
			answer.add(tempAnswer.charAt(i));
		}

		int index = 0;

		for (Character c : answer) {
			ArrayList<ATile> choices = new ArrayList<ATile>(Arrays.asList(new BaseTile(c), new BaseTile(c),
					new CountdownTile(c), new VolaTile(c), new HeatTile(c), new FadeTile(c), new AlternatorTile(c),
					new LockTile(c, guesses, tileAnswer, index)));
			if (r.nextInt(15) == 1) {
				tileAnswer.add(new InverseTile(c));
			} else {
				tileAnswer.add(choices.get(r.nextInt(choices.size())));

				// tileAnswer.add(choices.get(7));
			}
			// tileAnswer.add(new AlternatorTile(c));
			index += 1;
		}

	}

	@Override
	public WorldScene makeScene() {

		int guessOffset = guesses.size();

		int offset = 0;
		WorldScene base = new WorldScene(0, 0);

		WorldImage totalPoints = new TextImage("Score: " + points, 20, Color.black).movePinholeTo(new Posn(0, 0));
		base.placeImageXY(totalPoints, 50, 10);

		WorldImage curr = new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
		for (Character c : currGuess) {
			offset += 1;
			curr = new BesideImage(curr,
					new OverlayImage(new TextImage(c.toString(), 50, Color.white),
							new OverlayImage(new RectangleImage(50, 50, OutlineMode.OUTLINE, Color.black),
									new RectangleImage(50, 50, OutlineMode.SOLID, Color.gray))));
		}

		curr = curr.movePinholeTo(new Posn((offset - 1) * -50, 0));

		offset = 0;

		int offset2 = 0;

		base.placeImageXY(curr, 175, 80 + guessOffset * 61);

		WorldImage glist = new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);

		for (ArrayList<Pair<Character, Color>> g : guesses) {
			offset2 += 1;
			WorldImage guessRow = new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);

			for (Pair<Character, Color> p : g) {
				offset += 1;

				guessRow = new BesideImage(guessRow,
						new OverlayImage(new TextImage(p.v1.toString(), 50, Color.white),
								new OverlayImage(new RectangleImage(50, 50, OutlineMode.OUTLINE, Color.black),
										new RectangleImage(50, 50, OutlineMode.SOLID, p.v2))));
			}

			offset = 0;

			guessRow = guessRow.movePinholeTo(new Posn((offset - 1) * -50, 0));

			glist = new AboveImage(glist, guessRow);

		}

		glist = glist.movePinholeTo(new Posn(0, (offset2 - 1) * -61));

		base.placeImageXY(glist, 300, 50);

		String p = "";
		if(level >= 9) {
			p = " (Prestige Available!)";
		}
		
		WorldImage xpBar = new AboveImage(
				new AboveImage(new TextImage(this.prestigeIcon.get(this.prestigeLevel) + " Level: " + (this.level + 1) + p, 20, Color.black),
						new TextImage("Exp: " + this.exp + "/" + this.levels.get(this.level), 20, Color.black)),
				new OverlayImage(
						new RectangleImage(
								((int) ((double) (400.0 / ((double) this.levels.get(this.level)) * ((double) exp)))),
								25, OutlineMode.SOLID, Color.magenta),
						new RectangleImage(400, 25, OutlineMode.SOLID, Color.gray)));

		base.placeImageXY(xpBar, 300, 550);

		
		WorldImage inv = new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
		int itemIndex = 1;
		for (AItem i : inventory) {
			
			if(itemIndex == 10) {
				itemIndex = 0;
			}
			
			inv = new BesideImage(inv,
					new AboveImage(new OverlayImage(new TextImage(i.render(), 25, Color.black), 
							new RectangleImage(30, 30, OutlineMode.OUTLINE, Color.black)),
							new TextImage(""  + itemIndex, 15, Color.black)));
			itemIndex += 1;
		}
		
		inv = new AboveImage(new TextImage("Item Inventory	", 25, Color.black), inv);
		
		base.placeImageXY(inv, 300, 650);

		WorldImage opt = new TextImage("Optimal Comparison: " + ((double) ((double) this.optimal/10.0)) + "%", 18, Color.black);
		
		base.placeImageXY(opt, 300, 30);
		
		if(this.maxturns >= 7) {
			base.placeImageXY(new CircleImage(20, OutlineMode.SOLID, Color.green), 125, 445);
		}else {
			base.placeImageXY(new CircleImage(20, OutlineMode.SOLID, Color.red), 125, 445);
		}
		
		base.placeImageXY(new RectangleImage(250, 50, OutlineMode.OUTLINE, Color.black), 300, 445);
		base.placeImageXY(new RectangleImage(250, 50, OutlineMode.OUTLINE, Color.black), 300, 445 - 61);
		base.placeImageXY(new RectangleImage(250, 50, OutlineMode.OUTLINE, Color.black), 300, 445 - 122);
		base.placeImageXY(new RectangleImage(250, 50, OutlineMode.OUTLINE, Color.black), 300, 445 - 183);
		base.placeImageXY(new RectangleImage(250, 50, OutlineMode.OUTLINE, Color.black), 300, 445 - 244);
		base.placeImageXY(new RectangleImage(250, 50, OutlineMode.OUTLINE, Color.black), 300, 445 - 305);
		base.placeImageXY(new RectangleImage(250, 50, OutlineMode.OUTLINE, Color.black), 300, 445 - 366);
		base.placeImageXY(new TextImage("Guess " + this.guessNum + "/6", 18, Color.black), 550, 25);




		return base;
	}

	public void onTick() {
		
		this.guessNum = guesses.size() + 1;
		if(guessNum > 7) {
			guessNum = 7;
		}
		
		if(optimal < optimalSet) {
			optimal += 1;
		}else if(optimal > optimalSet) {
			optimal -= 1;
		}
		
		if (pointStore > 0) {	
			points = points + 10;
			pointStore = pointStore - 10;
		}

		if (pointStore < 0) {
			if (points > 0) {
				points = points - 10;
			}
			pointStore = pointStore + 10;
		}

		if (exp >= this.levels.get(this.level)) {
			this.level = level + 1;
			this.exp = 0;
		}

		if (expStore > 0) {
			exp = exp + 1;
			expStore = expStore - 1;
		}

		if (this.points < 500) {
			this.multi = 1;
		} else if (this.points < 2000) {
			this.multi = 1.2;
		} else if (this.points < 5000) {
			this.multi = 1.5;
		} else if (this.points < 10000) {
			this.multi = 2.0;
		} else if (this.points < 25000) {
			this.multi = 3.0;
		} else {
			this.multi = 5.0;
		}

	}

	public void onKeyEvent(String key) {

		if (new Utils().alphabet.contains(key) && this.guesses.size() < maxturns) {
			if (currGuess.size() < 5) {
				currGuess.add(key.charAt(0));
			}
		} else if (key.equals("enter")) {

			System.out.println(answer);

			String temp = "";
			for (Character c : currGuess) {
				temp = temp + c;
			}

			if (new Utils().answers.contains(temp) && guesses.size() < maxturns && !this.wordFound) {
				
				Random expr = new Random();
				
				int deduct = expr.nextInt(300);
				
				optimalSet -= deduct;
				
				if(optimalSet < -100) {
					optimalSet = -100;
				}
				
				
				expStore = expStore + ((int) ((expr.nextInt(10) + expr.nextInt(tempBonus)) * this.multi));

				ArrayList<String> code = new Utils().compareAnswer(currGuess, tileAnswer);
				ArrayList<Pair<Character, Color>> guess = new ArrayList<>();

				for (int i = 0; i < currGuess.size(); i++) {
					tileAnswer.get(i).storeC = currGuess.get(i);
				}

				if (!code.contains("grey") && code.contains("yellow") && !this.critScored) {
					this.critScored = true;
					this.pointStore = this.pointStore + 500;
				}

				if(!code.contains("green") && !code.contains("yellow")) {
					
					optimalSet = optimalSet - expr.nextInt(500);
					if(optimalSet < -100) {
						optimalSet = -100;
					}
					
					for(int i = 0; i < 5; i ++) {
						guess.add(new Pair<>(' ', Color.black));
					}
					
				}else {
					for (int i = 0; i < currGuess.size(); i++) {
	
						if (tileAnswer.get(i) instanceof LockTile && ((LockTile) tileAnswer.get(i)).locked) {
							int ki = ((LockTile) tileAnswer.get(i)).keyIndex;
							char num = ' ';
							if (ki == 0) {
								num = '1';
							} else if (ki == 1) {
								num = '2';
							} else if (ki == 2) {
								num = '3';
							} else if (ki == 3) {
								num = '4';
							} else if (ki == 4) {
								num = '5';
							}
	
							guess.add(new Pair<>(num, tileAnswer.get(i).color(code.get(i))));
						} else {
							guess.add(new Pair<>(currGuess.get(i), tileAnswer.get(i).color(code.get(i))));
						}
					}
				}

				guesses.add(guess);
				

				for (ATile t : tileAnswer) {
					t.update();
				}

				if (new Utils().correctCount(code) == 5) {
					
					this.optimalSet = this.optimal + expr.nextInt(50);
					
					if(this.guesses.size() == 1) {
						this.optimalSet = 1000 + expr.nextInt(100);
					}
					if(inventory.size() < 10) {
						ArrayList<AItem> choices = new ArrayList<>(Arrays.asList(new BonusExp(), new ResetWord(), new ExpCapsule(),
								new Seventh()));
						Random ir = new Random();
						
						inventory.add(choices.get(ir.nextInt(4)));
						//System.out.print(inventory);
					}
					
					this.wordFound = true;
					pointStore = pointStore + 50 + (500 * (6 - guesses.size()));

					for (ATile t : tileAnswer) {
						pointStore = pointStore + t.bonusPoints();
						expStore = expStore + ((int) (t.exp() * this.multi));
					}
				} else {
					if (guesses.size() == maxturns) {
						pointStore = pointStore - 1000;
					}
				}

				currGuess = new ArrayList<>();
			}

		} else if (key.equals("backspace")) {
			if (currGuess.size() > 0) {
				currGuess.remove(currGuess.size() - 1);
			}
		} else if (key.equals("control")) {
			
			this.optimal = 1000;
			this.optimalSet = 1000;
			if(this.guesses.size() < maxturns && !this.wordFound) {
				this.pointStore = this.pointStore - 1000;
			}
			
			tempBonus = 1;
			
			if(tr.nextInt(10) == 1) {
				this.maxturns = 7;
			}else {
				this.maxturns = 6;
			}
			
			guesses = new ArrayList<>();
			this.critScored = false;
			currGuess = new ArrayList<>();
			answer = new ArrayList<>();
			tileAnswer = new ArrayList<ATile>();
			this.wordFound = false;

			Random r = new Random();

			String tempAnswer = new Utils().answers.get(r.nextInt(new Utils().answers.size()));
			for (int i = 0; i < tempAnswer.length(); i++) {
				answer.add(tempAnswer.charAt(i));
			}

			int index = 0;
			for (Character c : answer) {
				ArrayList<ATile> choices = new ArrayList<ATile>(Arrays.asList(new BaseTile(c), new BaseTile(c),
						new CountdownTile(c), new VolaTile(c), new HeatTile(c), new FadeTile(c), new AlternatorTile(c),
						new LockTile(c, guesses, tileAnswer, index)));
				if (r.nextInt(15) == 1) {
					tileAnswer.add(new InverseTile(c));
				} else {
					tileAnswer.add(choices.get(r.nextInt(choices.size())));
				}
				// tileAnswer.add(new AlternatorTile(c));
				index += 1;
			}
		}else if(key.equals("shift")) {
			if(this.level >= 9) {
				this.prestigeLevel = this.prestigeLevel + 1;
				this.level = 0;
				this.exp = 0;
				this.expStore = 0;
			}
		}else if(new Utils().nums.contains(key)) {
			int index2 = Integer.parseInt(key);
			if(index2 == 0) {
				index2 = 10;
			}
			
			if(index2 <= inventory.size()) {
				String action = inventory.get(index2 - 1).use();
				inventory.remove(index2 - 1);
				
				if(action.equals("reset")) {
					this.optimal = 1000;
					this.optimalSet = 1000;
					if(tr.nextInt(10) == 1) {
						this.maxturns = 7;
					}else {
						this.maxturns = 6;
					}
					
					tempBonus = 1;
					
					guesses = new ArrayList<>();
					this.critScored = false;
					currGuess = new ArrayList<>();
					answer = new ArrayList<>();
					tileAnswer = new ArrayList<ATile>();
					this.wordFound = false;

					Random r = new Random();

					String tempAnswer = new Utils().answers.get(r.nextInt(new Utils().answers.size()));
					for (int i = 0; i < tempAnswer.length(); i++) {
						answer.add(tempAnswer.charAt(i));
					}

					int index = 0;
					for (Character c : answer) {
						ArrayList<ATile> choices = new ArrayList<ATile>(Arrays.asList(new BaseTile(c), new BaseTile(c),
								new CountdownTile(c), new VolaTile(c), new HeatTile(c), new FadeTile(c), new AlternatorTile(c),
								new LockTile(c, guesses, tileAnswer, index)));
						if (r.nextInt(15) == 1) {
							tileAnswer.add(new InverseTile(c));
						} else {
							tileAnswer.add(choices.get(r.nextInt(choices.size())));
						}
						// tileAnswer.add(new AlternatorTile(c));
						index += 1;
				}
				}else if(action.equals("expBonus")) {
					tempBonus = 50;
				}else if(action.equals("instantExp")) {
					expStore = (int) (expStore + (tr.nextInt(100) * this.multi));
				}else if(action.equals("seventh")) {
					this.maxturns = 7;
				}
					
				
			}
		}

	}

}


class ExamplesPowerdle {

	void testBigBang(Tester t) {
		GameGrid w = new GameGrid();
		w.bigBang(600, 800, 0.001);
	}

}
