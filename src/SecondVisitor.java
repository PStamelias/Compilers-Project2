import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.LinkedList; 
import java.util.List; 
import java.util.Iterator;

//@SuppressWarnings("Duplicates") // Remove IntelliJ warning about duplicate code
public class SecondVisitor extends GJDepthFirst<String,SymbolTable>{
	public String ClassName;/*Current Class Name*/
    public boolean On_Class;/* Check if we are on Class*/
    public String Fun_name_of_Class;/* Current Function Name*/
    public boolean On_Fun;/* Check if we are on Function*/
    public boolean thisarg;/* Helpful operator to handle this operator on Input*/
    public List<String> arg;/* Current arg of current function*/
    public String Current;  /* Very HelpFul  OPERATOR because i am taking the previous value on parsing program*/
    SecondVisitor(){
    	thisarg=false;
    	arg=new LinkedList<String>();
    	arg.clear();
    }
    public String exists(String var,String ClassName,String FunName,SymbolTable symtable){/*Check if var exists or not*/
    	SymbolTable.ClassInfo ob =symtable.My_map.get(ClassName);
		SymbolTable.MethodInfo f=ob.Method_map.get(FunName);
		for(Map.Entry<String,String> value : f.variables.entrySet()){
			if(var.equals(value.getValue())){
				return value.getValue();
			}
		}
		for(Map.Entry<String,String> value : f.parameters.entrySet()){
			if(var.equals(value.getValue()))
				return value.getValue();
		}
		for(Map.Entry<String,String> value : ob.Field_map.entrySet()){
			if(var.equals(value.getValue()))
				return value.getValue();
		}
		while(true){
			for(Map.Entry<String,String> value : ob.Field_map.entrySet()){
				if(var.equals(value.getValue()))
					return value.getValue();
			}
			if(ob.parent_Class!=null){
				String name=ob.parent_Class;
				ob =symtable.My_map.get(name);
			}
			else
				break;
		}
		return null;
    }
    public void check_args(List<String> arg,SymbolTable symtable,String ClassName,String FunName) throws Exception{/*Function for checking  right arguments on function call*/
    	SymbolTable.ClassInfo ob =symtable.My_map.get(ClassName);
		SymbolTable.MethodInfo f=ob.Method_map.get(FunName);
		int i=0;
		if(arg.size()!=f.parameters.size())
			throw new Exception("Wrong arguments on function "+FunName);
		String type=null;
		for(Map.Entry<String,String> value : f.parameters.entrySet()){
			String Current_Argument=arg.get(i);
			if(Current_Argument.equals("##INT_LIT")||Current_Argument.equals("int")){
				Current_Argument="int";
			}
			else if(Current_Argument.equals("true")||Current_Argument.equals("false"))
				Current_Argument="boolean";
			else if(Current_Argument.equals("this"))
				return ;
			else{	
				int enter=0;
				type=Search_Identifier(Current_Argument,symtable);
				if(type!=null){
					enter=1;
				}
				if(enter==0){
					type=exists(Current_Argument,ClassName,FunName,symtable);
				if(type!=null)
					enter=1;
				}
				if(enter==0)
					throw new Exception("Error: "+Current_Argument+" has not declared");
			}
			String wait_type=value.getValue();
			if(!wait_type.equals(type)&&!wait_type.equals(Current_Argument))
				throw new Exception("Error: Wrong input on Function "+ FunName);
			i++;
		}
    }

