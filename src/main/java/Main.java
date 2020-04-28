import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.util.deparser.StatementDeParser;


import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String argument;
        ArrayList<String> allArgs = new ArrayList<String>();
        System.out.println("Insert SQL Statements. Exit with '#'");
        do {

            argument = scan.nextLine();
            if(argument.equals("#")) {
                break;
            } else {
                allArgs.add(argument);
            }
        } while (true);
        try {
            for(String arg: allArgs){
                Statement stat = CCJSqlParserUtil.parse(arg);
                InspectorStatementVisitor vis = new InspectorStatementVisitor();
                stat.accept(vis);
                if(vis.foundProblem()){
                    System.out.println("Error: Unsupported SQL operation");
                    System.out.println(vis.problemList);
                }
            }

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }
}
