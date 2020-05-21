import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;

public class InspectorWhereExpressionVisitor implements ExpressionVisitor {
    boolean high = false;
    private boolean foundProblem = false;
    private String tablename;

    public InspectorWhereExpressionVisitor(String tablename){
        this.tablename = tablename;
    }
    private ArrayList<String> problemList = new ArrayList<String>();
    public ArrayList<String> accessColumnList = new ArrayList<String>();

    @Override
    public void visit(BitwiseRightShift aThis) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(BitwiseLeftShift aThis) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(NullValue nullValue) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Function function) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(SignedExpression signedExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(LongValue longValue) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(HexValue hexValue) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(DateValue dateValue) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(TimeValue timeValue) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }

    @Override
    public void visit(StringValue stringValue) {
        foundProblem = true;
        problemList.add("Wrong Where Clause");
    }

    @Override
    public void visit(Addition addition) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Division division) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(IntegerDivision division) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Multiplication multiplication) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Subtraction subtraction) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(AndExpression andExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(OrExpression orExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Between between) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        InspectorExpressionVisitor ievLeft = new InspectorExpressionVisitor();
        Expression left = equalsTo.getLeftExpression();
        left.accept(ievLeft);
        if(ievLeft.foundProblem()) {
            foundProblem = true;
            this.problemList.addAll(ievLeft.getProblemList());
        } else {
            for(String column: accessColumnList) {
                if(ExampleDBACL.isColumnInTableHigh(tablename, column)){
                    this.high = true;
                }
            }
            accessColumnList.addAll(ievLeft.getAccessColumnList());
            InspectorExpressionVisitor ievRight = new InspectorExpressionVisitor(true);
            Expression right = equalsTo.getRightExpression();
            right.accept(ievRight);
            if(ievRight.foundProblem()){
                this.foundProblem = true;
                this.problemList.addAll(ievRight.getProblemList());
            }
        }
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        this.foundProblem = true;
        problemList.add("Greater than");
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        this.foundProblem = true;
        problemList.add("Greater than");
    }

    @Override
    public void visit(InExpression inExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(FullTextSearch fullTextSearch) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(MinorThan minorThan) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Column tableColumn) {
        this.foundProblem = true;
        this.problemList.add("Table Reference where only values are allowed");
    }

    @Override
    public void visit(SubSelect subSelect) {
        foundProblem = true;
        problemList.add("Sub Selects not allowed");
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(WhenClause whenClause) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Concat concat) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Matches matches) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(CastExpression cast) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(Modulo modulo) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(AnalyticExpression aexpr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(ExtractExpression eexpr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(IntervalExpression iexpr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(OracleHierarchicalExpression oexpr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(RegExpMatchOperator rexpr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(JsonOperator jsonExpr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(UserVariable var) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(NumericBind bind) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(KeepExpression aexpr) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(MySQLGroupConcat groupConcat) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(ValueListExpression valueList) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(OracleHint hint) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(DateTimeLiteralExpression literal) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(NotExpression aThis) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(NextValExpression aThis) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(CollateExpression aThis) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(SimilarToExpression aThis) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }

    @Override
    public void visit(ArrayExpression aThis) {
        foundProblem = true;
        problemList.add("Type of Expression not supported");
    }



    public boolean foundProblem() {
        return foundProblem;
    }

    public ArrayList<String> getProblemList() {
        return problemList;
    }

    public ArrayList<String> getAccessColumnList() {
        return accessColumnList;
    }
}