    public String Search_Identifier(String input,SymbolTable symtable){/*Function that returns the input variable type*/
		if(this.Fun_name_of_Class.equals("main")&&this.On_Fun==true){
			SymbolTable.ClassInfo ob =symtable.My_map.get(this.ClassName);
			SymbolTable.MethodInfo f=ob.Method_map.get("main");
			for(Map.Entry<String,String> value : f.variables.entrySet()){
				if(input.equals(value.getKey()))
					return value.getValue();
			}
			for(Map.Entry<String,String> value : f.parameters.entrySet()){
				if(input.equals(value.getKey()))
					return value.getValue();
			}
		}
		else{
			SymbolTable.ClassInfo ob =symtable.My_map.get(this.ClassName);
			SymbolTable.MethodInfo f=ob.Method_map.get(this.Fun_name_of_Class);
			for(Map.Entry<String,String> value : f.variables.entrySet()){
				if(input.equals(value.getKey())){
					return value.getValue();
				}
			}
			for(Map.Entry<String,String> value : f.parameters.entrySet()){
				if(input.equals(value.getKey()))
					return value.getValue();
			}
			for(Map.Entry<String,String> value : ob.Field_map.entrySet()){
				if(input.equals(value.getKey()))
					return value.getValue();
			}
			while(true){
				for(Map.Entry<String,String> value : ob.Field_map.entrySet()){
					if(input.equals(value.getKey()))
						return value.getValue();
				}
				if(ob.parent_Class!=null){
					String name=ob.parent_Class;
					ob =symtable.My_map.get(name);
				}
				else
					break;
			}
		}
		return null;
	}

	/**
     * f0  -> "class"
     * f1  -> Identifier()
     * f2  -> "{"
     * f3  -> "public"
     * f4  -> "static"
     * f5  -> "void"
     * f6  -> "main"
     * f7  -> "("
     * f8  -> "String"
     * f9  -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
	public String visit(MainClass n,SymbolTable symtable) throws Exception{
		n.f1.accept(this,symtable);
		this.ClassName=this.Current;
		this.On_Fun=true;
		this.On_Class=false;
		this.Fun_name_of_Class="main";
		n.f15.accept(this,symtable);
		return null;
	}

	/**
    * f0-> "class"
    * f1-> Identifier
    * f2-> "{"
    * f3->(VarDeclaration)
    * f4->(MethodDeclaration)
    * f5-> "}"
    */
    public String visit(ClassDeclaration n,SymbolTable symtable) throws Exception{
    	n.f1.accept(this,symtable);
    	this.ClassName=this.Current;
		this.On_Fun=false;
		this.On_Class=true;
		this.Fun_name_of_Class=null;
		n.f4.accept(this,symtable);
        return null;
    }


