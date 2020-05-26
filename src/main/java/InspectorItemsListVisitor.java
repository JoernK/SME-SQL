import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NamedExpressionList;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;

public class InspectorItemsListVisitor implements ItemsListVisitor {
    ArrayList<Expression> expressionsList = new ArrayList<Expression>();
    boolean foundProblem = false;
    ArrayList<String> problemList = new ArrayList<String>();

    @Override
    public void visit(SubSelect subSelect) {
        foundProblem = true;
        problemList.add(subSelect.toString());
    }

    @Override
    public void visit(ExpressionList expressionList) {
        this.expressionsList.addAll(expressionList.getExpressions());
    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {
        foundProblem = true;
        problemList.add(namedExpressionList.toString());
    }

    @Override
    public void visit(MultiExpressionList multiExprList) {
        foundProblem = true;
        problemList.add(multiExprList.toString());
    }
}
