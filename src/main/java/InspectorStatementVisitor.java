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
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.ArrayList;

public class InspectorStatementVisitor implements StatementVisitor {
    private InspectorSelectVisitor selectVisitor = new InspectorSelectVisitor();
    private boolean foundProblem = false;
    ArrayList<String> problemList = new ArrayList<String>();

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
        foundProblem = true;
        problemList.add(update.toString());
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
        for(Statement stm : stmts.getStatements()){
            stm.accept(this);
        }
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
        select.getSelectBody().accept(selectVisitor);
        if(selectVisitor.foundProblem()){
            this.foundProblem = true;
            this.problemList.addAll(selectVisitor.getProblemList());
        }
        //TODO ADD SME capabilities
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
