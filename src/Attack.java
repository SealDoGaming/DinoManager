
public class Attack {
	private int damageDiceNum;
	private int damageDiceSize;
	private int hitBonus;
	private int damageBonus;
	
	public Attack(int num, int size, int hit,int damage) {
		damageDiceNum = num;
		damageDiceSize = size;
		hitBonus = hit;
		damageBonus = damage;
		
	}
	public int getDiceNum() {
		return damageDiceNum;
	}
	public int getDiceSize() {
		return damageDiceSize;
	}
	public int getHitBonus() {
		return hitBonus;
	}
	public int getDamageBonus() {
		return damageBonus;
	}
}
