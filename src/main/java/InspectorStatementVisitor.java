import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.ArrayList;
import java.util.List;

public class InspectorStatementVisitor implements StatementVisitor {

    String highStatement;
    String lowStatement;
    private boolean foundProblem = false;
    ArrayList<String> problemList;
    public InspectorStatementVisitor(){
        problemList = new ArrayList<String>();
        foundProblem = false;
    }


    @Override
    public void visit(Comment comment) {
        foundProblem = true;
        problemList.add(comment.toString());
    }

    @Override
    public void visit(Commit commit) {
        foundProblem = true;
        problemList.add(commit.toString());
    }

    @Override
    public void visit(Delete delete) {
        foundProblem = true;
        problemList.add(delete.toString());
    }

    @Override
    public void visit(Update update) {
        StringBuilder highExec = new StringBuilder("UPDATE ");
        StringBuilder lowExec = new StringBuilder("UPDATE ");
        ArrayList<String> writeColumnsLow = new ArrayList<String>();
        String tableName = update.getTable().toString();

        highExec.append(tableName).append(" SET ");
        lowExec.append(tableName).append(" SET ");
        if (update.getJoins() != null) {
            foundProblem = true;
            problemList.add(update.getJoins().toString());
        }

        int numberHighColumns = 0;
        int numberLowColumns = 0;
        if (!update.isUseSelect()) {
            for (int i= 0; i < update.getColumns().size(); i++) {
                // Inspect columns
                String column = update.getColumns().get(i).toString();

                boolean high = ExampleDBACL.isColumnInTableHigh(tableName, column);
                if(!high) {
                    writeColumnsLow.add(column);
                }
                // Inspect expression
                Expression expression = update.getExpressions().get(i);
                InspectorExpressionVisitor iev = new InspectorExpressionVisitor(true);
                expression.accept(iev);
                if(iev.foundProblem()){
                    this.foundProblem = true;
                    problemList.addAll(iev.getProblemList());
                } else {
                    if(high) {
                        if(numberHighColumns != 0){
                            highExec.append(", ");
                        }
                        highExec.append(column).append(" = ").append(expression.toString());
                        numberHighColumns++;
                    } else {
                        if(numberLowColumns != 0){
                            lowExec.append(", ");
                        }
                        lowExec.append(column).append(" = ").append(expression.toString());
                        numberLowColumns++;
                    }

                }

            }
        } else {
            this.foundProblem = true;
            problemList.add("Select inside UPDATE");
        }
        if (update.getFromItem() != null) {
            this.foundProblem = true;
            problemList.add("FROM" + update.getFromItem().toString());
        }

        if (update.getWhere() != null) {
            InspectorWhereExpressionVisitor iev = new InspectorWhereExpressionVisitor(tableName);
            update.getWhere().accept(iev);
            String whereColumn = iev.accessColumnList.get(0);
            if (iev.foundProblem()) {
                this.foundProblem = true;
                problemList.addAll(iev.getProblemList());
            } else if(writeColumnsLow.contains(whereColumn) && numberHighColumns > 0){
                this.foundProblem = true;
                problemList.add("WHERE column part of UPDATE column");
            } else {
                highExec.append(" WHERE ").append(update.getWhere().toString());
                if(iev.high) {
                    lowExec = null;
                } else {
                    lowExec.append(" WHERE ").append(update.getWhere().toString());
                }
            }
            if (update.getOrderByElements() != null) {
                this.foundProblem = true;
                problemList.add(PlainSelect.orderByToString(update.getOrderByElements()));
            }
            if (update.getLimit() != null) {
                this.foundProblem = true;
                problemList.add(update.getLimit().toString());
            }

            if (update.isReturningAllColumns()) {
                this.foundProblem = true;
                problemList.add(" RETURNING *");
            } else if (update.getReturningExpressionList() != null) {
                this.foundProblem = true;
                problemList.add(" RETURNING *");
            }
        }
        if(numberHighColumns != 0) {
            this.highStatement = highExec.toString();
        }
        if(lowExec != null){
            if(numberLowColumns != 0){
                this.lowStatement = lowExec.toString();
            }
        }
    }

    @Override
    public void visit(Insert insert) {
        foundProblem = true;
        problemList.add(insert.toString());
    }

    @Override
    public void visit(Replace replace) {
        foundProblem = true;
        problemList.add(replace.toString());
    }

    @Override
    public void visit(Drop drop) {
        foundProblem = true;
        problemList.add(drop.toString());
    }

    @Override
    public void visit(Truncate truncate) {
        foundProblem = true;
        problemList.add(truncate.toString());
    }

    @Override
    public void visit(CreateIndex createIndex) {
        foundProblem = true;
        problemList.add(createIndex.toString());
    }

    @Override
    public void visit(CreateTable createTable) {
        foundProblem = true;
        problemList.add(createTable.toString());
    }

    @Override
    public void visit(CreateView createView) {
        foundProblem = true;
        problemList.add(createView.toString());
    }

    @Override
    public void visit(AlterView alterView) {
        foundProblem = true;
        problemList.add(alterView.toString());
    }

    @Override
    public void visit(Alter alter) {
        foundProblem = true;
        problemList.add(alter.toString());
    }

    @Override
    public void visit(Statements stmts) {
        foundProblem = true;
        problemList.add(stmts.toString());

    }

    @Override
    public void visit(Execute execute) {
        foundProblem = true;
        problemList.add(execute.toString());
    }

    @Override
    public void visit(SetStatement set) {
        foundProblem = true;
        problemList.add(set.toString());
    }

    @Override
    public void visit(ShowColumnsStatement set) {
        foundProblem = true;
        problemList.add(set.toString());
    }

    @Override
    public void visit(Merge merge) {
        foundProblem = true;
        problemList.add(merge.toString());
    }

    @Override
    public void visit(Select select) {
        InspectorSelectVisitor selectVisitor = new InspectorSelectVisitor();
        select.getSelectBody().accept(selectVisitor);
        if(selectVisitor.foundProblem()){
            this.foundProblem = true;
            this.problemList.addAll(selectVisitor.getProblemList());
        } else {


            this.highStatement =  select.toString() ;

            String lowStatementUnprepared = select.toString();
            String table = selectVisitor.getFromTable().toString();

            for (String currentC : selectVisitor.getAccessColumnList()) {
                if (ExampleDBACL.isColumnInTableHigh(table, currentC)) {
                    lowStatementUnprepared = lowStatementUnprepared.replace(currentC, "NULL");

                }
            }
            this.lowStatement = lowStatementUnprepared;

        }
    }

    @Override
    public void visit(Upsert upsert) {
        foundProblem = true;
        problemList.add(upsert.toString());
    }

    @Override
    public void visit(UseStatement use) {
        foundProblem = true;
        problemList.add(use.toString());
    }

    @Override
    public void visit(Block block) {
        foundProblem = true;
        problemList.add(block.toString());
    }

    @Override
    public void visit(ValuesStatement values) {
        foundProblem = true;
        problemList.add(values.toString());
    }

    @Override
    public void visit(DescribeStatement describe) {
        foundProblem = true;
        problemList.add(describe.toString());
    }

    @Override
    public void visit(ExplainStatement aThis) {
        foundProblem = true;
        problemList.add(aThis.toString());
    }

    @Override
    public void visit(ShowStatement aThis) {
        foundProblem = true;
        problemList.add(aThis.toString());
    }

    @Override
    public void visit(DeclareStatement aThis) {
        foundProblem = true;
        problemList.add(aThis.toString());
    }

    public boolean foundProblem() {
        return foundProblem;
    }
}