    /**
    * fo->"class"
    * f1-> Identifier()
    * f2-> "extends"
    * f3-> Identifier()
    * f4-> "{"
    * f5-> (VarDeclaration)
    * f6-> (MethodDeclaration)
    * f7-> "}"
    */
    public String visit(ClassExtendsDeclaration n, SymbolTable symtable) throws Exception{
    	n.f1.accept(this,symtable);
       	this.ClassName=this.Current;
        this.On_Class=true;
        this.On_Fun=false;
        n.f6.accept(this,symtable);
    	return null;
    }

    
    /**
    * fo-> "public"
    * f1-> Type
    * f2-> Identifier
    * f3-> "("
    * f4-> (FormalParameterList)?
    * f5-> ")"
    * f6-> "{"
    * f7-> (VarDeclaration)
    * f8-> (Statement)
    * f9-> "return"
    * f10-> Expression
    * f11-> ";"
    * f12-> "}" 
    */
    public String visit(MethodDeclaration n,SymbolTable symtable) throws Exception{
        String e=this.ClassName;
        n.f2.accept(this,symtable);
        this.Fun_name_of_Class=this.Current;
        this.On_Class=false;
        this.On_Fun=true;
        n.f8.accept(this,symtable);
        n.f10.accept(this,symtable);
        String type=this.Current;
        this.ClassName=e;
        SymbolTable.ClassInfo ob =symtable.My_map.get(this.ClassName);
        SymbolTable.MethodInfo f= ob.Method_map.get(this.Fun_name_of_Class);
        if(type.equals("##INT_LIT"))
        	type="int";
        else if(type.equals("true")||type.equals("false")||type.equals("boolean"))
        	type="boolean";
        else{
        	String syn=Search_Identifier(type,symtable);
        	if(syn==null)
        		throw new Exception("Error: Variable --- "+type+" has not declared");
        	type=syn;
        }
        if(!f.return_type.equals(type))
        	throw new Exception("Error: Incompatible return type");
        return null;
    }

   
    /**
	* f0 -> Identifier
	* f1 -> "="
	* f2 -> Expression
	* f3 -> ";"
	*/
	public String visit(AssignmentStatement n,SymbolTable symtable) throws Exception{
		String id=n.f0.accept(this,symtable);
		if(!id.equals("Identifier"))
			throw new Exception("Error: Wrong type of Variable");
		String symbol=this.Current;
		String type_of_symbol=Search_Identifier(symbol,symtable);
		if(symbol==null)
			throw new Exception("Error: "+ this.Current+" has not declared");
		String type_of_expr=n.f2.accept(this,symtable);
		if(type_of_expr.equals("boolean")||type_of_expr.equals("true")||type_of_expr.equals("false")){
			type_of_expr="boolean";
			if(!type_of_symbol.equals(type_of_expr))
				throw new Exception("Error : Incompatible types expected "+ type_of_symbol+"  found "+type_of_expr);
			return null;
		}
		if(type_of_expr.equals("##INT_LIT")||type_of_expr.equals("int")){
			type_of_expr="int";
			if(!type_of_symbol.equals(type_of_expr))
				throw new Exception("Error : Incompatible types expected "+ type_of_symbol+"  found "+type_of_expr);
			return null;
		}
		if(type_of_expr.equals("AllocationExpression")){
			String newIden=this.Current;
			if(!symtable.My_map.containsKey(newIden))
				throw new Exception("Error: "+newIden+" has not declared");
			if(type_of_symbol.equals(newIden))
				return null;
			int enter=0;
			SymbolTable.ClassInfo e1 =symtable.My_map.get(newIden);
			while(true){
				if(e1.parent_Class==null)
					break;
				e1=symtable.My_map.get(e1.parent_Class);
				if(e1.Name.equals(type_of_symbol)){
					enter=1;
					break;
				}
			}
			if(enter==0)
				throw new Exception("Error:  Incompatible types");
			return null;
		}
		if(type_of_expr.equals("IntegerArrayAllocationExpression")){
			String eIden=this.Current;
			if(!type_of_symbol.equals(eIden))
				throw new Exception("Error:  Incompatible types");
			return null;
		}
		if(type_of_expr.equals("BooleanArrayAllocationExpression")){
			String eIden=this.Current;
			if(!type_of_symbol.equals(eIden))
				throw new Exception("Error:   Incompatible types");
			return null;
		}
		if(type_of_symbol.equals(type_of_expr)||type_of_symbol.equals(Search_Identifier(type_of_expr,symtable))){
			return  null;
		}
		if(!type_of_symbol.equals(type_of_expr)){
			String left=type_of_symbol;
			String right=Search_Identifier(this.Current,symtable);
			SymbolTable.ClassInfo e1 =symtable.My_map.get(right);
			if(e1==null){
				if(!symtable.My_map.containsKey(this.Current))
					throw new Exception("Error:  Incompatible types");
				return null;
			}
			int enter=0;
			while(true){
				if(e1.parent_Class==null)
					break;
				e1=symtable.My_map.get(e1.parent_Class);
				if(e1.Name.equals(left)){
					enter=1;
					break;
				}
			}
			if(enter==0)
				throw new Exception("Error:  Incompatible types");
			return null;
		}
		String type_ofIden=Search_Identifier(type_of_expr,symtable);
		if(type_ofIden==null)
			throw new Exception("Error: "+type_of_expr+" has not declared");
		if(!type_of_symbol.equals(type_ofIden))
			throw new Exception("Error : incompatible types");
		return null;
	}
	

