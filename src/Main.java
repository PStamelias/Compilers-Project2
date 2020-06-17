import syntaxtree.*;

import java.io.*;


class Main {
    public static void main (String[] args) throws Exception{
	if(args.length==0){
	    System.err.println("Usage: java Driver [file1] [file2] ... [fileN]");
	    System.exit(1);
	}
	FileInputStream fis = null;
	try{
		for(int i=0;i<args.length;i++){
		    fis = new FileInputStream(args[i]);
		    MiniJavaParser parser = new MiniJavaParser(fis);
		    SymbolTable symboltable=new SymbolTable();/*Initialize SymbolTable*/
		    FirstVisitor firstvisitor=new FirstVisitor();/*Initialize FirstVisitor*/
		    SecondVisitor secondvisitor=new SecondVisitor();/*Initialize SecondVisitor*/
		    Goal root = parser.Goal();
		    System.out.println("Program parsed successfully.");
		    try{
		    	root.accept(firstvisitor, symboltable);/*First Checking*/
		    	symboltable.Check_Types();/*Check if types which gathered from First Checking are  valid*/
		    	root.accept(secondvisitor,symboltable);/*Second Checking*/
		    	symboltable.Calculate_Offsets();/*Calculate OffsetTable*/
		    }catch(Exception e){
		    	System.err.println(e.getMessage());
		    }
		}
	}
	catch(ParseException ex){
	    System.out.println(ex.getMessage());
	}
	catch(FileNotFoundException ex){
	    System.err.println(ex.getMessage());
	}
	finally{
	    try{
		if(fis != null) fis.close();
	    }
	    catch(IOException ex){
		System.err.println(ex.getMessage());
	    }
	}
    }
}
