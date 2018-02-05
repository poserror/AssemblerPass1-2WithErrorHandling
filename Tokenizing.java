import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

/*class ErrorInstructiom extends Exception{
	private static final long serialVersionUID = 1L;
	ErrorInstructiom(String s)
	{
		System.out.println(s);
	}
}*/
public class Tokenizing
{
	byte[] b = null;
	FileOutputStream ir;
	FileInputStream in;
	Opcodes op = new Opcodes();							
	Scanner sc = new Scanner(System.in);
	String fname="File.txt",strFile,pass1 = "Pass1.txt",add_in_file = "",pass2="Pass2.txt";
	Hashtable<String,Integer> litTab = new Hashtable<String,Integer>();
	Hashtable<String,Integer> symTab = new Hashtable<String,Integer>();
	ArrayList<String> arr = new ArrayList<String>();
	
	public static void main(String[] args){	
		new Tokenizing().tokenize();
	}
	public boolean checkError()
	{
		int flag = 0;
		System.out.println(strFile);
		//try
		{
			if(!(strFile.contains("END")))
			{
				//throw new ErrorInstructiom("Missing End Of File");
				System.out.println(("Missing End Of File"));
				flag = 1;
			}
			if(strFile.contains("/*") && !(strFile.contains("*/") ))
			{
				//throw new ErrorInstructiom("Incorrect Syntax Of Comment");
				System.out.println(("Incorrect Syntax Of Comment"));
				flag = 1;
			}
			String[] splitStr = strFile.split("\\n");	
			String temp = " ";
			if(!(splitStr[0].contains("START")) && !(splitStr[0].contains("Start")))
			{
				//throw new ErrorInstructiom("Start not defined");
				System.out.println(("Start Not Defined"));
				flag = 1;				
			}
			for(int i=1 ; i<splitStr.length ; i++)
			{
				//System.out.println("________>>>>"+splitStr[i]);
				if(splitStr[i].contains(":"))
				{
					temp = splitStr[i].substring(0, splitStr[i].indexOf(':'));
					if((op.isImperativeStatement(temp)) || (op.isAssemblerDirective(temp)) || (op.isDeclarativeStatement(temp)))
					{
						//throw new ErrorInstructiom("Mnemonic Used as Label");
						System.out.println(("Mnemonic Used as Label"));
						flag = 1;
						break;
					}
				}
				if(splitStr[i].contains("ADD") || splitStr[i].contains("SUB") || splitStr[i].contains("BC") || splitStr[i].contains("MOVER")
						|| splitStr[i].contains("MOVEM"))
				{
					if(!(splitStr[i].contains(",")))
					{
						System.out.println(splitStr[i].substring(0, splitStr[i].length()).trim()+" not defined");
						flag = 1;
						break;
					}
				}
				if(splitStr[i].contains("DIV") || splitStr[i].contains("MUTL") || splitStr[i].contains("DS") || splitStr[i].contains("DC")
						|| splitStr[i].contains("ORIGIN") || splitStr[i].contains("EQU"))
				{
					if((splitStr[i].contains(",")))
					{
						System.out.println(splitStr[i].substring(0, splitStr[i].length()).trim()+" not defined");
						flag = 1;
						break;
					}
				}
			}
		}
		//catch(ErrorInstructiom e){return false;}
		//System.out.println("FLAg : "+flag);
		if(flag==0)
			return true;
		else
			return false;
	}
	public void tokenize()
	{
		try{
			//System.out.println("Enter File Name for processing : ");
			//fname = sc.next();				//take file name from user
			strFile = getStringOfFile();
			//System.out.println(checkError());
			if(checkError())
			{
				if(pass1())
					pass2();
				ir.close();
				in.close();
				sc.close();
			}				
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void pass2() throws Exception
	{
		strFile = "";
		System.out.println("\n\nPass2 generated in File "+pass2);
		in = new FileInputStream(pass1);//read the intermediate code file
		int k=0;
		while((k=in.read())!=-1)
		{
			strFile = strFile + (char)k;
		}
		add_in_file = "";
		ir = new FileOutputStream(pass2);
		String[] pass1Str = strFile.split("\\n");		
		for(int i=0 ; i<pass1Str.length ; i++)
		{
			String temp[] = pass1Str[i].split("\\s");
			for(int j=1 ; j<temp.length ; j++)//j=1 coz we don't need address of that particular inst in pass2	
			{
				String opcode="", regOperand="";
				String str="";

				add_in_file = temp[0];
				if(temp[j].contains(","))
				{			
					str = temp[j].substring(temp[j].indexOf("(")+1, temp[j].indexOf(","));
					opcode = temp[j].substring(temp[j].indexOf(',')+1, temp[j].indexOf(')'));
					if(str.equalsIgnoreCase("IS"))//to get the opcode if it is IS only
					{
						add_in_file = add_in_file +" + "+ opcode;//add the opcode taken from intermediate code of pass 1
						ir.write(10);
					}
					
					if(str.length()==1)//to get the literal
					{
						add_in_file = " "+litTab.get(temp[j].substring(temp[j].indexOf(',')+1, temp[j].indexOf(')')));
					}
					
					if(str.equalsIgnoreCase("AD") && Integer.parseInt(opcode)==05)
					{
						add_in_file = add_in_file +" + "+ opcode;//add the opcode taken from intermediate code of pass 1
						ir.write(10);
					}
					if(str.equalsIgnoreCase("AD") && Integer.parseInt(opcode)==04)
					{
						continue;
					}
					if(str.equalsIgnoreCase("DL") || (str.equalsIgnoreCase("AD")) && !(Integer.parseInt(opcode)==05))
					{
						ir.write(10);
					}
					b = add_in_file.getBytes();
					ir.write(b);
				}
				if(op.isRegister(temp[j]))
				{
					regOperand = op.getRegisterOpcode(temp[j]);
					add_in_file = " "+ regOperand;//add the reg operand taken from intermediate code of pass 1
					b = add_in_file.getBytes();
					ir.write(b);
				}
				if(symTab.containsKey(temp[j]) && !(temp[j-1].contains("AD")) && !(temp[j-1].contains("DL")) )
				{
					add_in_file = " "+symTab.get(temp[j]);
					b = add_in_file.getBytes();
					ir.write(b);
				}
			}
		}
	}
	public String getStringOfFile()throws Exception
	{
				
		in = new FileInputStream(fname);		
		strFile = new String("");
		int k=0;
		while((k=in.read())!=-1)	//read file and store in string
		{
			strFile  = strFile + (char)k;
		}

		return strFile;
	}
	public boolean pass1() throws Exception
	{
		int start_address = 0, index=0;
		ir = new FileOutputStream(pass1);//output file

		String[] resStr = strFile.split(",|\\s");
		
/*		for(int i=0 ; i<resStr.length ; i++)
			System.out.println(" :- "+resStr[i]);
*/		
		if(resStr[0].equalsIgnoreCase("Start"))	//identify start address if specified
		{
			char x = resStr[1].charAt(0);
			if(x>=48 && x<=57)
			{
				start_address = Integer.parseInt(resStr[1]);
				index = 3;
			}
		}
		//try
		{
			//System.out.println("Start Address : "+start_address);		
			for(int i=index ; i<resStr.length ; i++)		
			{			
				String qwerty = resStr[i].substring(resStr[i].indexOf(":")+1, resStr[i].length());
	
	/*================================================================================================================*/			
				if(!(qwerty.equalsIgnoreCase("EQU")) && resStr[i].contains(":") && !(op.isDeclarativeStatement(resStr[i].substring(resStr[i].indexOf(":")+1,resStr[i].length()))))
				{
					String temp = resStr[i];
					temp = temp.substring(0, temp.indexOf(":"));
					if(symTab.containsKey(temp))
					{
						//throw new ErrorInstructiom("Duplicate Symbol Defined "+temp);
						System.out.println("Duplicate Symbol Defined");
						return false;
					}
					symTab.put(temp, start_address);
					resStr[i] = resStr[i].substring(resStr[i].indexOf(":")+1,resStr[i].length());
				}
	/*===============================================================================================================*/
	
				if(!(qwerty.equalsIgnoreCase("EQU")) && 
						resStr[i].contains(":") && 
						(op.isDeclarativeStatement(resStr[i].substring(resStr[i].indexOf(":")+1,resStr[i].length()))))
				{
					String temp = resStr[i];
					temp = temp.substring(0, temp.indexOf(":"));
					//System.out.println("::::::::::::::"+temp+"::::::::"+symTab);

					symTab.put(temp, start_address);							
					temp = resStr[i].substring(resStr[i].indexOf(":")+1, resStr[i].length());
					add_in_file =  start_address + " ("+op.getOpcodeDL(temp)+") "+resStr[i+1];
					//System.out.println(add_in_file);
					store(add_in_file);
					start_address = start_address + Integer.parseInt(resStr[i+1]);
					i+=3;
				}
	/*================================================================================================*/
				if(resStr[i].equalsIgnoreCase("END"))//END
				{
					add_in_file =  start_address + " ("+op.getOpcodeAD(resStr[i])+") ";				
					if(i+2<resStr.length)
					{	
						if(!(resStr[i+2].equalsIgnoreCase("")))
						{
							String temp = resStr[i+2];
							temp = temp.replaceAll("=", "");
							temp = temp.replaceAll("'", "");
							add_in_file =  add_in_file + temp;
						}						
					}
					//System.out.println(add_in_file);
					store(add_in_file);
					break;
				}
	/*================================================================================================*/			
				if(resStr[i].equalsIgnoreCase("ORIGIN"))
				{
					
					String temp = resStr[i+1], temp2 = "";
					if(temp.contains("+"))
					{
						temp = temp.substring(0, temp.indexOf("+"));
						temp2 = resStr[i+1];
						temp2 = temp2.substring(temp2.indexOf("+")+1,temp2.length());
					}
					arr.add(temp);
					add_in_file = start_address+" ("+op.getOpcodeAD(resStr[i])+") " + temp +"+"+temp2;
					store(add_in_file);
					i+=2;	
					start_address = symTab.get(temp) + Integer.parseInt(temp2);
				}
	/*================================================================================================*/			
	
				if(resStr[i].contains("EQU") )
				{
					String symbol = "",inst="";
					if(resStr[i].contains(":"))
					{	
						symbol = resStr[i].substring(0, resStr[i].indexOf(":"));
						inst = resStr[i].substring(resStr[i].indexOf(":")+1,resStr[i].length());
					}
					else
					{
						symbol = resStr[i+1];
						inst = resStr[i];
					}
					add_in_file = start_address+" ("+op.getOpcodeAD(inst)+") "+ resStr[i+1];
						
					//System.out.println(add_in_file);
					symTab.put(symbol, symTab.get(resStr[i+1]));
					store(add_in_file);
				}
	
	/*================================================================================================*/			
				if(op.isImperativeStatement(resStr[i])) 					
				{
/*					if(resStr[i].equalsIgnoreCase("ADD") || resStr[i].equalsIgnoreCase("SUB") || resStr[i].equalsIgnoreCase("MOVER") || 
							resStr[i].equalsIgnoreCase("MOVEM"))
					{
						System.out.println("bbbbbbbbbbbbb"+resStr[i]+"ggggggggg"+resStr[i+1]);
					}
*/					if(resStr[i].contains(":"))
						resStr[i] = resStr[i].substring(resStr[i].indexOf(":")+1, resStr[i].length());
					add_in_file = start_address+" ("+op.getOpcodeIS(resStr[i])+")";
					if(!(resStr[i].equalsIgnoreCase("Stop")))
						start_address+=3;
					else
						start_address+=1;
					if(resStr[i+1].equalsIgnoreCase("AREG") || resStr[i+1].equalsIgnoreCase("BREG") || resStr[i+1].equalsIgnoreCase("CREG") || resStr[i+1].equalsIgnoreCase("DREG"))
					{
						add_in_file = add_in_file +" "+ resStr[i+1];
						i++;
					}
					if(op.checkLiteral(resStr[i+1]))
					{
						String temp = resStr[i+1];
						temp = temp.replaceAll("=", "");
						temp = temp.replaceAll("'", "");
						add_in_file = add_in_file + " (L,"+temp+")";					
						i++;
					}
					else if(op.checkSymbol(resStr[i+1]))
					{
						//System.out.println("it is sym :"+resStr[i+1]);
						arr.add(resStr[i+1]);
						String temp = resStr[i+1];
						add_in_file = add_in_file + " "+temp;
						i++;		
					}
					//System.out.println(add_in_file);							
					store(add_in_file);
				}
	/*================================================================================================*/
				if(resStr[i].equalsIgnoreCase("LTORG"))
				{
					int j=i+2;
					while(resStr[j].contains("="))
					{
						String temp = resStr[j];
						temp = temp.replaceAll("=", "");
						temp = temp.replaceAll("'", "");
						litTab.put(temp, start_address);
						add_in_file = start_address +" ("+ op.getOpcodeAD("LTORG")+") " + temp;
						//System.out.println(add_in_file);
						store(add_in_file);
						start_address+=1;					
						j+=2;
					}
				}			
			}
			//System.out.println("ARRRRR: "+arr);
			String str = checkSymbolAreDefined();
			//String str2 = checkSymbolnotDefinedRepeatedly();
			if(str.equals(""))
			{
				System.out.println("\n\nPass1 generated in File "+pass1);
				//System.out.println("\n\nData Structures in pass 1 -\n");
				//System.out.println("Literal Table -->"+litTab);
				//store("\nLiteral Table : "+litTab);
				FileOutputStream litFile = new FileOutputStream("LitTab.txt");//output file
				str = litTab.toString();
				b = str.getBytes();
				litFile.write(b);
				litFile.write(10);
				litFile.close();

				//System.out.println("Symbol Table  -->"+symTab);
				//store("\nSymbol Table : "+symTab);
				FileOutputStream symFile = new FileOutputStream("symTab.txt");//output file
				str = symTab.toString();
				b = str.getBytes();
				symFile.write(b);
				symFile.write(10);
				symFile.close();
			}
			else
			{
				System.out.println("Symbol not defined "+str);
				return false;
			}
		}
		//catch(ErrorInstructiom e){return false;}
		return true;
	}
	boolean store(String str) throws Exception
	{
		b = str.getBytes();
		ir.write(b);
		ir.write(10);		
		return true;
	}
	String checkSymbolAreDefined()
	{
		System.out.println(strFile);
		for(int i=0;i<arr.size();i++)
		{
			if(!(arr.get(i).equals("")))
			{
				String str = arr.get(i)+":";
				System.out.println("-->"+str+"-->"+arr.get(i));
				if((strFile.contains("SUB:")))
				{
					return "\nMnemonic Can't be used as label";
				}
					
				else if(strFile.contains(str))
				{
						continue;
				}
				else
				{
					return arr.get(i);
				}
			}
		}
		return "";		
	}
	String checkSymbolnotDefinedRepeatedly()
	{
		
		return "";		
	}
}