	/**
	* f0 -> Identifier
	* f1 -> "["
	* f2 -> Expression
	* f3 -> "]"
	* f4 -> "="
	* f5 -> Expression
	* f6 -> ";"
	*/
	public String visit(ArrayAssignmentStatement n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		String type_of_id=Search_Identifier(this.Current,symtable);
		if(type_of_id.equals("int[]"))
			type_of_id="int";
		if(type_of_id.equals("boolean[]"))
			type_of_id="boolean";	
		if(type_of_id==null)
			throw new Exception("Error: Cannot find symbol "+this.Current);
		String type_expr=n.f2.accept(this,symtable);
		if(!type_expr.equals("int")&&!type_expr.equals("##INT_LIT")){
			String type=Search_Identifier(type_expr,symtable);
			if(type==null)
				throw new Exception("Error:  incompatible types "+this.Current+"  cannot converted to int");
			if(!type.equals("int"))
				throw new Exception("Error:  incompatible types "+this.Current+"  cannot converted to int");
		}
		String type_result=n.f5.accept(this,symtable);
		if(type_result.equals("##INT_LIT"))
			type_result="int";
		if(!type_of_id.equals(type_result)&&!type_of_id.equals(Search_Identifier(type_result,symtable)))
			throw new Exception("Error:   incompatible types "+this.Current+"  cannot converted to "+ type_of_id);
		return null;
	}
	/**
	* f0 -> "{"
	* f1 -> (Statement)?
	* f2 -> "}"
	*/
	public String visit(Block n,SymbolTable symtable) throws Exception{
		String id=n.f1.accept(this,symtable);
		return null;
	} 

	

	/**
	* f0 -> "if"
	* f1 -> "("
	* f2 -> Expression
	* f3 -> ")"
	* f4 -> Statement
	* f5 -> "else"
	* f6 -> Statement
	*/
	public String visit(IfStatement n ,SymbolTable symtable) throws Exception{
		n.f2.accept(this,symtable);
		String type1=this.Current;
		if(!type1.equals("boolean")){
			if(!Search_Identifier(type1,symtable).equals("boolean"))
				throw new Exception("Error:Expression expected boolean type as argument found "+ type1);
		}
		n.f4.accept(this,symtable);
		n.f6.accept(this,symtable);
		return "i-else";
	}

	/**
	* f0 -> "System.out.println"
	* f1 -> "("
	* f2 -> Expression
	* f3 -> ")"
	* f4 -> ";"
	*/
	public String visit(PrintStatement n ,SymbolTable symtable) throws Exception{
		n.f2.accept(this,symtable);
		String type=this.Current;
		if(type.equals("boolean"))
			throw new Exception("Error:System.out.println expected int type as argument found "+ type);
		if(!type.equals("int")&&!type.equals("##INT_LIT")&&!Search_Identifier(type,symtable).equals("int"))
			throw new Exception("Error:System.out.println expected int type as argument found "+ type);
		return "orint";
	}

	/**
	* f0 -> "while"
	* f1 -> "("
	* f2 -> Expression
	* f3 -> ")"
	* f4 -> Statement
	*/
	public String visit(WhileStatement n ,SymbolTable symtable) throws Exception{
		n.f2.accept(this,symtable);
		String type=this.Current;
		if(!type.equals("boolean")&&!type.equals("true")&&!type.equals("false")&&!Search_Identifier(type,symtable).equals("boolean"))
			throw new Exception("Error:Expression expected boolean type as argument found "+ type);
		String se=n.f4.accept(this,symtable);
		return "while";
	}




	/**
	*f0     ->  Block
			|	AssignmentStatement
			|	ArrayAssignmentStatement
			|	IfStatement
			|	WhileStatement
			|	PrintStatement
	*/
	public String visit(Statement n ,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		return null;
	}




