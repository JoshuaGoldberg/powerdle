
abstract class AItem {

	public abstract String render();
	public abstract String use();
	
	
}

class BonusExp extends AItem{
	
	public String render() {
		return "💎";
	}
	
	public String use() {
		return "expBonus";
	}
	
	
}

class ResetWord extends AItem{
	
	public String render() {
		return "🚫";
	}
	
	public String use() {
		return "reset";
	}
	
	
}

class ExpCapsule extends AItem{
	
	public String render() {
		return "💵";
	}
	
	public String use() {
		return "instantExp";
	}
	
	
}

class Seventh extends AItem{
	
	public String render() {
		return "🆙";
	}
	
	public String use() {
		return "seventh";
	}
	
	
}
