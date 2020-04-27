import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.util.deparser.StatementDeParser;


import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String argument;
        argument = scan.nextLine();
        try {
            Statement stat = CCJSqlParserUtil.parse(argument);

            InspectorStatementVisitor vis = new InspectorStatementVisitor();
            stat.accept(vis);
            if(vis.foundProblem()){
                System.out.println("Error: Unsupported SQL operation");
                System.out.println(vis.problemList);
            }


        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }
}