	/**
	* f0 ->     IntergerLiteral	
	    	|	TrueLiteral
			|	FalseLiteral
			|	Identifier
			|	ThisExpression
			|	ArrayAllocationExpression
			|	AllocationExpression
			|	BracketExpression
	*/
	public String visit(PrimaryExpression n,SymbolTable symtable) throws Exception{
		String value=n.f0.accept(this,symtable);
		if(value==null)
			throw new Exception("Error: Missing value");
		if(value.equals("TrueLiteral")||value.equals("FalseLiteral")){
			return "boolean";
		}
		if(value.equals("##INT_LIT")){
			this.Current=value;
			return "int";
		}
		if(value.equals("Identifier")){
			return "Identifier";
		}
		if(value.equals("AllocationExpression")){
			return "AllocationExpression";
		}
		if(value.equals("IntegerArrayAllocationExpression")){
			this.Current="int[]";
			return "IntegerArrayAllocationExpression";
		}
		if(value.equals("BooleanArrayAllocationExpression")){
			this.Current="boolean[]";
			return "BooleanArrayAllocationExpression";
		}
		if(value.equals("this")){
			thisarg=true;
			return "this";
		}
		if(value.equals("BracketExpression"))
			return this.Current;
		return null;

	}




	



	/**
	* f0 -> NotExpression
		| PrimaryExpression
	*/
	public String visit(Clause n,SymbolTable symtable) throws Exception{
		String id=n.f0.accept(this,symtable);
		if(id.equals("NotExpression")){
			if(!(this.Current.equals("boolean")||this.Current.equals("true")||this.Current.equals("false")||Search_Identifier(this.Current,symtable).equals("boolean")))
				throw new Exception("Error:Expected boolean found "+ id);
		}
		return "Clause";
	}


	/**
	* f0 -> BooleanArrayAllocationExpression
		  | IntegerArrayAllocationExpression
	*/
	public String visit(ArrayAllocationExpression n,SymbolTable symtable) throws Exception{
		String type=n.f0.accept(this ,symtable);
		return type;
	}
	/**
	* f0 -> "new"
	* f1 -> "boolean"
	* f2 -> "["
	* f3 -> Expression
	* f4 -> "]"
	*/
	public String visit(BooleanArrayAllocationExpression n,SymbolTable symtable) throws Exception{
		n.f3.accept(this,symtable);
		String type=this.Current;
		if(!type.equals("int")&&!type.equals("##INT_LIT"))
			throw new Exception("Error: incompatible types: "+ type +" cannot be converted to int");
		this.Current="boolean[]";
		return "BooleanArrayAllocationExpression";
	}
	
	/**
	* f0 -> "new"
	* f1 -> "int"
	* f2 -> "["
	* f3 -> Expression
	* f4 -> "]"
	*/
	public String visit(IntegerArrayAllocationExpression n,SymbolTable symtable) throws Exception{
		n.f3.accept(this,symtable);
		String type=this.Current;
		if(!type.equals("int")&&!type.equals("##INT_LIT")&&!Search_Identifier(type,symtable).equals("int"))
			throw new Exception("Error: incompatible types: "+ type +" cannot be converted to int");
		this.Current="int[]";
		return "IntegerArrayAllocationExpression";
	}
	/**
	* f0 -> "new"
	* f1 -> Identifier
	* f2 -> "("
	* f3 -> ")"
	*/
	public String visit(AllocationExpression n,SymbolTable symtable) throws Exception{
		n.f1.accept(this,symtable);
		return "AllocationExpression";
	}

	/**
	* f0 -> "!"
	* f1 -> Clause
	*/
	public String visit(NotExpression n,SymbolTable symtable) throws Exception{
		n.f1.accept(this,symtable);
		return "NotExpression";
	}

	/**
	* f0 -> "("
	* f1 -> Expression
	* f2 -> ")"
	*/
	public String visit(BracketExpression n,SymbolTable symtable) throws Exception{
		n.f1.accept(this,symtable);
		return "BracketExpression";
	}

	/**
	* f0-> "this"
	*/
	public String visit(ThisExpression n,SymbolTable symtable) throws Exception{ 
		return "this";
	}

	/** 
	* f0 -> "true"
	*/
	public String visit(TrueLiteral n,SymbolTable symtable) throws Exception{ 
		this.Current="true";
		return "TrueLiteral";
	}



