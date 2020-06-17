import java.io.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
class Off{/*Offset class*/
	public String name;
	public int variable=0;
	public int Function=0;
}
public  class SymbolTable {
	public   LinkedHashMap<String,ClassInfo> My_map;
	SymbolTable(){
		My_map=new  LinkedHashMap<String,ClassInfo>();
	}
    public static class  ClassInfo{
    	public String parent_Class;
        public String Name;
    	public boolean Main_Class;
    	public LinkedHashMap<String,String> Field_map;
    	public LinkedHashMap<String,MethodInfo> Method_map;
    	ClassInfo(){
    		this.parent_Class=null;
    		this.Main_Class=false;
            this.Name=null;
    		this.Field_map=new LinkedHashMap<String,String>();
    		this.Method_map= new LinkedHashMap<String,MethodInfo>();
    	}
    }
    public static class MethodInfo{
    	public String name;
    	public String return_type;
    	public boolean overriding;
    	public LinkedHashMap<String,String> parameters;
    	public LinkedHashMap<String,String> variables;
    	MethodInfo(){
    		this.name=null;
    		this.return_type=null;
    		this.overriding=false;
    		this.parameters=new LinkedHashMap<String,String>();
    		this.variables=new LinkedHashMap<String,String>();
    	}
    }
    public void Calculate_Offsets() throws Exception{
    	String input_class=null;
    	int num=My_map.size();
    	Off[] offtable=new Off[num-1];
    	for(int i=0;i<num-1;i++)
    		offtable[i]=new Off();
    	for (Map.Entry<String, ClassInfo> entry : My_map.entrySet()) {
    		String class_name = entry.getKey();
        	SymbolTable.ClassInfo ob =My_map.get(class_name);
        	SymbolTable.MethodInfo f=ob.Method_map.get("main");
        	if(f!=null)
        		input_class=class_name;
    	}
    	int i=0;
    	for (Map.Entry<String, ClassInfo> entry : My_map.entrySet()){
    		String class_name = entry.getKey();
    		if(class_name.equals(input_class))
    			continue;
    		offtable[i++].name=class_name;
    	}
 		for(i=0;i<num-1;i++){
 			String Classname=offtable[i].name;
 			SymbolTable.ClassInfo ob =My_map.get(Classname);
 			if(ob.parent_Class==null){/* no extends*/
 				for(Map.Entry<String,String> val1 : ob.Field_map.entrySet()){
 					System.out.println(Classname+"."+val1.getKey()+" : "+offtable[i].variable);
 					if(val1.getValue().equals("int"))
 						offtable[i].variable+=4;
 					else if(val1.getValue().equals("boolean"))
 						offtable[i].variable+=1;
 					else 
 						offtable[i].variable+=8;
 				}
 				for(Map.Entry<String,MethodInfo> val2 : ob.Method_map.entrySet()){
 					System.out.println(Classname+"."+val2.getKey()+" : "+offtable[i].Function);
 					offtable[i].Function+=8;
 				}
 			}
 			else{
 				String parent_nam=ob.parent_Class;
 				int position=-1;
 				for(int j=0;j<num-1;j++){
 					if(offtable[j].name.equals(parent_nam))
 						position=j;
 				}
 				offtable[i].variable=offtable[position].variable;
 				offtable[i].Function=offtable[position].Function;
 				for(Map.Entry<String,String> val1 : ob.Field_map.entrySet()){
 					System.out.println(Classname+"."+val1.getKey()+" : "+offtable[i].variable);
 					if(val1.getValue().equals("int"))
 						offtable[i].variable+=4;
 					else if(val1.getValue().equals("boolean"))
 						offtable[i].variable+=1;
 					else 
 						offtable[i].variable+=8;
 				}
 				for(Map.Entry<String,MethodInfo> val2 : ob.Method_map.entrySet()){
 					SymbolTable.MethodInfo f=ob.Method_map.get(val2.getKey());
 					if(f.overriding==true)
 						continue;
 					System.out.println(Classname+"."+val2.getKey()+" : "+offtable[i].Function);
 					offtable[i].Function+=8;
 				}
 			}
 		}
    }




