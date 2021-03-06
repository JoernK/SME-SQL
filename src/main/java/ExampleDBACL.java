import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExampleDBACL {
    private static HashMap<String, HashMap<String, Boolean>> highColumnInTable = setUp();

    public static HashMap<String, HashMap<String, Boolean>> setUp(){
        HashMap<String, HashMap<String, Boolean>> highColumnInTable = new HashMap<String, HashMap<String, Boolean>>();
        HashMap<String, Boolean> all_cs = new HashMap<String, Boolean>();
        all_cs.put("column_name",false);
        highColumnInTable.put("all_column_names_students",all_cs);

        HashMap<String, Boolean> studs = new HashMap<String, Boolean>();
        studs.put("cid", false);
        studs.put("name", true);
        studs.put("subject", false);
        studs.put("course_level", true);
        studs.put("zip", true);
        studs.put("city", true);
        studs.put("phone", true);
        highColumnInTable.put("students",studs);
        return highColumnInTable;
    }

    public static boolean isColumnInTableHigh(String tableName, String columnName) {
        HashMap<String, Boolean> columnMap =  highColumnInTable.get(tableName.trim().toLowerCase());
        if(columnMap == null) {
            System.out.println("Error: No ACL for table: "+ tableName);
            System.exit(3);
        }
        Boolean ret = columnMap.get(columnName.trim().toLowerCase());
        if(ret == null) {
            System.out.println("Error: No ACL entry for column " + columnName + "in table " + tableName);
            System.exit(4);
        }
        return ret;
    }

    public static List<String> getLowColumns(String tableName){
        HashMap<String, Boolean> columnMap =  highColumnInTable.get(tableName.trim().toLowerCase());
        ArrayList<String> lowCols = new ArrayList<String>();
        for(HashMap.Entry<String, Boolean> entry : columnMap.entrySet()){
            if(!entry.getValue()){
                lowCols.add(entry.getKey());
            }
        }
        return lowCols;
    }

}
