import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.util.deparser.StatementDeParser;


import java.sql.Connection;
import java.sql.SQLException;
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

        ArrayList<String> lowStatements = new ArrayList<String>();
        ArrayList<String> highStatements = new ArrayList<String>();

        try {
            for(String arg: allArgs){
                Statement stat = CCJSqlParserUtil.parse(arg);
                InspectorStatementVisitor vis = new InspectorStatementVisitor();
                stat.accept(vis);
                if(vis.foundProblem()){
                    System.out.println("Error: Unsupported SQL operation");
                    System.out.println(vis.problemList);
                    System.exit(1);
                }
                lowStatements.add(vis.lowStatement);
                highStatements.add(vis.highStatement);
            }

        } catch (JSQLParserException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Low Execution: " + lowStatements);
        System.out.println("High Execution: " + highStatements);

        Connection conn = DBConnector.connect();
        if(conn == null) {
            System.err.println("No database connected exit.");
            System.exit(2);
        }

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