    public  void Check_Types() throws Exception{
        for (Map.Entry<String, ClassInfo> entry : My_map.entrySet()) {
          String class_name = entry.getKey();
          SymbolTable.ClassInfo ob =My_map.get(class_name);
          if(ob.Main_Class==true){
            String name_fun="main";
            SymbolTable.MethodInfo f=ob.Method_map.get(name_fun);
            for(Map.Entry<String,String> value : f.variables.entrySet()){
                String type_of_variable=value.getValue();
                int enter=0;
                if (type_of_variable.equals("int")||type_of_variable.equals("boolean")||type_of_variable.equals("boolean[]")||type_of_variable.equals("int[]"))
                    enter=1;
                String cl=null;
                for(Map.Entry<String,ClassInfo>  de:My_map.entrySet()){
                    cl=de.getKey();
                    if(type_of_variable.equals(cl))
                        enter=1;
                }
                if(enter==0)
                    throw new Exception("Error: Illegal type of variable  "+ value.getKey()+" on method main ");
            }
          }
          else{
             for(Map.Entry<String,String> value : ob.Field_map.entrySet()){
                String type_of_variable=value.getValue();
                int enter=0;
                if (type_of_variable.equals("int")||type_of_variable.equals("boolean")||type_of_variable.equals("boolean[]")||type_of_variable.equals("int[]"))
                    enter=1;    
                String cl=null;
                for(Map.Entry<String,ClassInfo>  de:My_map.entrySet()){
                    cl=de.getKey();
                    if(type_of_variable.equals(cl))
                        enter=1;
                }
                if(enter==0)
                    throw new Exception("Error: Illegal type of variable  "+ value.getKey()+" on class "+ class_name);

            }
            for(Map.Entry<String,MethodInfo> value : ob.Method_map.entrySet()){
                String Name_of_Fun=value.getKey();
                SymbolTable.MethodInfo f=ob.Method_map.get(Name_of_Fun);
                String return_type=f.return_type;
                int enter=0;
                if(return_type.equals("int")||return_type.equals("boolean")||return_type.equals("boolean[]")||return_type.equals("int[]"))
                    enter=1;
                String cl=null;
                for(Map.Entry<String,ClassInfo>  de:My_map.entrySet()){
                    cl=de.getKey();
                    if(return_type.equals(cl))
                        enter=1;
                }
                if(enter==0)
                    throw new Exception("Error: Illegal type of return value  "+ return_type+" on method "+f.name);
                for(Map.Entry<String,String> e : f.parameters.entrySet()){
                    enter=0;
                    String type_of_parameter=e.getValue();
                    if(type_of_parameter.equals("int")||type_of_parameter.equals("boolean")||type_of_parameter.equals("boolean[]")||type_of_parameter.equals("int[]"))
                        enter=1;
                    for(Map.Entry<String,ClassInfo>  de:My_map.entrySet()){
                        cl=de.getKey();
                        if(type_of_parameter.equals(cl))
                            enter=1;
                    }
                    if(enter==0)
                        throw new Exception("Error: Illegal type of parameter  "+ e.getKey()+" on method "+ f.name);
                }
                for(Map.Entry<String,String> e : f.variables.entrySet()){
                    enter=0;
                    String type_of_variable=e.getValue();
                    if(type_of_variable.equals("int")||type_of_variable.equals("boolean")||type_of_variable.equals("boolean[]")||type_of_variable.equals("int[]"))
                        enter=1;
                    for(Map.Entry<String,ClassInfo>  de:My_map.entrySet()){
                        cl=de.getKey();
                        if(type_of_variable.equals(cl))
                            enter=1;
                    }
                    if(enter==0)
                        throw new Exception("Error: Illegal type of variable  "+ e.getKey()+" on method "+ f.name);
                }
                if(f.overriding==true){
                    String name_of_parent_class=ob.parent_Class;
                    SymbolTable.ClassInfo parent =My_map.get(name_of_parent_class);
                    SymbolTable.MethodInfo f1=parent.Method_map.get(Name_of_Fun);
                    String return_type_ofparent=f1.return_type;
                    String return_type_ofchild=f.return_type;
                    if(!return_type_ofparent.equals(return_type_ofchild))
                        throw new Exception("Error: "+f1.name+" cannot Override: "+return_type_ofchild+" is not compatible with "+return_type_ofparent);
                    if(f1.parameters.size()!=f.parameters.size())
                        throw new Exception("Error:Cannot Override-Not same Prototype on Function "+f1.name);
                    int i=0;
                    for(Map.Entry<String,String> timh:f.parameters.entrySet()){
                        String type=timh.getValue();
                        String id=timh.getKey();
                        int val=0;
                        for(Map.Entry<String,String> timh1:f1.parameters.entrySet()){
                            String typ1=timh1.getValue();
                            String id1=timh1.getKey();
                            if(i==val){
                                if(!typ1.equals(type)||!id1.equals(id))
                                    throw new Exception("Error:Cannot Override-Not same Prototype on Function "+f1.name);
                            }
                            val++;
                        }
                        i++;
                    }
                }
            }
          }
       }
    }
}

