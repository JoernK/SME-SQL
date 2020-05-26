import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.util.deparser.StatementDeParser;


import java.awt.geom.RectangularShape;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static boolean stdOut = true;
    static boolean highStdOut = true;

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
                if(vis.lowStatement != null) {
                    lowStatements.add(vis.lowStatement);
                } else {
                    lowStatements.add("dummy");
                };
                if(vis.highStatement != null) {
                    highStatements.add(vis.highStatement);
                } else {
                    highStatements.add("dummy");
                }
            }

        } catch (JSQLParserException e) {
            System.out.println("Error: SQL Parser Exception");
            System.out.println("Details: " + e.getCause().toString());
            System.exit(1);
        }
        assert lowStatements.size() == highStatements.size();
        for(int i = 0; i < lowStatements.size(); i++) {
            System.out.println((i+1)+"th high execution: " + highStatements.get(i));
            System.out.println((i+1)+"th low execution: " + lowStatements.get(i));
        }

        Connection conn = DBConnector.connect();
        if(conn == null) {
            System.err.println("No database connected exit.");
            System.exit(2);
        }


        //Low execution
        for(int i = 0; i < lowStatements.size(); i++) {
            try {
                java.sql.Statement cstathigh = conn.createStatement();
                String curHighStr = highStatements.get(i);
                if(!curHighStr.equals("dummy")) {
                    if (curHighStr.contains("UPDATE")) {
                        cstathigh.executeUpdate(curHighStr);
                    } else if (curHighStr.contains("SELECT")) {
                        ResultSet resultHigh = cstathigh.executeQuery(highStatements.get(i));
                        if (stdOut && highStdOut) {
                            while (resultHigh.next()) {
                                for (int j = 0; j < 10; j++) {
                                    try {
                                        String cur = resultHigh.getString(j);
                                        if (cur != null) {
                                            System.out.print(cur.trim() + "\t|\t");
                                        } else {
                                            System.out.print("NULL" + "\t|\t");
                                        }

                                    } catch (SQLException e) {

                                    }
                                }
                                System.out.print("\n");
                            }
                        }
                    } else {
                        cstathigh.execute(curHighStr);
                    }
                }


                java.sql.Statement cstat = conn.createStatement();
                String curLowStr = lowStatements.get(i);
                if(!curLowStr.equals("dummy")) {
                    if(curLowStr.contains("UPDATE")) {
                        cstat.executeUpdate(curLowStr);
                    } else if(curLowStr.contains("SELECT")){
                        ResultSet result = cstat.executeQuery(curLowStr);
                        if (stdOut && !highStdOut) {
                            while (result.next()) {
                                for (int j = 0; j < 10; j++) {
                                    try {
                                        String cur = result.getString(j);
                                        if (cur != null) {
                                            System.out.print(cur.trim() + "\t|\t");
                                        } else {
                                            System.out.print("NULL" + "\t|\t");
                                        }

                                    } catch (SQLException e) {

                                    }
                                }
                                System.out.print("\n");
                            }
                        }
                    } else {
                    cstat.execute(curLowStr);
                }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