	/** 
	* f0 -> "false"
	*/
	public String visit(FalseLiteral n,SymbolTable symtable) throws Exception{ 
		this.Current="false";
		return "FalseLiteral";
	}


	/**
    *f0 -> Identifier()
    */
    public String visit(Identifier n,SymbolTable symtable) throws Exception{ 
    	this.Current=n.f0.toString();
    	return "Identifier";
    }
     /**
     * f0 -> INTEGER_LITERAL
     */
    public String visit(IntegerLiteral n, SymbolTable symbolTable) throws Exception {
		String e=n.f0.toString();
		this.Current=e;
		return "##INT_LIT";
	}

	/** 
	* f0 -> AndExpression
		|	CompareExpression
		|	PlusExpression
		|	MinusExpression
		|	TimesExpression
		|	ArrayLookup
		|	ArrayLength
		|	MessageSend
		|	Clause
	*/
	public String visit(Expression n,SymbolTable symtable) throws Exception{
		String type=n.f0.accept(this,symtable);
		if(type.equals("AndExpression")){
			this.Current="boolean";
			return "boolean";
		}
		else if(type.equals("CompareExpression")){
			this.Current="boolean";
			return "boolean";
		}
		else if(type.equals("PlusExpression"))
			return "int";
		else if(type.equals("MinusExpression"))
			return "int";
		else if(type.equals("TimesExpression"))
			return "int";
		else if(type.equals("ArrayLookup"))
			return this.Current;
		else if(type.equals("ArrayLength"))
			return "int";
		else if(type.equals("MessageSend"))
			return this.Current;
		else if(type.equals("Clause")){
			return this.Current;
		}
		else{
			String tipos=Search_Identifier(this.Current,symtable);
			if(tipos==null)
				throw new Exception("Error: "+this.Current+" has not declared");
			return tipos;
		}
	}



	/**
	* f0 -> Clause
	* f1 -> "&&"
	* f2 -> Clause
	*/
	public String visit(AndExpression n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		String id1=this.Current;
		if(!(id1.equals("true")||id1.equals("false")||id1.equals("boolean"))){
			String tipos1=Search_Identifier(id1,symtable);
			if(tipos1==null)
				throw new Exception("Error: "+this.Current+" not found");
			if(!tipos1.equals("boolean")&&!tipos1.equals("boolean[]"))
				throw new Exception("Error: Wrong type of Variable "+ this.Current);
		}
		n.f2.accept(this,symtable);
		String id2=this.Current;
		if(!(id2.equals("true")||id2.equals("false")||id2.equals("boolean"))){
			String tipos2=Search_Identifier(id2,symtable);
			if(tipos2==null)
				throw new Exception("Error: "+this.Current+" not found");
			if(!tipos2.equals("boolean")&&!tipos2.equals("boolean[]"))
				throw new Exception("Error: Wrong type of Variable "+ this.Current);
		}
		return "AndExpression";
	}












	 




	/**
	* f0 -> PrimaryExpression
	* f1 -> "<"
	* f2 -> PrimaryExpression
	*/
	public String visit(CompareExpression n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		String id1=this.Current;
		if(!id1.equals("##INT_LIT")&&!id1.equals("int")){
			if(Search_Identifier(id1,symtable)==null||!Search_Identifier(id1,symtable).equals("int"))
				throw new Exception("Error: Wrong type ");
		}
		n.f2.accept(this,symtable);
		String id2=this.Current;
		if(!id2.equals("##INT_LIT")&&!id2.equals("int")){
			if(Search_Identifier(id2,symtable)==null||!Search_Identifier(id2,symtable).equals("int"))
				throw new Exception("Error: Wrong type ");
		}
		return "CompareExpression";
	}



