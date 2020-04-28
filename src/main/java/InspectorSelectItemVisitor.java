import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;

import javax.management.AttributeList;
import java.util.ArrayList;

public class InspectorSelectItemVisitor implements SelectItemVisitor {

    private boolean foundProblem = false;
    private ArrayList<String> problemList = new ArrayList<String>();

    private Table fromTable;
    private ArrayList<Column> accessColumnList = new ArrayList<Column>();

    @Override
    public void visit(AllColumns allColumns) {
        foundProblem = true;
        problemList.add("* Not Supported");
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        foundProblem = true;
        problemList.add("* Not Supported");
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        if (selectExpressionItem.getAlias() != null) {
            foundProblem = true;
            problemList.add("Alias not supported");
        }
        if (selectExpressionItem.getExpression() == null) {
            foundProblem = true;
            problemList.add("Empty Select expression");
        }
        InspectorExpressionVisitor inspectorExpressionVisitor = new InspectorExpressionVisitor();
        selectExpressionItem.getExpression().accept(inspectorExpressionVisitor);
        if (inspectorExpressionVisitor.foundProblem()) {
            foundProblem = true;
            problemList.addAll(inspectorExpressionVisitor.getProblemList());
        } else {

            accessColumnList.addAll(inspectorExpressionVisitor.getAccessColumnList());
        }

    }

    public ArrayList<String> getProblemList() {
        return problemList;
    }

    public boolean foundProblem() {
        return foundProblem;
    }
    public ArrayList<Column> getAccessColumnList() {
        return accessColumnList;
    }

    public Table getFromTable() {
        return fromTable;
    }
}
