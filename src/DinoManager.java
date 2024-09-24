import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import java.util.Random;
import java.util.Scanner;




public class DinoManager extends JFrame implements TableModelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int WIDTH=600;
	private static int HEIGHT=450;
	private static int NUM_HIT_DIE = 3;
	private static Color critColor = new Color(189, 255, 246);
	private static Color hitColor = new Color(199, 255, 189);
	
	private static Color critMissColor = new Color(255, 59, 59);
	private static Color missColor = new Color(255, 189, 189);
	public final static Color defaultColor = Color.WHITE;
	
	private String[] columnNames = {
			"Dinosaur",
            "HP",
            "Temp HP",
            "Bite Attack",
            "Bite Dmg",
            "Claw Attack",
            "Claw Dmg"
		};
	
	
	
	private String[] dinoNames = {
			"Hektor",
			"Fredrickson",
			"Alfonsator III",
			"Musturd","Tubey",
			"Tree-trunk",
			"Sir Hank",
			"Sniffles"
		};
	private int[] columnLength = {185,35,65,80,75,85,75};
	
	private String defaultFilename = "DinoList.txt";
	private String currentFilename;
	
	//
	//private ArrayList<Object[]> data = new ArrayList<Object[]>();
	private Object[] defaultData = {"",0,0,0,0,0,0};
	
	// array size number of dinos by 3. First collumn is whether a crit or miss. second and third are rolls
	private int[][][] critData;
	private boolean usingCritTables = true;
		
	private int biteColumn = 3;
	private DinoTableModel dinoModel;
	private JTable table;
	
	
	
	private JPanel mainPanel = new JPanel();
	private JPanel topPanel = new JPanel();
	private JPanel controlPanel = new JPanel();
	
	private JMenuBar settingsBar = new JMenuBar();
	private JMenu dinoMenu = new JMenu("New");
	
	private JMenuItem createNewDinos = new JMenuItem("Create New Dino-List from Scratch");
	private JMenuItem addNewDinos = new JMenuItem("Add New Dinos to Dino-List");
	private JMenuItem importNewDinos = new JMenuItem("Import Dino List");

	private JMenuItem importDefaultDinos = new JMenuItem("Import Dino List from Default");
	
	private JMenu saveMenu = new JMenu("Save");
	
	private JMenuItem saveDinoList = new JMenuItem("Save");
	private JMenuItem saveDinoListAs = new JMenuItem("Save As");
	private JMenuItem saveDinoDefault = new JMenuItem("Save to Default");
	
	private JPanel buttonPanel = new JPanel();
	
	private JPanel resistPanel = new JPanel();
	private JCheckBox pierceBox = new JCheckBox("Piercing Resistance");
	private JCheckBox slashBox = new JCheckBox("Slashing Resistance");
	
	private boolean pierceResist = false;
	private boolean slashResist = false;
	
	private JButton summonButton = new JButton("Summon Dinos");
	private JButton attackButton = new JButton("Attack");
	private JButton advantageButton = new JButton("Attack w/ Advantage");
	
	private JPanel damagePanel = new JPanel();



	private JTextField armorField = new JTextField("10",5);
	private JButton calculateButton = new JButton("Calculate");
	
	
	private JPanel rollPanel = new JPanel();
	private JLabel diceResult = new JLabel("Roll 0d6+0 and 0d4+0");
	private JLabel damageResult = new JLabel("Damage Rolled: ");
	
	private JTextArea critInfo;
	
	private Random roller = new Random();
	
	private boolean healthGen = false;
	
	public static void main(String[] args) {
		DinoManager manager = new DinoManager("D&D Dinosaur Manager");
        manager.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        manager.setVisible(true);
        
        manager.testRolls(1000000);
        manager.testRolls(10000,6);
        manager.testRolls(10000,4);
        
        //manager.testRolls(1000);
        
	}
	public DinoManager(String title) {
		super(title);
		
		
		dinoModel = new DinoTableModel(getColumnNames());
		table = new JTable(dinoModel);
		table.setSize(WIDTH,HEIGHT);
		for(int i=0;i<columnLength.length;i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(new AttackCellRenderer());
		}
		
		//summonDinos();
		setupGUI();
        
        pack();
	}
	private void setupDinos() {
		setupDinos(defaultFilename);
	}
	private void setupDinos(String filename) {
		ArrayList<String> dinoNames = new ArrayList<String>();
		File dinoList = new File(filename);
		try {
			Scanner myReader = new Scanner(dinoList);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				dinoNames.add(data);
				System.out.println(data);
			}
			String[] convertedDinoNames = new String[dinoNames.size()];
			for(int i=0; i<dinoNames.size();i++) {
				convertedDinoNames[i] = dinoNames.get(i);
			}
			critData = new int[dinoNames.size()][2][3];
			
			this.dinoNames = convertedDinoNames;
			myReader.close();
		}catch (Exception e) {
			System.out.println("An error occurred with reading");
    	}
	}
	private void saveDinosDefault() {
		saveDinos(defaultFilename);
	}
	private void saveDinos() {
		if(currentFilename != null) {

			saveDinos(currentFilename);
		}else {
			saveAsDinos();
		}
	}
	private void saveAsDinos() {
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showSaveDialog(null);
		if(returnVal ==JFileChooser.APPROVE_OPTION) {
			saveDinos(fc.getSelectedFile().getPath());
		}
	}
	private void saveDinos(String filename) {
		try {
			File dinoList = new File(filename);
			if(dinoList.createNewFile()) {
				System.out.println("File created: " + dinoList.getName());
			} else {
				System.out.println("File already exists.");
			}
		}catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
    	}
		try {
			FileWriter dinoWriter = new FileWriter(filename,false);
			for(String name:dinoNames) {
				dinoWriter.write(name+"\n");
			}
			dinoWriter.close();
			System.out.println("Successfully wrote to the file.");
		}catch (IOException e) {
			System.out.println("An error occurred with writing");
			e.printStackTrace();
    	}
			
	}
	private void setupGUI() {
		JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(this.WIDTH,scrollPane.getPreferredSize().height));
        topPanel.setLayout(new BorderLayout());
        topPanel.add(scrollPane,BorderLayout.CENTER);
        mainPanel.setLayout(new BorderLayout());
        buttonSetup();
        /*
        JPanel tempPanel;
        
        tempPanel = new JPanel();
        tempPanel.add(armorField);
        buttonPanel.add(tempPanel);
        */
        dinoMenu.add(createNewDinos);
        dinoMenu.add(addNewDinos);
        dinoMenu.add(importNewDinos);
        dinoMenu.add(importDefaultDinos);
        
        saveMenu.add(saveDinoList);
        saveMenu.add(saveDinoListAs);
        saveMenu.add(saveDinoDefault);
        
        settingsBar.add(dinoMenu);
        settingsBar.add(saveMenu);
        
        buttonPanel.add(resistPanel);
        //buttonPanel.add(summonButton);
        buttonPanel.add(attackButton);
        buttonPanel.add(advantageButton);
        
        resistPanel.setLayout(new GridLayout(2,1));
        resistPanel.add(pierceBox);
        resistPanel.add(slashBox);
        
        
        
        damagePanel.add(armorField);
        damagePanel.add(Box.createHorizontalGlue());
        damagePanel.add(calculateButton);
        damagePanel.add(Box.createHorizontalGlue());
        damagePanel.add(rollPanel);
        rollPanel.setLayout(new GridLayout(2,1));
        rollPanel.add(diceResult);
        //rollPanel.add(Box.createVerticalGlue());
        rollPanel.add(damageResult);
        

        ;
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.PAGE_AXIS));
        controlPanel.add(buttonPanel);
        controlPanel.add(damagePanel);
        
        topPanel.add(controlPanel,BorderLayout.PAGE_END);
        mainPanel.add(topPanel,BorderLayout.CENTER);
        
        critInfo = new JTextArea("--Critical Hit Results--");
        critInfo.setPreferredSize(new Dimension(160,critInfo.getPreferredSize().height));
        int borderOffset =10;
        Border insideBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        Border outsideBorder =BorderFactory.createEmptyBorder(borderOffset, borderOffset, borderOffset, borderOffset);
        critInfo.setBorder(BorderFactory.createCompoundBorder(insideBorder,outsideBorder));
        critInfo.setBackground(mainPanel.getBackground());
        mainPanel.add(critInfo,BorderLayout.LINE_END);
        
        add(mainPanel);
        setJMenuBar(settingsBar);
        tableSetup();
        //this.setSize(WIDTH, HEIGHT);
	}
	private void tableSetup() {
		int sum=0;
		        
        TableColumn column = null;
        for (int i = 0; i < columnLength.length; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnLength[i]);

        }
        /*
        System.out.println();
         for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            sum+= column.getPreferredWidth();
            System.out.println(column.getPreferredWidth());
            
        }
        System.out.println(sum);
        */
	}
	public void menuButtonSetup() {
		createNewDinos.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                
            }  
        });  
		addNewDinos.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                
            }  
        });  
		importNewDinos.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
        		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        		int returnVal = fc.showOpenDialog(null);
        		if(returnVal ==JFileChooser.APPROVE_OPTION) {
        			setupDinos(fc.getSelectedFile().getPath());
        			summonDinos();
        		}
            }  
        });  
		importDefaultDinos.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                setupDinos();
                summonDinos();
            }  
        });  
		saveDinoList.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                saveDinos();
            }  
        });  
		saveDinoListAs.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                saveAsDinos();
            }  
        });  
		saveDinoDefault.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
        		saveDinosDefault();
            }  
        });  
		
	}
	public void buttonSetup() {
		
		menuButtonSetup();
		
		
		summonButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                summonDinos();
            }  
        });  
        attackButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
                rollAttacks();
        		calculateDamage();
            }  
        });
        advantageButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
        		rollAttacks(true);
        		calculateDamage();
            }  
        });
        calculateButton.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
        		calculateDamage();
            }  
        });
        pierceBox.addItemListener(new ItemListener() {    
            public void itemStateChanged(ItemEvent e) {      
            	pierceResist = e.getStateChange()==1;
            	calculateDamage();
            }    
         });    
        slashBox.addItemListener(new ItemListener() {    
            public void itemStateChanged(ItemEvent e) {      
            	slashResist = e.getStateChange()==1;
            	calculateDamage();
            }    
         });
        /*
        armorField.getDocument().addDocumentListener(new DocumentListener() {
        	public void changedUpdate(DocumentEvent e) {
        		calculateDamage();
        	}
        	public void removeUpdate(DocumentEvent e) {
        		
        	}
        	public void insertUpdate(DocumentEvent e) {
        		
        	}
        });
        */
        armorField.addActionListener(new ActionListener(){  
        	public void actionPerformed(ActionEvent e){  
        		System.out.println(e.getActionCommand());
        		calculateDamage();
            }  
        });
       
	}
	public void summonDinos() {
		/*
		for(int i=0;i<data.size();i++) {
			for(int j = 0; j<data.get(i).length;j++) {
				System.out.print(data.get(i)[j]+", ");
			}
			System.out.println();
		}
		System.out.println();
		*/
		
		for(int i = 0; i<dinoNames.length;i++) {
			Object[] newRow = new Object[columnNames.length];
			Velociraptor dino = new Velociraptor(dinoNames[i], NUM_HIT_DIE);
			newRow[0] = dino;
			if(healthGen) {
				newRow[1] = roll(dino.getNumHD(),Velociraptor.getHDSize(),Velociraptor.getBonusHP());
			}else {
				newRow[1] = 16;
			}
			
			for(int j = 2; j<columnNames.length;j++) {
				newRow[j] = 0;
			}
			if(i==dinoModel.getRowCount()) {
				dinoModel.addRow(newRow);
			}else {
				dinoModel.setRow(i,newRow);
			}
			
		}
		if(dinoNames.length<dinoModel.getRowCount()) {
			for(int i = dinoNames.length;i<dinoModel.getRowCount();i++) {
				dinoModel.removeRow(i);
				i--;
			}
		}
		dinoModel.fireTableDataChanged();
	}
	
	public void rollAttacks() {
		rollAttacks(false);
	}
	public void rollAttacks(boolean advantage) {
		System.out.println("Rolls:");
		for(int row = 0; row<table.getRowCount();row++) {
			
			System.out.println("--"+table.getValueAt(row, 0)+"--");
			
			for(int i = 0;i<2;i++) {
				
				Velociraptor dino = (Velociraptor)dinoModel.getRow(row)[0];
				Attack attack;
				int hitBonus;
				
				if(i==0) {
					System.out.print("Bite Rolls: ");
					attack = Velociraptor.getBiteAttack();
					hitBonus = Velociraptor.getBiteAttack().getHitBonus();
				}else {
					System.out.print("Claw Rolls: ");
					attack = Velociraptor.getClawAttack();
					hitBonus = Velociraptor.getClawAttack().getHitBonus();
				}
				int roll = roll(1,20);
				if(roll==20) {
					critData[row][i][0] = 1;
					critData[row][i][1] = roll(1,100);
					System.out.print("("+critData[row][i][1]+") ");
				}else if(roll==1) {
					critData[row][i][0] = -1;
					critData[row][i][1] = roll(1,100);
					System.out.print("("+critData[row][i][1]+") ");
				}else {
					critData[row][i][0] = 0;
					critData[row][i][1] = 0;
				}
				System.out.print(roll);
				if(advantage) {
					int secondRoll = roll(1,20);
					System.out.print(" & "+secondRoll);
					
					if(secondRoll==20) {
						if(roll==1) {
							critData[row][i][1]=0;
						}
						critData[row][i][0] = 1;
						critData[row][i][2] = roll(1,100);
						System.out.print("("+critData[row][i][2]+") ");
					}else if(secondRoll==1 && roll==1) {
						critData[row][i][0] = -1;
						critData[row][i][2] = roll(1,100);
						System.out.print("("+critData[row][i][2]+") ");
					}else {
						if(roll==1) {
							critData[row][i][1]=0;
						}
						if(roll!=20) {
							critData[row][i][0] = 0;
						}
						critData[row][i][2] = 0;
					}
					
					if((secondRoll+hitBonus)>(roll+hitBonus)) {
						roll = secondRoll;
					}
					
				}
				
				if((int)dinoModel.getRow(row)[1]==0) {
					roll=-4;
				}
				table.setValueAt(roll+hitBonus, row,this.biteColumn+i*2);
				
				int damage=0;
				if((int)dinoModel.getRow(row)[1]==0) {
					damage = 0;
				}
				else if(roll==20) {
					//CRIT
					if(usingCritTables) {
						
						for(int j=1;j<3;j++) {
							int critRoll=critData[row][i][j];
							if(critRoll!=0) {
								if(critRoll>=1&&critRoll<=15) {
									damage += roll(attack.getDiceNum()*2,attack.getDiceSize(),attack.getDamageBonus());
								}else if(critRoll>=16&&critRoll<=24) {
									damage += roll(attack.getDiceNum()*3,attack.getDiceSize(),attack.getDamageBonus());
								}else if(critRoll>=25&&critRoll<=29) {
									damage += roll(attack.getDiceNum()*4,attack.getDiceSize(),attack.getDamageBonus());
								}else{
									damage += roll(attack.getDiceNum()*2,attack.getDiceSize(),attack.getDamageBonus());
								}
							}
						}
					}else {
						damage = roll(attack.getDiceNum()*2,attack.getDiceSize(),attack.getDamageBonus());
					}
					
				}else {
					damage = roll(attack.getDiceNum(),attack.getDiceSize(),attack.getDamageBonus());
				}
				
				table.setValueAt(damage, row, this.biteColumn+1+i*2);
				//table.setValueAt(0, row,this.biteColumn+1+i*2);
				System.out.println();
			}
			System.out.println();
		}

	}
	public void calculateDamage() {
		int armorclass = 0;
		int sum = 0;
		int biteDice = 0;
		int clawDice = 0;
		int biteCount = 0;
		int clawCount = 0;
		Attack attack;
		int[] critCounters = new int[11];
		int[] missCounters = new int[7];
		for(int[][] i: critData) {
			for(int[] j: i) {
				System.out.print("[");
				System.out.print(Arrays.toString(i));
				System.out.print("]");
			}
			
		}
		try {
			armorclass =  Integer.valueOf(armorField.getText());
		} catch(NumberFormatException e) {
			System.out.println("That's not a valid number");
			return;
			
		}
		System.out.println("Armor Class Test");
		for(int row=0; row<table.getRowCount();row++) {
			System.out.println("--"+((Velociraptor)dinoModel.getRow(row)[0]).getName()+"--");
			DinoTableModel model = (DinoTableModel) table.getModel();
			for(int i = 0;i<2;i++) {
				int roll =(int) table.getValueAt(row, this.biteColumn+i*2);
				if(i==0) {
					attack = Velociraptor.getBiteAttack();
					System.out.print(" Bite: ");
					
				}else {
					attack = Velociraptor.getClawAttack();
					System.out.print(" Claws: ");
				}
				//System.out.print((roll-attack.getHitBonus()) );
				
				if((roll<armorclass)&&!((roll-attack.getHitBonus()) == 20)) {
					
					if(critData[row][i][0]==-1&&usingCritTables) {
						for(int j=1;j<3;j++) {
							int critRoll = critData[row][i][j];
							if(critRoll>=1&&critRoll<=15) {
								missCounters[0]+=1;
							}else if(critRoll>=16&&critRoll<=30) {
								missCounters[1]+=1;
							}else if(critRoll>=31&&critRoll<=45) {
								missCounters[2]+=1;
							}else if(critRoll>=46&&critRoll<=55) {
								missCounters[3]+=1;
							}else if(critRoll>=56&&critRoll<=70) {
								missCounters[4]+=1;
							}else if(critRoll>=71&&critRoll<=85) {
								missCounters[5]+=1;
							}else if(critRoll>=86&&critRoll<=100){
								missCounters[6]+=1;
							}
						}
						System.out.println("Critical Miss.");
						model.setColorAt(row, this.biteColumn+i*2, DinoManager.critMissColor);
					}else {
						System.out.println("Miss.");
						model.setColorAt(row, this.biteColumn+i*2, DinoManager.missColor);
					}
					continue;
				}else if(((roll-attack.getHitBonus()) == 20)) {
					model.setColorAt(row, this.biteColumn+i*2, DinoManager.critColor);
					System.out.println("Critical Hit!");
				}else {
					model.setColorAt(row, this.biteColumn+i*2, DinoManager.hitColor);
					System.out.println("Hit.");
				}
				
				
				int multi = 1;
				int damage=0;
				
				if(usingCritTables&&critData[row][i][0]==1) {
					multi = 0;
					for(int j=1;j<3;j++) {
						int critRoll=critData[row][i][j];
						if(critRoll!=0) {
							if(critRoll>=1&&critRoll<=15) {
								multi+=2;
								critCounters[0]+=1;
							}else if(critRoll>=16&&critRoll<=24) {
								multi+=3;
								critCounters[1]+=1;
							}else if(critRoll>=25&&critRoll<=29) {
								multi+=4;
								critCounters[2]+=1;
							}else if(critRoll>=30&&critRoll<=45) {
								multi+=2;
								critCounters[3]+=1;
							}else if(critRoll>=46&&critRoll<=55) {
								multi+=2;
								critCounters[4]+=1;
							}else if(critRoll>=56&&critRoll<=66) {
								multi+=2;
								critCounters[5]+=1;
							}else if(critRoll>=67&&critRoll<=69) {
								multi+=2;
								critCounters[6]+=1;
							}else if(critRoll>=70&&critRoll<=79) {
								multi+=2;
								critCounters[7]+=1;
							}else if(critRoll>=80&&critRoll<=98) {
								multi+=2;
								critCounters[8]+=1;
							}else if(critRoll==99) {
								multi+=2;
								critCounters[9]+=1;
							}else{
								multi+=2;
								critCounters[10]+=1;
							}
						}
					}
					
				}else if((roll-attack.getHitBonus()) == 20) {
					
					multi*=2;
					
				}
				
				//damage = roll(attack.getDiceNum()*multi,attack.getDiceSize(),attack.getDamageBonus());
				damage = (int)table.getValueAt(row,this.biteColumn+1+i*2);
				
				//table.setValueAt(damage,row,this.biteColumn+1+i*2);
				if(i==0) {
					biteDice+=multi*attack.getDiceNum();
					biteCount++;
					if(pierceResist) {
						damage/=2;
					}
					
				} else {
					clawDice+=multi*attack.getDiceNum();
					clawCount++;
					if(slashResist) {
						damage/=2;
					}
				}

				
				
				
				sum+= damage;
			}
			System.out.println();
			
		}

		int biteSize = Velociraptor.getBiteAttack().getDiceSize();
		int clawSize = Velociraptor.getClawAttack().getDiceSize();
		int biteBonus = Velociraptor.getBiteAttack().getDamageBonus();
		int clawBonus = Velociraptor.getClawAttack().getDamageBonus();
		
		diceResult.setText("Roll "+biteDice+"d"+biteSize+"+"+(biteCount*biteBonus)+" and "+clawDice+"d"+clawSize+"+"+(clawCount*clawBonus));
		damageResult.setText("Damage Rolled: "+sum);
		String critResults="";
		System.out.println("--Critical Hit Results--");
		critResults+="--Critical Hit Results--"+"\n";
		for(int i=0;i<critCounters.length;i++) {
			//System.out.println(i);
			switch(i) {
			case 0:
				System.out.println("Double Damage: "+critCounters[i]);
				critResults+="Double Damage: "+critCounters[i]+"\n";
				break;
			case 1:
				System.out.println("Triple Damage: "+critCounters[i]);
				critResults+="Triple Damage: "+critCounters[i]+"\n";
				break;
			case 2:
				System.out.println("Quadruple Damage: "+critCounters[i]);
				critResults+="Quadruple Damage: "+critCounters[i]+"\n";
				break;
			case 3:
				System.out.println("Enemy Knocked Prone: "+critCounters[i]);
				critResults+="Enemy Knocked Prone: "+critCounters[i]+"\n";
				break;
			case 4:
				System.out.println("Adv on attacks: "+critCounters[i]);
				critResults+="Adv on attacks: "+critCounters[i]+"\n";
				break;
			case 5:
				System.out.println("AC Reduced by 2: "+critCounters[i]);
				critResults+="AC Reduced by 2: "+critCounters[i]+"\n";
				break;
			case 6:
				System.out.println("AC Reduced by "+Velociraptor.getBiteAttack().getHitBonus()+": "+critCounters[i]);
				critResults+="AC Reduced by "+Velociraptor.getBiteAttack().getHitBonus()+": "+critCounters[i]+"\n";
				break;
			case 7:
				System.out.println("AC OBLITERATED: "+critCounters[i]);
				critResults+="AC OBLITERATED: "+critCounters[i]+"\n";
				break;
			case 8:
				System.out.println("Additional Attack: "+critCounters[i]);
				critResults+="Additional Attack: "+critCounters[i]+"\n";
				break;
			case 9:
				System.out.println("INSTANT DEATH!!!: "+critCounters[i]);
				critResults+="INSTANT DEATH!!!: "+critCounters[i]+"\n";
				break;
			case 10:
				System.out.println("Choice: "+critCounters[i]);
				critResults+="Choice: "+critCounters[i]+"\n";
			}
		}
		critInfo.setText(critResults);
		System.out.println(critInfo.getSize());
		System.out.println();
		System.out.println();
		return;
		
		
	}
	
	
	
	
	
	
	public int roll(int num, int size) {
		int sum = 0;
		for(int i = 0; i<num;i++) {
			sum +=( roller.nextInt(size)+1);
		}
		return sum;
	}
	
	public int roll(int num, int size, int bonus) {
		return roll(num,size)+bonus;
	}
	public void testRolls() {
		testRolls(100);
	}
	public void testRolls(int num) {
		testRolls(num,20);
	}
	public void testRolls(int num,int size) {
		ArrayList<Integer> test = new ArrayList<Integer>();
        
        for(int i = 0;i<num;i++) {
        	int rollResult = roll(1,size);
        	test.add(rollResult);
        }
        
        Collections.sort(test);
        /*
        for(int i:test){
        	System.out.println(i);
        	
        }
        */
        
        double percentAverage = 100*((double)num/size)/num;
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("Expected Average: "+(num/size)+" - Percent: "+df.format(percentAverage)+"%");
        for(int i = 1;i<=size;i++) {
        	int sum = 0;
        	for(int roll :test) {
        		if(roll==i) {
        			sum++;
        		}
        	}
        	
            System.out.println(i+": "+sum+" - Percent: "+(df.format(100*((double)sum)/num))+"%");
        }
        System.out.println();
	}
	public String[] getColumnNames() {
		return columnNames;
	}
	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);
        
        
	}
}