	/**
	* f0 -> PrimaryExpression
	* f1 -> "+"
	* f2 -> PrimaryExpression
	*/
	public String visit(PlusExpression n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		String id1=this.Current;
		if(!id1.equals("##INT_LIT")){
			if(Search_Identifier(id1,symtable)==null||!Search_Identifier(id1,symtable).equals("int"))
				throw new Exception("Error: Wrong type ");
		}
		n.f2.accept(this,symtable);
		String id2=this.Current;
		if(!id2.equals("##INT_LIT")){
			if(Search_Identifier(id2,symtable)==null||!Search_Identifier(id2,symtable).equals("int"))
				throw new Exception("Error: Wrong type ");
		}
		this.Current="int";
		return "PlusExpression";
	}

	/**
	* f0 -> PrimaryExpression
	* f1 -> "-"
	* f2 -> PrimaryExpression
	*/
	public String visit(MinusExpression n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		String id1=this.Current;
		if(!id1.equals("##INT_LIT")){
			if(Search_Identifier(id1,symtable)==null||!Search_Identifier(id1,symtable).equals("int"))
				throw new Exception("Error: Wrong type ");
		}
		n.f2.accept(this,symtable);
		String id2=this.Current;
		if(!id2.equals("##INT_LIT")){
			if(Search_Identifier(id2,symtable)==null||!Search_Identifier(id2,symtable).equals("int"))
				throw new Exception("Error: Wrong type ");
		}
		this.Current="int";
		return "MinusExpression";
	}

	

	/**
	* f0 -> PrimaryExpression
	* f1 -> "*"
	* f2 -> PrimaryExpression
	*/
	public String visit(TimesExpression n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		String id1=this.Current;
		if(!id1.equals("##INT_LIT")&&!id1.equals("int")){
			if(Search_Identifier(id1,symtable)==null||!Search_Identifier(id1,symtable).equals("int"))
				throw new Exception("Error: Wrong type ");
		}
		n.f2.accept(this,symtable);
		String id2=this.Current;
		if(!id2.equals("##INT_LIT")&&!id2.equals("int")){
			if(Search_Identifier(id2,symtable)==null||!Search_Identifier(id2,symtable).equals("int"))
				throw new Exception("Error: Wrong type ");
		}
		return "TimesExpression";
	}

	/**
	* f0->  PrimaryExpression
	* f1 -> "["
	* f2 -> PrimaryExpression
	* f3 -> "]"
	*/
	public String visit(ArrayLookup n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		String id1=this.Current;
		String type=Search_Identifier(id1,symtable);
		if(type==null)
			throw new Exception("Error: Variable "+id1+ "has not declared");
		n.f2.accept(this,symtable);
		String id2=this.Current;
		String type2=Search_Identifier(id2,symtable);
		if(id2.equals("##INT_LIT"))
			type2="int";
		if(!type2.equals("int"))
			throw new Exception("Error : expected int found "+ type2);
		if(type.equals("int[]"))
			type="int";
		else if(type.equals("boolean[]"))
			type="boolean";
		else 
			throw new Exception("Error:Expected Array  found type "+type);
		this.Current=type;
		return "ArrayLookup";
	}


	/**
	* f0 -> PrimaryExpression
	* f1 -> "."
	* f2 -> "length"
	*/
	public String visit(ArrayLength	n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		String id1=this.Current;
		String type=Search_Identifier(id1,symtable);
		if(type==null)
			throw new Exception("Error : Variable "+ id1+ " has not declared");
		if(!type.equals("int[]")&&!type.equals("boolean[]"))
			throw new Exception("Error : Expected array found "+type);
		this.Current="int";
		return "ArrayLength";
	}




