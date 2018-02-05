import java.util.Hashtable;

public class Opcodes 
{
	Hashtable<String,String> isInstructions = new Hashtable<String,String>();
	Hashtable<String,String> adInstructions = new Hashtable<String,String>();
	Hashtable<String,String> dlInstructions = new Hashtable<String,String>();
	Hashtable<String,String> register = new Hashtable<String,String>();
	Opcodes()
	{
		isInstructions.put("STOP" ,"IS,00");
		isInstructions.put("ADD"  ,"IS,01");
		isInstructions.put("SUB"  ,"IS,02");
		isInstructions.put("MULT" ,"IS,03");
		isInstructions.put("MOVER","IS,04");
		isInstructions.put("MOVEM","IS,05");
		isInstructions.put("COMP" ,"IS,06");
		isInstructions.put("BC"   ,"IS,07");
		isInstructions.put("DIV"  ,"IS,08");
		isInstructions.put("READ" ,"IS,09");
		isInstructions.put("PRINT","IS,10");
		
		dlInstructions.put("DC","DL,01");
		dlInstructions.put("DS","DL,02");
		
		adInstructions.put("START" ,"AD,01");
		adInstructions.put("END"   ,"AD,02");
		adInstructions.put("ORIGIN","AD,03");
		adInstructions.put("EQU"   ,"AD,04");
		adInstructions.put("LTORG" ,"AD,05");
		
		register.put("AREG","01");
		register.put("BREG","02");
		register.put("CREG","03");
		register.put("DREG","04");
	}
	public boolean isImperativeStatement(String str)
	{
		return isInstructions.containsKey(str);
	}
	public String getOpcodeIS(String str)
	{
		return isInstructions.get(str);
	}
	public boolean isAssemblerDirective(String str)
	{
		return adInstructions.containsKey(str);
	}
	public String getOpcodeAD(String str)
	{
		return adInstructions.get(str);
	}
	public boolean isDeclarativeStatement(String str)
	{
		return dlInstructions.containsKey(str);
	}
	public String getOpcodeDL(String str)
	{
		return dlInstructions.get(str);
	}
	public boolean checkSymbol(String str)
	{
		if(!isImperativeStatement(str) && 
				!isAssemblerDirective(str) && 
				!isDeclarativeStatement(str) &&
				!(str.equalsIgnoreCase("AREG")) && 
				!(str.equalsIgnoreCase("BREG")) && 
				!(str.equalsIgnoreCase("CREG")) && 
				!(str.equalsIgnoreCase("DREG")) &&
				!(str.contains("=")) && 
				!(str.equals("\\n")) && 
				!(str.equals("\\s")) && 
				!(str.contains("=")))
			return true;
		else
			return false;

	}
	public boolean checkLiteral(String str)
	{
		if(str.contains("="))
			return true;
		else
			return false;
		
	}
	public boolean isRegister(String str)
	{
		if(	(str.equalsIgnoreCase("AREG")) ||
			(str.equalsIgnoreCase("BREG")) || 
			(str.equalsIgnoreCase("CREG")) || 
			(str.equalsIgnoreCase("DREG")))
		{
			return true;
		}
		else
			return false;			
	}
	public String getRegisterOpcode(String str){
		return register.get(str);		
	}
	public String[] getAllInstructionWhichRequire1Operand()
	{
		String[] str = new String[6];
		str[0] = "MUL";
		str[1] = "DIV";
		str[2] = "ORIGIN";
		str[3] = "EQU";
		str[4] = "DS";
		str[5] = "DC";
		return str;
	}
	public static void main(String[] args) 
	{
	}
}

