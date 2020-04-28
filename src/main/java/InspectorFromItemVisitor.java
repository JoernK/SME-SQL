import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;

public class InspectorFromItemVisitor implements FromItemVisitor {
    private boolean foundProblem = false;
    private ArrayList<String> problemList = new ArrayList<String>();
    private Table fromTable;
    @Override
    public void visit(Table tableName) {
        fromTable = tableName;
    }

    @Override
    public void visit(SubSelect subSelect) {
        foundProblem = true;
        problemList.add("Sub select in From");
    }

    @Override
    public void visit(SubJoin subjoin) {
        foundProblem = true;
        problemList.add("Subjoin in From");
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        foundProblem = true;
        problemList.add("Lateral Subselect in From");
    }

    @Override
    public void visit(ValuesList valuesList) {
        foundProblem = true;
        problemList.add("Values List in From");
    }

    @Override
    public void visit(TableFunction tableFunction) {
        foundProblem = true;
        problemList.add("Table Function");
    }


    @Override
    public void visit(ParenthesisFromItem aThis) {
        foundProblem = true;
        problemList.add("Parenthesis From");
    }


    public boolean foundProblem() {
        return foundProblem;
    }

    public ArrayList<String> getProblemList() {
        return problemList;
    }

    public Table getFromTable() {
        return fromTable;
    }
}
