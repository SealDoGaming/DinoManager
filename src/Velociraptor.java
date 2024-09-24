
public class Velociraptor {
	private String name;
	private int numHD;
	private static int sizeHD = 4;
	private static int bonusHP = 3;
	private static Attack biteAttack = new Attack(1,6,4,2);
	private static Attack clawAttack = new Attack(1,4,4,2);
	
	
	
	public Velociraptor(String name,int numHD) {
		this.name = name;
		this.numHD = numHD;
		
		
	}
	public String getName() {
		return name;
	}
	public int getNumHD() {
		return numHD;
	}
	public String toString() {
		return name;
	}
	public static Attack getBiteAttack() {
		return biteAttack;
	}
	public static  Attack getClawAttack() {
		return clawAttack;
	}
	public static int getHDSize() {
		return sizeHD;
	}
	public static int getBonusHP() {
		return bonusHP;
	}
	
}