	/** 
	* f0 -> PrimaryExpression
	* f1 -> "."
	* f2 -> Identifier
	* f3 -> "("
	* f4 ->  ( ExpressionList )?
	* f5 -> ")"
	*/
	public String visit(MessageSend n,SymbolTable symtable) throws Exception{
		String val=null;
		String case1=n.f0.accept(this,symtable);
		if(case1.equals("Identifier")){
			String Class_name=this.Current;
			String type=Search_Identifier(Class_name,symtable);
			if(type==null)
				throw new Exception("Error: Class "+ Class_name+ " has not declared");
            SymbolTable.ClassInfo ob =symtable.My_map.get(type);
            n.f2.accept(this,symtable);
            String fun_name=this.Current;
            if(fun_name==null)
            	throw new Exception("Error: Missing function name");
        	if(!ob.Method_map.containsKey(fun_name))
            	throw new Exception("Error: "+ fun_name+ "does not exist on class "+ Class_name);
            SymbolTable.MethodInfo f= ob.Method_map.get(fun_name);
            arg.clear();
            n.f4.accept(this,symtable);
            check_args(arg,symtable,type,fun_name);
            arg.clear();
            this.Current=f.return_type;
		}
		else if(case1.equals("AllocationExpression")){
			String Class_name=this.Current;
			if(!symtable.My_map.containsKey(Class_name))
            	throw new Exception("Error:Object "+ Class_name+ " does not exist");
            SymbolTable.ClassInfo ob =symtable.My_map.get(Class_name);
            n.f2.accept(this,symtable);
            String fun_name=this.Current;
            if(fun_name==null)
            	throw new Exception("Error: Missing function name");
        	if(!ob.Method_map.containsKey(fun_name))
            	throw new Exception("Error: "+ fun_name+ "does not exist on class "+ Class_name);
            SymbolTable.MethodInfo f= ob.Method_map.get(fun_name);
            arg.clear();
            n.f4.accept(this,symtable);
            check_args(arg,symtable,Class_name,fun_name);
            arg.clear();
            this.Current=f.return_type;
		}
		else if(case1.equals("this")){
			SymbolTable.ClassInfo ob =symtable.My_map.get(this.ClassName);
			if(ob.Method_map.containsKey("main"))
				throw new Exception("Error: this operator can not be used to main");
			n.f2.accept(this,symtable);
            String fun_name=this.Current;
            if(fun_name==null)
            	throw new Exception("Error: Missing function name");
        	if(!ob.Method_map.containsKey(fun_name))
            	throw new Exception("Error: "+ fun_name+ "does not exist on class "+ ClassName);
            SymbolTable.MethodInfo f= ob.Method_map.get(fun_name);
            arg.clear();
            n.f4.accept(this,symtable);
            check_args(arg,symtable,ClassName,fun_name);
            arg.clear();
            this.Current=f.return_type;
		}
		else{
			String Class_name=this.Current;
			String type=Search_Identifier(Class_name,symtable);
			SymbolTable.ClassInfo ob =symtable.My_map.get(Class_name);
			if(type==null&&ob==null)
				throw new Exception("Error: Class "+ Class_name+ " has not declared");
			n.f2.accept(this,symtable);
			String fun_name=this.Current;
            if(fun_name==null)
            	throw new Exception("Error: Missing function name");
        	if(!ob.Method_map.containsKey(fun_name))
            	throw new Exception("Error: "+ fun_name+ "does not exist on class "+ Class_name);
            SymbolTable.MethodInfo f= ob.Method_map.get(fun_name);
            arg.clear();
            n.f4.accept(this,symtable);
			check_args(arg,symtable,ClassName,fun_name);
            arg.clear();
            this.Current=f.return_type;
		}
		return "MessageSend";
	}
	/**
	* f0 -> Expression
	* f1 -> ExpressionTail
	*/
	public String visit(ExpressionList n,SymbolTable symtable) throws Exception{
		String id=n.f0.accept(this,symtable);
		if(thisarg==false)
			arg.add(this.Current);
		else{
			arg.add("this");
			thisarg=false;
		}
		n.f1.accept(this,symtable);
		return null;
	}


	/**
	* f0 -> (ExpressionTerm)*
	*/
	public String visit(ExpressionTail n,SymbolTable symtable) throws Exception{
		n.f0.accept(this,symtable);
		return null;
	}

	

	
	/**
	* f0 -> ","
	* f1 -> Expression
	*/
	public String visit(ExpressionTerm n,SymbolTable symtable) throws Exception{
		n.f1.accept(this,symtable);
		arg.add(this.Current);
		return null;
	}

}
