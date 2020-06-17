import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.Iterator;

//@SuppressWarnings("Duplicates") // Remove IntelliJ warning about duplicate code
public class FirstVisitor extends GJDepthFirst<String,SymbolTable>{
    public String ClassName; /*Current Class Name*/
    public boolean On_Class; /* Check if we are on class or main method*/
    public String Fun_name_of_Class;/*Current Fun Name*/
    public boolean On_Fun;	/* Check if we are on Field decl or Method decl*/ 
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
        String class_name=n.f1.accept(this,symtable);
        if(symtable.My_map.containsKey(class_name)){
            throw new Exception("Error:Main Class Have Been Declared");
        }
        symtable.My_map.put(class_name,new SymbolTable.ClassInfo());
        this.ClassName=class_name;
        SymbolTable.ClassInfo ob =symtable.My_map.get(class_name);
        ob.parent_Class=null;
        ob.Main_Class=true;
        ob.Name=class_name;
        this.On_Fun=true;
        this.On_Class=false;
        String fun_name="main";
        String return_type="void";
        this.Fun_name_of_Class="main";
        boolean overriding_method=false;
        String id1=n.f11.accept(this,symtable);
        ob.Method_map.put(fun_name,new SymbolTable.MethodInfo());
        SymbolTable.MethodInfo f=ob.Method_map.get(fun_name);
        f.name=fun_name;
        f.return_type=return_type;
        f.overriding=overriding_method;
        f.parameters.put(id1,"String[]");
        n.f14.accept(this,symtable);
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
        String class_name=n.f1.accept(this,symtable);
        if(symtable.My_map.containsKey(class_name)){
            throw new Exception("Error:Class Have Been Declared");
        }
        symtable.My_map.put(class_name,new SymbolTable.ClassInfo());
        this.ClassName=class_name;
        SymbolTable.ClassInfo ob =symtable.My_map.get(class_name);
        ob.Name=class_name;
        ob.parent_Class=null;
        ob.Main_Class=false;
        this.On_Class=true;
        this.On_Fun=false;
        n.f3.accept(this,symtable);
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
        String class_name=n.f1.accept(this,symtable);
        if(symtable.My_map.containsKey(class_name)){
            throw new Exception("Error:Class Have Been Declared");
        }
        String parent_class=n.f3.accept(this,symtable);
        if(!(symtable.My_map.containsKey(parent_class)))
            throw new Exception("Error:Mother class "+parent_class+ " Does not Exist" );
        symtable.My_map.put(class_name,new SymbolTable.ClassInfo());
        this.ClassName=class_name;
        SymbolTable.ClassInfo ob =symtable.My_map.get(class_name);
        ob.Name=class_name;
        ob.parent_Class=parent_class;
        ob.Main_Class=false;
        this.On_Class=true;
        this.On_Fun=false;
        n.f5.accept(this,symtable);
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
    * f12->"}" 
    */
    public String visit(MethodDeclaration n,SymbolTable symtable) throws Exception{
        String class_name=this.ClassName;
        String return_type=n.f1.accept(this,symtable);
        String name_fun=n.f2.accept(this,symtable);
        this.Fun_name_of_Class=name_fun;
        this.On_Class=false;
        this.On_Fun=true;
        SymbolTable.ClassInfo ob =symtable.My_map.get(class_name);
        if(ob.Method_map.containsKey(name_fun))
            throw new Exception("Method "+name_fun+ " is already defined in class "+this.ClassName);
        ob.Method_map.put(name_fun,new SymbolTable.MethodInfo());
        SymbolTable.MethodInfo f=ob.Method_map.get(name_fun);
        f.return_type=return_type;
        f.name=name_fun;
        if(ob.parent_Class!=null){
            SymbolTable.ClassInfo e =symtable.My_map.get(ob.parent_Class);
            SymbolTable.MethodInfo some=e.Method_map.get(name_fun);
            if(some!=null)
                f.overriding=true;
            else
                f.overriding=false;

        }
        n.f4.accept(this,symtable);
        n.f7.accept(this,symtable);
        return null;
    }

    /**
    * f0-> FormalParameter
    * f1-> FormalParameterTail
    */
    public String visit(FormalParameterList n,SymbolTable symtable) throws Exception{
        n.f0.accept(this,symtable);
        n.f1.accept(this,symtable);
        return null;
    }

    /**
    * f0-> FormalParameterTerm
    */
    public String visit(FormalParameterTail n,SymbolTable symtable) throws Exception{
        n.f0.accept(this,symtable);
        return null;
    }

    /**
    * f0-> ","
    * f1-> FormalParameter
    */
    public String visit(FormalParameterTerm n,SymbolTable symtable) throws Exception{
        n.f1.accept(this,symtable);
        return null;
    }

    /**
    * f0-> Type
    * f1-> Identifier
    */
    public String visit(FormalParameter n,SymbolTable symtable) throws Exception{
        String type=n.f0.accept(this,symtable);
        String id=n.f1.accept(this,symtable);
        SymbolTable.ClassInfo ob =symtable.My_map.get(this.ClassName);
        SymbolTable.MethodInfo f=ob.Method_map.get(this.Fun_name_of_Class);
        if(f.parameters.containsKey(id))
            throw new Exception("variable "+id+" is already defined in method "+this.Fun_name_of_Class);
        f.parameters.put(id,type);
        return null;
    }


    /**
    *f0 -> Type()
    *f1 -> Identifier()
    *f2 -> ";"
    */
    public String visit(VarDeclaration n,SymbolTable symtable) throws Exception{
        String type=n.f0.accept(this,symtable);
        String id=n.f1.accept(this,symtable);
        String name_current_class=this.ClassName;
        SymbolTable.ClassInfo ob =symtable.My_map.get(name_current_class);
        if(this.On_Class==true){
            if(ob.Field_map.containsKey(id))
                throw new Exception("Error:variable "+id+" is already defined in Class "+this.ClassName);
            ob.Field_map.put(id,type);
        }
        else{
            SymbolTable.MethodInfo f=ob.Method_map.get(this.Fun_name_of_Class);
            if(f.variables.containsKey(id))
                throw new Exception("Error:variable "+id+" is already defined in method "+this.Fun_name_of_Class);
            f.variables.put(id,type);
        }
        return null;
    }




    /**
    *f0 -> int
    */
    public String visit(IntegerType n,SymbolTable symtable) throws Exception{ return "int"; }

    /**
    *f0 -> boolean
    */
    public String visit(BooleanType n,SymbolTable symtable) throws Exception{ return "boolean"; }

    /**
    *f0 -> int[]
    */
    public String visit(IntegerArrayType n,SymbolTable symtable) throws Exception{ return "int[]"; }


    /**
    * f0-> bool[]
    */
    public String visit(BooleanArrayType n,SymbolTable symtable) throws Exception{ return "boolean[]"; }


    /**
    *f0 -> Identifier()
    */
    public String visit(Identifier n,SymbolTable symtable) throws Exception{ return n.f0.toString(); }
}
